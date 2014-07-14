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

package game.engine.monica.core.property;

import game.engine.monica.util.StringID;

public abstract class AbstractBuffEffect<T> extends AbstractEffect<T> {

    protected AbstractBuffEffect(StringID id, EffectType type,
            PropertyID affectTo, EffectorInterface<T> effector,
            int startingTime, int intervalDuration, int duration,
            boolean isInterval) {
        super(id, type, affectTo, effector);
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
    protected final int startingTime;
    protected final int intervalDuration;
    protected final int duration;
    protected final boolean isInterval;
}
