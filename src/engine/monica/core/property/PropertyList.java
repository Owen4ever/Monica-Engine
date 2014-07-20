/*
 * The MIT License
 *
 * Copyright 2014 Owen.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package engine.monica.core.property;

import engine.monica.core.engine.CoreEngine;
import engine.monica.util.LinkedPointer;
import engine.monica.util.StringID;
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

    public void removeEffect(PropertyID id, StringID effectId) {
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
