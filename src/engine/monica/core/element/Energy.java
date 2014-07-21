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

public final class Energy {

    private static final long serialVersionUID = 46274354375486750L;

    public Energy(StringID id, String name) {
        if (id == null)
            throw new NullPointerException("The id is null.");
        this.id = id;
        setName(name);
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof Energy)
            return equals((Energy) obj);
        else
            return false;
    }

    public boolean equals(Energy e) {
        if (e == null)
            return false;
        return name.equals(e.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + " [ Name = " + name + " ]";
    }
    private final StringID id;
    private String name;
}
