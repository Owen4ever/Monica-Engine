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

package engine.monica.core.property.simple;

import engine.monica.core.property.AbstractEffect;
import engine.monica.core.property.AbstractProperty;
import engine.monica.core.property.ParentPropertyInterface;
import engine.monica.core.property.PropertyAdjustment;
import engine.monica.core.property.PropertyID;
import engine.monica.util.LinkedPointer;
import engine.monica.util.StringID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class SimpleProperty<T> extends AbstractProperty<T> {

    public SimpleProperty(PropertyID id, T defaultVal, T offsetVal) {
        super(id, defaultVal, offsetVal);
    }

    @Override
    protected void getCalcWriteLock() {
        while (true)
            if (!calcLocker.isWriteLocked() && calcLocker.writeLock().tryLock())
                if (calcLocker.getWriteHoldCount() != 1)
                    calcLocker.writeLock().unlock();
                else if (hasAdjustment() == PRO_ADJ_PAR
                        && ((SimpleProperty) ((AbstractProperty) parentProperty)).calcLocker.isWriteLocked())
                    calcLocker.writeLock().unlock();
                else
                    return;
    }

    @Override
    protected void unlockCalcWriteLock() {
        calcLocker.writeLock().unlock();
    }
    private final transient ReentrantReadWriteLock calcLocker = new ReentrantReadWriteLock();

    @Override
    public boolean isFixed() {
        return false;
    }

    @Override
    public LinkedPointer addEffect(AbstractEffect effect) {
        return null;
    }

    @Override
    public void removeEffect(StringID id) {
    }

    @Override
    public void removeEffect(LinkedPointer pointer) {
    }

    @Override
    public void setAdjustment(PropertyAdjustment adjustment) {
        if (adjustment == null)
            throw new NullPointerException("The adjustment is null.");
        getCalcWriteLock();
        try {
            this.adjustment = adjustment;
            hasAdjustment = PRO_ADJ_ADJ;
        } finally {
            unlockCalcWriteLock();
        }
    }

    @Override
    public void setAdjustment(ParentPropertyInterface parent) {
        if (parent == null)
            throw new NullPointerException("The parent property is null.");
        getCalcWriteLock();
        try {
            this.parentProperty = parent;
            hasAdjustment = PRO_ADJ_PAR;
        } finally {
            unlockCalcWriteLock();
        }
    }
}
