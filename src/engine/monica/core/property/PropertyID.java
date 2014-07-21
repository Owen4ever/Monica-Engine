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

package engine.monica.core.property;

import engine.monica.util.StringID;

public final class PropertyID implements Comparable<PropertyID> {

    public PropertyID(StringID id, PropertyType type, String name, String intro) {
        if (type == null)
            throw new NullPointerException("The PropertyID type is null.");
        setID(id);
        setName(name);
        setIntroduction(intro);
        this.type = type;
    }

    public PropertyType getType() {
        return type;
    }

    public StringID getID() {
        return id;
    }

    public void setID(StringID name) {
        if (name == null)
            throw new NullPointerException("The property ID name cannot be null.");
        this.id = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty())
            throw new NullPointerException("The name of PropertyID is null.");
    }

    public String getIntroduction() {
        return intro;
    }

    public void setIntroduction(String intro) {
        if (intro == null || intro.isEmpty())
            throw new NullPointerException("The property ID introduction cannot be null.");
        this.intro = intro;
    }

    @Override
    public int compareTo(PropertyID type) {
        return Integer.compare(id.hashCode(), type.id.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != getClass())
            return false;
        PropertyID pt = (PropertyID) obj;
        return pt.id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName()
                + " [ ID = " + id
                + ", Name = " + name + ", Type = " + type.name()
                + ", Introduction = " + intro + " ]";
    }
    private StringID id;
    private String name, intro;
    private final PropertyType type;

    public static enum PropertyType {

        INTEGER, BOOLEAN
    }
}
