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

public class EngineThread extends Thread {

    public EngineThread(Runnable r) {
        this(TG_UNGROUPED, r);
    }

    public EngineThread(Runnable r, String name) {
        this(TG_UNGROUPED, r, name);
    }

    public EngineThread(EngineThreadGroup g, Runnable r) {
        this(g, r, "Unname Thread " + getCurrentThreadCount());
    }

    public EngineThread(EngineThreadGroup g, Runnable r, String name) {
        super(g, r, name);
        synchronized (EngineThread.class) {
            threadCount++;
        }
    }

    @Override
    public void interrupt() {
        isInterrupted = true;
        super.interrupt();
    }

    @Override
    public boolean isInterrupted() {
        boolean f = isInterrupted;
        isInterrupted = false;
        return f;
    }
    private transient volatile boolean isInterrupted = false;

    public static void sleepNoEx(long delay) {
        try {
            sleep(delay);
        } catch (InterruptedException ex) {
        }
    }

    public static void sleepNoEx(long delay, int nano) {
        try {
            sleep(delay, nano);
        } catch (InterruptedException ex) {
        }
    }

    public static boolean sleepAndNeedWakeUp(long delay, ThreadRouser r) throws InterruptedException {
        r.addNeedWakeUpThread(Thread.currentThread());
        sleep(delay);
        return r.removeWakeUpThread(Thread.currentThread());
    }

    private static volatile int threadCount = 0;

    private static synchronized int getCurrentThreadCount() {
        return threadCount++;
    }

    private static final EngineThreadGroup TG_UNGROUPED
            = new EngineThreadGroup("Ungrouped");
}
