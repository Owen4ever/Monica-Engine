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

package engine.monica.core.object;

import engine.monica.core.map.DateTime;

public class RoleBirth {

    public RoleBirth(DateTime birth) {
        this.birth = birth;
    }

    public RoleBirth(int year, int month, int day, int hour, int minute, int second, int millisecond) {
        this(new DateTime(year, month, day, hour, minute, second, millisecond));
    }
    public final DateTime birth;
}
