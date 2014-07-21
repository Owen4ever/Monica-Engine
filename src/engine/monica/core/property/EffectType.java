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

package engine.monica.core.property;

public enum EffectType {

    // Number Effect
    TYPE_NUM_FIXED,
    TYPE_NUM_SIMPLE,
    TYPE_NUM_BUFF,
    TYPE_NUM_BUFF_INTERVAL,
    TYPE_NUM_DEBUFF,
    TYPE_NUM_DEBUFF_INTERVAL,
    TYPE_NUM_LONGTIME,
    TYPE_NUM_LONGTIME_INTERVAL,
    // Boolean Effect
    TYPE_BOOL_FIXED,
    TYPE_BOOL_SIMPLE,
    TYPE_BOOL_BUFF,
    TYPE_BOOL_BUFF_INTERVAL,
    TYPE_BOOL_DEBUFF,
    TYPE_BOOL_DEBUFF_INTERVAL,
    TYPE_BOOL_LONGTIME,
    TYPE_BOOL_LONGTIME_INTERVAL
}
