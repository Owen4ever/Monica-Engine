package game.engine.monica.core.base.element;

public abstract class AbstractElement {

    protected AbstractElement(String name, int turnToEnergy) {
        if (name == null || name.isEmpty())
            throw new NullPointerException("The name of Element is null.");
        if (turnToEnergy < 1)
            throw new ElementInitializationException("The number which turns to energy smaller than 1.");
        this.name = name;
        this.turnToEnergy = turnToEnergy;
    }

    public final String getName() {
        return name;
    }

    public abstract boolean canInteract(AbstractElement e);

    public final int turnToEnergy() {
        return turnToEnergy;
    }
    private final String name;
    private final int turnToEnergy;
}
