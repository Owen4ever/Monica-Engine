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

import game.engine.monica.core.property.AbstractIntervalBuffEffect;
import game.engine.monica.core.property.EffectType;
import game.engine.monica.core.property.IntervalEffectorInterface;
import game.engine.monica.core.property.PropertyID;
import game.engine.monica.util.StringID;

public class BoolIntervalBuffEffect
        extends AbstractIntervalBuffEffect<Boolean> {

    protected BoolIntervalBuffEffect(StringID id, PropertyID affectTo,
            IntervalEffectorInterface<Boolean> effector,
            int startingTime, int intervalDuration, int duration) {
        super(id, EffectType.TYPE_BOOL_BUFF_INTERVAL, affectTo,
                effector, startingTime, intervalDuration, duration);
    }

    @Override
    public BoolIntervalBuffEffect clone() {
        return new BoolIntervalBuffEffect(id, affectTo, getIntervalEffector(),
                startingTime, intervalDuration, duration);
    }

}
