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

package game.engine.monica.core.property.integer;

import game.engine.monica.core.property.EffectInitializeException;
import game.engine.monica.core.property.EffectType;
import game.engine.monica.core.property.ErrorTypeException;
import game.engine.monica.core.property.PropertyID;
import game.engine.monica.util.OMath;

public abstract class IntegerBuffEffect extends AbstractIntegerEffect {

    protected IntegerBuffEffect(PropertyID affectTo,
            int val, int startingTime,
            int intervalDuration, int duration, boolean isInterval) {
        super(EffectType.TYPE_INTEGER_BUFF, affectTo);
        if (duration < 100)
            throw new EffectInitializeException("The duration of the effect"
                    + "is less than 100 milliseconds (0.001 seconds).");
        if (startingTime < 0 || startingTime > duration)
            throw new EffectInitializeException("The starting time of the effect"
                    + "is less than 0 or is greater than max duration.");
        this.val = val;
        this.duration = duration;
        this.isInterval = isInterval;
        if (this.isInterval) {
            if (intervalDuration <= 0)
                throw new EffectInitializeException("The duration of the effect"
                        + "is less than 1 milliseconds (0.001 seconds).");
            this.intervalDuration = intervalDuration;
        } else
            this.intervalDuration = 0;
    }

    public final int getValue() {
        return val;
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
    protected final int val;
    protected final int duration;
    protected final boolean isInterval;
    protected final int intervalDuration;

    public static IntegerBuffEffect newBuffEffect(PropertyID affectTo,
            IntegerEffectCalcType calcType, int val, int startingTime,
            int duration) {
        return newBuffEffect(affectTo, calcType, val, startingTime, 0, duration, false);
    }

    public static IntegerBuffEffect newBuffEffect(PropertyID affectTo,
            IntegerEffectCalcType calcType, int val, int startingTime,
            int intervalDuration, int duration, boolean isInterval) {
        if (calcType == null)
            throw new ErrorTypeException("Error calculation type.");
        int i;
        switch (calcType) {
            case ADD_NUM:
                return new BuffEffect_ADD_NUM(affectTo, val, startingTime,
                        intervalDuration, duration, isInterval);
            case ADD_PER:
                i = OMath.isPowerOfTwo((100 + val) / 100);
                if (i != -1)
                    return new BuffEffect_ADD_PER_B(affectTo, i, startingTime,
                            intervalDuration, duration, isInterval);
                i = OMath.isPowerOfTwo(100 + val);
                if (i != -1)
                    return new BuffEffect_ADD_PER_A(affectTo, i, startingTime,
                            intervalDuration, duration, isInterval);
                return new BuffEffect_ADD_PER(affectTo, val, startingTime,
                        intervalDuration, duration, isInterval);
            case SUB_NUM:
                return new BuffEffect_SUB_NUM(affectTo, val, startingTime,
                        intervalDuration, duration, isInterval);
            case SUB_PER:
                i = OMath.isPowerOfTwo(100 - val);
                if (i != -1)
                    return new BuffEffect_SUB_PER(affectTo, val, startingTime,
                            intervalDuration, duration, isInterval);
            case MUL_NUM:
                i = OMath.isPowerOfTwo(val);
                if (i != -1)
                    return new BuffEffect_MUL_NUM_A(affectTo, val, startingTime,
                            intervalDuration, duration, isInterval);
                return new BuffEffect_MUL_NUM(affectTo, val, startingTime,
                        intervalDuration, duration, isInterval);
            case MUL_PER:
                i = OMath.isPowerOfTwo(val / 100);
                if (i != -1)
                    return new BuffEffect_MUL_PER_B(affectTo, val, startingTime,
                            intervalDuration, duration, isInterval);
                i = OMath.isPowerOfTwo(val);
                if (i != -1)
                    return new BuffEffect_MUL_PER_A(affectTo, val, startingTime,
                            intervalDuration, duration, isInterval);
                return new BuffEffect_MUL_PER(affectTo, val, startingTime,
                        intervalDuration, duration, isInterval);
            case DIV_NUM:
                i = OMath.isPowerOfTwo(val);
                if (i != -1)
                    return new BuffEffect_DIV_NUM_A(affectTo, val, startingTime,
                            intervalDuration, duration, isInterval);
                return new BuffEffect_DIV_NUM(affectTo, val, startingTime,
                        intervalDuration, duration, isInterval);
            case DIV_PER:
                i = OMath.isPowerOfTwo(val * 100);
                if (i != -1)
                    return new BuffEffect_DIV_PER_B(affectTo, val, startingTime,
                            intervalDuration, duration, isInterval);
                i = OMath.isPowerOfTwo(val);
                if (i != -1)
                    return new BuffEffect_DIV_PER_A(affectTo, val, startingTime,
                            intervalDuration, duration, isInterval);
                return new BuffEffect_DIV_PER(affectTo, val, startingTime,
                        intervalDuration, duration, isInterval);
            default:
                throw new ErrorTypeException("Error calculation type.");
        }
    }

    private static final class BuffEffect_ADD_NUM extends IntegerBuffEffect {

        private BuffEffect_ADD_NUM(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value + val;
        }
    }

    private static final class BuffEffect_ADD_PER extends IntegerBuffEffect {

        private BuffEffect_ADD_PER(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value * (100 + val) / 100;
        }
    }

    private static final class BuffEffect_ADD_PER_A extends IntegerBuffEffect {

        private BuffEffect_ADD_PER_A(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return (value << val) / 100;
        }
    }

    private static final class BuffEffect_ADD_PER_B extends IntegerBuffEffect {

        private BuffEffect_ADD_PER_B(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value << val;
        }
    }

    private static final class BuffEffect_SUB_NUM extends IntegerBuffEffect {

        private BuffEffect_SUB_NUM(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value - val;
        }
    }

    private static final class BuffEffect_SUB_PER extends IntegerBuffEffect {

        private BuffEffect_SUB_PER(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value * (100 - val);
        }
    }

    private static final class BuffEffect_SUB_PER_A extends IntegerBuffEffect {

        private BuffEffect_SUB_PER_A(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value << val;
        }
    }

    private static final class BuffEffect_MUL_NUM extends IntegerBuffEffect {

        private BuffEffect_MUL_NUM(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value * val;
        }
    }

    private static final class BuffEffect_MUL_NUM_A extends IntegerBuffEffect {

        private BuffEffect_MUL_NUM_A(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value << val;
        }
    }

    private static final class BuffEffect_MUL_PER extends IntegerBuffEffect {

        private BuffEffect_MUL_PER(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value * val / 100;
        }
    }

    private static final class BuffEffect_MUL_PER_A extends IntegerBuffEffect {

        private BuffEffect_MUL_PER_A(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return (value << val) / 100;
        }
    }

    private static final class BuffEffect_MUL_PER_B extends IntegerBuffEffect {

        private BuffEffect_MUL_PER_B(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value << val;
        }
    }

    private static final class BuffEffect_DIV_NUM extends IntegerBuffEffect {

        private BuffEffect_DIV_NUM(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value / val;
        }
    }

    private static final class BuffEffect_DIV_NUM_A extends IntegerBuffEffect {

        private BuffEffect_DIV_NUM_A(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value >> val;
        }
    }

    private static final class BuffEffect_DIV_PER extends IntegerBuffEffect {

        private BuffEffect_DIV_PER(PropertyID affectTo, int val, int startingTime, int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value / val / 100;
        }
    }

    private static final class BuffEffect_DIV_PER_A extends IntegerBuffEffect {

        private BuffEffect_DIV_PER_A(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return (value >> val) / 100;
        }
    }

    private static final class BuffEffect_DIV_PER_B extends IntegerBuffEffect {

        private BuffEffect_DIV_PER_B(PropertyID affectTo, int val, int startingTime,
                int intervalDuration, int duration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, duration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value >> val;
        }
    }
}
