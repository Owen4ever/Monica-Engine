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

import game.engine.monica.core.property.AbstractBuffEffect;
import game.engine.monica.core.property.EffectType;
import game.engine.monica.core.property.EffectorInterface;
import game.engine.monica.core.property.IntervalEffectorInterface;
import game.engine.monica.core.property.PropertyID;
import game.engine.monica.util.StringID;

public class NumberDebuffEffect extends AbstractBuffEffect<Double> {

    protected NumberDebuffEffect(StringID id, PropertyID affectTo,
            EffectorInterface<Double> effector, int startingTime,
            int duration) {
        super(id, EffectType.TYPE_NUM_DEBUFF, affectTo, effector,
                startingTime, 0, duration, false);
    }

    @Override
    public NumberDebuffEffect clone() {
        return new NumberDebuffEffect(id, affectTo, effector,
                startingTime, duration);
    }

    public static NumberBuffEffect newBuffEffect(StringID id,
            PropertyID affectTo, EffectorInterface<Double> effector,
            int startingTime, int duration) {
        return new NumberBuffEffect(id, affectTo, effector,
                startingTime, duration);
    }

    public static AbstractBuffEffect<Double> newBuffEffect(StringID id,
            PropertyID affectTo, EffectorInterface<Double> effector,
            int startingTime, int intervalDuration, int duration,
            boolean isInterval) {
        if (isInterval)
            return new NumberIntervalBuffEffect(id, affectTo,
                    (IntervalEffectorInterface<Double>) effector,
                    startingTime, intervalDuration, duration);
        else
            return new NumberBuffEffect(id, affectTo, effector,
                    startingTime, duration);
    }

    public static NumberIntervalBuffEffect newIntervalBuffEffect(StringID id,
            PropertyID affectTo, IntervalEffectorInterface<Double> effector,
            int startingTime, int intervalDuration, int duration) {
        return new NumberIntervalBuffEffect(id, affectTo, effector,
                startingTime, intervalDuration, duration);
    }
}
