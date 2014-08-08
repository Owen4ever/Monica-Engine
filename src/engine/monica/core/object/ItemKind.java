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

import engine.monica.util.StringID;

public final class ItemKind {

    public ItemKind(StringID id, String name) {
        this(id, name, PARENT);
    }

    public ItemKind(StringID id, String name, ItemKind parent) {
        if (id == null)
            throw new NullPointerException("the id is null.");
        if (name == null || name.isEmpty())
            throw new NullPointerException("the name is null.");
        this.id = id;
        this.name = name;
        hasParent = parent != null;
        this.parent = hasParent ? parent : PARENT;
    }

    private ItemKind() {
        id = null;
        name = null;
        hasParent = false;
        parent = null;
    }

    public StringID getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasParent() {
        return hasParent;
    }

    public ItemKind getParent() {
        return parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != getClass())
            return false;
        ItemKind k = (ItemKind) obj;
        return id.equals(k.id);
    }

    @Override
    public int hashCode() {
        return 135 + id.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName()
                + " [ ID = " + id
                + ", Name = " + name
                + ", Parent = " + (hasParent ? parent.name : "null");
    }
    private final StringID id;
    private final String name;
    private final boolean hasParent;
    private final ItemKind parent;

    private static final ItemKind PARENT = new ItemKind();
}
