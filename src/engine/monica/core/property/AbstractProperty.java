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

import engine.monica.util.LinkedPointer;
import engine.monica.util.StringID;

public abstract class AbstractProperty<T>
        implements Comparable<AbstractProperty> {

    public AbstractProperty(PropertyID id, T defaultVal, T offsetVal) {
        if (id == null)
            throw new NullPointerException("THe id is null.");
        if (id.getType() != PropertyID.PropertyType.INTEGER)
            throw new ErrorTypeException("Error property type.");
        this.type = id;
        this.defaultVal = defaultVal;
        this.offsetVal = offsetVal;
    }

    protected abstract void getCalcWriteLock();

    protected abstract void unlockCalcWriteLock();

    public final PropertyID getType() {
        return type;
    }

    public final T getDefaultValue() {
        return defaultVal;
    }

    public final void setDefaultValue(T val) {
        getCalcWriteLock();
        try {
            this.defaultVal = val;
        } finally {
            unlockCalcWriteLock();
        }
    }

    public final T getOffsetValue() {
        return offsetVal;
    }

    public final void setOffsetValue(T val) {
        getCalcWriteLock();
        try {
            this.offsetVal = val;
        } finally {
            unlockCalcWriteLock();
        }
    }

    public abstract boolean isFixed();

    public abstract LinkedPointer addEffect(AbstractEffect<T> effect);

    public abstract void removeEffect(StringID id);

    public abstract void removeEffect(LinkedPointer pointer);

    public final int hasAdjustment() {
        return hasAdjustment;
    }

    public final PropertyAdjustment<T> getAdjustment() {
        switch (hasAdjustment) {
            case PRO_ADJ_NONE:
                return null;
            case PRO_ADJ_ADJ:
                return adjustment;
            case PRO_ADJ_PAR:
                return parentProperty;
            default:
                return null;
        }
    }

    public abstract void setAdjustment(PropertyAdjustment<T> adjustment);

    public abstract void setAdjustment(ParentPropertyInterface<T> parent);

    public abstract T getTotalValue();

    @Override
    public int compareTo(AbstractProperty p) {
        if (p == null)
            throw new NullPointerException("The property which compares to"
                    + " is null.");
        return type.compareTo(p.type);
    }

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
