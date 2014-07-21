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

public class Type<T> {

    public Type(T t) {
        type = t;
    }

    public final T set(T t) {
        T tt = type;
        type = t;
        return tt;
    }

    public final T get() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass())
            return false;
        @SuppressWarnings("unchecked")
        Type<T> t = (Type<T>) obj;
        if (type == null)
            return t.type == null;
        return type.equals(t.type);
    }

    @Override
    public int hashCode() {
        return (type == null ? -1024 : type.hashCode());
    }
    private T type;
}
