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
                            elementRelations.put(key, ElementRelation.CAN);
                            break;
                        case CANNOT:
                            elementRelations.put(key, ElementRelation.CANNOT);
                            break;
                        case CONDITION:
                            elementRelations.put(key, ElementRelation.CONDITION);
                            elementConditions.put(key,
                                    getSystemCondition(e.getSystemID(),
                                            element.getSystemID()));
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
        return ret(lock, elements.remove(id) != null);
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

    public SystemRelation getRelation(ElementSystem e1, ElementSystem e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        return systemRelations
                .getOrDefault(new FinalPair<>(e1.getID(), e2.getID()),
                        SystemRelation.CANNOT);
    }

    public void setRelation(ElementSystem e1, ElementSystem e2, SystemRelation r) {
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

    public Condition getCondition(ElementSystem e1, ElementSystem e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        return elementConditions.get(new FinalPair<>(e1.getID(), e2.getID()));
    }

    public void setCondition(ElementSystem e1, ElementSystem e2, Condition l) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        if (l == null)
            throw new NullPointerException("The condition is null.");
        ret(lock, () -> {
            systemConditions.put(new FinalPair<>(e1.getID(), e2.getID()), l);
        });
    }
    private final HashMap<FinalPair<StringID, StringID>, SystemRelation> systemRelations = new HashMap<>();
    private final HashMap<FinalPair<StringID, StringID>, Condition> systemConditions = new HashMap<>();

    public void addElementSystem(ElementSystem system) {
        if (system == null)
            throw new NullPointerException("The ElementSystem is null.");
        ret(lock, () -> {
            if (!systems.containsKey(system.getID()))
                systems.put(system.getID(), system);
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
        return ret(lock, systems.remove(id) != null);
    }
    private final HashMap<StringID, ElementSystem> systems
            = new HashMap<>(CoreEngine.getDefaultQuantily(), 0.2f);

    @SuppressWarnings("unchecked")
    public ElementCountSet calc(ElementCountSet set1,
            ElementCountSet set2, Map map, Area area) {
        ArrayList<FinalPair<AbstractElement, Integer>> tempSet = new ArrayList<>();
        set1.getElementsAndCount().parallelStream().forEach(p1 -> {
            set2.getElementsAndCount().parallelStream().filter(p2 -> {
                switch (getElementRelation(p1.first, p2.first)) {
                    case CAN:
                        return true;
                    case CANNOT:
                        return false;
                    case CONDITION:
                        return elementConditionPassed(this, p1.first, p2.first);
                    default:
                        return false;
                }
            }).forEach(p3 -> {
                switch (getElementConflict(p1.first, p3.first)) {
                    case CANNOT:
                        int c1 = p1.last - p3.last;
                        if (c1 > 0)
                            tempSet.add(new FinalPair<>(p1.first, c1));
                        else if (c1 < 0)
                            tempSet.add(new FinalPair<>(p3.first, -c1));
                        break;
                    case CAN:
                        int c2 = p1.last - p3.last;
                        if (c2 > 0) {
                            c2 = getElementConflictProcesser(p1.first, p3.first)
                                    .conflict(p1, p3, c2, map, area);
                            tempSet.add(new FinalPair<>(p1.first, c2));
                        } else if (c2 < 0) {
                            c2 = -c2;
                            c2 = getElementConflictProcesser(p1.first, p3.first)
                                    .conflict(p1, p3, c2, map, area);
                            tempSet.add(new FinalPair<>(p3.first, c2));
                        }
                        break;
                    case CONDITION:
                        int c3 = p1.last - p3.last;
                        if (elementConditionPassed(this, p1.first, p3.first)) {
                            if (c3 > 0) {
                                c3 = getElementConflictProcesser(p1.first, p3.first)
                                        .conflict(p1, p3, c3, map, area);
                                tempSet.add(new FinalPair<>(p1.first, c3));
                            } else if (c3 < 0) {
                                c3 = -c3;
                                c3 = getElementConflictProcesser(p1.first, p3.first)
                                        .conflict(p1, p3, c3, map, area);
                                tempSet.add(new FinalPair<>(p3.first, c3));
                            }
                        } else {
                            if (c3 > 0)
                                tempSet.add(new FinalPair<>(p1.first, c3));
                            else if (c3 < 0)
                                tempSet.add(new FinalPair<>(p3.first, -c3));
                        }
                        break;
                }
            });
        });
        return new ElementCountSet(tempSet.toArray(new FinalPair[tempSet.size()]));
    }

    private static boolean elementConditionPassed(ElementEngine e,
            AbstractElement e1, AbstractElement e2) {
        Condition c = e.getElementCondition(e1, e2);
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

    private static <T> T ret(ReentrantReadWriteLock lock, T t) {
        getWriteLock(lock);
        try {
            return t;
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
