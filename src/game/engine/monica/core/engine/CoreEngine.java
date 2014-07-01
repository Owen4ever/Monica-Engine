package game.engine.monica.core.engine;

import game.engine.monica.core.time.CDateTime;
import game.engine.monica.core.time.CWorldDate;
import java.math.BigInteger;
import java.util.LinkedList;
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

    public static void setWorldDate(CDateTime dateTime) {
        if (isStart())
            throw new EngineException("The game has already started.");
        date = new CWorldDate(dateTime);
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

    public static CDateTime getCurrentDateTime() {
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
    private static volatile CWorldDate date;

    public static void set1msStopTime(int ms) {
        if (ms < 1)
            throw new EngineException("set1msStopTime( " + Integer.toString(ms) + " )");
        boolean is = isStart;
        if (is)
            pause();
        ohmsStopTimeLocker.lock();
        try {
            omsStopTime = ms;
            omsStopTimeChangeCount++;
        } finally {
            ohmsStopTimeLocker.unlock();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
        }
        if (is)
            setContinue();
    }

    public static int get1msStopTime() {
        return omsStopTime;
    }
    private static volatile int omsStopTime = 1;

    public static boolean is1msStopTimeChanged(int count) {
        return omsStopTimeChangeCount == count;
    }

    public static int get1msStopTimeChangeCount() {
        return omsStopTimeChangeCount;
    }
    private static volatile int omsStopTimeChangeCount = 0;
    private static final transient ReentrantReadWriteLock.WriteLock ohmsStopTimeLocker = new ReentrantReadWriteLock().writeLock();

    public static void addNeedWakeUpThread(Thread t) {
        if (t == null)
            throw new NullPointerException("The thread which needs to wake up is null.");
        needWakeUpThreads.add(t);
    }

    public static boolean removeWakeUpThread(Thread t) {
        if (t == null)
            throw new NullPointerException("THe thread which wants to remove from the list is null.");
        return needWakeUpThreads.remove(t);
    }

    public static void wakeUpAllThreads() {
        for (int i = 0; i < needWakeUpThreads.size(); ++i) {
            Thread c = needWakeUpThreads.remove(i);
            c.interrupt();
        }
    }
    private static final LinkedList<Thread> needWakeUpThreads = new LinkedList<>();
}
