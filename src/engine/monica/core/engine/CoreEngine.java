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

package engine.monica.core.engine;

import engine.monica.core.datetime.DateTime;
import engine.monica.core.datetime.WorldDate;
import engine.monica.core.element.ElementEngine;
import engine.monica.core.map.WorldFactory;
import engine.monica.util.OMath;
import engine.monica.util.StringID;
import engine.monica.util.ThreadRouser;
import engine.monica.util.Wrapper;
import engine.monica.util.condition.ProcesserInterface;
import engine.monica.util.condition.ProviderType;
import engine.monica.util.condition.UnkownProviderTypeException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class CoreEngine {

    public static boolean isStart() {
        return isStart;
    }

    public static void start() {
        if (date == null)
            throw new EngineException("Cannot start the game until WorldDate sets up.");
        if (isStart)
            throw new EngineException("The game engine "
                    + "has already started.");
        if (engineSCLock.tryLock() && omsSuspendTimeLocker.tryLock())
            try {
                date.ready();
                isStart = true;
                isContinuing = true;
                startTiming();
                date.start();
                runAll();
            } finally {
                omsSuspendTimeLocker.unlock();
                engineSCLock.unlock();
            }
    }

    public static void stop() {
        if (!isStart)
            throw new EngineException("The game engine has not started yet.");
        if (engineSCLock.tryLock() && omsSuspendTimeLocker.tryLock())
            try {
                isStart = false;
                isContinuing = false;
                date.stop();
                endTiming();
            } finally {
                omsSuspendTimeLocker.unlock();
                engineSCLock.unlock();
            }
    }
    private static volatile boolean isStart = false;

    public static boolean isContinuing() {
        return isContinuing;
    }

    public static void setContinue() {
        if (!isStart())
            throw new EngineException("The game engine has not started yet.");
        if (isContinuing)
            throw new EngineException("The game engine has already started.");
        if (engineSCLock.tryLock() && omsSuspendTimeLocker.tryLock())
            try {
                date.ready();
                isContinuing = true;
                startTiming();
                date.start();
                runAll();
            } finally {
                omsSuspendTimeLocker.unlock();
                engineSCLock.unlock();
            }
    }

    public static void pause() {
        if (!isStart())
            throw new EngineException("The game engine has not started yet.");
        if (!isContinuing)
            throw new EngineException("The game engine has already"
                    + " set to continue.");
        if (engineSCLock.tryLock() && omsSuspendTimeLocker.tryLock())
            try {
                isContinuing = false;
                date.stop();
                endTiming();
            } finally {
                omsSuspendTimeLocker.unlock();
                engineSCLock.unlock();
            }
    }
    private static volatile boolean isContinuing = false;
    private static final transient ReentrantReadWriteLock.WriteLock engineSCLock
            = new ReentrantReadWriteLock().writeLock();

    public static void setEngineAlreadyRunningTime(long time) {
        if (isContinuing())
            throw new EngineException("The game engine has already"
                    + " started yet.");
        if (time < 0)
            throw new EngineException("The running time is"
                    + " less than 0.");
        engineRunningTime = time;
    }

    public static long getEngineRunningTime() {
        return engineRunningTime;
    }

    private static void startTiming() {
        startTimingTime = System.currentTimeMillis();
    }

    private static void endTiming() {
        engineRunningTime += System.currentTimeMillis() - startTimingTime;
    }
    private static transient long engineRunningTime = 0;
    private static transient long startTimingTime;

    public static ThreadRouser getThreadRouser() {
        return engineThreadRouser;
    }
    private static final ThreadRouser engineThreadRouser = new ThreadRouser();

    public static void runThreadIfStart(Thread t) {
        if (isContinuing())
            t.start();
        else
            runThreadNextStart(t);
    }

    public static void runThreadIfStart(Runnable r) {
        if (isContinuing())
            new EngineThread(TG_TR, r).start();
        else
            runThreadNextStart(r);
    }

    public static void runThreadNextStart(Runnable r) {
        if (r == null)
            throw new NullPointerException("The runnable which will run"
                    + " when the game engine starts is null.");
        engineThreadRunner.add(new EngineThread(TG_TR, r));
    }

    private static void runAll() {
        if (!engineThreadRunner.isEmpty()) {
            engineThreadRunner.parallelStream().forEach(r -> {
                new EngineThread(TG_TR, r).start();
            });
            engineThreadRunner.clear();
        }
    }
    private static final HashSet<Runnable> engineThreadRunner = new HashSet<>();
    private static final EngineThreadGroup TG_TR
            = new EngineThreadGroup("Thread Runner");

    public static void setWorldDate(DateTime dateTime) {
        if (isStart())
            throw new EngineException("The game has already started.");
        date = new WorldDate(dateTime);
    }

    public static int getCurrentYear() {
        return date.getYear();
    }

    public static int getCurrentMonth() {
        return date.getMonth();
    }

    public static int getCurrentDay() {
        return date.getDay();
    }

    public static int getCurrentHour() {
        return date.getHour();
    }

    public static int getCurrentMinute() {
        return date.getMinute();
    }

    public static int getCurrentSecond() {
        return date.getSecond();
    }

    public static int getCurrentMilliSecond() {
        return date.getMilliSecond();
    }

    public static DateTime getCurrentDateTime() {
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

    public static BigInteger dateTimeToInteger() {
        if (!isStart())
            throw new EngineException("The game has not started yet.");
        return date.getCurrentDateTime().toInteger(date);
    }
    private static volatile WorldDate date;

    public static void set1msSuspendTime(int ms) {
        if (ms < 1)
            throw new EngineException("set1msStopTime( "
                    + Integer.toString(ms) + " ) < 1ms"
            );
        if (isStart)
            pause();
        omsSuspendTimeLocker.lock();
        try {
            if (ms != suspendTimeCalc.omsSuspendTime) {
                int i = OMath.isPowerOfTwo(ms);
                if (i == 1) {
                    suspendTimeCalc = new SuspendTimeCalculator.SuspendTimeCalculator_1();
                    quondamTimeCalc = new QuondamTimeCalculator.QuondamTimeCalculator_1();
                } else if (i != -1) {
                    suspendTimeCalc = new SuspendTimeCalculator.SuspendTimeCalculator_P2(i);
                    quondamTimeCalc = new QuondamTimeCalculator.QuondamTimeCalculator_P2(i);
                } else {
                    suspendTimeCalc = new SuspendTimeCalculator.SuspendTimeCalculator_Other(ms);
                    quondamTimeCalc = new QuondamTimeCalculator.QuondamTimeCalculator_Other(ms);
                }
                omsSuspendTimeChangeCount++;
            }
        } finally {
            omsSuspendTimeLocker.unlock();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        }
        if (isStart)
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

    public static int calcQuondamTime(int time) {
        return quondamTimeCalc.calc(time);
    }
    private static QuondamTimeCalculator quondamTimeCalc
            = new QuondamTimeCalculator.QuondamTimeCalculator_1();

    private static abstract class QuondamTimeCalculator {

        public QuondamTimeCalculator(int omsSuspendTime) {
            this.omsSuspendTime = omsSuspendTime;
        }

        abstract int calc(int t);
        protected int omsSuspendTime;

        private static final class QuondamTimeCalculator_1
                extends QuondamTimeCalculator {

            public QuondamTimeCalculator_1() {
                super(1);
            }

            @Override
            int calc(int t) {
                return t;
            }
        }

        private static final class QuondamTimeCalculator_P2
                extends QuondamTimeCalculator {

            public QuondamTimeCalculator_P2(int omsSuspendTime) {
                super(omsSuspendTime);
            }

            @Override
            int calc(int t) {
                return t >> omsSuspendTime;
            }
        }

        private static final class QuondamTimeCalculator_Other
                extends QuondamTimeCalculator {

            public QuondamTimeCalculator_Other(int omsSuspendTime) {
                super(omsSuspendTime);
            }

            @Override
            int calc(int t) {
                return t / omsSuspendTime;
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

    public static StringID newStringID(String sid) {
        if (sid == null || sid.isEmpty())
            throw new NullPointerException("The sid is null.");
        int index = strIds.indexOf(sid);
        if (index >= 0)
            return ids.get(index);
        else {
            StringID id = new StringID(sid);
            ids.add(id);
            strIds.add(sid);
            return id;
        }
    }

    public static StringID getStringID(String sid) {
        if (sid == null || sid.isEmpty())
            throw new NullPointerException("The sid is null.");
        int index = strIds.indexOf(sid);
        if (index >= 0)
            return ids.get(index);
        else
            return null;
    }
    private static final CopyOnWriteArrayList<StringID> ids
            = new CopyOnWriteArrayList<>();
    private static final CopyOnWriteArrayList<String> strIds
            = new CopyOnWriteArrayList<>();

    public static ElementEngine getElementEngine() {
        return elementEngine;
    }
    private static final ElementEngine elementEngine = new ElementEngine();

    public static WorldFactory getWorldFactory() {
        return worldFactory;
    }
    private static final WorldFactory worldFactory = new WorldFactory();

    public static void addProviderTypeProcesser(ProcesserInterface p) {
        if (p == null)
            throw new NullPointerException("The processer is null.");
        ptypeProcessers.add(p);
    }

    public static void processProviderType(ProviderType type) {
        Wrapper<Boolean> w = new Wrapper<>(true);
        ptypeProcessers.forEach(p -> {
            if (w.pack) {
                p.process(type);
                w.pack = false;
            }
        });
        if (w.pack)
            throw new UnkownProviderTypeException(type.toString());
    }
    private static final ArrayList<ProcesserInterface> ptypeProcessers
            = new ArrayList<>(8);
}
