package game.engine.monica.core.base.element;

import game.engine.monica.util.FinalPair;
import game.engine.monica.core.base.condition.ConditionList;
import java.util.HashMap;

public final class ElementEngine {

    public ElementEngine() {
    }

    public ElementRelation getRelation(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        return elementRelation.get(new FinalPair<>(e1, e2));
    }

    public ElementRelation setRelation(AbstractElement e1, AbstractElement e2, ElementRelation r) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The element is null.");
        if (r == null)
            throw new NullPointerException("The element relation is null.");
        return elementRelation.put(new FinalPair<>(e1, e2), r);
    }

    public ConditionList getCondition(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The AElement is null.");
        return conditions.get(new FinalPair<>(e1, e2));
    }

    public ConditionList setCondition(AbstractElement e1, AbstractElement e2, ConditionList l) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The AElement is null.");
        if (l == null)
            throw new NullPointerException("The condition list is null.");
        return conditions.put(new FinalPair<>(e1, e2), l);
    }
    private final HashMap<FinalPair, ElementRelation> elementRelation = new HashMap<>();
    private final HashMap<FinalPair, ConditionList> conditions = new HashMap<>();
}
