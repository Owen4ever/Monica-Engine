/*
 * The MIT License
 *
 * Copyright 2014 Owen.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package game.engine.monica.core.engine;

import game.engine.monica.core.datetime.DateTime;
import game.engine.monica.core.datetime.WorldDate;
import game.engine.monica.util.OMath;
import game.engine.monica.util.ThreadRouser;
import java.math.BigInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class CoreEngine {

    public static boolean isStart() {
        return isStart;
    }

    public static void start() {
        if (date == null)
            throw new EngineException("Cannot start the game until WorldDate sets up.");
        isStart = true;
        date.start();
    }
    private static volatile boolean isStart = false;

    public static boolean isContinuing() {
        return isStart & isContinuing;
    }

    public static void setContinue() {
        if (!isStart())
            throw new EngineException("The game engine has not started yet.");
        date.start();
        isContinuing = true;
    }

    public static void pause() {
        if (!isStart())
            throw new EngineException("The game engine has not started yet.");
        date.stop();
        isContinuing = false;
    }
    private static volatile boolean isContinuing = false;

    public static void setWorldDate(DateTime dateTime) {
        if (isStart())
            throw new EngineException("The game has already started.");
        date = new WorldDate(dateTime);
    }

    public static int getCurrentYear() {
        if (!isStart())
            throw new EngineException("The game has not started yet.");
        return date.getYear();
    }

    public static int getCurrentMonth() {
        if (!isStart())
            throw new EngineException("The game has not started yet.");
        return date.getMonth();
    }

    public static int getCurrentDay() {
        if (!isStart())
            throw new EngineException("The game has not started yet.");
        return date.getDay();
    }

    public static int getCurrentHour() {
        if (!isStart())
            throw new EngineException("The game has not started yet.");
        return date.getHour();
    }

    public static int getCurrentMinute() {
        if (!isStart())
            throw new EngineException("The game has not started yet.");
        return date.getMinute();
    }

    public static int getCurrentSecond() {
        if (!isStart())
            throw new EngineException("The game has not started yet.");
        return date.getSecond();
    }

    public static int getCurrentMilliSecond() {
        if (!isStart())
            throw new EngineException("The game has not started yet.");
        return date.getMilliSecond();
    }

    public static DateTime getCurrentDateTime() {
        if (!isStart())
            throw new EngineException("The game has not started yet.");
        return date.getCurrentDateTime();
    }

    public static void setLoopMonth(int mon) {
        if (isStart())
            throw new EngineException("The game has already started.");
        date.setLoopMonth(mon);
    }

    public static void setLoopDay(int day) {
        if (isStart())
            throw new EngineException("The game has already started.");
        date.setLoopDay(day);
    }

    public static void setLoopHour(int hour) {
        if (isStart())
            throw new EngineException("The game has already started.");
        date.setLoopHour(hour);
    }

    public static void setLoopMinute(int min) {
        if (isStart())
            throw new EngineException("The game has already started.");
        date.setLoopMinute(min);
    }

    public static void setLoopSecond(int sec) {
        if (isStart())
            throw new EngineException("The game has already started.");
        date.setLoopSecond(sec);
    }

    public static void setLoopMilliSecond(int msec) {
        if (isStart())
            throw new EngineException("The game has already started.");
        date.setLoopMilliSecond(msec);
    }

    public static BigInteger toInteger() {
        if (!isStart())
            throw new EngineException("The game has not started yet.");
        return date.getCurrentDateTime().toInteger(date);
    }
    private static volatile WorldDate date;

    public static void set1msSuspendTime(int ms) {
        if (ms < 1)
            throw new EngineException("set1msStopTime( " + Integer.toString(ms) + " )");
        boolean is = isStart;
        if (is)
            pause();
        omsSuspendTimeLocker.lock();
        try {
            if (ms != suspendTimeCalc.omsSuspendTime) {
                int i = OMath.isPowerOfTwo(ms);
                if (i == 1)
                    suspendTimeCalc = new SuspendTimeCalculator.SuspendTimeCalculator_1();
                else if (i != -1)
                    suspendTimeCalc = new SuspendTimeCalculator.SuspendTimeCalculator_P2(i);
                else
                    suspendTimeCalc = new SuspendTimeCalculator.SuspendTimeCalculator_Other(ms);
                omsSuspendTimeChangeCount++;
            }
        } finally {
            omsSuspendTimeLocker.unlock();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        }
        if (is)
            setContinue();
    }

    public static int calcSuspendTime(int time) {
        return suspendTimeCalc.calc(time);
    }

    public static int get1msSuspendTime() {
        return suspendTimeCalc.omsSuspendTime;
    }
    private static SuspendTimeCalculator suspendTimeCalc
            = new SuspendTimeCalculator.SuspendTimeCalculator_1();

    private static abstract class SuspendTimeCalculator {

        private SuspendTimeCalculator(int omsSuspendTime) {
            this.omsSuspendTime = omsSuspendTime;
        }

        abstract int calc(int t);
        protected volatile int omsSuspendTime;

        private static final class SuspendTimeCalculator_1
                extends SuspendTimeCalculator {

            private SuspendTimeCalculator_1() {
                super(1);
            }

            @Override
            int calc(int t) {
                return t;
            }
        }

        private static final class SuspendTimeCalculator_P2
                extends SuspendTimeCalculator {

            private SuspendTimeCalculator_P2(int omsSuspendTime) {
                super(omsSuspendTime);
            }

            @Override
            int calc(int t) {
                return t << omsSuspendTime;
            }
        }

        private static final class SuspendTimeCalculator_Other
                extends SuspendTimeCalculator {

            public SuspendTimeCalculator_Other(int omsSuspendTime) {
                super(omsSuspendTime);
            }

            @Override
            int calc(int t) {
                return t * omsSuspendTime;
            }
        }
    }

    public static boolean is1msSuspendTimeChanged(int count) {
        return omsSuspendTimeChangeCount == count;
    }

    public static int get1msSuspendTimeChangeCount() {
        return omsSuspendTimeChangeCount;
    }
    private static transient volatile int omsSuspendTimeChangeCount = 0;
    private static final transient ReentrantReadWriteLock.WriteLock omsSuspendTimeLocker
            = new ReentrantReadWriteLock().writeLock();

    public static final ThreadRouser engineThreadRouser = new ThreadRouser();

    public static int getDefaultQuantily() {
        return defaultQuantily;
    }

    public static void setDefaultQuantily(int n) {
        if (n < 1)
            throw new EngineException("The number which is used to"
                    + "set up the default quantily of lists, sets and maps"
                    + "is less than 1.");
        defaultQuantily = n;
    }
    private static int defaultQuantily = 16;
}
