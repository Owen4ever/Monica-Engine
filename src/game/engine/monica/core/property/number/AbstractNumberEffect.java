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

import game.engine.monica.core.property.AbstractEffect;
import game.engine.monica.core.property.EffectType;
import game.engine.monica.core.property.EffectorInterface;
import game.engine.monica.core.property.ErrorTypeException;
import game.engine.monica.core.property.PropertyID;
import game.engine.monica.util.StringID;

public abstract class AbstractNumberEffect extends AbstractEffect<Double>
        implements Cloneable {

    protected AbstractNumberEffect(StringID id, EffectType type,
            PropertyID affectTo, EffectorInterface<Double> effector,
            double val) {
        super(id, type, affectTo, effector);
    }

    @Override
    public Double getValue() {
        return val;
    }

    public void setValue(double val) {
        this.val = val;
    }
    protected double val;

    @Override
    public abstract AbstractNumberEffect clone();

    public static EffectorInterface<Double> getNumberEffector(NumberEffectCalcType calcType, final double val) {
        if (calcType == null)
            throw new ErrorTypeException("Error calculation type.");
        switch (calcType) {
            case ADD_NUM:
                return v -> v + val;
            case ADD_PER:
                return v -> v * (1d + val);
            case SUB_NUM:
                return v -> v - val;
            case SUB_PER:
                return v -> v * (1d - val);
            case MUL_NUM:
                return v -> v * val;
            case MUL_PER:
                return v -> v * val / 100d;
            case DIV_NUM:
                return v -> v / val;
            case DIV_PER:
                return v -> v / (val * 100d);
            default:
                throw new ErrorTypeException("Error calculation type.");
        }
    }
}
