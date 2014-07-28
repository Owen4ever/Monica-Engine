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

import engine.monica.core.graphics.GameObject;
import engine.monica.core.map.Area;
import engine.monica.core.map.Map;
import engine.monica.util.FinalPair;
import java.util.HashMap;

public final class ItemActionList {

    public ItemActionList() {
    }

    public ItemActionList add(String name, ItemActionInterface action) {
        if (!actions.containsKey(name))
            actions.put(name, action);
        return this;
    }

    public void remove(String name) {
        actions.remove(name);
    }

    public FinalPair<Boolean, String> action(String name,
            Role owner, Role user, GameObject usingObject,
            Item beUsingItem, Map map, Area area) {
        if (name == null || name.isEmpty())
            return DEFAULT_PAIR;
        return actions.getOrDefault(name, DEFAULT)
                .action(owner, user, usingObject, beUsingItem, map, area);
    }
    private final HashMap<String, ItemActionInterface> actions = new HashMap<>(4, 0.1f);

    private static final FinalPair<Boolean, String> DEFAULT_PAIR
            = new FinalPair<>(false, "Do not has this action.");
    private static final ItemActionInterface DEFAULT = (o, u, uo, buo, m, a) -> DEFAULT_PAIR;
}
