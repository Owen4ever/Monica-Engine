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
