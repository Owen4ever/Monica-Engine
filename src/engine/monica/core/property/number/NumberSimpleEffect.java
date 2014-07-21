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

import engine.monica.core.property.AbstractEffect;
import engine.monica.core.property.EffectType;
import engine.monica.core.property.EffectorInterface;
import engine.monica.core.property.PropertyID;
import engine.monica.util.StringID;

public class NumberSimpleEffect extends AbstractEffect<Double> {

    protected NumberSimpleEffect(StringID id, PropertyID affectTo,
            EffectorInterface<Double> effector) {
        super(id, EffectType.TYPE_NUM_SIMPLE, affectTo, effector);
    }

    @Override
    public NumberSimpleEffect clone() {
        return new NumberSimpleEffect(id, affectTo, effector);
    }

    public static NumberSimpleEffect newSimpleEffect(StringID id,
            PropertyID affectTo, EffectorInterface<Double> effector) {
        return new NumberSimpleEffect(id, affectTo, effector);
    }
}
