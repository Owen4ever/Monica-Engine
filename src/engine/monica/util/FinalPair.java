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

public final class FinalPair<F, L> {

    public FinalPair(F f, L l) {
        this.first = f;
        this.last = l;
        hashCode = 31 + first.hashCode() + last.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass())
            return false;
        return equals((FinalPair) obj);
    }

    public boolean equals(FinalPair p) {
        if (p == null)
            return false;
        return (first == null ? p.first == null : first.equals(p.first))
                && (last == null ? p.last == null : last.equals(p.last));
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
    public final F first;
    public final L last;
    private final int hashCode;

    public static <F, L> FinalPair<F, L> toFinalPair(Pair<F, L> pair) {
        return new FinalPair<>(pair.first, pair.last);
    }
}
