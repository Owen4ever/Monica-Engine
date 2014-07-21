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
        setValueTurnToEnergy(turnToEnergy);
    }

    public abstract boolean isCombined();

    public final StringID getSystemID() {
        return systemId;
    }

    public final StringID getID() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.isEmpty())
            throw new NullPointerException("The name of Element is null.");
        this.name = name;
    }

    public final int turnToEnergy() {
        return turnToEnergy;
    }

    public final void setValueTurnToEnergy(int val) {
        if (val < 1)
            throw new ElementInitializeException("The number"
                    + " which turns to energy smaller than 1.");
        this.turnToEnergy = val;
    }
    private final StringID systemId;
    private final StringID id;
    private String name;
    private int turnToEnergy;
}
