package game.engine.monica.core.engine;

public abstract class EngineRunnable implements Runnable {

    @Override
    public final void run() {
        loop();
    }

    protected abstract void loop();
}
