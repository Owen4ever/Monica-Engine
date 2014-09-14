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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;

/**
 * Most codes are copied from {@code java.util.Array#ArrayList}.
 *
 * @see java.util.Arrays#asList(java.lang.Object...)
 */
public final class SimpleArrayList<E> extends AbstractList<E>
        implements RandomAccess, Serializable {

    private static final long serialVersionUID = -2764017481108945198L;
    private final E[] array;

    public SimpleArrayList(E[] array) {
        this.array = array;
    }

    @SuppressWarnings("unchecked")
    public SimpleArrayList(Collection<E> c, Class<?> clazz) {
        if (c.isEmpty())
            throw new NullPointerException();
        array = c.toArray((E[]) Array.newInstance(clazz, c.size()));
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    public Object[] toArray() {
        return array.clone();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size)
            return Arrays.copyOf(this.array, size,
                    (Class<? extends T[]>) a.getClass());
        System.arraycopy(this.array, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    @Override
    public E get(int index) {
        return array[index];
    }

    @Override
    public E set(int index, E element) {
        E oldValue = array[index];
        array[index] = element;
        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < array.length; i++)
                if (array[i] == null)
                    return i;
        } else {
            for (int i = 0; i < array.length; i++)
                if (o.equals(array[i]))
                    return i;
        }
        return -1;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(array, Spliterator.CONCURRENT);
    }
}
