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

package game.engine.monica.core.element;

import game.engine.monica.util.StringID;

public final class ElementSystem {

    private static final long serialVersionUID = 46274354282777902L;

    public ElementSystem(StringID id, String name, Energy energy,
            ElementList elements, ElementSystem basedOn) {
        if (id == null)
            throw new NullPointerException("The id is null.");
        if (energy == null)
            throw new NullPointerException("The Energy is null.");
        if (elements == null)
            throw new NullPointerException("The ElementList is null.");
        this.id = id;
        setName(name);
        this.energy = energy;
        this.elements = elements;
        this.hasBasedElementSystem = basedOn != null;
        this.basedOn = basedOn;
    }

    public final StringID getID() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.isEmpty())
            throw new NullPointerException("The name is null.");
        this.name = name;
    }

    public final Energy getEnergy() {
        return energy;
    }

    public final ElementList getElements() {
        return elements;
    }

    public final boolean hasBasedElementSystem() {
        return hasBasedElementSystem;
    }

    public final ElementSystem getBasedElementSystem() {
        return basedOn;
    }

    public final boolean canTurnEnergyToBase() {
        return hasBasedElementSystem;
    }

    public int turnEnergyToBase(int energy) {
        return energy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ElementSystem))
            return false;
        return equals((ElementSystem) obj);
    }

    public boolean equals(ElementSystem s) {
        if (s == null)
            return false;
        return name.equals(s.name)
                && energy.equals(s.energy)
                && elements.equals(s.elements)
                && hasBasedElementSystem == s.hasBasedElementSystem
                && (hasBasedElementSystem ? basedOn.equals(s.basedOn) : true);
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31 + energy.hashCode() * 71
                + elements.hashCode() * 11
                + (hasBasedElementSystem ? basedOn.hashCode() : 12);
    }

    @Override
    public String toString() {
        return getClass().getName() + " [ Name = " + name
                + " , Energy =" + energy.getName()
                + " , Elements = " + elements
                + " , Based On = "
                + (hasBasedElementSystem ? basedOn.getName() : "Null")
                + " ]";
    }
    private final StringID id;
    private String name;
    private final Energy energy;
    private final ElementList elements;
    private final boolean hasBasedElementSystem;
    private final ElementSystem basedOn;
}
