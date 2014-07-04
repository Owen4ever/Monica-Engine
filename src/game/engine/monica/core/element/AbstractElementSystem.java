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

public abstract class AbstractElementSystem {

    private static final long serialVersionUID = 46274354282777902L;

    public AbstractElementSystem(String name, Energy energy, ElementList elements, AbstractElementSystem basedOn) {
        if (name == null || name.isEmpty())
            throw new NullPointerException("ElementSystem name is null.");
        if (energy == null)
            throw new NullPointerException("Energy is null.");
        if (elements == null)
            throw new NullPointerException("ElementList is null.");
        this.name = name;
        this.energy = energy;
        this.elements = elements;
        this.hasBasedElementSystem = basedOn != null;
        this.basedOn = basedOn;
    }

    public final String getName() {
        return name;
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

    public final AbstractElementSystem getBasedElementSystem() {
        return basedOn;
    }

    public abstract boolean canInteract(AbstractElementSystem s);

    public final boolean canTurnEnergyToBase() {
        return hasBasedElementSystem;
    }

    public abstract int turnEnergyToBase(int energy);

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AbstractElementSystem))
            return false;
        return equals((AbstractElementSystem) obj);
    }

    public boolean equals(AbstractElementSystem s) {
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
    private final String name;
    private final Energy energy;
    private final ElementList elements;
    private final boolean hasBasedElementSystem;
    private final AbstractElementSystem basedOn;
}
