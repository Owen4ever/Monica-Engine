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
import engine.monica.util.condition.Condition;
import engine.monica.util.StringID;
import engine.monica.util.Wrapper;
import engine.monica.util.condition.Provider;
import engine.monica.util.condition.ProviderType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ElementEngine {

    public ElementEngine() {
    }

    public void addElement(AbstractElement element) {
        if (element == null)
            throw new NullPointerException("The ElementSystem is null.");
        ret(lock, () -> {
            if (!elements.containsKey(element.getID())) {
                elements.values().parallelStream().forEach(e -> {
                    FinalPair<StringID, StringID> key
                            = new FinalPair<>(e.getID(), element.getID());
                    switch (getSystemRelation(e.getSystemID(),
                            element.getSystemID())) {
                        case CAN:
                            elementRelations.put(key, ElementRelation.SYSTEM_CAN);
                            break;
                        case CANNOT:
                            elementRelations.put(key, ElementRelation.CANNOT);
                            break;
                        case CONDITION:
                            elementRelations.put(key, ElementRelation.SYSTEM_CONDITION);
                            break;
                    }
                });
                elements.put(element.getID(), element);
            }
        });
    }

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
        return ret(lock, () -> elements.remove(id) != null);
    }
    private final HashMap<StringID, AbstractElement> elements
            = new HashMap<>(CoreEngine.getDefaultQuantily()
                    * CoreEngine.getDefaultQuantily(), 0.2f);

    public ElementRelation getElementRelation(StringID e1, StringID e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return elementRelations.getOrDefault(new FinalPair<>(e1, e2),
                ElementRelation.CANNOT);
    }

    public ElementRelation getElementRelation(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementRelations
                .getOrDefault(new FinalPair<>(e1.getID(), e2.getID()),
                        ElementRelation.CANNOT);
    }

    public void setElementRelation(AbstractElement e1, AbstractElement e2, ElementRelation r) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (r == null)
            throw new NullPointerException("The element relation is null.");
        ret(lock, () -> {
            elementRelations.put(new FinalPair<>(e1.getID(), e2.getID()), r);
        });
    }

    public Condition getElementCondition(StringID e1, StringID e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return elementConditions.get(new FinalPair<>(e1, e2));
    }

    public Condition getElementCondition(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementConditions.get(new FinalPair<>(e1.getID(), e2.getID()));
    }

    public void setElementCondition(AbstractElement e1, AbstractElement e2, Condition c) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The condition is null.");
        ret(lock, () -> {
            elementConditions.put(new FinalPair<>(e1.getID(), e2.getID()), c);
        });
    }

    public ElementConflict getElementConflict(StringID e1, StringID e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return elementConflicts.getOrDefault(new FinalPair<>(e1, e2),
                ElementConflict.CANNOT);
    }

    public ElementConflict getElementConflict(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementConflicts.getOrDefault(new FinalPair<>(e1.getID(), e2.getID()),
                ElementConflict.CANNOT);
    }

    public void setElementConflict(AbstractElement e1, AbstractElement e2, ElementConflict c) {
        if (e1 == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The conflict is null.");
        ret(lock, () -> {
            FinalPair<StringID, StringID> p = new FinalPair<>(e1.getID(), e2.getID());
            elementConflicts.put(p, c);
            elementConflictProcessers.put(p, (p1, p2, count, m, a) -> count << 1);
        });
    }

    public Condition getElementConflictCondition(StringID e1, StringID e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return elementConditions.get(new FinalPair<>(e1, e2));
    }

    public Condition getElementConflictCondition(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementConditions.get(new FinalPair<>(e1.getID(), e2.getID()));
    }

    public void setElementConflictCondition(AbstractElement e1, AbstractElement e2, Condition c) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The condition is null.");
        ret(lock, () -> {
            elementConflictConditions.put(new FinalPair<>(e1.getID(), e2.getID()), c);
        });
    }

    public ConflictProcesserInterface getElementConflictProcesser(StringID e1, StringID e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return elementConflictProcessers.get(new FinalPair<>(e1, e2));
    }

    public ConflictProcesserInterface getElementConflictProcesser(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementConflictProcessers.get(new FinalPair<>(e1.getID(), e2.getID()));
    }

    public void setElementConflictProcesser(AbstractElement e1, AbstractElement e2, ConflictProcesserInterface p) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (p == null)
            throw new NullPointerException("The condition is null.");
        ret(lock, () -> {
            elementConflictProcessers.put(new FinalPair<>(e1.getID(), e2.getID()), p);
        });
    }
    private final HashMap<FinalPair<StringID, StringID>, ElementRelation> elementRelations = new HashMap<>();
    private final HashMap<FinalPair<StringID, StringID>, Condition> elementConditions = new HashMap<>();
    private final HashMap<FinalPair<StringID, StringID>, ElementConflict> elementConflicts = new HashMap<>();
    private final HashMap<FinalPair<StringID, StringID>, Condition> elementConflictConditions = new HashMap<>();
    private final HashMap<FinalPair<StringID, StringID>, ConflictProcesserInterface> elementConflictProcessers = new HashMap<>();

    public SystemRelation getSystemRelation(StringID e1, StringID e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return systemRelations.getOrDefault(new FinalPair<>(e1, e2),
                SystemRelation.CANNOT);
    }

    public SystemRelation getSystemRelation(ElementSystem e1, ElementSystem e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        return systemRelations
                .getOrDefault(new FinalPair<>(e1.getID(), e2.getID()),
                        SystemRelation.CANNOT);
    }

    public void setSystemRelation(ElementSystem e1, ElementSystem e2, SystemRelation r) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        if (r == null)
            throw new NullPointerException("The ElementSystem relation is"
                    + " null.");
        ret(lock, () -> {
            systemRelations.put(new FinalPair<>(e1.getID(), e2.getID()), r);
        });
    }

    public Condition getSystemCondition(StringID e1, StringID e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return systemConditions.get(new FinalPair<>(e1, e2));
    }

    public Condition getSystemCondition(ElementSystem e1, ElementSystem e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        return elementConditions.get(new FinalPair<>(e1.getID(), e2.getID()));
    }

    public void setSystemCondition(ElementSystem e1, ElementSystem e2, Condition l) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        if (l == null)
            throw new NullPointerException("The condition is null.");
        ret(lock, () -> {
            systemConditions.put(new FinalPair<>(e1.getID(), e2.getID()), l);
        });
    }

    public void addElementSystem(ElementSystem system) {
        if (system == null)
            throw new NullPointerException("The ElementSystem is null.");
        ret(lock, () -> {
            if (!systems.containsKey(system.getID())) {
                systems.put(system.getID(), system);
                systems.values().parallelStream().forEach(s -> {
                    FinalPair<StringID, StringID> p = new FinalPair<>(s.getID(), system.getID());
                    if (s.hasBasedElementSystem() && system.hasBasedElementSystem()) {
                        switch (getSystemRelation(s.getBasedElementSystem(), system.getBasedElementSystem())) {
                            case CAN:
                            case BASE_CAN:
                                systemRelations.put(p, SystemRelation.BASE_CAN);
                                break;
                            case CANNOT:
                                break;
                            case CONDITION:
                                systemRelations.put(p, SystemRelation.BASE_CONDITION);
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

    public ElementSystem getElementSystem(StringID id) {
        if (id == null)
            throw new NullPointerException("The StringID is null.");
        return systems.get(id);
    }

    public boolean removeElementSystem(StringID id) {
        if (id == null)
            throw new NullPointerException("The StringID is null.");
        return ret(lock, () -> systems.remove(id) != null);
    }
    private final HashMap<StringID, ElementSystem> systems = new HashMap<>(CoreEngine.getDefaultQuantily(), 0.2f);
    private final HashMap<FinalPair<StringID, StringID>, SystemRelation> systemRelations = new HashMap<>();
    private final HashMap<FinalPair<StringID, StringID>, Condition> systemConditions = new HashMap<>();

    /*private FinalPair<AbstractElement, Integer>
            calc_concentration(FinalPair<AbstractElement, Integer> p,
                    Map map, Area area) {
        return p; // TODO:
    }

    private ArrayList<Pair<AbstractElement, Integer>>
            calc_sys(ArrayList<Pair<AbstractElement, Integer>> list,
                    FinalPair<AbstractElement, Integer> p1,
                    FinalPair<AbstractElement, Integer> p2,
                    Map map, Area area) {
        if (calc_sys_0(list,
                getElementSystem(p1.first.getSystemID()),
                getElementSystem(p2.first.getSystemID())))
            calc0(list, p1, p2, map, area);
        return list;
    }

    private boolean calc_sys_0(ArrayList<Pair<AbstractElement, Integer>> list,
            ElementSystem s1, ElementSystem s2) {
        switch (getSystemRelation(s1, s2)) {
            case CAN:
                break;
            case BASE_CAN:
                return calc_sys_0(list, s1.getBasedElementSystem(), s2.getBasedElementSystem());
            case CONDITION:
                if (systemConditionPassed(this, s1, s2))
                    return true;
        }
        return false;
    }

    private ArrayList<Pair<AbstractElement, Integer>>
            calc0(ArrayList<Pair<AbstractElement, Integer>> list,
                    FinalPair<AbstractElement, Integer> p1,
                    FinalPair<AbstractElement, Integer> p2,
                    Map map, Area area) {
        switch (getElementConflict(p1.first, p2.first)) {
            case CANNOT:
                boolean com1 = p1.first.isCombined();
                boolean com2 = p2.first.isCombined();
                if (com1 & com2)
                    return calc_combined2(list, p1, p2, map, area);
                else if (com1)
                    return calc_combined1(list, p1, p2, map, area);
                else if (com2)
                    return calc_combined1(list, p2, p1, map, area);
                else
                    return calc_default(list, p1, p2, map, area);
            case CAN:
                int c2 = p1.last - p2.last;
                if (c2 > 0) {
                    c2 = getElementConflictProcesser(p1.first, p2.first)
                            .conflict(p1, p2, c2, map, area);
                    list.add(new Pair<>(p1.first, c2));
                } else if (c2 < 0) {
                    c2 = -c2;
                    c2 = getElementConflictProcesser(p1.first, p2.first)
                            .conflict(p1, p2, c2, map, area);
                    list.add(new Pair<>(p2.first, c2));
                }
                break;
            case CONDITION:
                if (elementConditionPassed(this, p1.first, p2.first))
                    return calc_conflict(list, p1, p2, map, area);
                else
                    return calc_default(list, p1, p2, map, area);
        }
        return list;
    }

    private ArrayList<Pair<AbstractElement, Integer>>
            calc_conflict(ArrayList<Pair<AbstractElement, Integer>> list,
                    FinalPair<AbstractElement, Integer> p1,
                    FinalPair<AbstractElement, Integer> p2,
                    Map map, Area area) {
        int c1 = p1.last - p2.last;
        if (c1 > 0) {
            c1 = getElementConflictProcesser(p1.first, p2.first)
                    .conflict(p1, p2, c1, map, area);
            list.add(new Pair<>(p1.first, c1));
        } else if (c1 < 0) {
            c1 = -c1;
            c1 = getElementConflictProcesser(p1.first, p2.first)
                    .conflict(p2, p1, c1, map, area);
            list.add(new Pair<>(p2.first, -c1));
        }
        return list;
    }

    private ArrayList<Pair<AbstractElement, Integer>>
            calc_default(ArrayList<Pair<AbstractElement, Integer>> list,
                    FinalPair<AbstractElement, Integer> p1,
                    FinalPair<AbstractElement, Integer> p2,
                    Map map, Area area) {
        int c = p1.last - p2.last;
        if (c > 0)
            list.add(new Pair<>(p1.first, c));
        else if (c < 0)
            list.add(new Pair<>(p2.first, -c));
        return list;
    }

    private ArrayList<Pair<AbstractElement, Integer>>
            calc_combined1(ArrayList<Pair<AbstractElement, Integer>> list,
                    FinalPair<AbstractElement, Integer> p1,
                    FinalPair<AbstractElement, Integer> p2,
                    Map map, Area area) {
        Arrays.asList(((CombinedElement) p1.first).getCombinedElements()).parallelStream().forEach(p -> {
            switch (getElementRelation(p.first, p2.first)) {
                case CAN:
                    calc0(list, p, p2, map, area);
                    break;
                case CANNOT:
                    break;
                case SYSTEM_CAN:
                    calc_sys(list, p1, p2, map, area);
                    break;
                case CONDITION:
                    if (elementConditionPassed(this, p1.first, p2.first))
                        calc0(list, p1, p2, map, area);
                    break;
            }
        });
        return list;
    }

    private ArrayList<Pair<AbstractElement, Integer>>
            calc_combined2(ArrayList<Pair<AbstractElement, Integer>> list,
                    FinalPair<AbstractElement, Integer> p1,
                    FinalPair<AbstractElement, Integer> p2,
                    Map map, Area area) {
        Arrays.asList(((CombinedElement) p1.first).getCombinedElements())
                .parallelStream().forEach(p3 -> {
                    Arrays.asList(((CombinedElement) p2.first).getCombinedElements())
                    .parallelStream().forEach(p4 -> {
                        calc_(list, p3, p4, map, area);
                    });
                });
        return list;
    }

    private ArrayList<Pair<AbstractElement, Integer>>
            calc_(ArrayList<Pair<AbstractElement, Integer>> list,
                    FinalPair<AbstractElement, Integer> p1,
                    FinalPair<AbstractElement, Integer> p2,
                    Map map, Area area) {
        switch (getElementRelation(p1.first, p2.first)) {
            case CAN:
                calc0(list, calc_concentration(p1, map, area),
                        calc_concentration(p2, map, area), map, area);
                break;
            case SYSTEM_CAN:
                calc_sys(list, calc_concentration(p1, map, area),
                        calc_concentration(p2, map, area), map, area);
                break;
            case CANNOT:
                break;
            case CONDITION:
                if (elementConditionPassed(this, p1.first, p2.first))
                    calc0(list, calc_concentration(p1, map, area),
                            calc_concentration(p2, map, area), map, area);
                break;
            case SYSTEM_CONDITION:
                if (!systemConditionPassed(this,
                        getElementSystem(p1.first.getSystemID()),
                        getElementSystem(p2.first.getSystemID())))
                    calc0(list, calc_concentration(p1, map, area),
                            calc_concentration(p2, map, area), map, area);
                break;
            default:
                break;
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public ElementCountSet calc(ElementCountSet set1,
            ElementCountSet set2, Map map, Area area) {
        ArrayList<Pair<AbstractElement, Integer>> list = new ArrayList<>();
        set1.getElementsAndCount().parallelStream().forEach(p1 -> {
            set2.getElementsAndCount().parallelStream().forEach(p2 -> {
                calc_(list, p1, p2, map, area);
            });
        });
        ArrayList<FinalPair<AbstractElement, Integer>> tempList = new ArrayList<>(list.size());
        list.parallelStream().collect(() -> new ArrayList<Pair<AbstractElement, Integer>>(),
                (l, p) -> {
                    Wrapper<Boolean> dontContain = new Wrapper<>(true);
                    l.parallelStream()
                    .filter(tpair -> tpair.first.getID().equals(p.first.getID()))
                    .forEach(tempPair -> {
                        tempPair.last += p.last;
                        dontContain.pack = false;
                    });
                    if (dontContain.pack) {
                        l.add(p);
                    }
                }, (ll1, ll2) -> {
                    ll1.addAll(ll2);
                }).forEach(p -> {
                    tempList.add(FinalPair.toFinalPair(p));
                });
        return new ElementCountSet(tempList.toArray(new FinalPair[tempList.size()]));
    }*/

    public ElementCountSet calc(ElementCountSet set1, ElementCountSet set2,
            Map map, Area area) {
        HashMap<AbstractElement, Wrapper<Integer>> hashMap = new HashMap<>();

        set1.getElementsAndCount().parallelStream().forEach(p1 -> {
            set2.getElementsAndCount().parallelStream().forEach(p2 -> {
            });
        });

        FinalPair<AbstractElement, Integer>[] pairs = new FinalPair[hashMap.size()];
        Wrapper<Integer> count = new Wrapper<>(0);
        hashMap.forEach((e, w) -> {
            pairs[count.pack] = new FinalPair<>(e, w.pack);
            count.pack++;
        });
        return new ElementCountSet(pairs);
    }

    private HashMap<AbstractElement, Wrapper<Integer>>
            calc0(HashMap<AbstractElement, Wrapper<Integer>> hashMap,
                    FinalPair<AbstractElement, Integer> p1,
                    FinalPair<AbstractElement, Integer> p2,
                    Map map, Area area) {
        switch (getElementRelation(p1.first, p2.first)) {
            case CANNOT:
                break;
            case CAN:
                break;
            case SYSTEM_CAN:
                break;
            case CONDITION:
                break;
            case SYSTEM_CONDITION:
                break;
            default:
                break;
        }
        return hashMap;
    }

    private FinalPair<AbstractElement, Integer>
            calc_concentration(FinalPair<AbstractElement, Integer> p,
                    Map map, Area area) {
        if (area.getElementConcentration().getConcentration(p.first)
                > area.getElementConcentration().getTotal() * 0.75d)
            return new FinalPair<>(p.first, (int) (p.last * 1.5d) + 1);
        return p;
    }

    private HashMap<AbstractElement, Wrapper<Integer>>
            calc_checkConflict(HashMap<AbstractElement, Wrapper<Integer>> hashMap,
                    FinalPair<AbstractElement, Integer> p1,
                    FinalPair<AbstractElement, Integer> p2,
                    Map map, Area area) {
        return hashMap;
    }

    private static boolean elementConditionPassed(ElementEngine e,
            AbstractElement e1, AbstractElement e2) {
        return conditionPassed(e.getElementCondition(e1, e2));
    }

    private static boolean systemConditionPassed(ElementEngine e,
            ElementSystem s1, ElementSystem s2) {
        return conditionPassed(e.getSystemCondition(s1, s2));
    }

    private static boolean conditionPassed(Condition c) {
        ArrayList<Provider> list = new ArrayList<>(c.count());
        ProviderType[] types = c.getConditionTypes();
        for (ProviderType type : types)
            list.addAll(CoreEngine.processProviderType(type));
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
    private final transient ReentrantReadWriteLock lock
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
