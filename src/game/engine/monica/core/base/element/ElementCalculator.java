package game.engine.monica.core.base.element;

public abstract class ElementCalculator {

    public abstract int calc();

    public boolean equals(ElementCalculator c) {
        return this == c;
    }
}
