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

package game.engine.monica.core.property.bool;

import game.engine.monica.core.engine.CoreEngine;
import game.engine.monica.core.engine.EngineThread;
import game.engine.monica.core.engine.EngineThreadGroup;
import game.engine.monica.core.property.AbstractBuffEffect;
import game.engine.monica.core.property.AbstractEffect;
import game.engine.monica.core.property.AbstractIntervalBuffEffect;
import game.engine.monica.core.property.AbstractIntervalLongTimeEffect;
import game.engine.monica.core.property.AbstractLongTimeEffect;
import game.engine.monica.core.property.AbstractProperty;
import game.engine.monica.core.property.EffectPointer;
import game.engine.monica.core.property.ErrorTypeException;
import game.engine.monica.core.property.ParentPropertyInterface;
import game.engine.monica.core.property.PropertyAdjustment;
import game.engine.monica.core.property.PropertyID;
import game.engine.monica.util.StringID;
import game.engine.monica.util.Wrapper;
import game.engine.monica.util.annotation.UnOverridable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BoolProperty extends AbstractProperty<Boolean> {

    public BoolProperty(PropertyID id) {
        this(id, false, false);
    }

    public BoolProperty(PropertyID id, boolean defaultVal, boolean offsetVal) {
        super(id, defaultVal, offsetVal);
    }

    protected final ReentrantReadWriteLock getCalcLock() {
        return calcLocker;
    }

    @Override
    @UnOverridable
    protected void getCalcWriteLock() {
        while (true) {
            if (!calcLocker.isWriteLocked() && calcLocker.writeLock().tryLock())
                if (calcLocker.getWriteHoldCount() != 1)
                    calcLocker.writeLock().unlock();
                else if (hasAdjustment() == PRO_ADJ_PAR
                        && ((BoolProperty) ((AbstractProperty) parentProperty))
                                .calcLocker.isWriteLocked())
                    calcLocker.writeLock().unlock();
                else
                    return;
        }
    }

    @Override
    @UnOverridable
    protected void unlockCalcWriteLock() {
        isCalc = false;
        calcLocker.writeLock().unlock();
    }

    private EffectPointer addAdditionValue(AbstractEffect<Boolean> e) {
        currentEffectPointer = currentEffectPointer.linkNew();
        effects.put(currentEffectPointer, e);
        return currentEffectPointer;
    }

    private EffectPointer addBuffEffect(AbstractBuffEffect<Boolean> e) {
        currentEffectPointer = currentEffectPointer.linkNew();
        effects.put(currentEffectPointer, e);
        BuffRunnable r = new BuffRunnable(currentEffectPointer);
        bltThreads.put(currentEffectPointer, r);
        if (CoreEngine.isContinuing())
            new EngineThread(TG_PROPERTY_NUM, r).start();
        else
            CoreEngine.runThreadNextStart(r);
        return currentEffectPointer;
    }

    private EffectPointer addLongTimeEffect(AbstractLongTimeEffect<Boolean> e) {
        currentEffectPointer = currentEffectPointer.linkNew();
        effects.put(currentEffectPointer, e);
        LTRunnable r = new LTRunnable(currentEffectPointer);
        if (e.isInterval()) {
            bltThreads.put(currentEffectPointer, r);
            if (CoreEngine.isContinuing())
                new EngineThread(TG_PROPERTY_NUM, r).start();
            else
                CoreEngine.runThreadNextStart(r);
        }
        return currentEffectPointer;
    }
    protected static final EngineThreadGroup TG_PROPERTY_NUM
            = new EngineThreadGroup("BoolProperty Thread Group");

    private void setFixedEffect(BoolFixedEffect e) {
        fixedEffect = e;
        isFixed = true;
    }

    @Override
    public final boolean isFixed() {
        return isFixed;
    }

    @Override
    public final EffectPointer addEffect(AbstractEffect<Boolean> e) {
        if (e == null)
            throw new NullPointerException("The effect is null.");
        if (!e.affectTo().equals(type))
            throw new NullPointerException("The effect cannot "
                    + "affect this property.");
        getCalcWriteLock();
        try {
            switch (e.getEffectType()) {
                case TYPE_NUM_SIMPLE:
                    return addAdditionValue(e);
                case TYPE_NUM_BUFF:
                case TYPE_NUM_BUFF_INTERVAL:
                case TYPE_NUM_DEBUFF:
                case TYPE_NUM_DEBUFF_INTERVAL:
                    return addBuffEffect((AbstractBuffEffect<Boolean>) e);
                case TYPE_NUM_FIXED:
                    setFixedEffect((BoolFixedEffect) e);
                    return null;
                case TYPE_NUM_LONGTIME:
                    return addLongTimeEffect((AbstractLongTimeEffect<Boolean>) e);
                default:
                    throw new ErrorTypeException("Error effect type.");
            }
        } finally {
            unlockCalcWriteLock();
        }
    }

    @Override
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
                    AbstractEffect<Boolean> e = entry.getValue();
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
                        case TYPE_NUM_LONGTIME_INTERVAL:
                            bltThreads.remove(p);
                    }
                }
            });
        } finally {
            unlockCalcWriteLock();
        }
    }

    @Override
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

    @Override
    public final void setAdjustment(PropertyAdjustment<Boolean> a) {
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

    @Override
    public final void setAdjustment(ParentPropertyInterface<Boolean> p) {
        if (p == null)
            throw new NullPointerException("The ParentProperty is null.");
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
    public final Boolean getTotalValue() {
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
                totalVal = fixedEffect.affect(false);
            else {
                Wrapper<Boolean> tval = new Wrapper<>(defaultVal | offsetVal);
                switch (hasAdjustment) {
                    case -1:
                        effects.forEach((p, e) -> {
                            tval.pack = e.affect(tval.pack);
                        });
                        totalVal = tval.pack;
                        break;
                    case 0:
                        effects.forEach((p, e) -> {
                            tval.pack = adjustment
                                    .adjust(e.affect(tval.pack));
                        });
                        break;
                    case 1:
                        effects.forEach((p, e) -> {
                            parentProperty.getTotalValue();
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
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != BoolProperty.class)
            return false;
        BoolProperty p = (BoolProperty) obj;
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
                strAdjustment = ", Has Adjustment ]";
                break;
            case 1:
                strAdjustment = ", Has Parent ]";
                break;
            case -1:
            default:
                strAdjustment = ", No Adjustment ]";
        }
        String strTypeID = type.getID().toString();
        return new StringBuilder(160 + strTypeID.length())
                .append(getClass().getName())
                .append("[ ID = ").append(strTypeID)
                .append(", Type = ").append(type.getType().toString())
                .append(", Default value = ").append(defaultVal)
                .append(", Offset value = ").append(offsetVal)
                .append(", Total value = ").append(getTotalValue())
                .append(strAdjustment).toString();
    }
    private final ConcurrentHashMap<EffectPointer, AbstractEffect<Boolean>> effects
            = new ConcurrentHashMap<>(CoreEngine.getDefaultQuantily(), .5f);
    private transient final HashMap<EffectPointer, AbstractEffect<Boolean>> tempEffects
            = new HashMap<>(CoreEngine.getDefaultQuantily(), .5f);
    private final HashMap<EffectPointer, Runnable> bltThreads
            = new HashMap<>(CoreEngine.getDefaultQuantily(), .2f);
    private transient final HashMap<EffectPointer, Runnable> tempBltThreads
            = new HashMap<>(CoreEngine.getDefaultQuantily(), .2f);
    protected EffectPointer currentEffectPointer = EffectPointer.newFirstPointer();
    private boolean isFixed = false;
    protected BoolFixedEffect fixedEffect;
    protected transient volatile boolean isCalc = false;
    private final transient ReentrantReadWriteLock calcLocker
            = new ReentrantReadWriteLock();
    protected transient boolean totalVal;

    private final class BuffRunnable
            implements Comparable<BuffRunnable>, Runnable {

        public BuffRunnable(EffectPointer pointer) {
            this.pointer = pointer;
        }

        @Override
        public void run() {
            AbstractBuffEffect<Boolean> effect
                    = (AbstractBuffEffect<Boolean>) effects.get(pointer);
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
                                continue;
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
                        ((AbstractIntervalBuffEffect<Boolean>) effect)
                                .getIntervalEffector().intervalChange();
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
        public int compareTo(BuffRunnable r) {
            if (r == null)
                throw new NullPointerException("Compare with"
                        + " a null BoolBuffRunnable.");
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
            AbstractLongTimeEffect<Boolean> effect
                    = (AbstractLongTimeEffect<Boolean>) effects.get(pointer);
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
                            startingTimeAlready = CoreEngine
                                    .calcQuondamTime((int) (endTime - startingTime));
                            continue;
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
                        continue;
                    }
                    if (already == effect.getIntervalDuration())
                        ((AbstractIntervalLongTimeEffect<Boolean>) effect)
                                .getIntervalEffector().intervalChange();
                }
            }
        }

        @Override
        public int compareTo(LTRunnable r) {
            if (r == null)
                throw new NullPointerException("Compare with"
                        + " a null BoolLongTimeRunnable.");
            return Integer.compare(pointer.pointer(), r.pointer.pointer());
        }
        private boolean isInStartingTime;
        private int startingTimeAlready;
        protected EffectPointer pointer;
        private int already = 0;
    }
}