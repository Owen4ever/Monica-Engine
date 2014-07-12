/*
 * The MIT License
 *
 * Copyright 2014 Owen.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package game.engine.monica.core.property;

public final class EffectPointer {

    public EffectPointer(int pointer) {
        this.pointer = pointer;
    }

    public int pointer() {
        return pointer;
    }

    public EffectPointer previous() {
        return previous;
    }

    public EffectPointer next() {
        return next;
    }

    public void delete() {
        if (previous != null)
            previous.next = next;
        if (next != null) {
            next.previous = previous;
            //next.decrease();
        }
        pointer = -1;
    }

    private void decrease() {
        pointer--;
        if (next != null)
            next.decrease();
    }

    public EffectPointer linkNew() {
        EffectPointer newOne = new EffectPointer(pointer + 1);
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
        EffectPointer p = (EffectPointer) obj;
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
    EffectPointer previous = null;
    EffectPointer next = null;

    public static EffectPointer newFirstPointer() {
        return new EffectPointer(-1);
    }
}
