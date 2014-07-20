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

package engine.monica.core.map;

public final class VisibleLevel {

    public VisibleLevel(int level) {
        this.level = level;
    }

    public boolean isVisible() {
        return level >= INT_VISIBLE_MIN;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    private int level;

    public static final int INT_INVISIBLE_MAX = -25500;
    public static final int INT_INVISIBLE_ORDINARY = -4000;
    public static final int INT_INVISIBLE_MIN = 0;
    public static final int INT_VISIBLE_MIN = 1;
    public static final int INT_VISIBLE_ORDINARY = 6000;
    public static final int INT_VISIBLE_MAX = 25500;

    public static final VisibleLevel LV_INVISIBLE_MAX = new VisibleLevel(INT_INVISIBLE_MAX);
    public static final VisibleLevel LV_INVISIBLE_ORDINARY = new VisibleLevel(INT_INVISIBLE_ORDINARY);
    public static final VisibleLevel LV_INVISIBLE_MIN = new VisibleLevel(INT_INVISIBLE_MIN);
    public static final VisibleLevel LV_VISIBLE_MIN = new VisibleLevel(INT_VISIBLE_MIN);
    public static final VisibleLevel LV_VISIBLE_ORDINARY = new VisibleLevel(INT_VISIBLE_ORDINARY);
    public static final VisibleLevel LV_VISIBLE_MAX = new VisibleLevel(INT_VISIBLE_MAX);
}
