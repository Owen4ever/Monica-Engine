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

public abstract class AbstractProperty<T> {

    public AbstractProperty(PropertyID id, T defaultVal, T offsetVal) {
        if (id == null)
            throw new NullPointerException("THe id is null.");
        if (id.getType() != PropertyID.PropertyType.INTEGER)
            throw new ErrorTypeException("Error property type.");
        this.type = id;
        this.defaultVal = defaultVal;
        this.offsetVal = offsetVal;
    }

    public abstract boolean isFixed();

    public abstract EffectPointer addEffect(AbstractEffect<T> effect);

    public final int hasAdjustment() {
        return hasAdjustment;
    }

    public abstract void setAdjustment(PropertyAdjustment<T> adjustment);

    public abstract void setAdjustment(ParentPropertyInterface<T> parent);

    public abstract T getTotalValue();

    protected final PropertyID type;
    protected T defaultVal, offsetVal;
    /**
     * The {@code NumberProperty} does not have an {@code adjustment} or a
     * {@code NumberParentProperty} if {@code hasAdjustment} equals -1; The
     * number property only has an {@code adjustment} if {@code hasAdjustment}
     * equals 0; In addition, the {@code NumberProperty} only has a
     * {@code NumberParentProperty} if {@code hasAdjustment} equals 1.
     */
    protected int hasAdjustment = PRO_ADJ_NONE;
    protected PropertyAdjustment<T> adjustment = null;
    protected ParentPropertyInterface<T> parentProperty = null;

    protected static final int PRO_ADJ_NONE = -1;
    protected static final int PRO_ADJ_ADJ = 0;
    protected static final int PRO_ADJ_PAR = 1;
}
