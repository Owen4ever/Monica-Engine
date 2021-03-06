/*
 * Copyright (C) 2014 Owen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package engine.monica.core.element;

import static engine.monica.core.element.ElementRelation.CAN;
import static engine.monica.core.element.ElementRelation.CANNOT;
import static engine.monica.core.element.ElementRelation.COMBINED_CAN;
import static engine.monica.core.element.ElementRelation.CONDITION;
import static engine.monica.core.element.ElementRelation.SYSTEM_CAN;
import static engine.monica.core.element.SystemRelation.BASE_CAN;
import engine.monica.core.engine.CoreEngine;
import static engine.monica.core.engine.EngineConstants.ELEMENT_CALC_DEFAULT;
import static engine.monica.core.engine.EngineConstants.ELEMENT_CONCENTRATION_CALC_DEFAULT;
import engine.monica.core.world.Area;
import engine.monica.core.world.Map;
import engine.monica.util.Convertor;
import engine.monica.util.FinalPair;
import static engine.monica.util.Lock.*;
import engine.monica.util.NonOrderedFinalPair;
import engine.monica.util.SimpleArrayList;
import engine.monica.util.Wrapper;
import engine.monica.util.condition.Condition;
import engine.monica.util.condition.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ElementEngine {

    public ElementEngine() {
    }

    public void addElement(AbstractElement element) {
        if (element == null)
            throw new NullPointerException("The ElementSystem is null.");
        RETW(defaultEngineLock, () -> {
            if (!elements.containsKey(element.getID())) {
                elements.values().parallelStream().forEach(e -> {
                    NonOrderedFinalPair<String, String> key
                            = new NonOrderedFinalPair<>(e.getID(), element.getID());
                    switch (getSystemRelation(e.getSystemID(),
                            element.getSystemID())) {
                        case CANNOT:
                            elementRelations.put(key, ElementRelation.CANNOT);
                            break;
                        case CAN:
                        case BASE_CAN:
                        case CONDITION:
                            elementRelations.put(key, ElementRelation.SYSTEM_CAN);
                            break;
                    }
                    elementConflicts.put(key, ElementConflict.CANNOT);
                });
                elements.put(element.getID(), element);
                elementConcentrationCalcs.put(element.getID(), ELEMENT_CONCENTRATION_CALC_DEFAULT);
                NonOrderedFinalPair<String, String> self = new NonOrderedFinalPair<>(element.getID(), element.getID());
                elementRelations.put(self, ElementRelation.CAN);
                elementCalcs.put(self, ELEMENT_CALC_DEFAULT);
            }
        });
    }

    public AbstractElement addBasedElement(String systemId,
            String id, String name, int turnToEnergy) {
        AbstractElement e = new BaseElement(systemId, id, name, turnToEnergy);
        addElement(e);
        return e;
    }

    @SafeVarargs
    public final AbstractElement addCombinedElement(String systemId,
            String id, String name,
            FinalPair<AbstractElement, Integer>... elementAndCount) {
        AbstractElement e = new CombinedElement(systemId, id,
                name, elementAndCount);
        addElement(e);
        return e;
    }

    public AbstractElement getElement(String id) {
        if (id == null)
            throw new NullPointerException("The String is null.");
        return elements.get(id);
    }

    public boolean removeElement(String id) {
        if (id == null)
            throw new NullPointerException("The String is null.");
        return RETW(defaultEngineLock, () -> elements.remove(id) != null);
    }

    public SimpleArrayList<AbstractElement> getElements() {
        return new SimpleArrayList<>(elements.values(), AbstractElement.class);
    }

    public ElementRelation getElementRelation(String e1, String e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return elementRelations.getOrDefault(new NonOrderedFinalPair<>(e1, e2),
                ElementRelation.CANNOT);
    }

    public ElementRelation getElementRelation(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementRelations
                .getOrDefault(new NonOrderedFinalPair<>(e1.getID(), e2.getID()),
                        ElementRelation.CANNOT);
    }

    public void setElementRelation(AbstractElement e1, AbstractElement e2, ElementRelation r) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (r == null)
            throw new NullPointerException("The element relation is null.");
        RETW(defaultEngineLock, () -> {
            elementRelations.put(new NonOrderedFinalPair<>(e1.getID(), e2.getID()), r);
        });
    }

    public void setElementRelationToAll(AbstractElement e, ElementRelation r) {
        if (e == null)
            throw new NullPointerException("The element is null.");
        if (r == null)
            throw new NullPointerException("The element relation is null.");
        RETW(defaultEngineLock, () -> {
            getElements().parallelStream().forEach(ae -> {
                if (!ae.getID().equals(e.getID()))
                    setElementRelation(e, ae, r);
            });
        });
    }

    public ElementCalculator getElementCalculator(String e1, String e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementCalcs.get(new NonOrderedFinalPair<>(e1, e2));
    }

    public ElementCalculator getElementCalculator(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementCalcs.get(new NonOrderedFinalPair<>(e1.getID(), e2.getID()));
    }

    public void setElementCalculator(AbstractElement e1, AbstractElement e2, ElementCalculator c) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The condition is null.");
        RETW(defaultEngineLock, () -> {
            elementCalcs.put(new NonOrderedFinalPair<>(e1.getID(), e2.getID()), c);
        });
    }

    public ElementConcentrationCalculator getElementConcentrationCalculator(String e) {
        if (e == null)
            throw new NullPointerException("The sid is null.");
        return elementConcentrationCalcs.get(e);
    }

    public ElementConcentrationCalculator getElementConcentrationCalculator(AbstractElement e) {
        if (e == null)
            throw new NullPointerException("The element is null.");
        return elementConcentrationCalcs.get(e.getID());
    }

    public void setElementConcentrationCalculator(AbstractElement e, ElementConcentrationCalculator c) {
        if (e == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The condition is null.");
        RETW(defaultEngineLock, () -> {
            elementConcentrationCalcs.put(e.getID(), c);
        });
    }

    public Condition getElementCondition(String e1, String e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return elementConditions.get(new NonOrderedFinalPair<>(e1, e2));
    }

    public Condition getElementCondition(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementConditions.get(new NonOrderedFinalPair<>(e1.getID(), e2.getID()));
    }

    public void setElementCondition(AbstractElement e1, AbstractElement e2, Condition c) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The condition is null.");
        RETW(defaultEngineLock, () -> {
            elementConditions.put(new NonOrderedFinalPair<>(e1.getID(), e2.getID()), c);
        });
    }

    public void setElementConditionToAll(AbstractElement e, Condition c) {
        if (e == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The condition is null.");
        RETW(defaultEngineLock, () -> {
            elementConditions.keySet().forEach(p -> {
                if (p.first.equals(p.last)
                        && (p.first.equals(e.getID()) || p.last.equals(e.getID())))
                    elementConditions.put(p, c);
            });
        });
    }

    public ElementConflict getElementConflict(String e1, String e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return elementConflicts.getOrDefault(new NonOrderedFinalPair<>(e1, e2),
                ElementConflict.CANNOT);
    }

    public ElementConflict getElementConflict(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementConflicts.getOrDefault(new NonOrderedFinalPair<>(e1.getID(), e2.getID()),
                ElementConflict.CANNOT);
    }

    public void setElementConflict(AbstractElement e1, AbstractElement e2, ElementConflict c) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The conflict is null.");
        RETW(defaultEngineLock, () -> {
            NonOrderedFinalPair<String, String> p = new NonOrderedFinalPair<>(e1.getID(), e2.getID());
            elementConflicts.put(p, c);
            elementConflictProcessers.put(p, (p1, p2, m, a)
                    -> new FinalPair<>(new FinalPair<>(p1.first, p1.last * 2),
                            new FinalPair<>(p2.first, p2.last * 2)));
        });
    }

    public void setElementConflictToAll(AbstractElement e, ElementConflict c) {
        if (e == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The conflict is null.");
        RETW(defaultEngineLock, () -> {
            getElements().parallelStream().forEach(ae -> {
                if (!ae.getID().equals(e.getID()))
                    setElementConflict(e, ae, c);
            });
        });
    }

    public Condition getElementConflictCondition(String e1, String e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return elementConditions.get(new NonOrderedFinalPair<>(e1, e2));
    }

    public Condition getElementConflictCondition(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementConditions.get(new NonOrderedFinalPair<>(e1.getID(), e2.getID()));
    }

    public void setElementConflictCondition(AbstractElement e1, AbstractElement e2, Condition c) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The condition is null.");
        RETW(defaultEngineLock, () -> {
            elementConflictConditions.put(new NonOrderedFinalPair<>(e1.getID(), e2.getID()), c);
        });
    }

    public void setElementConflictConditionToAll(AbstractElement e, Condition c) {
        if (e == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The condition is null.");
        RETW(defaultEngineLock, () -> {
            elementConflictConditions.keySet().forEach(p -> {
                if (p.first.equals(p.last)
                        && (p.first.equals(e.getID()) || p.last.equals(e.getID())))
                    elementConflictConditions.put(p, c);
            });
        });
    }

    public ConflictProcesser getElementConflictProcesser(String e1, String e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return elementConflictProcessers.get(new NonOrderedFinalPair<>(e1, e2));
    }

    public ConflictProcesser getElementConflictProcesser(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementConflictProcessers.get(new NonOrderedFinalPair<>(e1.getID(), e2.getID()));
    }

    public void setElementConflictProcesser(AbstractElement e1, AbstractElement e2, ConflictProcesser p) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (p == null)
            throw new NullPointerException("The condition is null.");
        RETW(defaultEngineLock, () -> {
            elementConflictProcessers.put(new NonOrderedFinalPair<>(e1.getID(), e2.getID()), p);
        });
    }
    private final HashMap<String, AbstractElement> elements
            = new HashMap<>(CoreEngine.getDefaultQuantity() * CoreEngine.getDefaultQuantity(), 0.2f);
    private final HashMap<NonOrderedFinalPair<String, String>, ElementRelation> elementRelations = new HashMap<>();
    private final HashMap<String, ElementConcentrationCalculator> elementConcentrationCalcs = new HashMap<>();
    private final HashMap<NonOrderedFinalPair<String, String>, ElementCalculator> elementCalcs = new HashMap<>();
    private final HashMap<NonOrderedFinalPair<String, String>, Condition> elementConditions = new HashMap<>();
    private final HashMap<NonOrderedFinalPair<String, String>, ElementConflict> elementConflicts = new HashMap<>();
    private final HashMap<NonOrderedFinalPair<String, String>, Condition> elementConflictConditions = new HashMap<>();
    private final HashMap<NonOrderedFinalPair<String, String>, ConflictProcesser> elementConflictProcessers = new HashMap<>();

    public void addElementSystem(ElementSystem system) {
        if (system == null)
            throw new NullPointerException("The ElementSystem is null.");
        RETW(defaultEngineLock, () -> {
            if (!systems.containsKey(system.getID())) {
                systems.put(system.getID(), system);
                systems.values().parallelStream().forEach(s -> {
                    NonOrderedFinalPair<String, String> p = new NonOrderedFinalPair<>(s.getID(), system.getID());
                    if (s.hasBasedElementSystem() && system.hasBasedElementSystem()) {
                        switch (getSystemRelation(s.getBasedElementSystem(), system.getBasedElementSystem())) {
                            case CAN:
                            case BASE_CAN:
                                systemRelations.put(p, SystemRelation.BASE_CAN);
                                break;
                            case CANNOT:
                                break;
                            case CONDITION:
                                systemRelations.put(p, SystemRelation.BASE_CAN);
                                break;
                        }
                    } else
                        systemRelations.put(p, SystemRelation.CANNOT);
                });
            }
        });
    }

    public ElementSystem addElementSystem(String id, String name, Energy energy,
            ElementList elements) {
        return addElementSystem(id, name, energy, elements, null, null);
    }

    public ElementSystem addElementSystem(String id, String name, Energy energy,
            ElementList elements, ElementSystem basedOn, Convertor<Integer> c) {
        ElementSystem s = new ElementSystem(id, name, energy, elements, basedOn, c);
        addElementSystem(s);
        return s;
    }

    public ElementSystem getSystem(String id) {
        if (id == null)
            throw new NullPointerException("The String is null.");
        return systems.get(id);
    }

    public boolean removeElementSystem(String id) {
        if (id == null)
            throw new NullPointerException("The String is null.");
        return RETW(defaultEngineLock, () -> systems.remove(id) != null);
    }

    public SimpleArrayList<ElementSystem> getSystems() {
        return new SimpleArrayList<>(systems.values(), ElementSystem.class);
    }

    public SystemRelation getSystemRelation(String e1, String e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return systemRelations.getOrDefault(new NonOrderedFinalPair<>(e1, e2),
                SystemRelation.CANNOT);
    }

    public SystemRelation getSystemRelation(ElementSystem e1, ElementSystem e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        return systemRelations
                .getOrDefault(new NonOrderedFinalPair<>(e1.getID(), e2.getID()),
                        SystemRelation.CANNOT);
    }

    public void setSystemRelation(ElementSystem e1, ElementSystem e2, SystemRelation r) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        if (r == null)
            throw new NullPointerException("The ElementSystem relation is null.");
        if (r == SystemRelation.CANNOT)
            getElements().parallelStream().forEach(fe1 -> {
                getElements().parallelStream().forEach(fe2 -> {
                    if (getElementRelation(fe1, fe2) == ElementRelation.SYSTEM_CAN)
                        setElementRelation(fe1, fe2, ElementRelation.CANNOT);
                });
            });
        else
            getElements().parallelStream().forEach(fe1 -> {
                getElements().parallelStream().forEach(fe2 -> {
                    if (getElementRelation(fe1, fe2) == ElementRelation.CANNOT)
                        setElementRelation(fe1, fe2, ElementRelation.SYSTEM_CAN);
                });
            });
        RETW(defaultEngineLock, () -> {
            systemRelations.put(new NonOrderedFinalPair<>(e1.getID(), e2.getID()), r);
        });
    }

    public void setSystemRelationToAll(ElementSystem e, SystemRelation r) {
        if (e == null)
            throw new NullPointerException("The ElementSystem is null.");
        if (r == null)
            throw new NullPointerException("The ElementSystem relation is null.");
        RETW(defaultEngineLock, () -> {
            getSystems().parallelStream().forEach(s -> {
                if (!s.getID().equals(e.getID()))
                    setSystemRelation(e, s, r);
            });
        });
    }

    public Condition getSystemCondition(String e1, String e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return systemConditions.get(new NonOrderedFinalPair<>(e1, e2));
    }

    public Condition getSystemCondition(ElementSystem e1, ElementSystem e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        return systemConditions.get(new NonOrderedFinalPair<>(e1.getID(), e2.getID()));
    }

    public void setSystemCondition(ElementSystem e1, ElementSystem e2, Condition l) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        if (l == null)
            throw new NullPointerException("The condition is null.");
        RETW(defaultEngineLock, () -> {
            systemConditions.put(new NonOrderedFinalPair<>(e1.getID(), e2.getID()), l);
        });
    }

    public void setSystemConditionToAll(ElementSystem e, Condition l) {
        if (e == null)
            throw new NullPointerException("The ElementSystem is null.");
        if (l == null)
            throw new NullPointerException("The condition is null.");
        RETW(defaultEngineLock, () -> {
            systemConditions.keySet().forEach(p -> {
                if (p.first.equals(p.last)
                        && (p.first.equals(e.getID()) || p.last.equals(e.getID())))
                    systemConditions.put(p, l);
            });
        });
    }
    private final HashMap<String, ElementSystem> systems = new HashMap<>(CoreEngine.getDefaultQuantity(), 0.2f);
    private final HashMap<NonOrderedFinalPair<String, String>, SystemRelation> systemRelations = new HashMap<>();
    private final HashMap<NonOrderedFinalPair<String, String>, Condition> systemConditions = new HashMap<>();

    @SuppressWarnings("unchecked")
    public ElementCountSet calc(ElementCountSet set1, ElementCountSet set2,
            Map map, Area area) {
        ConcurrentHashMap<AbstractElement, Wrapper<Integer>> hashMap = new ConcurrentHashMap<>();
        set1.getElementsAndCount().parallelStream().forEach(p1 -> {
            set2.getElementsAndCount().parallelStream().forEach(p2 -> {
                calc0(hashMap, p1, p2, map, area);
            });
        });
        new ArrayList<>(hashMap.keySet()).forEach(e -> {
            if (hashMap.get(e).pack < 1)
                hashMap.remove(e);
        });
        FinalPair<AbstractElement, Integer>[] pairs = new FinalPair[hashMap.size()];
        Wrapper<Integer> count = new Wrapper<>(0);
        hashMap.forEach((e, w) -> {
            pairs[count.pack] = new FinalPair<>(e, w.pack);
            count.pack++;
        });
        return new ElementCountSet(pairs);
    }

    private void calc0(ConcurrentHashMap<AbstractElement, Wrapper<Integer>> hashMap,
            FinalPair<AbstractElement, Integer> p1,
            FinalPair<AbstractElement, Integer> p2,
            Map map, Area area) {
        p1 = calc_concentrate(p1, map, area);
        p2 = calc_concentrate(p2, map, area);
        switch (getElementRelation(p1.first, p2.first)) {
            case CANNOT:
                break;
            case CAN:
                if (calc_canConflict(p1, p2, map, area))
                    calc_conflict(hashMap, p1, p2, map, area);
                else
                    calc_default(hashMap, p1, p2, map, area);
                break;
            case COMBINED_CAN:
                boolean combined1 = p1.first.isCombined();
                boolean combined2 = p2.first.isCombined();
                if (combined1 && combined2)
                    calc_combined2(hashMap, p1, p2, map, area);
                else if (combined1)
                    calc_combined1(hashMap, p1, p2, map, area);
                else if (combined2)
                    calc_combined1(hashMap, p1, p2, map, area);
                break;
            case SYSTEM_CAN:
                calc_system(hashMap, p1, p2, map, area);
                break;
            case CONDITION:
                if (elementConditionPassed(this, p1.first, p2.first))
                    if (calc_canConflict(p1, p2, map, area))
                        calc_conflict(hashMap, p1, p2, map, area);
                    else
                        calc_default(hashMap, p1, p2, map, area);
                break;
            default:
                break;
        }
    }

    private boolean calc_canConflict(FinalPair<AbstractElement, Integer> p1,
            FinalPair<AbstractElement, Integer> p2, Map map, Area area) {
        switch (getElementConflict(p1.first, p2.first)) {
            case CANNOT:
                return false;
            case CAN:
                return true;
            case CONDITION:
                if (elementConditionPassed(this, p1.first, p2.first))
                    return true;
                return false;
            default:
                return false;
        }
    }

    private FinalPair<AbstractElement, Integer>
            calc_concentrate(FinalPair<AbstractElement, Integer> p,
                    Map map, Area area) {
        return getElementConcentrationCalculator(p.first.getID()).calcByConcentration(p, map, area);
    }

    private static void calc_check(ConcurrentHashMap<AbstractElement, Wrapper<Integer>> hashMap,
            FinalPair<AbstractElement, Integer> p) {
        Wrapper<Integer> w = hashMap.get(p.first);
        if (w == null) {
            w = new Wrapper<>(p.last);
            hashMap.put(p.first, w);
        } else
            w.pack += p.last;
    }

    private static void calc_check_exist(ConcurrentHashMap<AbstractElement, Wrapper<Integer>> hashMap,
            FinalPair<AbstractElement, Integer> p) {
        Wrapper<Integer> w = hashMap.get(p.first);
        if (w != null)
            w.pack += p.last;
    }

    private void calc_default(ConcurrentHashMap<AbstractElement, Wrapper<Integer>> hashMap,
            FinalPair<AbstractElement, Integer> p1,
            FinalPair<AbstractElement, Integer> p2,
            Map map, Area area) {
        FinalPair<AbstractElement, Integer>[] ps
                = getElementCalculator(p1.first, p2.first).calc(p1, p2, map, area);
        if (ps != null && ps.length > 0) {
            for (FinalPair<AbstractElement, Integer> p : ps)
                calc_check(hashMap, p);
        }
    }

    /**
     * @param p1 The pair with CombinedElement and Integer.
     * @param p2 The pair with AbstractElement (not CombinedElement) and
     * Integer.
     */
    private void calc_combined1(ConcurrentHashMap<AbstractElement, Wrapper<Integer>> hashMap,
            FinalPair<AbstractElement, Integer> p1,
            FinalPair<AbstractElement, Integer> p2,
            Map map, Area area) {
        new SimpleArrayList<>(((CombinedElement) p1.first).getCombinedElements())
                .forEach(pair -> {
                    FinalPair<AbstractElement, Integer> tpair
                    = new FinalPair<>(pair.first, pair.last * p1.last);
                    calc0(hashMap, tpair, p2, map, area);
                });
    }

    /**
     * @param p1 The pair with CombinedElement and Integer.
     * @param p2 The pair with CombinedElement and Integer.
     */
    private void calc_combined2(ConcurrentHashMap<AbstractElement, Wrapper<Integer>> hashMap,
            FinalPair<AbstractElement, Integer> p1,
            FinalPair<AbstractElement, Integer> p2,
            Map map, Area area) {
        SimpleArrayList<FinalPair<AbstractElement, Integer>> l1
                = new SimpleArrayList<>(((CombinedElement) p1.first).getCombinedElements());
        SimpleArrayList<FinalPair<AbstractElement, Integer>> l2
                = new SimpleArrayList<>(((CombinedElement) p2.first).getCombinedElements());
        l1.forEach(pair1 -> {
            FinalPair<AbstractElement, Integer> tp1
                    = new FinalPair<>(pair1.first, pair1.last * p1.last);
            l2.forEach(pair2 -> {
                FinalPair<AbstractElement, Integer> tp2
                        = new FinalPair<>(pair2.first, pair2.last * p2.last);
                calc0(hashMap, tp1, tp2, map, area);
            });
        });
    }

    private void calc_conflict(ConcurrentHashMap<AbstractElement, Wrapper<Integer>> hashMap,
            FinalPair<AbstractElement, Integer> p1,
            FinalPair<AbstractElement, Integer> p2,
            Map map, Area area) {
        FinalPair<FinalPair<AbstractElement, Integer>, FinalPair<AbstractElement, Integer>> tempPs
                = getElementConflictProcesser(p1.first, p2.first).conflict(p1, p2, map, area);
        p1 = tempPs.first;
        p2 = tempPs.last;
        FinalPair<AbstractElement, Integer>[] ps
                = getElementCalculator(p1.first, p2.first).calc(p1, p2, map, area, true);
        if (ps != null && ps.length > 0) {
            for (FinalPair<AbstractElement, Integer> p : ps)
                calc_check(hashMap, p);
        }
    }

    private void calc_system(ConcurrentHashMap<AbstractElement, Wrapper<Integer>> hashMap,
            FinalPair<AbstractElement, Integer> p1,
            FinalPair<AbstractElement, Integer> p2,
            Map map, Area area) {
        switch (getSystemRelation(p1.first.getSystemID(), p2.first.getSystemID())) {
            case CAN:
                calc_system_default(hashMap, p1, p2, map, area);
                break;
            case BASE_CAN:
                calc_system_base(hashMap, p1, p2, map, area);
                break;
            case CONDITION:
                if (systemConditionPassed(this, p1.first.getSystemID(), p2.first.getSystemID()))
                    calc_system_default(hashMap, p1, p2, map, area);
                break;
            default:
                break;
        }
    }

    private void calc_system_default(ConcurrentHashMap<AbstractElement, Wrapper<Integer>> hashMap,
            FinalPair<AbstractElement, Integer> p1,
            FinalPair<AbstractElement, Integer> p2,
            Map map, Area area) {
        int i1 = p1.first.toEnergy() * p1.last;
        int i2 = p2.first.toEnergy() * p2.last;
        int i = i1 - i2;
        if (i > 0) {
            calc_check_exist(hashMap, new FinalPair<>(p1.first, -p1.last));
            calc_check(hashMap, new FinalPair<>(p1.first, i / p1.first.toEnergy()));
        } else if (i < 0) {
            calc_check_exist(hashMap, new FinalPair<>(p2.first, -p2.last));
            calc_check(hashMap, new FinalPair<>(p2.first, -i / p2.first.toEnergy()));
        }
    }

    private void calc_system_base(ConcurrentHashMap<AbstractElement, Wrapper<Integer>> hashMap,
            FinalPair<AbstractElement, Integer> p1,
            FinalPair<AbstractElement, Integer> p2,
            Map map, Area area) {
        calc_system_base0(hashMap,
                p1, getSystem(p1.first.getSystemID()),
                p2, getSystem(p2.first.getSystemID()),
                map, area);
    }

    private void calc_system_base0(ConcurrentHashMap<AbstractElement, Wrapper<Integer>> hashMap,
            FinalPair<AbstractElement, Integer> p1,
            ElementSystem s1,
            FinalPair<AbstractElement, Integer> p2,
            ElementSystem s2,
            Map map, Area area) {
        switch (getSystemRelation(p1.first.getSystemID(), p2.first.getSystemID())) {
            case CAN:
                calc_system_base_default(hashMap, p1, s1, p2, s2, map, area);
                break;
            case BASE_CAN:
                calc_system_base0(hashMap,
                        p1, s1.getBasedElementSystem(),
                        p2, s2.getBasedElementSystem(),
                        map, area);
                break;
            case CONDITION:
                if (systemConditionPassed(this, p1.first.getSystemID(), p2.first.getSystemID()))
                    calc_system_base_default(hashMap, p1, s1, p2, s2, map, area);
                break;
            default:
                break;
        }
    }

    private void calc_system_base_default(ConcurrentHashMap<AbstractElement, Wrapper<Integer>> hashMap,
            FinalPair<AbstractElement, Integer> p1,
            ElementSystem s1,
            FinalPair<AbstractElement, Integer> p2,
            ElementSystem s2,
            Map map, Area area) {
        int i = calc_system_base_toEnergy(p1, s1) - calc_system_base_toEnergy(p2, s2);
        if (i > 0) {
            calc_check_exist(hashMap, new FinalPair<>(p1.first, -p1.last));
            calc_check(hashMap, new FinalPair<>(p1.first, i / p1.first.toEnergy()));
        } else if (i < 0) {
            calc_check_exist(hashMap, new FinalPair<>(p2.first, -p2.last));
            calc_check(hashMap, new FinalPair<>(p2.first, -i / p2.first.toEnergy()));
        }
    }

    private int calc_system_base_toEnergy(FinalPair<AbstractElement, Integer> p, ElementSystem s) {
        int result = p.last * p.first.toEnergy();
        ElementSystem e = getSystem(p.first.getSystemID());
        while (!e.equals(s))
            if (e.hasBasedElementSystem())
                result = e.turnEnergyToBase(result);
        return result;
    }

    private static boolean elementConditionPassed(ElementEngine e,
            AbstractElement e1, AbstractElement e2) {
        return conditionPassed(e.getElementCondition(e1, e2));
    }

    private static boolean systemConditionPassed(ElementEngine e,
            String s1, String s2) {
        return conditionPassed(e.getSystemCondition(s1, s2));
    }

    private static boolean conditionPassed(Condition c) {
        ArrayList<Provider> list = new ArrayList<>(c.count());
        new SimpleArrayList<>(c.getConditionTypes()).forEach(pt -> {
            list.addAll(CoreEngine.processProviderType(pt));
        });
        return c.match(list.toArray(new Provider[list.size()]));
    }
    private final transient ReentrantReadWriteLock defaultEngineLock
            = new ReentrantReadWriteLock();
}
