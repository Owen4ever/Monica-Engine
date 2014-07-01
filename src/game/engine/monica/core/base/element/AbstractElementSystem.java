package game.engine.monica.core.base.element;

public abstract class AbstractElementSystem {

    private static final long serialVersionUID = 46274354282777902L;

    public AbstractElementSystem(String name, Energy energy, ElementList elements, AbstractElementSystem basedOn) {
        if (name == null || name.isEmpty())
            throw new NullPointerException("ElementSystem name is null.");
        if (energy == null)
            throw new NullPointerException("Energy is null.");
        if (elements == null)
            throw new NullPointerException("ElementList is null.");
        this.name = name;
        this.energy = energy;
        this.elements = elements;
        this.hasBasedElementSystem = basedOn != null;
        this.basedOn = basedOn;
    }

    public final String getName() {
        return name;
    }

    public final Energy getEnergy() {
        return energy;
    }

    public final ElementList getElements() {
        return elements;
    }

    public final boolean hasBasedElementSystem() {
        return hasBasedElementSystem;
    }

    public final AbstractElementSystem getBasedElementSystem() {
        return basedOn;
    }

    public abstract boolean canInteract(AbstractElementSystem s);

    public final boolean canTurnEnergyToBase() {
        return hasBasedElementSystem;
    }

    public abstract int turnEnergyToBase(int energy);

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AbstractElementSystem))
            return false;
        return equals((AbstractElementSystem) obj);
    }

    public boolean equals(AbstractElementSystem s) {
        if (s == null)
            return false;
        return name.equals(s.name)
                && energy.equals(s.energy)
                && elements.equals(s.elements)
                && hasBasedElementSystem == s.hasBasedElementSystem
                && (hasBasedElementSystem ? basedOn.equals(s.basedOn) : true);
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31 + energy.hashCode() * 71
                + elements.hashCode() * 11
                + (hasBasedElementSystem ? basedOn.hashCode() : 12);
    }

    @Override
    public String toString() {
        return getClass().getName() + " [ Name = " + name
                + " , Energy =" + energy.getName()
                + " , Elements = " + elements
                + " , Based On = "
                + (hasBasedElementSystem ? basedOn.getName() : "Null")
                + " ]";
    }
    private final String name;
    private final Energy energy;
    private final ElementList elements;
    private final boolean hasBasedElementSystem;
    private final AbstractElementSystem basedOn;
}
