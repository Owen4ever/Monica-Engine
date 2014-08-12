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

package engine.monica.core.property.effect;

import engine.monica.core.property.PropertyID;

public abstract class AbstractEffect<T> implements Cloneable {

    protected AbstractEffect(String id, EffectType type, PropertyID affectTo,
            Effector<T> effector) {
        if (id == null)
            throw new EffectInitializeException("The effect id is null.");
        this.id = id;
        if (type == null)
            throw new EffectInitializeException("The effect type is null.");
        this.effectType = type;
        setAffectTo(affectTo);
        setEffector(effector);
    }

    public final String getID() {
        return id;
    }

    public final EffectType getEffectType() {
        return effectType;
    }

    public final PropertyID affectTo() {
        return affectTo;
    }

    public final void setAffectTo(PropertyID affectTo) {
        if (affectTo == null)
            throw new NullPointerException("The PropertyID which affects to is null.");
        this.affectTo = affectTo;
    }

    public final Effector<T> getEffector() {
        return effector;
    }

    public final void setEffector(Effector<T> effector) {
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
    private final String id;
    private final EffectType effectType;
    private PropertyID affectTo;
    private Effector<T> effector;
}
