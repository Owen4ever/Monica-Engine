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

package game.engine.monica.core.property.integer;

import game.engine.monica.core.engine.CoreEngine;
import game.engine.monica.core.engine.EngineThread;
import game.engine.monica.core.property.EffectPointer;
import game.engine.monica.core.property.ErrorTypeException;
import game.engine.monica.core.property.PropertyID;
import game.engine.monica.util.FinalPair;
import game.engine.monica.util.Wrapper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IntegerProperty implements Comparable<IntegerProperty> {

    public IntegerProperty(PropertyID id, boolean isPercentVal) {
        this(id, isPercentVal, 0, 0);
    }

    public IntegerProperty(PropertyID id, boolean isPercentVal,
            int defaultVal, int offsetVal) {
        if (id == null)
            throw new NullPointerException("THe id is null.");
        if (id.getType() != PropertyID.PropertyType.INTEGER)
            throw new ErrorTypeException("Error property type.");
        this.type = id;
        isPerVal = isPercentVal;
        this.defaultVal = defaultVal;
        this.offsetVal = offsetVal;
    }

    public final int getDefaultValue() {
        return defaultVal;
    }

    public final void setDefaultValue(int val) {
        this.defaultVal = val;
        isCalc = false;
    }

    public final int getOffsetValue() {
        return offsetVal;
    }

    public final void setOffsetValue(int val) {
        this.offsetVal = val;
        isCalc = false;
    }

    public final EffectPointer addAdditionValue(AbstractIntegerEffect e) {
        if (e == null)
            throw new NullPointerException("The addition effect is null.");
        calcLocker.lock();
        try {
            if (currentAdditionEffectPointer == null)
                currentAdditionEffectPointer = EffectPointer.newFirstPointer();
            else
                currentAdditionEffectPointer = currentAdditionEffectPointer.linkNewOne();
            addVal.put(currentAdditionEffectPointer, e);
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
        return currentAdditionEffectPointer;
    }

    public final void removeAdditionValue(EffectPointer pointer) {
        calcLocker.lock();
        try {
            pointer.delete();
            addVal.remove(pointer);
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
    }

    public final EffectPointer addBuffEffect(IntegerBuffEffect e) {
        if (e == null)
            throw new NullPointerException("The buff effect is null.");
        calcLocker.lock();
        try {
            if (currentBuffEffectPointer == null)
                currentBuffEffectPointer = EffectPointer.newFirstPointer();
            else
                currentBuffEffectPointer = currentBuffEffectPointer.linkNewOne();
            buffVal.put(currentBuffEffectPointer, new FinalPair<>(e, new Wrapper<Integer>(0)));
            BuffRunnable r = new BuffRunnable(currentBuffEffectPointer);
            buffThreadSet.add(r);
            new EngineThread(r).start();
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
        return currentBuffEffectPointer;
    }

    public final void removeBuffEffect(EffectPointer pointer) {
        calcLocker.lock();
        try {
            pointer.delete();
            buffVal.remove(pointer);
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
    }

    public final EffectPointer addLongTimeEffect(IntegerLongTimeEffect e) {
        if (e == null)
            throw new NullPointerException("The buff effect is null.");
        calcLocker.lock();
        try {
            if (currentLongTimeEffectPointer == null)
                currentLongTimeEffectPointer = EffectPointer.newFirstPointer();
            else
                currentLongTimeEffectPointer = currentLongTimeEffectPointer.linkNewOne();
            longTimeVal.put(currentLongTimeEffectPointer, e);
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
        return currentLongTimeEffectPointer;
    }

    public final void removeLongTimeEffect(EffectPointer pointer) {
        calcLocker.lock();
        try {
            pointer.delete();
            longTimeVal.remove(pointer);
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
    }

    public final void setFixedEffect(IntegerFixedEffect e) {
        if (e == null)
            throw new NullPointerException("The IntegerFixedEffect is null.");
        calcLocker.lock();
        try {
            fixedVal = e.affect(0);
            isFixed = true;
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
    }

    public final void cancelFixedEffect() {
        calcLocker.lock();
        try {
            isFixed = false;
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
    }

    public final boolean isFixed() {
        return isFixed;
    }

    public final EffectPointer addEffect(AbstractIntegerEffect e) {
        if (e == null)
            throw new NullPointerException("The effect is null.");
        switch (e.effectType) {
            case TYPE_INTEGER_NUM:
                return addAdditionValue(e);
            case TYPE_INTEGER_BUFF:
                return addBuffEffect((IntegerBuffEffect) e);
            case TYPE_INTEGER_FIXED:
                setFixedEffect((IntegerFixedEffect) e);
                return null;
            case TYPE_INTEGER_LONGTIME:
                return addLongTimeEffect((IntegerLongTimeEffect) e);
            default:
                throw new ErrorTypeException("Error effect type.");
        }
    }

    public final IntegerPropertyAdjustment getAdjustment() {
        return adjustment;
    }

    public final void setAdjustment(IntegerPropertyAdjustment a) {
        if (a == null)
            throw new NullPointerException("The adjustment is null.");
        calcLocker.lock();
        try {
            this.adjustment = a;
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
    }

    public final void removeAdjustment() {
        calcLocker.lock();
        try {
            this.hasAdjustment = true;
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
    }

    public final int getTotalValue() {
        if (isCalc)
            return totalVal;
        else {
            calcTotalValue();
            return totalVal;
        }
    }

    private void calcTotalValue() {
        calcLocker.lock();
        try {
            if (isFixed)
                totalVal = fixedVal;
            Wrapper<Integer> tval = new Wrapper<>(defaultVal + offsetVal);
            addVal.forEach((EffectPointer p, AbstractIntegerEffect e) -> {
                tval.pack = e.affect(tval.pack);
            });
            buffVal.forEach((EffectPointer p,
                    FinalPair<IntegerBuffEffect, Wrapper<Integer>> e) -> {
                        tval.pack = e.first.affect(tval.pack);
                    });
            totalVal = adjustment.adjust(tval.pack);
            isCalc = true;
        } finally {
            calcLocker.unlock();
        }
    }
    private final transient ReentrantReadWriteLock.WriteLock calcLocker
            = new ReentrantReadWriteLock().writeLock();

    @Override
    public int compareTo(IntegerProperty t) {
        return type.compareTo(t.type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != IntegerProperty.class)
            return false;
        IntegerProperty p = (IntegerProperty) obj;
        return type.equals(p.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + "[ ID = " + type.getName()
                + ", Type = " + type.getType()
                + ", Default value = " + defaultVal
                + ", Offset value = " + offsetVal
                + ", Total value = " + getTotalValue()
                + ",  Has adjustment" + " ]";
    }
    private final PropertyID type;
    protected int defaultVal, offsetVal;
    protected final ConcurrentHashMap<EffectPointer, AbstractIntegerEffect> addVal
            = new ConcurrentHashMap<>(CoreEngine.getDefaultQuantily(), 0.5f);
    protected EffectPointer currentAdditionEffectPointer = null;
    protected final ConcurrentHashMap<EffectPointer, FinalPair<IntegerBuffEffect, Wrapper<Integer>>> buffVal
            = new ConcurrentHashMap<>(CoreEngine.getDefaultQuantily(), 0.5f);
    private final PriorityBlockingQueue<BuffRunnable> buffThreadSet
            = new PriorityBlockingQueue<>(CoreEngine.getDefaultQuantily());
    protected EffectPointer currentBuffEffectPointer = null;
    protected final ConcurrentHashMap<EffectPointer, IntegerLongTimeEffect> longTimeVal
            = new ConcurrentHashMap<>(CoreEngine.getDefaultQuantily(), 0.5f);
    protected EffectPointer currentLongTimeEffectPointer = null;
    protected boolean hasAdjustment = false;
    protected IntegerPropertyAdjustment adjustment = null;
    private boolean isFixed = false;
    protected int fixedVal;
    protected final boolean isPerVal;
    protected transient volatile boolean isCalc = false;
    protected transient int totalVal;

    private final class BuffRunnable
            implements Comparable<BuffRunnable>, Runnable {

        public BuffRunnable(EffectPointer pointer) {
            this.pointer = pointer;
        }

        @Override
        public void run() {
            FinalPair<IntegerBuffEffect, Wrapper<Integer>> v = buffVal.get(pointer);
            IntegerBuffEffect effect = v.first;
            int ce1mscc = CoreEngine.get1msSuspendTimeChangeCount();
            int time = CoreEngine.get1msSuspendTime() * effect.getMaxDuration();
            if (effect.isInterval()) {
                int it = CoreEngine.calcSuspendTime(effect.getIntervalDuration());
                while (CoreEngine.isStart()) {
                    if (CoreEngine.is1msSuspendTimeChanged(ce1mscc)) {
                        time = CoreEngine.calcSuspendTime(effect.getMaxDuration());
                        it = CoreEngine.calcSuspendTime(effect.getIntervalDuration());
                    }
                    while (CoreEngine.isContinuing()) {
                        long startingTime = System.currentTimeMillis();
                        try {
                            EngineThread.sleepAndNeedWakeUp(it - already);
                        } catch (InterruptedException ex) {
                            long endTime = System.currentTimeMillis();
                            already = (int) (endTime - startingTime)
                                    / CoreEngine.get1msSuspendTime();
                        }
                        currentCount++;
                        if (currentCount == time) {
                            buffVal.remove(pointer);
                            buffThreadSet.remove(this);
                            return;
                        }
                        if (currentCount % it == 0)
                            v.last.pack = effect.getValue() * currentCount;
                    }
                }
            } else {
                while (CoreEngine.isStart()) {
                    if (CoreEngine.is1msSuspendTimeChanged(ce1mscc))
                        time = CoreEngine.calcSuspendTime(effect.getMaxDuration());
                    while (CoreEngine.isContinuing()) {
                        long startingTime = System.currentTimeMillis();
                        try {
                            EngineThread.sleepAndNeedWakeUp(time - already);
                        } catch (InterruptedException ex) {
                            long endTime = System.currentTimeMillis();
                            already += (int) (endTime - startingTime);
                            continue;
                        }
                        buffVal.remove(pointer);
                        buffThreadSet.remove(this);
                    }
                }
            }
        }

        @Override
        public int compareTo(BuffRunnable r) {
            if (r == null)
                throw new NullPointerException("Compare with a null PropertyRunnable.");
            return Integer.compare(pointer.pointer(), r.pointer.pointer());
        }
        protected EffectPointer pointer;
        private int currentCount = 0;
        private int already = 0;
    }
}
