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

package engine.monica.core.input;

public interface Shortcut {

    int getControlKey();

    default boolean hasControlKey() {
        return getControlKey() == ControlKey.NONE;
    }

    String getInputType();

    interface ControlKey {

        int NONE = 0b0000;
        int CTRL_L = 0b0001;
        int CTRL_R = 0b0010;
        int SHIFT_L = 0b0011;
        int SHIFT_R = 0b0100;
        int ALT_L = 0b0101;
        int ALT_R = 0b0110;
        int WIN_L = 0b0111;
        int WIN_R = 0b1000;
        int META = 0b1001;
    }
}
