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

import game.engine.monica.core.property.EffectType;
import game.engine.monica.core.property.ErrorTypeException;
import game.engine.monica.core.property.PropertyID;
import game.engine.monica.util.OMath;

public abstract class NumberEffect extends AbstractIntegerEffect {

    protected NumberEffect(PropertyID affectTo, int val) {
        super(EffectType.TYPE_INTEGER_NUM, affectTo);
        this.val = val;
    }

    public final int getValue() {
        return val;
    }
    protected final int val;

    public static NumberEffect newNumberEffect(PropertyID affectTo, IntegerEffectCalcType calcType, int val) {
        if (calcType == null)
            throw new ErrorTypeException("Error calculation type.");
        int i;
        switch (calcType) {
            case ADD_NUM:
                return new NumberEffect_ADD_NUM(affectTo, val);
            case ADD_PER:
                i = OMath.isPowerOfTwo((100 + val) / 100);
                if (i != -1)
                    return new NumberEffect_ADD_PER_B(affectTo, i);
                i = OMath.isPowerOfTwo(100 + val);
                if (i != -1)
                    return new NumberEffect_ADD_PER_A(affectTo, i);
                return new NumberEffect_ADD_PER(affectTo, val);
            case SUB_NUM:
                return new NumberEffect_SUB_NUM(affectTo, val);
            case SUB_PER:
                i = OMath.isPowerOfTwo(100 - val);
                if (i != -1)
                    return new NumberEffect_SUB_PER(affectTo, val);
            case MUL_NUM:
                i = OMath.isPowerOfTwo(val);
                if (i != -1)
                    return new NumberEffect_MUL_NUM_A(affectTo, val);
                return new NumberEffect_MUL_NUM(affectTo, val);
            case MUL_PER:
                i = OMath.isPowerOfTwo(val / 100);
                if (i != -1)
                    return new NumberEffect_MUL_PER_B(affectTo, val);
                i = OMath.isPowerOfTwo(val);
                if (i != -1)
                    return new NumberEffect_MUL_PER_A(affectTo, val);
                return new NumberEffect_MUL_PER(affectTo, val);
            case DIV_NUM:
                i = OMath.isPowerOfTwo(val);
                if (i != -1)
                    return new NumberEffect_DIV_NUM_A(affectTo, val);
                return new NumberEffect_DIV_NUM(affectTo, val);
            case DIV_PER:
                i = OMath.isPowerOfTwo(val * 100);
                if (i != -1)
                    return new NumberEffect_DIV_PER_B(affectTo, val);
                i = OMath.isPowerOfTwo(val);
                if (i != -1)
                    return new NumberEffect_DIV_PER_A(affectTo, val);
                return new NumberEffect_DIV_PER(affectTo, val);
            default:
                throw new ErrorTypeException("Error calculation type.");
        }
    }

    private static final class NumberEffect_ADD_NUM extends NumberEffect {

        private NumberEffect_ADD_NUM(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return value + val;
        }
    }

    private static final class NumberEffect_ADD_PER extends NumberEffect {

        private NumberEffect_ADD_PER(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return value * (100 + val) / 100;
        }
    }

    private static final class NumberEffect_ADD_PER_A extends NumberEffect {

        private NumberEffect_ADD_PER_A(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return (value << val) / 100;
        }
    }

    private static final class NumberEffect_ADD_PER_B extends NumberEffect {

        private NumberEffect_ADD_PER_B(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return value << val;
        }
    }

    private static final class NumberEffect_SUB_NUM extends NumberEffect {

        private NumberEffect_SUB_NUM(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return value - val;
        }
    }

    private static final class NumberEffect_SUB_PER extends NumberEffect {

        private NumberEffect_SUB_PER(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return value * (100 - val);
        }
    }

    private static final class NumberEffect_SUB_PER_A extends NumberEffect {

        private NumberEffect_SUB_PER_A(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return value << val;
        }
    }

    private static final class NumberEffect_MUL_NUM extends NumberEffect {

        private NumberEffect_MUL_NUM(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return value * val;
        }
    }

    private static final class NumberEffect_MUL_NUM_A extends NumberEffect {

        private NumberEffect_MUL_NUM_A(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return value << val;
        }
    }

    private static final class NumberEffect_MUL_PER extends NumberEffect {

        private NumberEffect_MUL_PER(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return value * val / 100;
        }
    }

    private static final class NumberEffect_MUL_PER_A extends NumberEffect {

        private NumberEffect_MUL_PER_A(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return (value << val) / 100;
        }
    }

    private static final class NumberEffect_MUL_PER_B extends NumberEffect {

        private NumberEffect_MUL_PER_B(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return value << val;
        }
    }

    private static final class NumberEffect_DIV_NUM extends NumberEffect {

        private NumberEffect_DIV_NUM(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return value / val;
        }
    }

    private static final class NumberEffect_DIV_NUM_A extends NumberEffect {

        private NumberEffect_DIV_NUM_A(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return value >> val;
        }
    }

    private static final class NumberEffect_DIV_PER extends NumberEffect {

        private NumberEffect_DIV_PER(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return value / val / 100;
        }
    }

    private static final class NumberEffect_DIV_PER_A extends NumberEffect {

        private NumberEffect_DIV_PER_A(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return (value >> val) / 100;
        }
    }

    private static final class NumberEffect_DIV_PER_B extends NumberEffect {

        private NumberEffect_DIV_PER_B(PropertyID affectTo, int val) {
            super(affectTo, val);
        }

        @Override
        public int affect(int value) {
            return value >> val;
        }
    }
}
