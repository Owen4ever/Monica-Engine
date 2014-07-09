/*
 * The MIT License
 *
 * Copyright 2014 Owen.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package game.engine.monica.core.element;

import game.engine.monica.util.FinalPair;
import game.engine.monica.core.condition.ConditionInterface;
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

    public ConditionInterface getCondition(AbstractElement e1, AbstractElement e2) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The AElement is null.");
        return conditions.get(new FinalPair<>(e1, e2));
    }

    public ConditionInterface setCondition(AbstractElement e1, AbstractElement e2, ConditionInterface l) {
        if (e1 == null || e2 == null)
            throw new NullPointerException("The AElement is null.");
        if (l == null)
            throw new NullPointerException("The condition is null.");
        return conditions.put(new FinalPair<>(e1, e2), l);
    }
    private final HashMap<FinalPair, ElementRelation> elementRelation = new HashMap<>();
    private final HashMap<FinalPair, ConditionInterface> conditions = new HashMap<>();
}
