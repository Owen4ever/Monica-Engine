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

import engine.monica.core.engine.CoreEngine;
import engine.monica.core.map.Area;
import engine.monica.core.map.Map;
import engine.monica.util.FinalPair;
import engine.monica.util.NonOrderedFinalPair;
import engine.monica.util.SimpleArrayList;
import engine.monica.util.condition.Condition;
import engine.monica.util.StringID;
import engine.monica.util.Wrapper;
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
        ret(defaultEngineLock, () -> {
            if (!elements.containsKey(element.getID())) {
                elements.values().parallelStream().forEach(e -> {
                    NonOrderedFinalPair<StringID, StringID> key
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
                elementConcentrationCalcs.put(element.getID(), EE_DEFAULT_CONCENTRATION_CALC);
                NonOrderedFinalPair<StringID, StringID> self = new NonOrderedFinalPair<>(element.getID(), element.getID());
                elementRelations.put(self, ElementRelation.CAN);
                elementCalcs.put(self, EE_DEFAULT_ELEMENT_CALC);
            }
        });
    }
    private static final ElementConcentrationCalculatorInterface EE_DEFAULT_CONCENTRATION_CALC
            = (p, map, area) -> {
                double da = area.getElementConcentration().getConcentration(p.first);
                double t = area.getElementConcentration().getTotal();
                double per = da / t;
                if (per >= .8)
                    return new FinalPair<>(p.first, p.last << 1);
                else if (per >= .68)
                    return new FinalPair<>(p.first, (p.last * 3) >> 1);
                else if (per >= .56)
                    return new FinalPair<>(p.first, (p.last * 10) >> 3);
                else
                    return p;
            };
    @SuppressWarnings("unchecked")
    private static final ElementCalculatorInterface EE_DEFAULT_ELEMENT_CALC
            = (p1, p2, map, area, c) -> {
                int i = p1.last - p2.last;
                if (i > 0)
                    return new FinalPair[]{new FinalPair<>(p1.first, i), new FinalPair<>(p2.first, -p2.last)};
                else if (i < 0)
                    return new FinalPair[]{new FinalPair<>(p2.first, i), new FinalPair<>(p1.first, -p1.last)};
                else
                    return null;
            };

    public AbstractElement addBasedElement(StringID systemId,
            StringID id, String name, int turnToEnergy) {
        AbstractElement e = new BasedElement(systemId, id, name, turnToEnergy);
        addElement(e);
        return e;
    }

    @SafeVarargs
    public final AbstractElement addCombinedElement(StringID systemId,
            StringID id, String name,
            FinalPair<AbstractElement, Integer>... elementAndCount) {
        AbstractElement e = new CombinedElement(systemId, id,
                name, elementAndCount);
        addElement(e);
        return e;
    }

    public AbstractElement getElement(StringID id) {
        if (id == null)
            throw new NullPointerException("The StringID is null.");
        return elements.get(id);
    }

    public boolean removeElement(StringID id) {
        if (id == null)
            throw new NullPointerException("The StringID is null.");
        return ret(defaultEngineLock, () -> elements.remove(id) != null);
    }

    public SimpleArrayList<AbstractElement> getElements() {
        return new SimpleArrayList<>(elements.values(), AbstractElement.class);
    }

    public ElementRelation getElementRelation(StringID e1, StringID e2) {
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
        ret(defaultEngineLock, () -> {
            elementRelations.put(new NonOrderedFinalPair<>(e1.getID(), e2.getID()), r);
        });
    }

    public ElementCalculatorInterface getElementCalculator(StringID e1, StringID e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return elementCalcs.get(new NonOrderedFinalPair<>(e1, e2));
    }

    public ElementCalculatorInterface getElementCalculator(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementCalcs.get(new NonOrderedFinalPair<>(e1.getID(), e2.getID()));
    }

    public void setElementCalculator(AbstractElement e1, AbstractElement e2, ElementCalculatorInterface c) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The condition is null.");
        ret(defaultEngineLock, () -> {
            elementCalcs.put(new NonOrderedFinalPair<>(e1.getID(), e2.getID()), c);
        });
    }

    public ElementConcentrationCalculatorInterface getElementConcentrationCalculator(StringID e) {
        if (e == null)
            throw new NullPointerException("The sid is null.");
        return elementConcentrationCalcs.get(e);
    }

    public ElementConcentrationCalculatorInterface getElementConcentrationCalculator(AbstractElement e) {
        if (e == null)
            throw new NullPointerException("The element is null.");
        return elementConcentrationCalcs.get(e.getID());
    }

    public void setElementConcentrationCalculator(AbstractElement e, ElementConcentrationCalculatorInterface c) {
        if (e == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The condition is null.");
        ret(defaultEngineLock, () -> {
            elementConcentrationCalcs.put(e.getID(), c);
        });
    }

    public Condition getElementCondition(StringID e1, StringID e2) {
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
        ret(defaultEngineLock, () -> {
            elementConditions.put(new NonOrderedFinalPair<>(e1.getID(), e2.getID()), c);
        });
    }

    public ElementConflict getElementConflict(StringID e1, StringID e2) {
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
        if (e1 == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The conflict is null.");
        ret(defaultEngineLock, () -> {
            NonOrderedFinalPair<StringID, StringID> p = new NonOrderedFinalPair<>(e1.getID(), e2.getID());
            elementConflicts.put(p, c);
            elementConflictProcessers.put(p, (p1, p2, m, a)
                    -> new FinalPair<>(new FinalPair<>(p1.first, p1.last * 2),
                            new FinalPair<>(p2.first, p2.last * 2)));
        });
    }

    public Condition getElementConflictCondition(StringID e1, StringID e2) {
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
        ret(defaultEngineLock, () -> {
            elementConflictConditions.put(new NonOrderedFinalPair<>(e1.getID(), e2.getID()), c);
        });
    }

    public ConflictProcesserInterface getElementConflictProcesser(StringID e1, StringID e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return elementConflictProcessers.get(new NonOrderedFinalPair<>(e1, e2));
    }

    public ConflictProcesserInterface getElementConflictProcesser(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementConflictProcessers.get(new NonOrderedFinalPair<>(e1.getID(), e2.getID()));
    }

    public void setElementConflictProcesser(AbstractElement e1, AbstractElement e2, ConflictProcesserInterface p) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (p == null)
            throw new NullPointerException("The condition is null.");
        ret(defaultEngineLock, () -> {
            elementConflictProcessers.put(new NonOrderedFinalPair<>(e1.getID(), e2.getID()), p);
        });
    }
    private final HashMap<StringID, AbstractElement> elements
            = new HashMap<>(CoreEngine.getDefaultQuantily() * CoreEngine.getDefaultQuantily(), 0.2f);
    private final HashMap<NonOrderedFinalPair<StringID, StringID>, ElementRelation> elementRelations = new HashMap<>();
    private final HashMap<StringID, ElementConcentrationCalculatorInterface> elementConcentrationCalcs = new HashMap<>();
    private final HashMap<NonOrderedFinalPair<StringID, StringID>, ElementCalculatorInterface> elementCalcs = new HashMap<>();
    private final HashMap<NonOrderedFinalPair<StringID, StringID>, Condition> elementConditions = new HashMap<>();
    private final HashMap<NonOrderedFinalPair<StringID, StringID>, ElementConflict> elementConflicts = new HashMap<>();
    private final HashMap<NonOrderedFinalPair<StringID, StringID>, Condition> elementConflictConditions = new HashMap<>();
    private final HashMap<NonOrderedFinalPair<StringID, StringID>, ConflictProcesserInterface> elementConflictProcessers = new HashMap<>();

    public void addElementSystem(ElementSystem system) {
        if (system == null)
            throw new NullPointerException("The ElementSystem is null.");
        ret(defaultEngineLock, () -> {
            if (!systems.containsKey(system.getID())) {
                systems.put(system.getID(), system);
                systems.values().parallelStream().forEach(s -> {
                    NonOrderedFinalPair<StringID, StringID> p = new NonOrderedFinalPair<>(s.getID(), system.getID());
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

    public ElementSystem addElementSystem(StringID id, String name, Energy energy,
            ElementList elements) {
        return addElementSystem(id, name, energy, elements, null, null);
    }

    public ElementSystem addElementSystem(StringID id, String name, Energy energy,
            ElementList elements, ElementSystem basedOn, ConvertorInterface<Integer> c) {
        ElementSystem s = new ElementSystem(id, name, energy, elements, basedOn, c);
        addElementSystem(s);
        return s;
    }

    public ElementSystem getSystem(StringID id) {
        if (id == null)
            throw new NullPointerException("The StringID is null.");
        return systems.get(id);
    }

    public boolean removeElementSystem(StringID id) {
        if (id == null)
            throw new NullPointerException("The StringID is null.");
        return ret(defaultEngineLock, () -> systems.remove(id) != null);
    }

    public SimpleArrayList<ElementSystem> getSystems() {
        return new SimpleArrayList<>(systems.values(), ElementSystem.class);
    }

    public SystemRelation getSystemRelation(StringID e1, StringID e2) {
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
            throw new NullPointerException("The ElementSystem relation is"
                    + " null.");
        ret(defaultEngineLock, () -> {
            systemRelations.put(new NonOrderedFinalPair<>(e1.getID(), e2.getID()), r);
        });
    }

    public Condition getSystemCondition(StringID e1, StringID e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return systemConditions.get(new NonOrderedFinalPair<>(e1, e2));
    }

    public Condition getSystemCondition(ElementSystem e1, ElementSystem e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        return elementConditions.get(new NonOrderedFinalPair<>(e1.getID(), e2.getID()));
    }

    public void setSystemCondition(ElementSystem e1, ElementSystem e2, Condition l) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        if (l == null)
            throw new NullPointerException("The condition is null.");
        ret(defaultEngineLock, () -> {
            systemConditions.put(new NonOrderedFinalPair<>(e1.getID(), e2.getID()), l);
        });
    }
    private final HashMap<StringID, ElementSystem> systems = new HashMap<>(CoreEngine.getDefaultQuantily(), 0.2f);
    private final HashMap<NonOrderedFinalPair<StringID, StringID>, SystemRelation> systemRelations = new HashMap<>();
    private final HashMap<NonOrderedFinalPair<StringID, StringID>, Condition> systemConditions = new HashMap<>();

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
        return getElementConcentrationCalculator(p.first.getID()).concentrate(p, map, area);
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
            StringID s1, StringID s2) {
        return conditionPassed(e.getSystemCondition(s1, s2));
    }

    private static boolean conditionPassed(Condition c) {
        ArrayList<Provider> list = new ArrayList<>(c.count());
        new SimpleArrayList<>(c.getConditionTypes()).forEach(pt -> {
            list.addAll(CoreEngine.processProviderType(pt));
        });
        return c.match(list.toArray(new Provider[list.size()]));
    }

    private static void getWriteLock(ReentrantReadWriteLock lock) {
        do {
            if (!lock.isWriteLocked() && lock.writeLock().tryLock())
                if (lock.getWriteHoldCount() != 1)
                    lock.writeLock().unlock();
                else
                    break;
        } while (true);
    }
    private final transient ReentrantReadWriteLock defaultEngineLock
            = new ReentrantReadWriteLock();

    @FunctionalInterface
    private static interface VoidReturn {

        void func();
    }

    @FunctionalInterface
    private static interface TReturn<T> {

        T func();
    }

    private static <T> T ret(ReentrantReadWriteLock lock, TReturn<T> t) {
        getWriteLock(lock);
        try {
            return t.func();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static void ret(ReentrantReadWriteLock lock, VoidReturn r) {
        getWriteLock(lock);
        try {
            r.func();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
