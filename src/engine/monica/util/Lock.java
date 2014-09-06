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

package engine.monica.util;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

public final class Lock {

    public static void getWriteLock(ReentrantReadWriteLock lock) {
        if (!lock.isWriteLocked() && lock.writeLock().tryLock()
                && lock.getWriteHoldCount() != 1)
            lock.writeLock().unlock();
        do {
            if (!lock.isWriteLocked() && lock.writeLock().tryLock()
                    && lock.getWriteHoldCount() != 1)
                lock.writeLock().unlock();
            else
                break;
        } while (true);
    }

    public static void getReadLock(ReentrantReadWriteLock lock) {
        if (lock.getReadLockCount() == 0 && lock.readLock().tryLock()
                && lock.getReadHoldCount() != 1)
            lock.readLock().unlock();
        do {
            if (lock.getReadLockCount() == 0 && lock.readLock().tryLock()
                    && lock.getReadHoldCount() != 1)
                lock.readLock().unlock();
            else
                break;
        } while (true);
    }

    @FunctionalInterface
    public static interface VoidReturn {

        void func();
    }

    @FunctionalInterface
    public static interface TReturn<T> {

        T func();
    }

    public static <T> T RET(ReentrantReadWriteLock lock, TReturn<T> t) {
        getWriteLock(lock);
        try {
            return t.func();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void RET(ReentrantReadWriteLock lock, VoidReturn r) {
        getWriteLock(lock);
        try {
            r.func();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
