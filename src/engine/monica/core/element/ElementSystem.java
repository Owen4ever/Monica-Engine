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

import engine.monica.util.StringID;

public final class ElementSystem {

    private static final long serialVersionUID = 46274354282777902L;

    public ElementSystem(StringID id, String name, Energy energy,
            ElementList elements) {
        this(id, name, energy, elements, null, null);
    }

    public ElementSystem(StringID id, String name, Energy energy,
            ElementList elements, ElementSystem basedOn,
            ConvertorInterface<Integer> c) {
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
        if (hasBasedElementSystem && c == null)
            throw new NullPointerException("The convertor is null.");
        this.convertor = c;
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
        return convertor.convert(energy);
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
    private ConvertorInterface<Integer> convertor;
}
