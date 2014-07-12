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

import game.engine.monica.core.property.EffectInitializeException;
import game.engine.monica.core.property.EffectType;
import game.engine.monica.core.property.EffectorInterface;
import game.engine.monica.core.property.IntervalEffectorInterface;
import game.engine.monica.core.property.PropertyID;
import game.engine.monica.util.StringID;

public class NumberBuffEffect extends AbstractNumberEffect {

    protected NumberBuffEffect(StringID id, PropertyID affectTo,
            EffectorInterface<Double> effector,
            double val, int startingTime,
            int intervalDuration, int duration, boolean isInterval) {
        super(id, EffectType.TYPE_NUM_BUFF, affectTo, effector, val);
        if (duration < 100)
            throw new EffectInitializeException("The duration of the effect"
                    + "is less than 100 milliseconds (0.001 seconds).");
        if (startingTime < 0 || startingTime > duration)
            throw new EffectInitializeException("The starting time of the effect"
                    + "is less than 0 or is greater than max duration.");
        this.duration = duration;
        this.startingTime = startingTime;
        this.isInterval = isInterval;
        if (this.isInterval) {
            if (intervalDuration <= 0)
                throw new EffectInitializeException("The duration of the effect"
                        + "is less than 1 milliseconds (0.001 seconds).");
            this.intervalDuration = intervalDuration;
        } else
            this.intervalDuration = 0;
    }

    public final int getStartingTime() {
        return startingTime;
    }

    public final boolean isInterval() {
        return isInterval;
    }

    public final int getIntervalDuration() {
        return intervalDuration;
    }

    public final int getMaxDuration() {
        return duration;
    }

    @Override
    public NumberBuffEffect clone() {
        return new NumberBuffEffect(id, affectTo, effector, val,
                startingTime, intervalDuration, duration, isInterval);
    }
    protected final int startingTime;
    protected final int duration;
    protected final boolean isInterval;
    protected final int intervalDuration;

    public static NumberBuffEffect newBuffEffect(StringID id,
            PropertyID affectTo, NumberEffectCalcType calcType, double val,
            int startingTime, int duration) {
        return newBuffEffect(id, affectTo, calcType, null, val, startingTime,
                0, duration, false);
    }

    public static NumberBuffEffect newBuffEffect(StringID id,
            PropertyID affectTo, NumberEffectCalcType calcType,
            IntervalEffectorInterface<Double> effector, double val,
            int startingTime, int intervalDuration, int duration,
            boolean isInterval) {
        if (isInterval)
            return new NumberIntervalBuffEffect(id, affectTo, effector, val,
                    startingTime, intervalDuration, duration);
        else
            return new NumberBuffEffect(id, affectTo,
                    getNumberEffector(calcType, val), val,
                    startingTime, intervalDuration, duration, isInterval);
    }

    public static NumberIntervalBuffEffect newIntervalBuffEffect(StringID id,
            PropertyID affectTo, IntervalEffectorInterface<Double> effector,
            int startingTime, int intervalDuration, int duration) {
        return (NumberIntervalBuffEffect) newBuffEffect(id, affectTo, null,
                effector, 0, startingTime, intervalDuration, duration, true);
    }
}
