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

public class NumberLongTimeEffect extends AbstractNumberEffect {

    protected NumberLongTimeEffect(StringID id, PropertyID affectTo,
            EffectorInterface<Double> effector, double val, int startingTime,
            int intervalDuration, boolean isInterval) {
        super(id, EffectType.TYPE_NUM_LONGTIME, affectTo, effector, val);
        if (startingTime < 0)
            throw new EffectInitializeException("The starting time of the effect"
                    + " is less than 0.");
        this.isInterval = isInterval;
        this.startingTime = startingTime;
        if (this.isInterval) {
            if (intervalDuration <= 0)
                throw new EffectInitializeException("The duration of the effect"
                        + " is less than 1 milliseconds (0.001 seconds).");
            this.intervalDuration = intervalDuration;
        } else
            this.intervalDuration = 0;
    }

    public final boolean isInterval() {
        return isInterval;
    }

    public final int getIntervalDuration() {
        return intervalDuration;
    }

    @Override
    public NumberLongTimeEffect clone() {
        return new NumberLongTimeEffect(id, affectTo, effector,
                val, startingTime, intervalDuration, isInterval);
    }
    protected final int startingTime;
    protected final boolean isInterval;
    protected final int intervalDuration;

    public static NumberLongTimeEffect newLongTimeEffect(StringID id,
            PropertyID affectTo, NumberEffectCalcType calcType, double val,
            int startingTime) {
        return newLongTimeEffect(id, affectTo, calcType, null, val,
                startingTime, 0, false);
    }

    public static NumberLongTimeEffect newLongTimeEffect(StringID id,
            PropertyID affectTo, NumberEffectCalcType calcType,
            EffectorInterface<Double> effector, double val, int startingTime,
            int intervalDuration, boolean isInterval) {
        if (isInterval)
            return new NumberIntervalLongTimeEffect(id, affectTo,
                    (IntervalEffectorInterface<Double>) effector, val,
                    startingTime, intervalDuration);
        else
            return new NumberLongTimeEffect(id, affectTo,
                    getNumberEffector(calcType, val), val,
                    startingTime, intervalDuration, isInterval);
    }
}
