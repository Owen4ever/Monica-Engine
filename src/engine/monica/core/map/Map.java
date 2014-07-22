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

import engine.monica.core.graphics.GameObject;
import engine.monica.util.VectorInterface;
import java.util.List;

public interface Map extends ConfigInterface {

    World getWorld();

    Area getArea(VectorInterface v);

    void setArea(VectorInterface v, Area a);

    void createIrregularArea(VectorInterface v, Area a);

    Area[] getNearbyAreas(VectorInterface v);

    Area[] getNearbyAreas(Area a);

    ObserverInterface getObserver();

    void setObserver(ObserverInterface o);

    List<GameObject> getObjects();

    void addObject(GameObject obj);

    boolean removeObject(GameObject obj);
}
