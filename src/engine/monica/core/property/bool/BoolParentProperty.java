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

package engine.monica.core.property.bool;

import engine.monica.core.property.ParentPropertyInterface;
import engine.monica.core.property.PropertyID;

public abstract class BoolParentProperty extends BoolProperty
        implements ParentPropertyInterface<Boolean> {

    public BoolParentProperty(PropertyID id) {
        this(id, false, false);
    }

    public BoolParentProperty(PropertyID id,
            boolean defaultVal, boolean offsetVal) {
        super(id, defaultVal, offsetVal);
    }
}
