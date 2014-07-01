package game.engine.monica.core.engine;

public final class EngineThreadGroup extends ThreadGroup {

    public EngineThreadGroup(String name) {
        super(name);
    }

    public EngineThreadGroup(ThreadGroup parent, String name) {
        super(parent, name);
    }
}
