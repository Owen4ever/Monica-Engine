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

package game.engine.monica.core.property.number;

import game.engine.monica.core.property.PropertyID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class NumberParentProperty extends NumberProperty
        implements NumberPropertyAdjustment {

    public NumberParentProperty(PropertyID id, boolean isPercentVal) {
        super(id, isPercentVal);
    }

    public NumberParentProperty(PropertyID id, boolean isPercentVal,
            double defaultVal, double offsetVal) {
        super(id, isPercentVal, defaultVal, offsetVal);
    }

    public NumberParentProperty(PropertyID id, boolean isPercentVal,
            double defaultVal, double offsetVal, NumberProperty childProperty) {
        super(id, isPercentVal, defaultVal, offsetVal);
        setChild(childProperty);
    }

    public final boolean hasChild() {
        return hasChild;
    }

    public final NumberProperty getChild() {
        return childProperty;
    }

    public final void setChild(NumberProperty childProperty) {
        if (childProperty == null)
            throw new NullPointerException("The child property is null.");
        childLock.lock();
        try {
            this.childProperty = childProperty;
            hasChild = true;
        } finally {
            childLock.unlock();
        }
    }

    public final void removeChild() {
        if (hasChild) {
            childLock.lock();
            try {
                if (hasChild) {
                    hasChild = false;
                    childProperty = null;
                }
            } finally {
                childLock.unlock();
            }
        }
    }

    @Override
    protected final void calcTotalValue() {
        super.calcTotalValue();
        if (hasChild)
            childProperty.beNotify();
    }
    protected volatile boolean hasChild = false;
    protected NumberProperty childProperty = null;
    private transient final ReentrantReadWriteLock.WriteLock childLock
            = new ReentrantReadWriteLock().writeLock();
}
