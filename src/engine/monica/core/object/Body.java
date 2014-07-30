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

package engine.monica.core.object;

import engine.monica.core.graphics.GameObject;
import engine.monica.core.property.PropertyList;
import engine.monica.util.StringID;
import engine.monica.util.VectorInterface;

public final class Body<V extends VectorInterface> {

    public Body(StringID id, Name name, String describe,
            PropertyList defaultProperties, TalentInterface[] talents,
            GameObject<V>[] bodyObjects) {
        if (id == null)
            throw new NullPointerException("The id is null.");
        if (name == null)
            throw new NullPointerException("The name is null.");
        if (describe == null)
            throw new NullPointerException("The describe is null.");
        this.id = id;
        this.name = name;
        this.describe = describe;
        if (defaultProperties != null)
            this.defaultProperties.addAll(defaultProperties);
        this.talents = talents == null ? new TalentInterface[0] : talents;
        this.bodyObjects = bodyObjects;
    }

    public StringID getID() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public String getDescribe() {
        return describe;
    }

    public PropertyList getDefaultProperties() {
        return defaultProperties;
    }

    public TalentInterface[] getTalents() {
        return talents;
    }

    public GameObject<V>[] getBodyObjects() {
        return bodyObjects;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != getClass())
            return false;
        Body b = (Body) obj;
        return id.equals(b.id);
    }

    @Override
    public int hashCode() {
        return 128 + id.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName()
                + " [ ID = " + id
                + ", Name = " + name.getName()
                + ", Describe = " + describe + " ]";
    }
    private final StringID id;
    private final Name name;
    private final String describe;
    private final PropertyList defaultProperties = new PropertyList();
    private final TalentInterface[] talents;
    private final GameObject<V>[] bodyObjects;
}
