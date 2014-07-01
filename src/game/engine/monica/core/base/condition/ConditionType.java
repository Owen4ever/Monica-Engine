package game.engine.monica.core.base.condition;

public class ConditionType {

    public ConditionType(String type) {
        if (type == null || type.isEmpty())
            throw new NullPointerException("The type name is null.");
        this.type = type;
    }

    public String name() {
        return type;
    }
    private final String type;
}
