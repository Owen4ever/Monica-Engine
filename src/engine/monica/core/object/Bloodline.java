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

import engine.monica.core.property.PropertyList;
import engine.monica.util.Vector;

public class Bloodline<V extends Vector<V>> implements NamesGetter {

    public Bloodline(String id, Names names, PropertyList properties, Talent<V>[] talents) {
        if (id == null)
            throw new NullPointerException("The id is null.");
        if (names == null)
            throw new NullPointerException("The names is null.");
        if (properties == null)
            throw new NullPointerException("The properties are null.");
        if (talents == null)
            throw new NullPointerException("The talents are null.");
        for (Talent<V> t : talents)
            if (t == null)
                throw new NullPointerException("The talent is null.");
        this.id = id;
        this.names = names;
        this.properties = properties;
        this.talents = talents;
    }

    @Override
    public Names getNames() {
        return names;
    }
    private final String id;
    private final Names names;
    private final PropertyList properties;
    private final Talent<V>[] talents;
}
