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
import engine.monica.util.Vector;

public interface EnableObjectInterface<V extends Vector<V>> {

    boolean enable(Ability<V> ability);

    boolean enable(Career<V> career);

    boolean enable(ElementSystem system);

    boolean enable(Bloodline<V> bloodline);

    boolean enable(Item<V> item);

    boolean enable(Race<V> race);

    boolean enable(Role<V> role);

    boolean enable(RoleGender gender);
}
