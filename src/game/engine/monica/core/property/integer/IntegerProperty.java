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
import game.engine.monica.core.engine.EngineRunnable;
import game.engine.monica.core.engine.EngineThread;
import game.engine.monica.core.property.EffectPointer;
import game.engine.monica.core.property.ErrorTypeException;
import game.engine.monica.core.property.PropertyID;
import game.engine.monica.util.FinalPair;
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
        additionEffectPointerLocker.lock();
        try {
            if (currentAdditionEffectPointer == null)
                currentAdditionEffectPointer = EffectPointer.newFirstPointer();
            else
                currentAdditionEffectPointer = currentAdditionEffectPointer.linkNewOne();
            addVal.put(currentAdditionEffectPointer, e);
            isCalc = false;
        } finally {
            additionEffectPointerLocker.unlock();
        }
        return currentAdditionEffectPointer;
    }
    private final transient ReentrantReadWriteLock.WriteLock additionEffectPointerLocker
            = new ReentrantReadWriteLock().writeLock();

    public final void removeAdditionValue(EffectPointer pointer) {
        additionEffectPointerLocker.lock();
        try {
            pointer.delete();
            addVal.remove(pointer);
            isCalc = false;
        } finally {
            additionEffectPointerLocker.unlock();
        }
    }

    public final EffectPointer addBuffEffect(IntegerBuffEffect e) {
        if (e == null)
            throw new NullPointerException("The buff effect is null.");
        buffEffectPointerLocker.lock();
        try {
            if (currentBuffEffectPointer == null)
                currentBuffEffectPointer = EffectPointer.newFirstPointer();
            else
                currentBuffEffectPointer = currentBuffEffectPointer.linkNewOne();
            buffVal.put(currentBuffEffectPointer, new FinalPair<>(e, new MapValue(0)));
            isCalc = false;
        } finally {
            buffEffectPointerLocker.unlock();
        }
        return currentBuffEffectPointer;
    }
    private final transient ReentrantReadWriteLock.WriteLock buffEffectPointerLocker
            = new ReentrantReadWriteLock().writeLock();

    public final void removeBuffEffect(EffectPointer pointer) {
        buffEffectPointerLocker.lock();
        try {
            pointer.delete();
            buffVal.remove(pointer);
            isCalc = false;
        } finally {
            buffEffectPointerLocker.unlock();
        }
    }

    public final EffectPointer addLongTimeEffect(IntegerLongTimeEffect e) {
        if (e == null)
            throw new NullPointerException("The buff effect is null.");
        longTimeEffectPointerLocker.lock();
        try {
            if (currentLongTimeEffectPointer == null)
                currentLongTimeEffectPointer = EffectPointer.newFirstPointer();
            else
                currentLongTimeEffectPointer = currentLongTimeEffectPointer.linkNewOne();
            longTimeVal.put(currentLongTimeEffectPointer, e);
            isCalc = false;
        } finally {
            longTimeEffectPointerLocker.unlock();
        }
        return currentLongTimeEffectPointer;
    }
    private final transient ReentrantReadWriteLock.WriteLock longTimeEffectPointerLocker
            = new ReentrantReadWriteLock().writeLock();

    public final void removeLongTimeEffect(EffectPointer pointer) {
        longTimeEffectPointerLocker.lock();
        try {
            pointer.delete();
            longTimeVal.remove(pointer);
            isCalc = false;
        } finally {
            longTimeEffectPointerLocker.unlock();
        }
    }

    public final void setFixedEffect(IntegerFixedEffect e) {
        if (e == null)
            throw new NullPointerException("The IntegerFixedEffect is null.");
        fixedEffectPointerLocker.lock();
        try {
            fixedVal = e.affect(0);
            isFixed = true;
            isCalc = false;
        } finally {
            fixedEffectPointerLocker.unlock();
        }
    }

    public final void cancelFixedEffect() {
        fixedEffectPointerLocker.lock();
        try {
            isFixed = false;
            isCalc = false;
        } finally {
            fixedEffectPointerLocker.unlock();
        }
    }

    public final boolean isFixed() {
        return isFixed;
    }
    private final transient ReentrantReadWriteLock.WriteLock fixedEffectPointerLocker
            = new ReentrantReadWriteLock().writeLock();

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
        this.adjustment = a;
        isCalc = false;
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
            additionEffectPointerLocker.lock();
            try {
                addVal.forEach((EffectPointer p, AbstractIntegerEffect e) -> {
                    totalVal = e.affect(totalVal);
                });
            } finally {
                additionEffectPointerLocker.unlock();
            }
            buffEffectPointerLocker.lock();
            try {
                buffVal.forEach((EffectPointer p,
                        FinalPair<IntegerBuffEffect, MapValue> e) -> {
                        });
            } finally {
                buffEffectPointerLocker.unlock();
            }
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
    private final PropertyID type;
    protected int defaultVal, offsetVal;
    protected final ConcurrentHashMap<EffectPointer, AbstractIntegerEffect> addVal
            = new ConcurrentHashMap<>(CoreEngine.getDefaultQuantily(), 0.5f);
    protected EffectPointer currentAdditionEffectPointer = null;
    protected final ConcurrentHashMap<EffectPointer, FinalPair<IntegerBuffEffect, MapValue>> buffVal
            = new ConcurrentHashMap<>(CoreEngine.getDefaultQuantily(), 0.5f);
    private final PriorityBlockingQueue<BuffRunnable> buffThreadSet
            = new PriorityBlockingQueue<>(CoreEngine.getDefaultQuantily());
    protected EffectPointer currentBuffEffectPointer = null;
    protected final ConcurrentHashMap<EffectPointer, IntegerLongTimeEffect> longTimeVal
            = new ConcurrentHashMap<>(CoreEngine.getDefaultQuantily(), 0.5f);
    protected EffectPointer currentLongTimeEffectPointer = null;
    protected IntegerPropertyAdjustment adjustment = null;
    private boolean isFixed = false;
    protected int fixedVal;
    protected final boolean isPerVal;
    protected transient volatile boolean isCalc = false;
    protected transient int totalVal;

    protected static final class MapValue {

        public MapValue(int val) {
            this.val = val;
        }
        public int val;
    }

    private final class BuffRunnable extends EngineRunnable
            implements Comparable<BuffRunnable> {

        public BuffRunnable(EffectPointer pointer) {
            this.pointer = pointer;
        }

        @Override
        protected void loop() {
            FinalPair<IntegerBuffEffect, MapValue> v = buffVal.get(pointer);
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
                            v.last.val = effect.getValue() * currentCount;
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
