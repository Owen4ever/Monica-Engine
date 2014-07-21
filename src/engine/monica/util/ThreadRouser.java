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

import java.util.LinkedList;

public final class ThreadRouser {

    public ThreadRouser() {
    }

    public void addNeedWakeUpThread(Thread t) {
        if (t == null)
            throw new NullPointerException("The thread which needs to wake up is null.");
        needWakeUpThreads.add(t);
    }

    public boolean removeWakeUpThread(Thread t) {
        if (t == null)
            throw new NullPointerException("THe thread which wants to remove from the list is null.");
        return needWakeUpThreads.remove(t);
    }

    public void wakeUpAllThreads() {
        for (int i = 0; i < needWakeUpThreads.size(); ++i) {
            Thread c = needWakeUpThreads.remove(i);
            c.interrupt();
        }
    }
    private static final LinkedList<Thread> needWakeUpThreads = new LinkedList<>();
}
