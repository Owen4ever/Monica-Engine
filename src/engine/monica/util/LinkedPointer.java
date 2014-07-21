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

public final class LinkedPointer {

    public LinkedPointer(int pointer) {
        this.pointer = pointer;
    }

    public int pointer() {
        return pointer;
    }

    public LinkedPointer previous() {
        return previous;
    }

    public LinkedPointer next() {
        return next;
    }

    public void delete() {
        if (previous != null)
            previous.next = next;
        if (next != null) {
            next.previous = previous;
            next.decrease();
        }
        pointer = -1;
    }

    private void decrease() {
        pointer--;
        if (next != null)
            next.decrease();
    }

    public LinkedPointer linkNew() {
        LinkedPointer newOne = new LinkedPointer(pointer + 1);
        this.next = newOne;
        newOne.previous = this;
        return newOne;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != getClass())
            return false;
        LinkedPointer p = (LinkedPointer) obj;
        return pointer == p.pointer;
    }

    @Override
    public int hashCode() {
        return pointer;
    }

    @Override
    public String toString() {
        return Integer.toString(pointer, 10);
    }
    int pointer;
    private LinkedPointer previous = null;
    private LinkedPointer next = null;

    public static LinkedPointer first() {
        return new LinkedPointer(-1);
    }
}
