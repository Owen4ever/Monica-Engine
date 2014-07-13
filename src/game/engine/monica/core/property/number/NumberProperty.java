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
import game.engine.monica.core.property.AbstractProperty;
import game.engine.monica.core.property.EffectPointer;
import game.engine.monica.core.property.ErrorTypeException;
import game.engine.monica.core.property.PropertyID;
import game.engine.monica.util.StringID;
import game.engine.monica.util.Wrapper;
import game.engine.monica.util.annotation.UnOverridable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NumberProperty extends AbstractProperty<Double>
        implements Comparable<NumberProperty> {

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

    protected final ReentrantReadWriteLock getCalcLock() {
        return calcLocker;
    }

    @UnOverridable
    protected void getCalcWriteLock() {
        int i = 0;
        while (true) {
            if (!calcLocker.isWriteLocked() && calcLocker.writeLock().tryLock())
                if (calcLocker.getWriteHoldCount() != 1) {
                    calcLocker.writeLock().unlock();
                } else if (hasAdjustment() == PRO_ADJ_PAR
                        && ((NumberProperty) parentProperty).calcLocker.isWriteLocked())
                    calcLocker.writeLock().unlock();
                else {
                    return;
                }
        }
    }

    @UnOverridable
    protected void unlockCalcWriteLock() {
        isCalc = false;
        calcLocker.writeLock().unlock();
    }

    public final double getDefaultValue() {
        return defaultVal;
    }

    public final void setDefaultValue(double val) {
        getCalcWriteLock();
        this.defaultVal = val;
        unlockCalcWriteLock();
    }

    public final double getOffsetValue() {
        return offsetVal;
    }

    public final void setOffsetValue(double val) {
        getCalcWriteLock();
        this.offsetVal = val;
        unlockCalcWriteLock();
    }

    private EffectPointer addAdditionValue(AbstractNumberEffect e) {
        currentEffectPointer = currentEffectPointer.linkNew();
        effects.put(currentEffectPointer, e);
        return currentEffectPointer;
    }

    private EffectPointer addBuffEffect(NumberBuffEffect e) {
        currentEffectPointer = currentEffectPointer.linkNew();
        effects.put(currentEffectPointer, e);
        BuffRunnable r = new BuffRunnable(currentEffectPointer);
        bltThreads.put(currentEffectPointer, r);
        EngineThread t = new EngineThread(r);
        if (CoreEngine.isContinuing())
            t.start();
        else
            CoreEngine.runThreadNextStart(t);
        return currentEffectPointer;
    }

    private EffectPointer addLongTimeEffect(NumberLongTimeEffect e) {
        currentEffectPointer = currentEffectPointer.linkNew();
        effects.put(currentEffectPointer, e);
        LTRunnable r = new LTRunnable(currentEffectPointer);
        if (e.isInterval()) {
            bltThreads.put(currentEffectPointer, r);
            EngineThread t = new EngineThread(r);
            if (CoreEngine.isContinuing())
                t.start();
            else
                CoreEngine.runThreadNextStart(t);
        }
        return currentEffectPointer;
    }

    private void setFixedEffect(NumberFixedEffect e) {
        fixedEffect = e;
        isFixed = true;
    }

    public final boolean isFixed() {
        return isFixed;
    }

    public final EffectPointer addEffect(AbstractNumberEffect e) {
        if (e == null)
            throw new NullPointerException("The effect is null.");
        if (!e.affectTo().equals(type))
            throw new NullPointerException("The effect cannot "
                    + "affect this property.");
        getCalcWriteLock();
        try {
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
        } finally {
            unlockCalcWriteLock();
        }
    }

    public final void removeEffect(final StringID sid) {
        if (sid == null)
            throw new NullPointerException("The StringID is null.");
        getCalcWriteLock();
        try {
            if (isFixed && fixedEffect.getID().equals(sid))
                isFixed = false;
            effects.entrySet().parallelStream().forEach(entry -> {
                if (entry.getValue().getID().equals(sid)) {
                    EffectPointer p = entry.getKey();
                    AbstractNumberEffect e = entry.getValue();
                    effects.remove(p);
                    tempEffects.clear();
                    tempEffects.putAll(effects);
                    effects.clear();
                    effects.putAll(tempEffects);
                    p.delete();
                    switch (e.getEffectType()) {
                        case TYPE_NUM_BUFF:
                        case TYPE_NUM_BUFF_INTERVAL:
                        case TYPE_NUM_DEBUFF:
                        case TYPE_NUM_DEBUFF_INTERVAL:
                            bltThreads.remove(p);
                            tempBltThreads.clear();
                            tempBltThreads.putAll(bltThreads);
                            bltThreads.clear();
                            bltThreads.putAll(bltThreads);
                            return;
                        case TYPE_NUM_LONGTIME:
                            if (((NumberLongTimeEffect) e).isInterval())
                                bltThreads.remove(p);
                            return;
                    }
                }
            });
        } finally {
            unlockCalcWriteLock();
        }
    }

    public final void removeEffect(EffectPointer pointer) {
        if (pointer == null)
            throw new NullPointerException("The EffectPointer is null.");
        getCalcWriteLock();
        try {
            effects.remove(pointer);
            tempEffects.clear();
            tempEffects.putAll(effects);
            effects.clear();
            effects.putAll(tempEffects);
            bltThreads.remove(pointer);
            pointer.delete();
        } finally {
            unlockCalcWriteLock();
        }
    }

    public final int hasAdjustment() {
        return hasAdjustment;
    }

    public final NumberPropertyAdjustment getAdjustment() {
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

    public final void setAdjustment(NumberPropertyAdjustment a) {
        if (a == null)
            throw new NullPointerException("The adjustment is null.");
        getCalcWriteLock();
        try {
            this.hasAdjustment = PRO_ADJ_ADJ;
            this.adjustment = a;
            if (isCalc)
                totalVal = adjustment.adjust(totalVal);
        } finally {
            unlockCalcWriteLock();
        }
    }

    public final void setAdjustment(NumberParentProperty p) {
        if (p == null)
            throw new NullPointerException("The NumberParentProperty is null.");
        getCalcWriteLock();
        try {
            this.hasAdjustment = PRO_ADJ_PAR;
            this.parentProperty = p;
            if (isCalc)
                totalVal = parentProperty.adjust(totalVal);
        } finally {
            unlockCalcWriteLock();
        }
    }

    public final void removeAdjustment() {
        getCalcWriteLock();
        try {
            this.hasAdjustment = PRO_ADJ_NONE;
        } finally {
            unlockCalcWriteLock();
        }
    }

    @Override
    public final Double getTotalValue() {
        if (isCalc)
            return totalVal;
        else {
            calcTotalValue();
            return totalVal;
        }
    }

    private void calcTotalValue() {
        getCalcWriteLock();
        try {
            if (isFixed)
                totalVal = fixedEffect.affect(0d);
            else {
                Wrapper<Double> tval = new Wrapper<>(defaultVal + offsetVal);
                switch (hasAdjustment) {
                    case -1:
                        effects.forEach((EffectPointer p,
                                AbstractNumberEffect e) -> {
                                    tval.pack = e.affect(tval.pack);
                                });
                        totalVal = tval.pack;
                        break;
                    case 0:
                        effects.forEach((EffectPointer p,
                                AbstractNumberEffect e) -> {
                                    tval.pack = adjustment
                                    .adjust(e.affect(tval.pack));
                                });
                        break;
                    case 1:
                        effects.forEach((EffectPointer p,
                                AbstractNumberEffect e) -> {
                                    parentProperty.beNotify();
                                    tval.pack = parentProperty
                                    .adjust(e.affect(tval.pack));
                                });
                        break;
                }
                totalVal = tval.pack;
            }
            isCalc = true;
        } finally {
            unlockCalcWriteLock();
        }
    }

    @Override
    public void beNotify() {
        calcTotalValue();
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
        String strAdjustment;
        switch (hasAdjustment) {
            case 0:
                strAdjustment = ", Has Adjustment";
                break;
            case 1:
                strAdjustment = ", Has Parent";
                break;
            case -1:
            default:
                strAdjustment = ", No Adjustment";
        }
        String strTypeID = type.getID().toString();
        return new StringBuilder(160 + strTypeID.length())
                .append(getClass().getName())
                .append("[ ID = ").append(strTypeID)
                .append(", Type = ").append(type.getType().toString())
                .append(", Default value = ").append(defaultVal)
                .append(", Offset value = ").append(offsetVal)
                .append(", Total value = ").append(getTotalValue())
                .append(strAdjustment).append(" ]").toString();
    }
    private final PropertyID type;
    protected double defaultVal, offsetVal;
    private final ConcurrentHashMap<EffectPointer, AbstractNumberEffect> effects
            = new ConcurrentHashMap<>(CoreEngine.getDefaultQuantily(), 0.5f);
    private transient final HashMap<EffectPointer, AbstractNumberEffect> tempEffects
            = new HashMap<>(CoreEngine.getDefaultQuantily(), 0.5f);
    protected EffectPointer currentEffectPointer = EffectPointer.newFirstPointer();
    private final HashMap<EffectPointer, Runnable> bltThreads
            = new HashMap<>(CoreEngine.getDefaultQuantily(), 0.2f);
    private transient final HashMap<EffectPointer, Runnable> tempBltThreads
            = new HashMap<>(CoreEngine.getDefaultQuantily(), 0.2f);
    /**
     * The {@code NumberProperty} does not have an {@code adjustment} or a
     * {@code NumberParentProperty} if {@code hasAdjustment} equals -1; The
     * number property only has an {@code adjustment} if {@code hasAdjustment}
     * equals 0; In addition, the {@code NumberProperty} only has a
     * {@code NumberParentProperty} if {@code hasAdjustment} equals 1.
     */
    protected int hasAdjustment = PRO_ADJ_NONE;
    protected NumberPropertyAdjustment adjustment = null;
    protected NumberParentProperty parentProperty = null;
    private boolean isFixed = false;
    protected NumberFixedEffect fixedEffect;
    protected final boolean isPerVal;
    protected transient volatile boolean isCalc = false;
    private final transient ReentrantReadWriteLock calcLocker
            = new ReentrantReadWriteLock();
    protected transient double totalVal;

    private static final int PRO_ADJ_NONE = -1;
    private static final int PRO_ADJ_ADJ = 0;
    private static final int PRO_ADJ_PAR = 1;

    private final class BuffRunnable
            implements Comparable<BuffRunnable>, Runnable {

        public BuffRunnable(EffectPointer pointer) {
            this.pointer = pointer;
        }

        @Override
        public void run() {
            NumberBuffEffect effect = (NumberBuffEffect) effects.get(pointer);
            int ce1mscc = CoreEngine.get1msSuspendTimeChangeCount();
            int time = CoreEngine.calcSuspendTime(effect.getMaxDuration());
            if (effect.getStartingTime() > 0) {
                isInStartingTime = true;
                startingTimeAlready = effect.getStartingTime();
            }
            if (effect.isInterval()) {
                int it = CoreEngine
                        .calcSuspendTime(effect.getIntervalDuration());
                while (CoreEngine.isStart()) {
                    if (CoreEngine.is1msSuspendTimeChanged(ce1mscc))
                        it = CoreEngine
                                .calcSuspendTime(effect.getIntervalDuration());
                    while (CoreEngine.isContinuing()) {
                        if (isInStartingTime) {
                            long startingTime = System.currentTimeMillis();
                            try {
                                EngineThread
                                        .sleepAndNeedWakeUp(CoreEngine
                                                .calcSuspendTime(startingTimeAlready));
                            } catch (InterruptedException ex) {
                                long endTime = System.currentTimeMillis();
                                startingTimeAlready = CoreEngine.calcQuondamTime((int) (endTime - startingTime));
                            }
                            isInStartingTime = false;
                        }
                        long startingTime = System.currentTimeMillis();
                        try {
                            EngineThread.sleepAndNeedWakeUp(it
                                    - CoreEngine.calcSuspendTime(already));
                        } catch (InterruptedException ex) {
                            long endTime = System.currentTimeMillis();
                            already += CoreEngine.calcQuondamTime((int) (endTime - startingTime));
                            continue;
                        }
                        already = 0;
                        alreadyTime += effect.getIntervalDuration();
                        if (alreadyTime + effect.getStartingTime()
                                >= effect.getMaxDuration()) {
                            removeEffect(pointer);
                            return;
                        }
                        getCalcWriteLock();
                        ((NumberIntervalBuffEffect) effect)
                                .getIntervalEffector().intervalIncrease();
                        unlockCalcWriteLock();
                    }
                }
            } else {
                while (CoreEngine.isStart()) {
                    if (CoreEngine.is1msSuspendTimeChanged(ce1mscc))
                        time = CoreEngine
                                .calcSuspendTime(effect.getMaxDuration());
                    while (CoreEngine.isContinuing()) {
                        if (isInStartingTime) {
                            long startingTime = System.currentTimeMillis();
                            try {
                                EngineThread
                                        .sleepAndNeedWakeUp(CoreEngine
                                                .calcSuspendTime(startingTimeAlready));
                            } catch (InterruptedException ex) {
                                long endTime = System.currentTimeMillis();
                                startingTimeAlready -= CoreEngine.calcQuondamTime((int) (endTime - startingTime));
                                continue;
                            }
                            isInStartingTime = false;
                        }
                        long startingTime = System.currentTimeMillis();
                        try {
                            EngineThread.sleepAndNeedWakeUp(time
                                    - CoreEngine.calcSuspendTime(already));
                        } catch (InterruptedException ex) {
                            long endTime = System.currentTimeMillis();
                            already += CoreEngine.calcQuondamTime((int) (endTime - startingTime));
                            continue;
                        }
                        removeEffect(pointer);
                    }
                }
            }
        }

        @Override
        public int compareTo(BuffRunnable r
        ) {
            if (r == null)
                throw new NullPointerException("Compare with"
                        + " a null NumberBuffRunnable.");
            return Integer.compare(pointer.pointer(), r.pointer.pointer());
        }
        protected EffectPointer pointer;
        private boolean isInStartingTime;
        private int startingTimeAlready;
        private int alreadyTime = 0;
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
                if (CoreEngine.is1msSuspendTimeChanged(ce1mscc))
                    it = CoreEngine
                            .calcSuspendTime(effect.getIntervalDuration());
                while (CoreEngine.isContinuing()) {
                    if (isInStartingTime) {
                        long startingTime = System.currentTimeMillis();
                        try {
                            EngineThread
                                    .sleepAndNeedWakeUp(CoreEngine
                                            .calcSuspendTime(startingTimeAlready));
                        } catch (InterruptedException ex) {
                            long endTime = System.currentTimeMillis();
                            startingTimeAlready = (int) ((endTime - startingTime)
                                    / CoreEngine.get1msSuspendTime());
                        }
                        isInStartingTime = false;
                        startingTimeAlready = 0;
                    }
                    long startingTime = System.currentTimeMillis();
                    try {
                        EngineThread.sleepAndNeedWakeUp(it - already);
                    } catch (InterruptedException ex) {
                        long endTime = System.currentTimeMillis();
                        already = (int) (endTime - startingTime)
                                / CoreEngine.get1msSuspendTime();
                    }
                    if (already == effect.getIntervalDuration())
                        ((NumberIntervalLongTimeEffect) effect)
                                .getIntervalEffector().intervalIncrease();
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
        private boolean isInStartingTime;
        private int startingTimeAlready;
        protected EffectPointer pointer;
        private int already = 0;
    }
}
