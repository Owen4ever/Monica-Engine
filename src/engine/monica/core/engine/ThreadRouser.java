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

package engine.monica.core.engine;

import static engine.monica.util.Lock.*;
import java.util.HashSet;
import java.util.concurrent.locks.StampedLock;

public final class ThreadRouser {

    public ThreadRouser() {
    }

    public void addNeedWakeUpThread(Thread t) {
        RETW(lock, () -> {
            needWakeUpThreads.add(t);
        });
    }

    public boolean removeWakeUpThread(Thread t) {
        return RETW(lock, () -> needWakeUpThreads.remove(t));
    }

    public void wakeUpAllThreads() {
        RETW(lock, () -> {
            needWakeUpThreads.parallelStream().forEach(t -> {
                t.interrupt();
            });
        });
    }
    private transient final StampedLock lock = new StampedLock();
    private final HashSet<Thread> needWakeUpThreads = new HashSet<>(CoreEngine.getDefaultQuantity());
}
