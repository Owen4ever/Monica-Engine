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

package game.engine.monica.util;

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
