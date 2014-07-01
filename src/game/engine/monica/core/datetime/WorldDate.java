package game.engine.monica.core.datetime;

import game.engine.monica.core.engine.CoreEngine;
import game.engine.monica.core.engine.EngineRunnable;
import game.engine.monica.core.engine.EngineThread;
import game.engine.monica.core.engine.EngineThreadGroup;

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
            throw new WorldDateInitilizeException();
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
        return new DateTime(year, mon, day, hour, min, sec, msec);
    }
    private volatile int year, mon, day, hour, min, sec, msec;

    public void setLoopMonth(int mon) {
        if (mon < 1)
            throw new WorldDateInitilizeException();
        loopMon = mon;
    }

    public void setLoopDay(int day) {
        if (day < 1)
            throw new WorldDateInitilizeException();
        loopDay = day;
    }

    public void setLoopHour(int hour) {
        if (hour < 1)
            throw new WorldDateInitilizeException();
        loopHour = hour;
    }

    public void setLoopMinute(int min) {
        if (min < 1)
            throw new WorldDateInitilizeException();
        loopMin = min;
    }

    public void setLoopSecond(int sec) {
        if (sec < 1)
            throw new WorldDateInitilizeException();
        loopSec = sec;
    }

    public void setLoopMilliSecond(int msec) {
        if (msec < 1)
            throw new WorldDateInitilizeException();
        loopMSec = msec;
    }
    int loopMon = 12, loopDay = 30,
            loopHour = 24, loopMin = 60, loopSec = 60, loopMSec = 1000;

    public void start() {
        isStart = true;
        final long omsst = CoreEngine.get1msStopTime();
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
        new EngineThread(TG_TIME, new EngineRunnable() {
            @Override
            public final void loop() {
                int firstWaitTime = loopMSec - msec;
                if (firstWaitTime > 1)
                    EngineThread.sleepWithoutException(firstWaitTime * wait_msec);
                while (isStart) {
                    if (msec >= loopMSec) {
                        msec = 0;
                        sec++;
                        EngineThread.sleepWithoutException(wt_msec);
                        continue;
                    }
                    EngineThread.sleepWithoutException(0);
                }
            }
        }, "MilliSecond Checker").start();
        new EngineThread(TG_TIME, new EngineRunnable() {
            @Override
            public final void loop() {
                int firstWaitTime = loopSec - sec;
                if (firstWaitTime > 1)
                    EngineThread.sleepWithoutException(firstWaitTime * wait_sec);
                while (isStart) {
                    if (sec >= loopSec) {
                        sec = 0;
                        min++;
                        EngineThread.sleepWithoutException(wt_sec);
                        continue;
                    }
                    EngineThread.sleepWithoutException(0);
                }
            }
        }, "Second Checker").start();
        new EngineThread(TG_TIME, new EngineRunnable() {
            @Override
            public final void loop() {
                int firstWaitTime = loopMin - min;
                if (firstWaitTime > 1)
                    EngineThread.sleepWithoutException(firstWaitTime * wait_min);
                while (isStart) {
                    if (min >= loopMin) {
                        min = 0;
                        hour++;
                        EngineThread.sleepWithoutException(wt_min);
                        continue;
                    }
                    EngineThread.sleepWithoutException(0);
                }
            }
        }, "Minute Checker").start();
        new EngineThread(TG_TIME, new EngineRunnable() {
            @Override
            public final void loop() {
                int firstWaitTime = loopHour - hour;
                if (firstWaitTime > 0)
                    EngineThread.sleepWithoutException(firstWaitTime * wait_hour);
                while (isStart) {
                    if (hour >= loopHour) {
                        hour = 0;
                        day++;
                        EngineThread.sleepWithoutException(wt_hour);
                        continue;
                    }
                    EngineThread.sleepWithoutException(0);
                }
            }
        }, "Hour Checker").start();
        new EngineThread(TG_TIME, new EngineRunnable() {
            @Override
            public final void loop() {
                int firstWaitTime = loopDay - day;
                if (firstWaitTime > 0)
                    EngineThread.sleepWithoutException(firstWaitTime * wait_day);
                while (isStart) {
                    if (day >= loopDay) {
                        day = 0;
                        mon++;
                        EngineThread.sleepWithoutException(wt_day);
                        continue;
                    }
                    EngineThread.sleepWithoutException(0);
                }
            }
        }, "Day Checker").start();
        new EngineThread(TG_TIME, new EngineRunnable() {
            @Override
            public final void loop() {
                int firstWaitTime = loopMon - mon;
                if (firstWaitTime > 0)
                    EngineThread.sleepWithoutException(firstWaitTime * wait_mon);
                while (isStart) {
                    if (mon >= loopMon) {
                        mon = 0;
                        year++;
                        EngineThread.sleepWithoutException(wt_mon);
                        continue;
                    }
                    EngineThread.sleepWithoutException(0);
                }
            }
        }, "Month Checker").start();
        EngineThread timeMainThread
                = new EngineThread(TG_TIME, new EngineRunnable() {
                    @Override
                    public final void loop() {
                        while (isStart) {
                            msec++;
                            EngineThread.sleepWithoutException(omsst);
                        }
                    }
                }, "Time Counter");
        timeMainThread.setPriority(Thread.MAX_PRIORITY);
        timeMainThread.start();
    }

    public void stop() {
        isStart = false;
    }

    public boolean isStart() {
        return isStart;
    }
    private volatile boolean isStart = false;

    @Override
    public String toString() {
        return "[ Year = " + year + ", Month = " + mon + ", Day = " + day
                + ", Hour = " + hour + ", Minute = " + min
                + ", Second = " + sec + ", MilliSecond = " + msec + " ]";
    }

    private static final EngineThreadGroup TG_TIME = new EngineThreadGroup("World Date Time Thread Group");
}
