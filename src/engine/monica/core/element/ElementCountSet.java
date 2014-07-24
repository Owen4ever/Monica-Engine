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
import engine.monica.util.FinalPair;
import engine.monica.util.SimpleArrayList;
import java.util.HashSet;
import java.util.List;

public final class ElementCountSet {

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public ElementCountSet(FinalPair<AbstractElement, Integer>... p) {
        if (p == null)
            throw new NullPointerException("Element is null.");
        FinalPair<AbstractElement, Integer>[] ae = new FinalPair[p.length];
        HashSet<AbstractElement> temp = new HashSet<>(p.length, 1f);
        if (p.length != 0)
            for (int i = 0; i != p.length; ++i)
                if (p[i] == null)
                    throw new NullPointerException("Element is null.");
                else {
                    if (!temp.add(p[i].first))
                        throw new AlreadyExistsInContainerException("The element"
                                + " has already existed in the array.");
                    ae[i] = p[i];
                }
        elements = new SimpleArrayList<>(ae);
    }

    public List<FinalPair<AbstractElement, Integer>> getElementsAndCount() {
        return elements;
    }

    public int size() {
        return elements.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ElementList))
            return false;
        return equals((ElementCountSet) obj);
    }

    public boolean equals(ElementCountSet set) {
        if (set == null)
            return false;
        if (size() != set.size())
            return false;
        for (int i = 0; i != size(); ++i)
            if (elements.hashCode() == set.hashCode())
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        int eh = elements.hashCode();
        for (int i = 1; i != size(); ++i) {
            eh *= i;
            eh += elements.get(i).hashCode();
        }
        return 74 + eh;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(6 * size()).append("[ ");
        for (FinalPair<AbstractElement, Integer> p : elements)
            sb.append(p.first.getName())
                    .append("(").append(p.last).append(")").append(", ");
        sb.replace(sb.length() - 2, sb.length(), " ]");
        return sb.toString();
    }
    private final List<FinalPair<AbstractElement, Integer>> elements;
}
