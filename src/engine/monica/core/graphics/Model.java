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

import engine.monica.util.Vector;

public interface Model<V extends Vector<V>> {

    /**
     * Return all the objects this model contains.
     */
    GraphicObject<V>[] getObjects();

    /**
     * Return the essential object.
     */
    GraphicObject<V> getMainObject();

    /**
     * Return the skeleton of this model.
     */
    GraphicObject<V>[] getSkeletonObjects();

    /**
     * Return the object which named with the parameter {@code name}.
     */
    GraphicObject<V> getObject(String name);

    /**
     * Return the current frame index.
     */
    int getFrame();

    /**
     * Set the frame index.
     */
    void setFrame(int frame);

    /**
     * Return the max count of the frames.
     */
    int getMaxFrame();

    /**
     * Set the speed of every single frame. The default value must be 1.
     */
    void setFrameSpeed(int speed);

    /**
     * Return the speed of every single frame.
     */
    int getFrameSpeed();

    /**
     * Draw the model on the {@code CanvasInterface} with the specified
     * {@code GraphicsInterface}.
     */
    void draw(CanvasInterface c, GraphicInterface g);
}
