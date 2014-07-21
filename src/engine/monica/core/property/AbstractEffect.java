/*
 * Copyright (C) 2014 Owen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package engine.monica.core.property;

import engine.monica.util.StringID;

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
