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

import game.engine.monica.core.property.AbstractBuffEffect;
import game.engine.monica.core.property.EffectType;
import game.engine.monica.core.property.EffectorInterface;
import game.engine.monica.core.property.IntervalEffectorInterface;
import game.engine.monica.core.property.PropertyID;
import game.engine.monica.util.StringID;

public class BoolBuffEffect extends AbstractBuffEffect<Boolean> {

    protected BoolBuffEffect(StringID id, PropertyID affectTo,
            EffectorInterface<Boolean> effector,
            int startingTime, int duration) {
        super(id, EffectType.TYPE_BOOL_BUFF, affectTo, effector,
                startingTime, 0, duration, false);
    }

    @Override
    public BoolBuffEffect clone() {
        return new BoolBuffEffect(id, affectTo, getEffector(),
                startingTime, duration);
    }

    public static BoolBuffEffect newBuffEffect(StringID id,
            PropertyID affectTo, EffectorInterface<Boolean> effector,
            int startingTime, int duration) {
        return new BoolBuffEffect(id, affectTo, effector,
                startingTime, duration);
    }

    public static AbstractBuffEffect<Boolean> newBuffEffect(StringID id,
            PropertyID affectTo, EffectorInterface<Boolean> effector,
            int startingTime, int intervalDuration, int duration,
            boolean isInterval) {
        if (isInterval)
            return new BoolIntervalBuffEffect(id, affectTo,
                    (IntervalEffectorInterface<Boolean>) effector,
                    startingTime, intervalDuration, duration);
        else
            return new BoolBuffEffect(id, affectTo, effector,
                    startingTime, duration);
    }
}
