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

package engine.monica.core.property;

import engine.monica.core.engine.CoreEngine;
import engine.monica.util.LinkedPointer;

import java.util.concurrent.ConcurrentHashMap;

public final class PropertyList {

    public PropertyList(AbstractProperty... pros) {
        if (pros.length == 0)
            throw new NullPointerException("The property array is null.");
        for (AbstractProperty p : pros)
            if (p == null)
                throw new NullPointerException("The property is null.");
            else
                properties.put(p.getType(), p);
    }

    public void addAll(PropertyList list) {
        if (list == null)
            throw new NullPointerException("The PropertyList is null.");
        properties.putAll(list.properties);
    }

    public void addProperty(AbstractProperty p) {
        if (p == null)
            throw new NullPointerException("The property is null.");
        properties.put(p.getType(), p);
    }

    public void removeProperty(PropertyID id) {
        if (id == null)
            throw new NullPointerException("The PropertyID is null.");
        properties.remove(id);
    }

    public AbstractProperty getProperty(PropertyID id) {
        return properties.get(id);
    }

    @SuppressWarnings("unchecked")
    public LinkedPointer addEffect(PropertyID id, AbstractEffect e) {
        return properties.get(id).addEffect(e);
    }

    public void removeEffect(PropertyID id, String effectId) {
        properties.get(id).removeEffect(effectId);
    }

    public void removeEffect(PropertyID id, LinkedPointer pointer) {
        properties.get(id).removeEffect(pointer);
    }

    public int propertyCount() {
        return properties.size();
    }
    private final ConcurrentHashMap<PropertyID, AbstractProperty> properties
            = new ConcurrentHashMap<>(CoreEngine.getDefaultQuantily(), 0.2f);
}
