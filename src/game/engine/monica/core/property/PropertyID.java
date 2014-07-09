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

package game.engine.monica.core.property;

import game.engine.monica.util.StringID;

public final class PropertyID implements Comparable<PropertyID> {

    public PropertyID(StringID id, PropertyType type, String name, String intro) {
        if (type == null)
            throw new NullPointerException("The PropertyID type is null.");
        setID(id);
        setName(name);
        setIntroduction(intro);
        this.type = type;
    }

    public PropertyType getType() {
        return type;
    }

    public StringID getID() {
        return id;
    }

    public void setID(StringID name) {
        if (name == null)
            throw new NullPointerException("The property ID name cannot be null.");
        this.id = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty())
            throw new NullPointerException("The name of PropertyID is null.");
    }

    public String getIntroduction() {
        return intro;
    }

    public void setIntroduction(String intro) {
        if (intro == null || intro.isEmpty())
            throw new NullPointerException("The property ID introduction cannot be null.");
        this.intro = intro;
    }

    @Override
    public int compareTo(PropertyID type) {
        return Integer.compare(id.hashCode(), type.id.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != getClass())
            return false;
        PropertyID pt = (PropertyID) obj;
        return pt.id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName()
                + " [ ID = " + id
                + ", Name = " + name + ", Type = " + type.name()
                + ", Introduction = " + intro + " ]";
    }
    private StringID id;
    private String name, intro;
    private final PropertyType type;

    public static enum PropertyType {

        INTEGER, BOOLEAN
    }
}
