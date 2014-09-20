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

import engine.monica.core.engine.CoreEngine;
import engine.monica.core.engine.EngineThread;
import engine.monica.core.engine.EngineThreadGroup;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class WorldDate {

    public WorldDate() {
        this(new DateTime(1, 1, 1, 0, 0, 0, 0));
    }

    public WorldDate(DateTime dt) {
        setCurrentDate(dt);
    }

    public void setCurrentDate(DateTime dt) {
        if (dt == null || dt.year < 0 || dt.month < 0 || dt.day < 0
                || dt.hour < 0 || dt.minute < 0 || dt.second < 0 || dt.millisecond < 0)
            throw new WorldDateInitializeException();
        this.year = dt.year;
        this.mon = dt.month;
        this.day = dt.day;
        this.hour = dt.hour;
        this.min = dt.minute;
        this.sec = dt.second;
        this.msec = dt.millisecond;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return mon;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return min;
    }

    public int getSecond() {
        return sec;
    }

    public int getMilliSecond() {
        return msec;
    }

    public DateTime getCurrentDateTime() {
        final int year = this.year,
                month = this.mon,
                day = this.day,
                hour = this.hour,
                minute = this.min,
                second = this.sec,
                milliSecond = this.msec;
        return new DateTime(year, month, day, hour, minute, second, milliSecond);
    }
    private volatile int year, mon, day, hour, min, sec, msec;

    public void setLoopMonth(int mon) {
        if (mon < 1)
            throw new WorldDateInitializeException();
        loopMon = mon;
    }

    public void setLoopDay(int day) {
        if (day < 1)
            throw new WorldDateInitializeException();
        loopDay = day;
    }

    public void setLoopHour(int hour) {
        if (hour < 1)
            throw new WorldDateInitializeException();
        loopHour = hour;
    }

    public void setLoopMinute(int min) {
        if (min < 1)
            throw new WorldDateInitializeException();
        loopMin = min;
    }

    public void setLoopSecond(int sec) {
        if (sec < 1)
            throw new WorldDateInitializeException();
        loopSec = sec;
    }

    public void setLoopMilliSecond(int msec) {
        if (msec < 1)
            throw new WorldDateInitializeException();
        loopMSec = msec;
    }
    int loopMon = 12, loopDay = 30,
            loopHour = 24, loopMin = 60, loopSec = 60, loopMSec = 1000;

    public void ready() {
        final long omsst = CoreEngine.get1msSuspendTime();
        final long wait_msec = omsst;
        final long wait_sec = wait_msec * loopMSec;
        final long wait_min = wait_sec * loopSec;
        final long wait_hour = wait_min * loopMin;
        final long wait_day = wait_hour * loopHour;
        final long wait_mon = wait_day * loopDay;
        final long wt_msec = wait_sec * 950 / 1000;
        final long wt_sec = wait_min * 950 / 1000 + wt_msec;
        final long wt_min = wait_hour * 950 / 1000 + wt_sec;
        final long wt_hour = wait_day * 950 / 1000 + wt_min;
        final long wt_day = wait_mon * 950 / 1000 + wt_hour;
        final long wt_mon = wait_mon * loopMon * 950 / 1000 + wt_day;
        t_c_msec = new EngineThread(TG_TIME, () -> {
            int firstWaitTime = loopMSec - msec;
            if (firstWaitTime > 1)
                EngineThread.sleepWithoutException(firstWaitTime * wait_msec);
            while (isStart) {
                while (msec >= loopMSec) {
                    msec -= loopMSec;
                    sec++;
                }
                EngineThread.sleepWithoutException(wt_msec);
            }
        }, "MilliSecond Checker");
        t_c_sec = new EngineThread(TG_TIME, () -> {
            int firstWaitTime = loopSec - sec;
            if (firstWaitTime > 1)
                EngineThread.sleepWithoutException(firstWaitTime * wait_sec);
            while (isStart) {
                while (sec >= loopSec) {
                    sec -= loopSec;
                    min++;
                }
                EngineThread.sleepWithoutException(wt_sec);
            }
        }, "Second Checker");
        t_c_min = new EngineThread(TG_TIME, () -> {
            int firstWaitTime = loopMin - min;
            if (firstWaitTime > 1)
                EngineThread.sleepWithoutException(firstWaitTime * wait_min);
            while (isStart) {
                while (min >= loopMin) {
                    min -= loopMin;
                    hour++;
                }
                EngineThread.sleepWithoutException(wt_min);
            }
        }, "Minute Checker");
        t_c_hour = new EngineThread(TG_TIME, () -> {
            int firstWaitTime = loopHour - hour;
            if (firstWaitTime > 0)
                EngineThread.sleepWithoutException(firstWaitTime * wait_hour);
            while (isStart) {
                while (hour >= loopHour) {
                    hour -= loopHour;
                    day++;
                }
                EngineThread.sleepWithoutException(wt_hour);
            }
        }, "Hour Checker");
        t_c_day = new EngineThread(TG_TIME, () -> {
            int firstWaitTime = loopDay - day;
            if (firstWaitTime > 0)
                EngineThread.sleepWithoutException(firstWaitTime * wait_day);
            while (isStart) {
                while (day >= loopDay) {
                    day -= loopDay;
                    mon++;
                }
                EngineThread.sleepWithoutException(wt_day);
            }
        }, "Day Checker");
        t_c_mon = new EngineThread(TG_TIME, () -> {
            int firstWaitTime = loopMon - mon;
            if (firstWaitTime > 0)
                EngineThread.sleepWithoutException(firstWaitTime * wait_mon);
            while (isStart) {
                if (mon >= loopMon) {
                    mon -= loopMon;
                    year++;
                }
                EngineThread.sleepWithoutException(wt_mon);
            }
        }, "Month Checker");
        timeMainThread
                = new EngineThread(TG_TIME, () -> {
                    while (isStart) {
                        EngineThread.sleepWithoutException(omsst);
                        msec++;
                    }
                }, "Time Counter");
        timeMainThread.setPriority(Thread.MAX_PRIORITY);
        isReady = true;
    }
    private transient EngineThread t_c_msec, t_c_sec, t_c_min, t_c_hour,
            t_c_day, t_c_mon;
    private transient EngineThread timeMainThread;

    public void start() {
        if (isReady) {
            isStart = true;
            timeMainThread.start();
            t_c_msec.start();
            t_c_sec.start();
            t_c_min.start();
            t_c_hour.start();
            t_c_day.start();
            t_c_mon.start();
        }
    }

    public void stop() {
        timeLocker.lock();
        try {
            isStart = false;
        } finally {
            timeLocker.unlock();
        }
    }

    public boolean isStart() {
        return isStart;
    }
    private transient volatile boolean isReady = false;
    private transient volatile boolean isStart = false;
    private final transient ReentrantReadWriteLock.WriteLock timeLocker
            = new ReentrantReadWriteLock().writeLock();

    @Override
    public String toString() {
        return "[ Year = " + year + ", Month = " + mon + ", Day = " + day
                + ", Hour = " + hour + ", Minute = " + min
                + ", Second = " + sec + ", MilliSecond = " + msec + " ]";
    }

    private static final EngineThreadGroup TG_TIME = new EngineThreadGroup("World Date Time Thread Group");
}
