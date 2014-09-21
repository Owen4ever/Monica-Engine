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

import engine.monica.core.element.ElementSystem;

public interface EnableObject extends EnableObjectInterface {

    @Override
    default boolean enable(Ability ability) { return true; }

    @Override
    default boolean enable(Career career) { return true; }

    @Override
    default boolean enable(ElementSystem system) { return true; }

    @Override
    default boolean enable(Item item) { return true; }

    @Override
    default boolean enable(Race race) { return true; }

    @Override
    default boolean enable(Role role) { return true; }

    @Override
    default boolean enable(Bloodline bloodline) { return true; }

    @Override
    default boolean enable(RoleGender gender) { return true; }
}
