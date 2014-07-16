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

package game.engine.monica.core.element;

public final class ElementList {

    public ElementList(AbstractElement... e) {
        if (e == null)
            throw new NullPointerException("Element is null.");
        elements = new AbstractElement[e.length];
        if (e.length != 0)
            for (int i = 0; i != e.length; ++i)
                if (e[i] == null)
                    throw new NullPointerException("Element is null.");
                else
                    elements[i] = e[i];
    }

    public AbstractElement[] getElements() {
        return elements;
    }

    public int size() {
        return elements.length;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ElementList))
            return false;
        return equals((ElementList) obj);
    }

    public boolean equals(ElementList list) {
        if (list == null)
            return false;
        if (size() != list.size())
            return false;
        for (int i = 0; i != size(); ++i)
            if (elements[i] != list.elements[i])
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        int eh = elements[0].hashCode();
        for (int i = 1; i != size(); ++i) {
            eh *= i;
            eh += elements[i].hashCode();
        }
        return 74 + eh;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(6 * size()).append("[ ");
        for (AbstractElement e : elements)
            sb.append(e.getName()).append(", ");
        sb.replace(sb.length() - 2, sb.length(), " ]");
        return sb.toString();
    }
    private final AbstractElement[] elements;
}
