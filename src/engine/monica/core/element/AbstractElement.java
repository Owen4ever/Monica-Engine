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

package engine.monica.core.element;

import engine.monica.util.StringID;

public abstract class AbstractElement {

    protected AbstractElement(StringID id, String name, int turnToEnergy) {
        if (id == null)
            throw new NullPointerException("The id is null.");
        this.id = id;
        setName(name);
        setValueTurnToEnergy(turnToEnergy);
    }

    public abstract boolean isCombined();

    public final StringID getID() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        if (name == null || name.isEmpty())
            throw new NullPointerException("The name of Element is null.");
        this.name = name;
    }

    public final int turnToEnergy() {
        return turnToEnergy;
    }

    public final void setValueTurnToEnergy(int val) {
        if (val < 1)
            throw new ElementInitializeException("The number"
                    + " which turns to energy smaller than 1.");
        this.turnToEnergy = val;
    }
    private final StringID id;
    private String name;
    private int turnToEnergy;
}
