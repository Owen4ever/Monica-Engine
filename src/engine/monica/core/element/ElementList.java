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

import engine.monica.util.AlreadyExistsInContainerException;
import java.util.HashSet;

public final class ElementList {

    @SafeVarargs
    public ElementList(AbstractElement... e) {
        elements = new AbstractElement[e.length];
        if (e.length != 0) {
            HashSet<AbstractElement> temp = new HashSet<>(e.length, 1f);
            for (int i = 0; i != e.length; ++i)
                if (e[i] == null)
                    throw new NullPointerException("Element is null.");
                else {
                    if (!temp.add(e[i]))
                        throw new AlreadyExistsInContainerException("The element"
                                + " has already existed in the array.");
                    elements[i] = e[i];
                }
        }
    }

    public AbstractElement[] getElements() {
        return elements;
    }

    public int size() {
        return elements.length;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ElementList))
            return false;
        return equals((ElementList) obj);
    }

    public boolean equals(ElementList list) {
        if (list == null)
            return false;
        if (size() != list.size())
            return false;
        for (int i = 0; i != size(); ++i)
            if (elements[i] != list.elements[i])
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        int eh = elements[0].hashCode();
        for (int i = 1; i != size(); ++i) {
            eh *= i;
            eh += elements[i].hashCode();
        }
        return 74 + eh;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(6 * size()).append("[ ");
        for (AbstractElement e : elements)
            sb.append(e.getName()).append(", ");
        sb.replace(sb.length() - 2, sb.length(), " ]");
        return sb.toString();
    }
    private final AbstractElement[] elements;
}
