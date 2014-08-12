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

import engine.monica.core.engine.CoreEngine;
import static engine.monica.util.Lock.RET;
import static engine.monica.util.Lock.getWriteLock;
import engine.monica.util.RegesteredIDException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class EffectFactory {

    public EffectFactory() {
    }

    public <T> EffectCreator<T> getCreator(Class<T> c) {
        if (c == null)
            throw new NullPointerException("The class is null.");
        EffectCreator<T> creator = (EffectCreator<T>) creators.get(c);
        if (creator == null) {
            creator = new EffectCreator<>();
            creators.put(c, creator);
        }
        return creator;
    }

    public final class EffectCreator<T> {

        private EffectCreator() {
        }

        public AbstractEffect<T> newModifiedEffect(String id, PropertyID affectTo,
                T val) {
            checkID(id);
            return new ModifiedEffect<>(id, affectTo, val);
        }

        public AbstractEffect<T> newSimpleEffect(String id, PropertyID affectTo,
                Effector<T> effector) {
            checkID(id);
            return new SimpleEffect<>(id, affectTo, effector);
        }

        public AbstractEffect<T> newBuffEffect(String id, PropertyID affectTo,
                Effector<T> effector, int beginningTime, int duration) {
            checkID(id);
            return new BuffEffect<>(id, affectTo, effector, beginningTime, duration);
        }

        public AbstractEffect<T> newIntervalBuffEffect(String id,
                PropertyID affectTo, IntervalEffector<T> effector,
                int beginningTime, int intervalDuration, int duration) {
            checkID(id);
            return new IntervalBuffEffect<>(id, affectTo, effector,
                    beginningTime, intervalDuration, duration);
        }
    }

    private void checkID(String id) {
        getWriteLock(lock);
        RET(lock, () -> {
            if (ids.contains(id))
                throw new RegesteredIDException("Regestered ID: " + id + ".");
            ids.add(id);
        });
    }
    private final HashMap<Class<?>, EffectCreator<?>> creators
            = new HashMap<>(CoreEngine.getDefaultQuantily(), .4f);
    private final HashSet<String> ids
            = new HashSet<>(CoreEngine.getDefaultQuantily(), .4f);
    private final transient ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
}
