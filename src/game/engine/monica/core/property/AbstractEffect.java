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

public abstract class AbstractEffect<T> implements Cloneable {

    protected AbstractEffect(StringID id, EffectType type, PropertyID affectTo,
            EffectorInterface<T> effector) {
        setID(id);
        setEffectType(type);
        setAffectTo(affectTo);
        setEffector(effector);
    }

    public final StringID getID() {
        return id;
    }

    public final void setID(StringID id) {
        if (id == null)
            throw new NullPointerException("The effect id is null.");
        this.id = id;
    }

    public final EffectType getEffectType() {
        return effectType;
    }

    public final void setEffectType(EffectType type) {
        if (type == null)
            throw new NullPointerException("The effect type is null.");
        this.effectType = type;
    }

    public final PropertyID affectTo() {
        return affectTo;
    }

    public final void setAffectTo(PropertyID affectTo) {
        if (affectTo == null)
            throw new NullPointerException("The PropertyID which affects to is null.");
        this.affectTo = affectTo;
    }

    public final EffectorInterface<T> getEffector() {
        return effector;
    }

    public final void setEffector(EffectorInterface<T> effector) {
        if (effector == null)
            throw new NullPointerException("The effector is null.");
        this.effector = effector;
    }

    public final T affect(T t) {
        return effector.affect(t);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[ EffectType = " + effectType + ", "
                + affectTo.getType()
                + ", Affect to = " + affectTo.getID() + " ]";
    }
    protected StringID id;
    protected EffectType effectType;
    protected PropertyID affectTo;
    protected EffectorInterface<T> effector;
}
