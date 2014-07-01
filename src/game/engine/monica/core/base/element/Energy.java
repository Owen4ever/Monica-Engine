package game.engine.monica.core.base.element;

public final class Energy {

    private static final long serialVersionUID = 46274354375486750L;

    public Energy(String name) {
        if (name == null || name.isEmpty())
            throw new NullPointerException("Energy name cannot be null.");
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof Energy)
            return equals((Energy) obj);
        else
            return false;
    }

    public boolean equals(Energy e) {
        if (e == null)
            return false;
        return name.equals(e.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + " [ Name = " + name + " ]";
    }
    private final String name;
}
