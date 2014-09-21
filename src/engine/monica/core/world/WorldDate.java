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

package engine.monica.core.world;

import engine.monica.core.engine.EngineThread;
import engine.monica.core.engine.EngineThreadGroup;

public final class WorldDate {

    public WorldDate() {
        this(0, l -> l + 1, () -> 1);
    }

    public WorldDate(long time, TimeCalc calc, SleepTimeSupplier sleepTime) {
        if (time < 0)
            throw new WorldDateInitializeException("The time must >= 0.");
        if (calc == null)
            throw new WorldDateInitializeException("The time calculator is null.");
        if (sleepTime == null)
            throw new WorldDateInitializeException("The sleep time supplier is null.");
        this.time = time;
        this.calc = calc;
        this.sleepTime = sleepTime;
    }

    public long getCurrentTime() {
        return time;
    }

    public void setCurrentTime(long time) {
        if (isStart)
            throw new WorldException("Cannot set time after the world starts timing.");
        if (time < 0)
            throw new WorldDateInitializeException("The time must >= 0.");
        this.time = time;
    }

    public void setTimeCalculator(TimeCalc calc) {
        if (calc == null)
            throw new WorldDateInitializeException("The time calculator is null.");
        this.calc = calc;
    }

    public void setSleepTimeSupplier(SleepTimeSupplier sleepTime) {
        if (sleepTime == null)
            throw new WorldDateInitializeException("The sleep time supplier is null.");
        this.sleepTime = sleepTime;
    }

    public void start() {
        synchronized (lockObject) {
            isStart = true;
            timer.start();
        }
    }

    public void stop() {
        synchronized (lockObject) {
            isStart = false;
            timer.interrupt();
        }
    }
    private volatile long time;
    private TimeCalc calc;
    private SleepTimeSupplier sleepTime;
    private transient boolean isStart = false;
    private final transient EngineThread timer = new EngineThread(new EngineThreadGroup("WorldDate#" + hashCode()),
            () -> {
                while (isStart) {
                    try {
                        EngineThread.sleep(sleepTime.getSleepTime());
                        time = calc.calc(time);
                    } catch (InterruptedException ex) {
                    }
                }
            }, "Timer");
    private final transient Object lockObject = new Object();

    @FunctionalInterface
    public interface TimeCalc {

        long calc(long t);
    }

    @FunctionalInterface
    public interface SleepTimeSupplier {

        long getSleepTime();
    }
}
