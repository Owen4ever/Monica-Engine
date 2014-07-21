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

package engine.monica.util;

public final class StringID {

    public StringID(String id) {
        if (id == null || id.isEmpty())
            throw new NullPointerException("The string id is null.");
        this.id = id;
        this.hashCode = (id.hashCode() << 4) + 2730;
        this.toString = "(" + id + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || StringID.class != obj.getClass())
            return false;
        return equals((StringID) obj);
    }

    public boolean equals(StringID sid) {
        if (sid == null)
            return false;
        return id.equals(sid.id);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return toString;
    }
    private final String id;
    private final int hashCode;
    private final String toString;
}
