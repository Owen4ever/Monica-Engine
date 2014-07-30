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
import engine.monica.core.property.PropertyList;
import engine.monica.util.FinalPair;
import engine.monica.util.VectorInterface;

public interface Item<V extends VectorInterface> {

    GameObject<V>[] getGameObjects();

    ItemKind getItemKind();

    Name getName();

    PropertyList getProperties();

    ItemActionList getItemActions();

    Material[] getMaterials();

    default FinalPair<FinalPair<Boolean, String>, GameObject[]> action(String name,
            Role owner, Role user, GameObject usingObject,
            Map map, Area area) {
        return getItemActions().action(name, owner, user, usingObject, this, map, area);
    }
}
