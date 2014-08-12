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

package engine.monica.core.property.number;

import engine.monica.core.engine.CoreEngine;
import engine.monica.core.engine.EngineThread;
import engine.monica.core.engine.EngineThreadGroup;
import engine.monica.core.property.effect.BuffEffect;
import engine.monica.core.property.effect.AbstractEffect;
import engine.monica.core.property.effect.ModifiedEffect;
import engine.monica.core.property.effect.IntervalBuffEffect;
import engine.monica.core.property.effect.IntervalLongTimeEffect;
import engine.monica.core.property.effect.LongTimeEffect;
import engine.monica.core.property.AbstractProperty;
import engine.monica.core.property.ErrorTypeException;
import engine.monica.core.property.PropertyID;
import engine.monica.util.LinkedPointer;

import engine.monica.util.Wrapper;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NumberProperty extends AbstractProperty<Double> {

    public NumberProperty(PropertyID id) {
        this(id, 0, 0);
    }

    public NumberProperty(PropertyID id, double defaultVal, double offsetVal) {
        super(id, defaultVal, offsetVal);
    }

    protected final ReentrantReadWriteLock getCalcLock() {
        return calcLocker;
    }

    @Override
    protected void lock() {
        while (true) {
            if (!calcLocker.isWriteLocked() && calcLocker.writeLock().tryLock())
                if (calcLocker.getWriteHoldCount() != 1)
                    calcLocker.writeLock().unlock();
                else if (hasAdjustment() == PRO_ADJ_PAR
                        && ((NumberProperty) ((AbstractProperty) parentProperty)).calcLocker.isWriteLocked())
                    calcLocker.writeLock().unlock();
                else
                    return;
        }
    }

    @Override
    protected void unlock() {
        isCalc = false;
        calcLocker.writeLock().unlock();
    }

    private LinkedPointer addAdditionValue(AbstractEffect<Double> e) {
        currentEffectPointer = currentEffectPointer.linkNew();
        effects.put(currentEffectPointer, e);
        return currentEffectPointer;
    }

    private LinkedPointer addBuffEffect(BuffEffect<Double> e) {
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

    private LinkedPointer addLongTimeEffect(LongTimeEffect<Double> e) {
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
            = new EngineThreadGroup("NumberProperty Thread Group");

    private void setFixedEffect(ModifiedEffect<Double> e) {
        fixedEffect = e;
        isFixed = true;
    }

    @Override
    public final boolean isFixed() {
        return isFixed;
    }

    @Override
    public final LinkedPointer addEffect(AbstractEffect<Double> e) {
        if (e == null)
            throw new NullPointerException("The effect is null.");
        if (!e.affectTo().equals(type))
            throw new NullPointerException("The effect cannot "
                    + "affect this property.");
        lock();
        try {
            switch (e.getEffectType()) {
                case TYPE_SIMPLE:
                    return addAdditionValue(e);
                case TYPE_BUFF:
                case TYPE_BUFF_INTERVAL:
                    return addBuffEffect((BuffEffect<Double>) e);
                case TYPE_MODIFIED:
                    setFixedEffect((ModifiedEffect<Double>) e);
                    return null;
                case TYPE_LONGTIME:
                case TYPE_LONGTIME_INTERVAL:
                    return addLongTimeEffect((LongTimeEffect<Double>) e);
                default:
                    throw new ErrorTypeException("Error effect type.");
            }
        } finally {
            unlock();
        }
    }

    @Override
    public final void removeEffect(final String sid) {
        if (sid == null)
            throw new NullPointerException("The String is null.");
        lock();
        try {
            if (isFixed && fixedEffect.getID().equals(sid))
                isFixed = false;
            effects.entrySet().parallelStream().forEach(entry -> {
                if (entry.getValue().getID().equals(sid)) {
                    LinkedPointer p = entry.getKey();
                    AbstractEffect<Double> e = entry.getValue();
                    effects.remove(p);
                    tempEffects.clear();
                    tempEffects.putAll(effects);
                    effects.clear();
                    effects.putAll(tempEffects);
                    p.delete();
                    switch (e.getEffectType()) {
                        case TYPE_BUFF:
                        case TYPE_BUFF_INTERVAL:
                            bltThreads.remove(p);
                            tempBltThreads.clear();
                            tempBltThreads.putAll(bltThreads);
                            bltThreads.clear();
                            bltThreads.putAll(bltThreads);
                            return;
                        case TYPE_LONGTIME_INTERVAL:
                            bltThreads.remove(p);
                            return;
                    }
                }
            });
        } finally {
            unlock();
        }
    }

    @Override
    public final void removeEffect(LinkedPointer pointer) {
        if (pointer == null)
            throw new NullPointerException("The EffectPointer is null.");
        lock();
        try {
            effects.remove(pointer);
            tempEffects.clear();
            tempEffects.putAll(effects);
            effects.clear();
            effects.putAll(tempEffects);
            bltThreads.remove(pointer);
            pointer.delete();
        } finally {
            unlock();
        }
    }

    public final void removeAdjustment() {
        lock();
        try {
            this.hasAdjustment = PRO_ADJ_NONE;
        } finally {
            unlock();
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
        lock();
        try {
            if (isFixed)
                totalVal = fixedEffect.affect(0d);
            else {
                Wrapper<Double> tval = new Wrapper<>(defaultVal + offsetVal);
                switch (hasAdjustment) {
                    case -1:
                        effects.forEach((LinkedPointer p,
                                AbstractEffect<Double> e) -> {
                                    tval.pack = e.affect(tval.pack);
                                });
                        totalVal = tval.pack;
                        break;
                    case 0:
                        effects.forEach((LinkedPointer p,
                                AbstractEffect<Double> e) -> {
                                    tval.pack = adjustment
                                    .adjust(e.affect(tval.pack));
                                });
                        break;
                    case 1:
                        effects.forEach((LinkedPointer p,
                                AbstractEffect<Double> e) -> {
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
            unlock();
        }
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
    private final ConcurrentHashMap<LinkedPointer, AbstractEffect<Double>> effects
            = new ConcurrentHashMap<>(CoreEngine.getDefaultQuantily(), .5f);
    private transient final HashMap<LinkedPointer, AbstractEffect<Double>> tempEffects
            = new HashMap<>(CoreEngine.getDefaultQuantily(), .5f);
    private final HashMap<LinkedPointer, Runnable> bltThreads
            = new HashMap<>(CoreEngine.getDefaultQuantily(), .2f);
    private transient final HashMap<LinkedPointer, Runnable> tempBltThreads
            = new HashMap<>(CoreEngine.getDefaultQuantily(), .2f);
    protected LinkedPointer currentEffectPointer = LinkedPointer.first();
    private boolean isFixed = false;
    protected ModifiedEffect<Double> fixedEffect;
    protected transient volatile boolean isCalc = false;
    private final transient ReentrantReadWriteLock calcLocker
            = new ReentrantReadWriteLock();
    protected transient double totalVal;

    private final class BuffRunnable
            implements Comparable<BuffRunnable>, Runnable {

        public BuffRunnable(LinkedPointer pointer) {
            this.pointer = pointer;
        }

        @Override
        public void run() {
            BuffEffect<Double> effect
                    = (BuffEffect<Double>) effects.get(pointer);
            int ce1mscc = CoreEngine.get1msSuspendTimeChangeCount();
            int time = CoreEngine.calcSuspendTime(effect.getMaxDuration());
            if (effect.getBeginningTime() > 0) {
                isInStartingTime = true;
                startingTimeAlready = effect.getBeginningTime();
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
                        if (alreadyTime + effect.getBeginningTime()
                                >= effect.getMaxDuration()) {
                            removeEffect(pointer);
                            return;
                        }
                        lock();
                        ((IntervalBuffEffect<Double>) effect)
                                .getIntervalEffector().intervalChange();
                        unlock();
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
                        + " a null NumberBuffRunnable.");
            return Integer.compare(pointer.pointer(), r.pointer.pointer());
        }
        protected LinkedPointer pointer;
        private boolean isInStartingTime;
        private int startingTimeAlready;
        private int alreadyTime = 0;
        private int already = 0;
    }

    private final class LTRunnable
            implements Comparable<LTRunnable>, Runnable {

        public LTRunnable(LinkedPointer pointer) {
            this.pointer = pointer;
        }

        @Override
        public void run() {
            LongTimeEffect<Double> effect
                    = (LongTimeEffect<Double>) effects.get(pointer);
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
                        ((IntervalLongTimeEffect<Double>) effect)
                                .getIntervalEffector().intervalChange();
                }
            }
        }

        @Override
        public int compareTo(LTRunnable r) {
            if (r == null)
                throw new NullPointerException("Compare with"
                        + " a null NumberLongTimeRunnable.");
            return Integer.compare(pointer.pointer(), r.pointer.pointer());
        }
        private boolean isInStartingTime;
        private int startingTimeAlready;
        protected LinkedPointer pointer;
        private int already = 0;
    }
}
