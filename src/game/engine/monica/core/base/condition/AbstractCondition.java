package game.engine.monica.core.base.condition;

public abstract class AbstractCondition {

    public AbstractCondition(ConditionType type) {
        if (type == null)
            throw new NullPointerException("The condition type is null.");
        this.type = type;
    }

    public ConditionType getType() {
        return type;
    }

    public abstract String getIntroduction();

    public abstract boolean alreadyMeet();

    public abstract boolean meet(Object... obj);
    private final ConditionType type;
}
