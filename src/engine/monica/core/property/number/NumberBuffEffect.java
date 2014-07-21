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

package engine.monica.core.property.number;

import engine.monica.core.property.AbstractBuffEffect;
import engine.monica.core.property.EffectType;
import engine.monica.core.property.EffectorInterface;
import engine.monica.core.property.IntervalEffectorInterface;
import engine.monica.core.property.PropertyID;
import engine.monica.util.StringID;

public class NumberBuffEffect extends AbstractBuffEffect<Double> {

    protected NumberBuffEffect(StringID id, PropertyID affectTo,
            EffectorInterface<Double> effector,
            int startingTime, int duration) {
        super(id, EffectType.TYPE_NUM_BUFF, affectTo, effector,
                startingTime, 0, duration, false);
    }

    @Override
    public NumberBuffEffect clone() {
        return new NumberBuffEffect(id, affectTo, effector,
                startingTime, duration);
    }

    public static NumberBuffEffect newBuffEffect(StringID id,
            PropertyID affectTo, EffectorInterface<Double> effector,
            int startingTime, int duration) {
        return new NumberBuffEffect(id, affectTo, effector,
                startingTime, duration);
    }

    public static AbstractBuffEffect<Double> newBuffEffect(StringID id,
            PropertyID affectTo, EffectorInterface<Double> effector,
            int startingTime, int intervalDuration, int duration,
            boolean isInterval) {
        if (isInterval)
            return new NumberIntervalBuffEffect(id, affectTo,
                    (IntervalEffectorInterface<Double>) effector,
                    startingTime, intervalDuration, duration);
        else
            return new NumberBuffEffect(id, affectTo, effector,
                    startingTime, duration);
    }

    public static NumberIntervalBuffEffect newIntervalBuffEffect(StringID id,
            PropertyID affectTo, IntervalEffectorInterface<Double> effector,
            int startingTime, int intervalDuration, int duration) {
        return new NumberIntervalBuffEffect(id, affectTo, effector,
                startingTime, intervalDuration, duration);
    }
}
