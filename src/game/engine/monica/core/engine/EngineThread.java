package game.engine.monica.core.engine;

public class EngineThread extends Thread {

    public EngineThread(EngineRunnable r) {
        this(TG_UNGROUPED, r);
    }

    public EngineThread(EngineRunnable r, String name) {
        this(TG_UNGROUPED, r, name);
    }

    public EngineThread(EngineThreadGroup g, EngineRunnable r) {
        this(g, r, "Eternal World - Unname Thread " + getCurrentThreadCount());
    }

    public EngineThread(EngineThreadGroup g, EngineRunnable r, String name) {
        super(g, r, name);
    }

    public static void sleepWithoutException(long delay) {
        try {
            sleep(delay);
        } catch (InterruptedException ex) {
        }
    }

    public static void sleepWithoutException(long delay, int nano) {
        try {
            sleep(delay, nano);
        } catch (InterruptedException ex) {
        }
    }

    public static void sleepAndNeedWakeUp(long delay) {
        CoreEngine.addNeedWakeUpThread(Thread.currentThread());
        sleepWithoutException(delay);
        CoreEngine.removeWakeUpThread(Thread.currentThread());
    }
    private static int threadCount = 0;

    private static synchronized int getCurrentThreadCount() {
        return threadCount++;
    }

    private static final EngineThreadGroup TG_UNGROUPED = new EngineThreadGroup("Ungrouped");
}
