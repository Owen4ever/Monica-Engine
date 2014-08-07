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

/**
 * The base class of the element in the game world. Everything can be a element,
 * such as {@code Water} is a element which is a member of
 * {@code Natural Element System} and it is a {@code BaseElement},
 * {@code Electricity} is a element which is a part of
 * {@code Electrical Element System}, and also {@code Water} can be a element
 * wich is belong to {@code Magical Element System} and is is a
 * {@code CombinedElement} which creates by the {@code Water} in
 * {@code Natural Element System}.
 */
public abstract class AbstractElement {

    protected AbstractElement(StringID systemId,
            StringID id, String name, int turnToEnergy) {
        if (systemId == null)
            throw new NullPointerException("The ElementSystem id is null.");
        if (id == null)
            throw new NullPointerException("The id is null.");
        this.systemId = systemId;
        this.id = id;
        setName(name);
        setValueToEnergy(turnToEnergy);
    }

    /**
     * If the object inherits from {@code CombinedElement}, this mothed will
     * return true, otherwise will return false.
     */
    public abstract boolean isCombined();

    /**
     * Return the ID of the {@code ElementSystem} which includes this
     * {@code AbstractElement}.
     */
    public final StringID getSystemID() {
        return systemId;
    }

    /**
     * Return the ID of this {@code AbstractElement}.
     */
    public final StringID getID() {
        return id;
    }

    /**
     * Return the {@code Name} of this {@code AbstractElement}.
     */
    public final String getName() {
        return name;
    }

    /**
     * To set the {@code Name} of this {@code AbstractElement}.
     */
    public final void setName(String name) {
        if (name == null || name.isEmpty())
            throw new NullPointerException("The name of Element is null.");
        this.name = name;
    }

    public final int toEnergy() {
        return toEnergy;
    }

    public final void setValueToEnergy(int val) {
        if (val < 1)
            throw new ElementInitializeException("The number"
                    + " which turns to energy smaller than 1.");
        this.toEnergy = val;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != getClass())
            return false;
        AbstractElement e = (AbstractElement) obj;
        return id.equals(e.id) && systemId.equals(e.systemId);
    }

    @Override
    public int hashCode() {
        return systemId.hashCode() + id.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName()
                + " [ Name = " + name
                + ", ID = " + id
                + ", SystemID = " + systemId
                + ", To Energy = " + toEnergy + " ]";
    }
    private final StringID systemId;
    private final StringID id;
    private String name;
    private int toEnergy;
}
