/*
 * The MIT License
 *
 * Copyright 2014 Owen.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package engine.monica.core.element;

import engine.monica.core.engine.CoreEngine;
import engine.monica.util.FinalPair;
import engine.monica.util.condition.SingleCondition;
import engine.monica.util.StringID;
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

    public SingleCondition getElementCondition(StringID e1, StringID e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return elementConditions.get(new FinalPair<>(e1, e2));
    }

    public SingleCondition getElementCondition(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementConditions.get(new FinalPair<>(e1.getID(), e2.getID()));
    }

    public void setElementCondition(AbstractElement e1, AbstractElement e2, SingleCondition c) {
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
            elementConflicts.put(new FinalPair<>(e1.getID(), e2.getID()), c);
        });
    }

    public SingleCondition getElementConflictCondition(StringID e1, StringID e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return elementConditions.get(new FinalPair<>(e1, e2));
    }

    public SingleCondition getElementConflictCondition(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementConditions.get(new FinalPair<>(e1.getID(), e2.getID()));
    }

    public void setElementConflictCondition(AbstractElement e1, AbstractElement e2, SingleCondition c) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (c == null)
            throw new NullPointerException("The condition is null.");
        ret(lock, () -> {
            elementConflictConditions.put(new FinalPair<>(e1.getID(), e2.getID()), c);
        });
    }
    private final HashMap<FinalPair<StringID, StringID>, ElementRelation> elementRelations = new HashMap<>();
    private final HashMap<FinalPair<StringID, StringID>, SingleCondition> elementConditions = new HashMap<>();
    private final HashMap<FinalPair<StringID, StringID>, ElementConflict> elementConflicts = new HashMap<>();
    private final HashMap<FinalPair<StringID, StringID>, SingleCondition> elementConflictConditions = new HashMap<>();

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

    public SingleCondition getSystemCondition(StringID e1, StringID e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The sid is null.");
        return systemConditions.get(new FinalPair<>(e1, e2));
    }

    public SingleCondition getCondition(ElementSystem e1, ElementSystem e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        return elementConditions.get(new FinalPair<>(e1.getID(), e2.getID()));
    }

    public void setCondition(ElementSystem e1, ElementSystem e2, SingleCondition l) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        if (l == null)
            throw new NullPointerException("The condition is null.");
        ret(lock, () -> {
            systemConditions.put(new FinalPair<>(e1.getID(), e2.getID()), l);
        });
    }
    private final HashMap<FinalPair<StringID, StringID>, SystemRelation> systemRelations = new HashMap<>();
    private final HashMap<FinalPair<StringID, StringID>, SingleCondition> systemConditions = new HashMap<>();

    public void addElementSystem(ElementSystem system) {
        if (system == null)
            throw new NullPointerException("The ElementSystem is null.");
        ret(lock, () -> {
            if (!systems.containsKey(system.getID()))
                systems.put(system.getID(), system);
        });
    }

    public ElementSystem addElementSystem(StringID id, String name, Energy energy,
            ElementList elements, ElementSystem basedOn) {
        ElementSystem s = new ElementSystem(id, name, energy, elements, basedOn);
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
    private static interface VReturn {

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

    private static void ret(ReentrantReadWriteLock lock, VReturn r) {
        getWriteLock(lock);
        try {
            r.func();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
