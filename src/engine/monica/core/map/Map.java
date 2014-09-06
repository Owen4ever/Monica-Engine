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

import engine.monica.core.graphics.GraphicObject;
import engine.monica.util.Vector;
import java.util.List;

public interface Map<V extends Vector<V>> extends ConfigInterface {

    World getWorld();

    Area getArea(V v);

    void setArea(V v, Area a);

    void createIrregularArea(V v, Area a);

    Area[] getNearbyAreas(V v);

    Area[] getNearbyAreas(Area a);

    List<GraphicObject> getNodes();

    void addNode(GraphicObject obj);

    boolean removeNode(GraphicObject obj);

    /**
     * Test two GraphicObjects can colide each other.
     *
     * @param obj1 A GraphicObject which has collided obj2.
     * @param obj2 A GraphicObject which has been collided by obj1.
     */
    default boolean canColide(GraphicObject obj1, GraphicObject obj2) {
        return obj1.canColide(obj2);
    }

    default void colide(GraphicObject obj1, GraphicObject obj2) {
        obj1.colide(obj2);
        obj2.colide(obj1);
    }
}
