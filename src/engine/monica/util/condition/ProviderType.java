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

package engine.monica.util.condition;

import engine.monica.core.engine.CoreEngine;
import engine.monica.util.AlreadyExistsInContainerException;
import java.util.HashSet;

public final class ProviderType {

    public ProviderType(String id) {
        if (id == null)
            throw new NullPointerException("The id is null.");
        this.id = id;
    }

    public String getID() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return id.equals(((ProviderType) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode() + 1;
    }

    @Override
    public String toString() {
        return getClass().getName() + "(" + id + ")";
    }
    private final String id;

    public static String newID(String sid) {
        if (sid == null || sid.isEmpty())
            throw new NullPointerException("The sid is null.");
        if (sids.contains(sid))
            throw new AlreadyExistsInContainerException("The sid has already"
                    + " put into the map.");
        sids.add(sid);
        return sid;
    }

    public static String getString(String sid) {
        if (sid == null || sid.isEmpty())
            throw new NullPointerException("The sid is null.");
        if (sids.contains(sid))
            return sid;
        return null;
    }
    private static final HashSet<String> sids
            = new HashSet<>(CoreEngine.getDefaultQuantily(), 0.4f);
}
