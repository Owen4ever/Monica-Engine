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

import engine.monica.core.engine.CoreEngine;
import engine.monica.core.property.PropertyID;
import engine.monica.util.AlreadyExistsInContainerException;
import static engine.monica.util.Lock.RET;
import static engine.monica.util.Lock.getWriteLock;
import engine.monica.util.RegesteredIDException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class EffectFactory {

    public EffectFactory() {
    }

    @SuppressWarnings("unchecked")
    public <T> EffectCreator<T> getCreator(Class<T> c) {
        if (c == null)
            throw new NullPointerException("The class is null.");
        EffectCreator<T> creator = (EffectCreator<T>) creators.get(c);
        if (creator == null) {
            creator = new DefaultEffectCreator<>();
            creators.put(c, creator);
        }
        return creator;
    }

    public <T> DefaultEffectCreator<T> getDefault(Class<T> c) {
        EffectCreator<T> creator = getCreator(c);
        if (creator instanceof DefaultEffectCreator)
            return (DefaultEffectCreator<T>) creator;
        return new DefaultEffectCreator<>();
    }

    public <T> EffectCreator<T> addCreator(Class<T> c, EffectCreator<T> creator) {
        if (c == null)
            throw new NullPointerException("The class is null.");
        if (creator == null)
            throw new NullPointerException("The creator is null.");
        if (creators.containsKey(c))
            throw new AlreadyExistsInContainerException("The creator exists in the map.");
        creators.put(c, creator);
        return creator;
    }

    public class DefaultEffectCreator<T> implements EffectCreator<T> {

        protected DefaultEffectCreator() {
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

        public AbstractEffect<T> newLongTimeEffect(String id, PropertyID affectTo,
                Effector<T> effector, int beginningTime) {
            checkID(id);
            return new LongTimeEffect<>(id, affectTo, effector, beginningTime);
        }

        public AbstractEffect<T> newIntervalLongTimeEffect(String id, PropertyID affectTo,
                IntervalEffector<T> effector, int beginningTime,
                int intervalDuration) {
            checkID(id);
            return new IntervalLongTimeEffect<>(id, affectTo, effector,
                    beginningTime, intervalDuration);
        }
    }

    public void checkID(String id) {
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
