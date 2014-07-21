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

import engine.monica.core.property.AbstractLongTimeEffect;
import engine.monica.core.property.EffectType;
import engine.monica.core.property.EffectorInterface;
import engine.monica.core.property.IntervalEffectorInterface;
import engine.monica.core.property.PropertyID;
import engine.monica.util.StringID;

public class NumberLongTimeEffect extends AbstractLongTimeEffect<Double> {

    protected NumberLongTimeEffect(StringID id, PropertyID affectTo,
            EffectorInterface<Double> effector, int startingTime) {
        super(id, EffectType.TYPE_NUM_LONGTIME, affectTo, effector,
                startingTime, 0, false);
    }

    @Override
    public NumberLongTimeEffect clone() {
        return new NumberLongTimeEffect(id, affectTo, effector, startingTime);
    }

    public static NumberLongTimeEffect newLongTimeEffect(StringID id,
            PropertyID affectTo, EffectorInterface<Double> effector,
            int startingTime) {
        return new NumberLongTimeEffect(id, affectTo, effector,
                startingTime);
    }

    public static AbstractLongTimeEffect<Double> newLongTimeEffect(StringID id,
            PropertyID affectTo, EffectorInterface<Double> effector,
            int startingTime, int intervalDuration, boolean isInterval) {
        if (isInterval)
            return new NumberIntervalLongTimeEffect(id, affectTo,
                    (IntervalEffectorInterface<Double>) effector,
                    startingTime, intervalDuration);
        else
            return newLongTimeEffect(id, affectTo, effector, startingTime);
    }
}
