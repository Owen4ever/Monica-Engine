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
import engine.monica.util.StringID;
import engine.monica.util.VectorInterface;

public interface SkillInterface<V extends VectorInterface> {

    String getName();

    void setName(String name);

    StringID getID();

    GameObject<V>[] getReleaseObjects();

    ReleaseType getReleaseType();

    <T> T get(String key);

    <T> void set(String key, T val);

    public enum ReleaseType {

        ACTIVE, PASSIVE
    }
}
