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
import engine.monica.util.StringID;

public final class Career implements NamesGetter {

    public Career(StringID id, Names names, PropertyList defaultProperties) {
        if (id == null)
            throw new NullPointerException("The id is null.");
        if (names == null)
            throw new NullPointerException("The name is null.");
        if (defaultProperties == null)
            throw new NullPointerException("The default properties are null.");
        this.id = id;
        this.names = names;
        this.defaultProperties = defaultProperties;
    }

    public StringID getID() {
        return id;
    }

    @Override
    public Names getNames() {
        return names;
    }

    public PropertyList getProperties() {
        return defaultProperties;
    }
    private final StringID id;
    private final Names names;
    private final PropertyList defaultProperties;
}
