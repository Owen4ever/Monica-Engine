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

public final class ElementEngine {

    public ElementEngine() {
    }

    public ElementRelation getRelation(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementRelations.get(new FinalPair<>(e1.getID(), e2.getID()));
    }

    public void setRelation(AbstractElement e1, AbstractElement e2, ElementRelation r) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (r == null)
            throw new NullPointerException("The element relation is null.");
        elementRelations.put(new FinalPair<>(e1.getID(), e2.getID()), r);
    }

    public SingleCondition getCondition(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementConditions.get(new FinalPair<>(e1.getID(), e2.getID()));
    }

    public void setCondition(AbstractElement e1, AbstractElement e2, SingleCondition l) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (l == null)
            throw new NullPointerException("The condition is null.");
        elementConditions.put(new FinalPair<>(e1.getID(), e2.getID()), l);
    }
    private final HashMap<FinalPair<StringID, StringID>, ElementRelation> elementRelations
            = new HashMap<>();
    private final HashMap<FinalPair<StringID, StringID>, SingleCondition> elementConditions
            = new HashMap<>();

    public ElementRelation getRelation(ElementSystem e1, ElementSystem e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        return elementRelations.get(new FinalPair<>(e1.getID(), e2.getID()));
    }

    public void setRelation(ElementSystem e1, ElementSystem e2, SystemRelation r) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The ElementSystem is null.");
        if (r == null)
            throw new NullPointerException("The ElementSystem relation is"
                    + " null.");
        systemRelations.put(new FinalPair<>(e1.getID(), e2.getID()), r);
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
        systemConditions.put(new FinalPair<>(e1.getID(), e2.getID()), l);
    }
    private final HashMap<FinalPair<StringID, StringID>, SystemRelation> systemRelations
            = new HashMap<>();
    private final HashMap<FinalPair<StringID, StringID>, SingleCondition> systemConditions
            = new HashMap<>();

    public void addElementSystem(ElementSystem system) {
        if (system == null)
            throw new NullPointerException("The ElementSystem is null.");
        if (!systems.containsKey(system.getID()))
            systems.put(system.getID(), system);
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
        return systems.remove(id) != null;
    }
    private final HashMap<StringID, ElementSystem> systems
            = new HashMap<>(CoreEngine.getDefaultQuantily(), 0.2f);

    public void addElement(AbstractElement element) {
        if (element == null)
            throw new NullPointerException("The ElementSystem is null.");
        if (!elements.containsKey(element.getID()))
            elements.put(element.getID(), element);
    }

    public AbstractElement addBasedElement(StringID id, String name,
            int turnToEnergy) {
        AbstractElement e = new BasedElement(id, name, turnToEnergy);
        addElement(e);
        return e;
    }

    public AbstractElement addCombinedElement(StringID id, String name,
            FinalPair<AbstractElement, Integer> elementAndCount) {
        AbstractElement e = new CombinedElement(id, name, elementAndCount);
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
        return elements.remove(id) != null;
    }
    private final HashMap<StringID, AbstractElement> elements
            = new HashMap<>(CoreEngine.getDefaultQuantily()
                    * CoreEngine.getDefaultQuantily(), 0.2f);
}
