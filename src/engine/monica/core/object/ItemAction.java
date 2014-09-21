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

import engine.monica.core.world.Area;
import engine.monica.core.world.Map;
import engine.monica.util.FinalPair;
import engine.monica.util.result.BoolMsgResult;
import engine.monica.util.Vector;

@FunctionalInterface
public interface ItemAction<V extends Vector<V>> {

    /**
     * Do an action to the item.
     *
     * @param owner The owner of the item.
     * @param user The user of the item.
     * @param target The target.
     * @param beUsingItem The item.
     * @param map The map.
     * @param area The area.
     * @return Boolean: Is succeed? String: Visible message. TangibleObject[]:
     * The objects the action created.
     */
    FinalPair<BoolMsgResult, TangibleObject<V>[]>
            action(Role<V> owner, Role<V> user, TangibleObject<V> target,
                    Item<V> beUsingItem, Map<V> map, Area<V> area);
}
