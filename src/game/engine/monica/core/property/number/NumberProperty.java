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

import game.engine.monica.core.engine.CoreEngine;
import game.engine.monica.core.engine.EngineThread;
import game.engine.monica.core.property.EffectPointer;
import game.engine.monica.core.property.ErrorTypeException;
import game.engine.monica.core.property.PropertyID;
import game.engine.monica.util.StringID;
import game.engine.monica.util.Wrapper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NumberProperty implements Comparable<NumberProperty> {

    public NumberProperty(PropertyID id, boolean isPercentVal) {
        this(id, isPercentVal, 0, 0);
    }

    public NumberProperty(PropertyID id, boolean isPercentVal,
            double defaultVal, double offsetVal) {
        if (id == null)
            throw new NullPointerException("THe id is null.");
        if (id.getType() != PropertyID.PropertyType.INTEGER)
            throw new ErrorTypeException("Error property type.");
        this.type = id;
        isPerVal = isPercentVal;
        this.defaultVal = defaultVal;
        this.offsetVal = offsetVal;
    }

    public final double getDefaultValue() {
        return defaultVal;
    }

    public final void setDefaultValue(double val) {
        this.defaultVal = val;
        isCalc = false;
    }

    public final double getOffsetValue() {
        return offsetVal;
    }

    public final void setOffsetValue(double val) {
        this.offsetVal = val;
        isCalc = false;
    }

    public EffectPointer addAdditionValue(AbstractNumberEffect e) {
        calcLocker.lock();
        try {
            currentEffectPointer = currentEffectPointer.linkNew();
            effects.put(currentEffectPointer, e);
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
        return currentEffectPointer;
    }

    private EffectPointer addBuffEffect(NumberBuffEffect e) {
        calcLocker.lock();
        try {
            currentEffectPointer = currentEffectPointer.linkNew();
            effects.put(currentEffectPointer, e);
            BuffRunnable r = new BuffRunnable(currentEffectPointer);
            bltThreads.put(currentEffectPointer, r);
            EngineThread t = new EngineThread(r);
            if (CoreEngine.isContinuing())
                t.start();
            else
                //CoreEngine.set
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
        return currentEffectPointer;
    }

    private EffectPointer addLongTimeEffect(NumberLongTimeEffect e) {
        calcLocker.lock();
        try {
            currentEffectPointer = currentEffectPointer.linkNew();
            effects.put(currentEffectPointer, e);
            LTRunnable r = new LTRunnable(currentEffectPointer);
            if (e.isInterval()) {
                bltThreads.put(currentEffectPointer, r);
                new EngineThread(r).start();
            }
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
        return currentEffectPointer;
    }

    private void setFixedEffect(NumberFixedEffect e) {
        calcLocker.lock();
        try {
            fixedEffect = e;
            isFixed = true;
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
    }

    public final boolean isFixed() {
        return isFixed;
    }

    public final EffectPointer addEffect(AbstractNumberEffect e) {
        if (e == null)
            throw new NullPointerException("The effect is null.");
        switch (e.getEffectType()) {
            case TYPE_NUM_NUMBER:
                return addAdditionValue(e);
            case TYPE_NUM_BUFF:
            case TYPE_NUM_BUFF_INTERVAL:
            case TYPE_NUM_DEBUFF:
            case TYPE_NUM_DEBUFF_INTERVAL:
                return addBuffEffect((NumberBuffEffect) e);
            case TYPE_NUM_FIXED:
                setFixedEffect((NumberFixedEffect) e);
                return null;
            case TYPE_NUM_LONGTIME:
                return addLongTimeEffect((NumberLongTimeEffect) e);
            default:
                throw new ErrorTypeException("Error effect type.");
        }
    }

    public final void removeEffect(final StringID sid) {
        if (sid == null)
            throw new NullPointerException("The StringID is null.");
        calcLocker.lock();
        try {
            if (isFixed && fixedEffect.getID().equals(sid))
                isFixed = false;
            effects.forEach((EffectPointer p, AbstractNumberEffect e) -> {
                if (e.getID().equals(sid)) {
                    p.delete();
                    effects.remove(p);
                }
            });
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
    }

    public final NumberPropertyAdjustment getAdjustment() {
        return adjustment;
    }

    public final void setAdjustment(NumberPropertyAdjustment a) {
        if (a == null)
            throw new NullPointerException("The adjustment is null.");
        calcLocker.lock();
        try {
            this.hasAdjustment = true;
            this.adjustment = a;
            if (isCalc)
                totalVal = adjustment.adjust(totalVal);
        } finally {
            calcLocker.unlock();
        }
    }

    public final void removeAdjustment() {
        calcLocker.lock();
        try {
            this.hasAdjustment = false;
            isCalc = false;
        } finally {
            calcLocker.unlock();
        }
    }

    public final double getTotalValue() {
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
                totalVal = fixedEffect.affect(0d);
            else {
                Wrapper<Double> tval = new Wrapper<>(defaultVal + offsetVal);
                effects.forEach((EffectPointer p, AbstractNumberEffect e) -> {
                    tval.pack = e.affect(tval.pack);
                    System.out.println("\t" + tval.pack);
                });
                totalVal = hasAdjustment ? adjustment.adjust(tval.pack)
                        : tval.pack;
            }
            isCalc = true;
        } finally {
            calcLocker.unlock();
        }
    }

    @Override
    public int compareTo(NumberProperty t) {
        return type.compareTo(t.type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != NumberProperty.class)
            return false;
        NumberProperty p = (NumberProperty) obj;
        return type.equals(p.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + "[ ID = " + type.getID()
                + ", Type = " + type.getType()
                + ", Default value = " + defaultVal
                + ", Offset value = " + offsetVal
                + ", Total value = " + getTotalValue()
                + ",  Has adjustment" + " ]";
    }
    private final PropertyID type;
    protected double defaultVal, offsetVal;
    protected final ConcurrentHashMap<EffectPointer, AbstractNumberEffect> effects
            = new ConcurrentHashMap<>(CoreEngine.getDefaultQuantily(), 0.5f);
    protected EffectPointer currentEffectPointer = null;
    private final ConcurrentHashMap<EffectPointer, Runnable> bltThreads
            = new ConcurrentHashMap<>(CoreEngine.getDefaultQuantily(), 0.2f);
    protected boolean hasAdjustment = false;
    protected NumberPropertyAdjustment adjustment = null;
    private boolean isFixed = false;
    protected NumberFixedEffect fixedEffect;
    protected final boolean isPerVal;
    protected transient volatile boolean isCalc = false;
    private final transient ReentrantReadWriteLock.WriteLock calcLocker
            = new ReentrantReadWriteLock().writeLock();
    protected transient double totalVal;

    private final class BuffRunnable
            implements Comparable<BuffRunnable>, Runnable {

        public BuffRunnable(EffectPointer pointer) {
            this.pointer = pointer;
        }

        @Override
        public void run() {
            NumberBuffEffect effect = (NumberBuffEffect) effects.get(pointer);
            int ce1mscc = CoreEngine.get1msSuspendTimeChangeCount();
            int time = CoreEngine.get1msSuspendTime() * effect.getMaxDuration();
            if (effect.isInterval()) {
                int it = CoreEngine
                        .calcSuspendTime(effect.getIntervalDuration());
                while (CoreEngine.isStart()) {
                    if (CoreEngine.is1msSuspendTimeChanged(ce1mscc)) {
                        time = CoreEngine
                                .calcSuspendTime(effect.getMaxDuration());
                        it = CoreEngine
                                .calcSuspendTime(effect.getIntervalDuration());
                    }
                    if (time % it == 0) {
                        while (CoreEngine.isContinuing()) {
                            long startingTime = System.currentTimeMillis();
                            try {
                                EngineThread.sleepAndNeedWakeUp(it
                                        - CoreEngine.calcSuspendTime(time));
                            } catch (InterruptedException ex) {
                                long endTime = System.currentTimeMillis();
                                already = (int) (endTime - startingTime)
                                        / CoreEngine.get1msSuspendTime();
                            }
                            currentCount++;
                            if (currentCount == time) {
                                removeEffect(effect.getID());
                                return;
                            }
                            if (CoreEngine.calcSuspendTime(currentCount)
                                    % it == 0)
                                ((NumberIntervalBuffEffect) effect)
                                        .intervalBuffIncrease();
                        }
                    } else {
                    }
                }
            } else {
                while (CoreEngine.isStart()) {
                    if (CoreEngine.is1msSuspendTimeChanged(ce1mscc))
                        time = CoreEngine
                                .calcSuspendTime(effect.getMaxDuration());
                    while (CoreEngine.isContinuing()) {
                        long startingTime = System.currentTimeMillis();
                        try {
                            EngineThread.sleepAndNeedWakeUp(time
                                    - CoreEngine.calcSuspendTime(already));
                        } catch (InterruptedException ex) {
                            long endTime = System.currentTimeMillis();
                            already += (int) (endTime - startingTime)
                                    / CoreEngine.get1msSuspendTime();
                            continue;
                        }
                        removeEffect(effect.getID());
                    }
                }
            }
        }

        @Override
        public int compareTo(BuffRunnable r) {
            if (r == null)
                throw new NullPointerException("Compare with"
                        + " a null NumberBuffRunnable.");
            return Integer.compare(pointer.pointer(), r.pointer.pointer());
        }
        protected EffectPointer pointer;
        private int currentCount = 0;
        private int already = 0;
    }

    private final class LTRunnable
            implements Comparable<LTRunnable>, Runnable {

        public LTRunnable(EffectPointer pointer) {
            this.pointer = pointer;
        }

        @Override
        public void run() {
            NumberLongTimeEffect effect
                    = (NumberLongTimeEffect) effects.get(pointer);
            int ce1mscc = CoreEngine.get1msSuspendTimeChangeCount();
            int it = CoreEngine
                    .calcSuspendTime(effect.getIntervalDuration());
            while (CoreEngine.isStart()) {
                if (CoreEngine.is1msSuspendTimeChanged(ce1mscc)) {
                    it = CoreEngine
                            .calcSuspendTime(effect.getIntervalDuration());
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
                    if (currentCount % it == 0)
                        ((NumberIntervalLongTimeEffect) effect)
                                .intervalBuffIncrease();
                }
            }
        }

        @Override
        public int compareTo(LTRunnable r) {
            if (r == null)
                throw new NullPointerException("Compare with"
                        + " a null NumberIntervalLongTimeRunnable.");
            return Integer.compare(pointer.pointer(), r.pointer.pointer());
        }
        protected EffectPointer pointer;
        private int currentCount = 0;
        private int already = 0;
    }
}
