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

import java.util.Set;

public interface ConfigInterface {

    /**
     * Set the value to the key and the value cannot be {@code null}.
     */
    <T> void set(String key, T value);

    /**
     * Return {@code null} if configures do not contain the key, otherwise,
     * return the value of the key.
     */
    <T> T get(String key);

    /**
     * Return {@code null} if configures do not contain the key, otherwise,
     * return the value of the key.
     */
    default <T> T get(String key, Class<T> clazz) {
        return (T) get(key);
    }

    boolean remove(String key);

    default boolean containKey(String key) {
        return get(key) != null;
    }

    Set<String> keySet();

    default void clearConfig() {
        keySet().stream().forEach(key -> remove(key));
    }
}
