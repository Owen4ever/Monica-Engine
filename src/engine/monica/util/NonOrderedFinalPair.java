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

public class NonOrderedFinalPair<F, L> {

    public NonOrderedFinalPair(F f, L l) {
        this.first = f;
        this.last = l;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass())
            return false;
        return equals((NonOrderedFinalPair) obj);
    }

    public boolean equals(NonOrderedFinalPair p) {
        if (p == null)
            return false;
        if (first == null)
            if (last == null)
                return p.first == null && p.last == null;
            else if (p.first == null)
                return last.equals(p.last);
            else if (p.last == null)
                return last.equals(p.first);
            else
                return false;
        else if (last == null)
            if (first == null)
                return p.first == null && p.last == null;
            else if (p.last == null)
                return first.equals(p.first);
            else if (p.first == null)
                return first.equals(p.last);
            else
                return false;
        else {
            if (first.equals(p.first))
                return last.equals(p.last);
            else if (first.equals(p.last))
                return last.equals(p.first);
            else
                return false;
        }
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = 31 + first.hashCode() + last.hashCode();
            if (hashCode == 0)
                hashCode = 12;
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + " [ First = " + first
                + ", Last = " + last + " ]";
    }
    public final F first;
    public final L last;
    private int hashCode;

    public static <F, L> NonOrderedFinalPair<F, L> to(FinalPair<F, L> p) {
        return new NonOrderedFinalPair<>(p.first, p.last);
    }

    public static <F, L> NonOrderedFinalPair<F, L> to(Pair<F, L> p) {
        return new NonOrderedFinalPair<>(p.first, p.last);
    }
}
