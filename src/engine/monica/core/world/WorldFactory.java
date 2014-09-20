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

package engine.monica.core.world;

import engine.monica.core.engine.CoreEngine;
import engine.monica.util.LinkedPointer;
import java.util.HashMap;

public final class WorldFactory {

    public WorldFactory() {
    }

    public World createBy(ConfigInterface c) {
        World w = new World(c);
        pointer = pointer.linkNew();
        worlds.put(pointer, w);
        return w;
    }

    public World getWorld(int index) {
        return worlds.get(new LinkedPointer(index));
    }
    private final HashMap<LinkedPointer, World> worlds
            = new HashMap<>(CoreEngine.getDefaultQuantity(), 0.1f);
    private LinkedPointer pointer = LinkedPointer.first();
}
