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

import engine.monica.core.map.World;
import engine.monica.core.map.WorldFactory;
import engine.monica.util.AlreadyExistsInContainerException;
import engine.monica.util.OMath;
import engine.monica.util.SimpleArrayList;
import engine.monica.util.Wrapper;
import engine.monica.util.condition.ProcesserInterface;
import engine.monica.util.condition.Provider;
import engine.monica.util.condition.ProviderType;
import engine.monica.util.condition.UnkownProviderTypeException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class CoreEngine {

    public static ThreadRouser newThreadRouser() {
        return threadRouserSpliter.newRouser();
    }

    public static boolean deleteThreadRouser(ThreadRouser r) {
        return threadRouserSpliter.deleteRouser(r);
    }

    public static void rouseAll() {
        threadRouserSpliter.rousers.forEach(r -> r.wakeUpAllThreads());
    }
    private static final ThreadRouserSpliter threadRouserSpliter = new ThreadRouserSpliter();

    public static ThreadRunner newThreadRunner() {
        return threadRunnerSpliter.newRunner();
    }

    public static boolean deleteThreadRunner(ThreadRunner r) {
        return threadRunnerSpliter.deleteRunner(r);
    }

    public static void runAll() {
        threadRunnerSpliter.runners.parallelStream().forEach(r -> {
            r.runAll();
        });
    }
    private static final ThreadRunnerSpliter threadRunnerSpliter = new ThreadRunnerSpliter();

    public static boolean isStart() {
        return currentWorld.isStart();
    }

    public static void start() {
        if (currentWorld == null)
            throw new EngineException("Cannot start the game until World sets up.");
        if (isStart())
            throw new EngineException("The game engine has already started.");
        if (engineSCLock.tryLock() && omsSuspendTimeLocker.tryLock())
            try {
                currentWorld.start();
                runAll();
            } finally {
                omsSuspendTimeLocker.unlock();
                engineSCLock.unlock();
            }
    }

    public static void stop() {
        if (!isStart())
            throw new EngineException("The game engine has not started yet.");
        if (engineSCLock.tryLock() && omsSuspendTimeLocker.tryLock())
            try {
                currentWorld.stop();
            } finally {
                omsSuspendTimeLocker.unlock();
                engineSCLock.unlock();
            }
    }

    public static boolean isContinuing() {
        return currentWorld.isStart();
    }

    public static void setContinue() {
        if (!isStart())
            throw new EngineException("The game engine has not started yet.");
        if (isContinuing())
            throw new EngineException("The game engine has already started.");
        if (engineSCLock.tryLock() && omsSuspendTimeLocker.tryLock())
            try {
                currentWorld.setContinue();
                runAll();
            } finally {
                omsSuspendTimeLocker.unlock();
                engineSCLock.unlock();
            }
    }

    public static void pause() {
        if (!isStart())
            throw new EngineException("The game engine has not started yet.");
        if (!isContinuing())
            throw new EngineException("The game engine has already set to continue.");
        if (engineSCLock.tryLock() && omsSuspendTimeLocker.tryLock())
            try {
                currentWorld.setPause();
            } finally {
                omsSuspendTimeLocker.unlock();
                engineSCLock.unlock();
            }
    }
    private static final transient ReentrantReadWriteLock.WriteLock engineSCLock = new ReentrantReadWriteLock().writeLock();

    public static void set1msSuspendTime(int ms) {
        if (ms < 1)
            throw new EngineException("set1msStopTime( " + Integer.toString(ms) + " ) < 1ms");
        if (isStart())
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
        if (isStart())
            setContinue();
    }

    public static int calcSuspendTime(int time) {
        return suspendTimeCalc.calc(time);
    }

    public static int get1msSuspendTime() {
        return suspendTimeCalc.omsSuspendTime;
    }
    private static SuspendTimeCalculator suspendTimeCalc = new SuspendTimeCalculator.SuspendTimeCalculator_1();

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
    private static QuondamTimeCalculator quondamTimeCalc = new QuondamTimeCalculator.QuondamTimeCalculator_1();

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
    private static final transient ReentrantReadWriteLock.WriteLock omsSuspendTimeLocker = new ReentrantReadWriteLock().writeLock();

    public static int getDefaultQuantity() {
        return defaultQuantily;
    }

    public static void setDefaultQuantily(int n) {
        if (n < 1)
            throw new EngineException("The quantily is less than 1.");
        defaultQuantily = n;
    }
    private static int defaultQuantily = 16;

    public static String newID(String sid) {
        if (sid == null || sid.isEmpty())
            throw new NullPointerException("The sid is null.");
        if (ids.contains(sid))
            throw new AlreadyExistsInContainerException("The id has already added into the set.");
        else {
            ids.add(sid);
            return sid;
        }
    }

    public static String getID(String sid) {
        if (sid == null || sid.isEmpty())
            throw new NullPointerException("The sid is null.");
        if (ids.contains(sid))
            return sid;
        else
            return null;
    }
    private static final HashSet<String> ids = new HashSet<>(getDefaultQuantity(), 0.4f);

    public static WorldFactory getWorldFactory() {
        return worldFactory;
    }
    private static final WorldFactory worldFactory = new WorldFactory();

    public static World getCurrentWorld() {
        return currentWorld.getWorld();
    }

    public static void setCurrentWorld(World w) {
        if (w == null)
            throw new NullPointerException("The world is null.");
        currentWorld = new CurrentWorld.WrapperWorld(w);
    }

    private static interface CurrentWorld {

        boolean isStart();

        boolean isContinuing();

        void start();

        void stop();

        void setContinue();

        void setPause();

        World getWorld();

        static final class EmptyWorld implements CurrentWorld {

            @Override
            public boolean isStart() {
                return false;
            }

            @Override
            public boolean isContinuing() {
                return false;
            }

            @Override
            public void start() {
                throw new EngineException("The world has not set up.");
            }

            @Override
            public void stop() {
                throw new EngineException("The world has not set up.");
            }

            @Override
            public void setContinue() {
                throw new EngineException("The world has not set up.");
            }

            @Override
            public void setPause() {
                throw new EngineException("The world has not set up.");
            }

            @Override
            public World getWorld() {
                return null;
            }
        }

        static final class WrapperWorld implements CurrentWorld {

            private WrapperWorld(World w) {
                world = w;
            }

            @Override
            public boolean isStart() {
                return world.isStart();
            }

            @Override
            public boolean isContinuing() {
                return world.isContinuing();
            }

            @Override
            public void start() {
                world.start();
            }

            @Override
            public void stop() {
                world.stop();
            }

            @Override
            public void setContinue() {
                world.setContinue();
            }

            @Override
            public void setPause() {
                world.setPause();
            }

            @Override
            public World getWorld() {
                return world;
            }
            private final World world;
        }
    }
    private static CurrentWorld currentWorld = new CurrentWorld.EmptyWorld();

    public static void addProviderTypeProcesser(ProcesserInterface p) {
        if (p == null)
            throw new NullPointerException("The processer is null.");
        ptypeProcessers.add(p);
    }

    public static List<Provider> processProviderType(ProviderType type) {
        Wrapper<Boolean> w = new Wrapper<>(true);
        ArrayList<Provider> list = new ArrayList<>();
        ptypeProcessers.forEach(p -> {
            if (w.pack) {
                Provider[] objs = p.process(type);
                if (objs != null) {
                    list.addAll(new SimpleArrayList<>(objs));
                    w.pack = false;
                }
            }
        });
        if (w.pack)
            throw new UnkownProviderTypeException(type.toString());
        return list;
    }
    private static final ArrayList<ProcesserInterface> ptypeProcessers = new ArrayList<>(getDefaultQuantity());
}
