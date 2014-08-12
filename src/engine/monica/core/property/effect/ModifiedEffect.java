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

package engine.monica.core.property.effect;

import engine.monica.core.property.PropertyID;
import engine.monica.util.Wrapper;

public class ModifiedEffect<T> extends AbstractEffect<T> {

    public ModifiedEffect(String id, PropertyID affectTo, T val) {
        super(id, EffectType.TYPE_MODIFIED, affectTo, v -> null);
        this.val = new Wrapper<>(val);
        setEffector(v -> this.val.pack);
    }

    public T getValue() {
        return val.pack;
    }

    public void setValue(T t) {
        val.pack = t;
    }
    private final Wrapper<T> val;
}
