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

package engine.monica.core.property.bool;

import engine.monica.core.property.AbstractBuffEffect;
import engine.monica.core.property.EffectType;
import engine.monica.core.property.EffectorInterface;
import engine.monica.core.property.IntervalEffectorInterface;
import engine.monica.core.property.PropertyID;
import engine.monica.util.StringID;

public class BoolBuffEffect extends AbstractBuffEffect<Boolean> {

    protected BoolBuffEffect(StringID id, PropertyID affectTo,
            EffectorInterface<Boolean> effector,
            int startingTime, int duration) {
        super(id, EffectType.TYPE_BOOL_BUFF, affectTo, effector,
                startingTime, 0, duration, false);
    }

    @Override
    public BoolBuffEffect clone() {
        return new BoolBuffEffect(id, affectTo, getEffector(),
                startingTime, duration);
    }

    public static BoolBuffEffect newBuffEffect(StringID id,
            PropertyID affectTo, EffectorInterface<Boolean> effector,
            int startingTime, int duration) {
        return new BoolBuffEffect(id, affectTo, effector,
                startingTime, duration);
    }

    public static AbstractBuffEffect<Boolean> newBuffEffect(StringID id,
            PropertyID affectTo, EffectorInterface<Boolean> effector,
            int startingTime, int intervalDuration, int duration,
            boolean isInterval) {
        if (isInterval)
            return new BoolIntervalBuffEffect(id, affectTo,
                    (IntervalEffectorInterface<Boolean>) effector,
                    startingTime, intervalDuration, duration);
        else
            return new BoolBuffEffect(id, affectTo, effector,
                    startingTime, duration);
    }
}
