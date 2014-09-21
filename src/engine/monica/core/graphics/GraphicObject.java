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

package engine.monica.core.graphics;

import engine.monica.core.world.Map;
import engine.monica.core.world.VisibleLevel;
import engine.monica.util.Vector;

public interface GraphicObject<V extends Vector<V>> {

    /**
     * Get the map which the {@code  GraphicObject} is located.
     */
    Map<V> getLocatedMap();

    VisibleLevel getVisibleLevel();

    default boolean isVisible() {
        return getVisibleLevel().isVisible();
    }

    GraphicObject<V> getParent();

    void setParent(GraphicObject<V> obj);

    default boolean hasParent() {
        return getParent() != null;
    }

    GraphicObject<V>[] getChilds();

    void addChild(GraphicObject<V> obj);

    boolean removeChild(GraphicObject<V> obj);

    default V getAbsoluteLocation() {
        GraphicObject<V> parent = getParent();
        return parent == null ? getLocation()
                : parent.getAbsoluteLocation().add(getLocation());
    }

    V getLocation();

    void setLocation(V v);

    V getSize();

    V setSize(V v);

    double getScale();

    void setScale(double d);

    <T> T getAttribute(String key);

    <T> void setAttribute(String key, T val);

    boolean canColide(GraphicObject<V> o);

    void colide(GraphicObject<V> o);
}
