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

package engine.monica.core.graphics;

import engine.monica.core.map.VisibleLevel;
import engine.monica.util.VectorInterface;

public interface GameObject {

    VisibleLevel getVisibleLevel();

    default boolean isVisible() {
        return getVisibleLevel().isVisible();
    }

    GameObject getParent();

    void setParent(GameObject obj);

    default boolean hasParent() {
        return getParent() != null;
    }

    default <L extends VectorInterface> L getAbsoluteLocation() {
        GameObject parent = getParent();
        return parent == null ? getLocation()
                : parent.getAbsoluteLocation().add(getLocation());
    }

    <L extends VectorInterface> L getLocation();

    <L extends VectorInterface> void setLocation(L v);

    <S extends VectorInterface> S getSize();

    <S extends VectorInterface> void setSie(S v);

    double getScale();

    void setScale(double d);
}
