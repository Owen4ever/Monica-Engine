package game.engine.monica.core.engine;

public class EngineException extends RuntimeException {

    public EngineException() {
        super("Set a value to engine's property failed.");
    }

    public EngineException(String reason) {
        super("Set a value to engine's property failed. Reason: " + reason);
    }
}
