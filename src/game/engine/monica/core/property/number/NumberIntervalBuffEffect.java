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

import game.engine.monica.core.property.EffectorInterface;
import game.engine.monica.core.property.PropertyID;
import game.engine.monica.util.StringID;

public class NumberIntervalBuffEffect extends NumberBuffEffect {

    public NumberIntervalBuffEffect(StringID id, PropertyID affectTo,
            EffectorInterface effector, int val,
            int startingTime, int intervalDuration, int duration) {
        super(id, affectTo, effector, val,
                startingTime, intervalDuration, duration, true);
        if (startingTime == 0)
            currentVal = val;
        else
            currentVal = 0;
    }

    public void intervalBuffIncrease() {
        currentVal += val;
    }

    public int getCurrentValue() {
        return currentVal;
    }

    public void setCurrentValue(int currentVal) {
        this.currentVal = currentVal;
    }
    private int currentVal;
}
