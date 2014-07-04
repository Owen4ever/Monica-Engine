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

public abstract class IntegerLongTimeEffect extends AbstractIntegerEffect {

    protected IntegerLongTimeEffect(PropertyID affectTo,
            int val, int startingTime, int intervalDuration, boolean isInterval) {
        super(EffectType.TYPE_INTEGER_BUFF, affectTo);
        if (startingTime < 0)
            throw new EffectInitializeException("The starting time of the effect"
                    + "is less than 0.");
        this.val = val;
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

    public final int getintervalDuration() {
        return intervalDuration;
    }
    protected final int val;
    protected final boolean isInterval;
    protected final int intervalDuration;

    public static IntegerLongTimeEffect newLongTimeEffect(PropertyID affectTo,
            IntegerEffectCalcType calcType, int val, int startingTime,
            int intervalDuration, boolean isInterval) {
        if (calcType == null)
            throw new ErrorTypeException("Error calculation type.");
        int i;
        switch (calcType) {
            case ADD_NUM:
                return new LongTimeEffect_ADD_NUM(affectTo, val, startingTime,
                        intervalDuration, isInterval);
            case ADD_PER:
                i = OMath.isPowerOfTwo((100 + val) / 100);
                if (i != -1)
                    return new LongTimeEffect_ADD_PER_B(affectTo, i, startingTime,
                            intervalDuration, isInterval);
                i = OMath.isPowerOfTwo(100 + val);
                if (i != -1)
                    return new LongTimeEffect_ADD_PER_A(affectTo, i, startingTime,
                            intervalDuration, isInterval);
                return new LongTimeEffect_ADD_PER(affectTo, val, startingTime,
                        intervalDuration, isInterval);
            case SUB_NUM:
                return new LongTimeEffect_SUB_NUM(affectTo, val, startingTime,
                        intervalDuration, isInterval);
            case SUB_PER:
                i = OMath.isPowerOfTwo(100 - val);
                if (i != -1)
                    return new LongTimeEffect_SUB_PER(affectTo, val, startingTime,
                            intervalDuration, isInterval);
            case MUL_NUM:
                i = OMath.isPowerOfTwo(val);
                if (i != -1)
                    return new LongTimeEffect_MUL_NUM_A(affectTo, val, startingTime,
                            intervalDuration, isInterval);
                return new LongTimeEffect_MUL_NUM(affectTo, val, startingTime,
                        intervalDuration, isInterval);
            case MUL_PER:
                i = OMath.isPowerOfTwo(val / 100);
                if (i != -1)
                    return new LongTimeEffect_MUL_PER_B(affectTo, val, startingTime,
                            intervalDuration, isInterval);
                i = OMath.isPowerOfTwo(val);
                if (i != -1)
                    return new LongTimeEffect_MUL_PER_A(affectTo, val, startingTime,
                            intervalDuration, isInterval);
                return new LongTimeEffect_MUL_PER(affectTo, val, startingTime,
                        intervalDuration, isInterval);
            case DIV_NUM:
                i = OMath.isPowerOfTwo(val);
                if (i != -1)
                    return new LongTimeEffect_DIV_NUM_A(affectTo, val, startingTime,
                            intervalDuration, isInterval);
                return new LongTimeEffect_DIV_NUM(affectTo, val, startingTime,
                        intervalDuration, isInterval);
            case DIV_PER:
                i = OMath.isPowerOfTwo(val * 100);
                if (i != -1)
                    return new LongTimeEffect_DIV_PER_B(affectTo, val, startingTime,
                            intervalDuration, isInterval);
                i = OMath.isPowerOfTwo(val);
                if (i != -1)
                    return new LongTimeEffect_DIV_PER_A(affectTo, val, startingTime,
                            intervalDuration, isInterval);
                return new LongTimeEffect_DIV_PER(affectTo, val, startingTime,
                        intervalDuration, isInterval);
            default:
                throw new ErrorTypeException("Error calculation type.");
        }
    }

    private static final class LongTimeEffect_ADD_NUM extends IntegerLongTimeEffect {

        private LongTimeEffect_ADD_NUM(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value + val;
        }
    }

    private static final class LongTimeEffect_ADD_PER extends IntegerLongTimeEffect {

        private LongTimeEffect_ADD_PER(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value * (100 + val) / 100;
        }
    }

    private static final class LongTimeEffect_ADD_PER_A extends IntegerLongTimeEffect {

        private LongTimeEffect_ADD_PER_A(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return (value << val) / 100;
        }
    }

    private static final class LongTimeEffect_ADD_PER_B extends IntegerLongTimeEffect {

        private LongTimeEffect_ADD_PER_B(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value << val;
        }
    }

    private static final class LongTimeEffect_SUB_NUM extends IntegerLongTimeEffect {

        private LongTimeEffect_SUB_NUM(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value - val;
        }
    }

    private static final class LongTimeEffect_SUB_PER extends IntegerLongTimeEffect {

        private LongTimeEffect_SUB_PER(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value * (100 - val);
        }
    }

    private static final class LongTimeEffect_SUB_PER_A extends IntegerLongTimeEffect {

        private LongTimeEffect_SUB_PER_A(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value << val;
        }
    }

    private static final class LongTimeEffect_MUL_NUM extends IntegerLongTimeEffect {

        private LongTimeEffect_MUL_NUM(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value * val;
        }
    }

    private static final class LongTimeEffect_MUL_NUM_A extends IntegerLongTimeEffect {

        private LongTimeEffect_MUL_NUM_A(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value << val;
        }
    }

    private static final class LongTimeEffect_MUL_PER extends IntegerLongTimeEffect {

        private LongTimeEffect_MUL_PER(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value * val / 100;
        }
    }

    private static final class LongTimeEffect_MUL_PER_A extends IntegerLongTimeEffect {

        private LongTimeEffect_MUL_PER_A(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return (value << val) / 100;
        }
    }

    private static final class LongTimeEffect_MUL_PER_B extends IntegerLongTimeEffect {

        private LongTimeEffect_MUL_PER_B(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value << val;
        }
    }

    private static final class LongTimeEffect_DIV_NUM extends IntegerLongTimeEffect {

        private LongTimeEffect_DIV_NUM(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value / val;
        }
    }

    private static final class LongTimeEffect_DIV_NUM_A extends IntegerLongTimeEffect {

        private LongTimeEffect_DIV_NUM_A(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value >> val;
        }
    }

    private static final class LongTimeEffect_DIV_PER extends IntegerLongTimeEffect {

        private LongTimeEffect_DIV_PER(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value / val / 100;
        }
    }

    private static final class LongTimeEffect_DIV_PER_A extends IntegerLongTimeEffect {

        private LongTimeEffect_DIV_PER_A(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return (value >> val) / 100;
        }
    }

    private static final class LongTimeEffect_DIV_PER_B extends IntegerLongTimeEffect {

        private LongTimeEffect_DIV_PER_B(PropertyID affectTo, int val,
                int startingTime, int intervalDuration, boolean isInterval) {
            super(affectTo, val, startingTime, intervalDuration, isInterval);
        }

        @Override
        public int affect(int value) {
            return value >> val;
        }
    }
}
