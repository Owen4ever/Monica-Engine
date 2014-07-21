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

public abstract class AbstractBuffEffect<T> extends AbstractEffect<T> {

    protected AbstractBuffEffect(StringID id, EffectType type,
            PropertyID affectTo, EffectorInterface<T> effector,
            int startingTime, int intervalDuration, int duration,
            boolean isInterval) {
        super(id, type, affectTo, effector);
        if (duration < 100)
            throw new EffectInitializeException("The duration of the effect"
                    + "is less than 100 milliseconds (0.001 seconds).");
        if (startingTime < 0 || startingTime > duration)
            throw new EffectInitializeException("The starting time of the effect"
                    + "is less than 0 or is greater than max duration.");
        this.duration = duration;
        this.startingTime = startingTime;
        this.isInterval = isInterval;
        if (this.isInterval) {
            if (intervalDuration <= 0)
                throw new EffectInitializeException("The duration of the effect"
                        + "is less than 1 milliseconds (0.001 seconds).");
            this.intervalDuration = intervalDuration;
        } else
            this.intervalDuration = 0;
    }

    public final int getStartingTime() {
        return startingTime;
    }

    public final boolean isInterval() {
        return isInterval;
    }

    public final int getIntervalDuration() {
        return intervalDuration;
    }

    public final int getMaxDuration() {
        return duration;
    }
    protected final int startingTime;
    protected final int intervalDuration;
    protected final int duration;
    protected final boolean isInterval;
}
