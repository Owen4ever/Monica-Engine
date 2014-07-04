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

package game.engine.monica.core.condition;

public final class ConditionList {

    public ConditionList(AbstractCondition e, AbstractCondition... e2) {
        if (e == null)
            throw new NullPointerException("Condition is null.");
        conditions = new AbstractCondition[e2.length + 1];
        if (e2.length != 0)
            for (int i = 0; i != e2.length; ++i)
                if (e2[i] == null)
                    throw new NullPointerException("Element is null.");
                else
                    conditions[i + 1] = e2[i];
        conditions[0] = e;
    }

    public AbstractCondition[] getConditions() {
        return conditions;
    }

    public int size() {
        return conditions.length;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ConditionList))
            return false;
        return equals((ConditionList) obj);
    }

    public boolean equals(ConditionList list) {
        if (list == null)
            return false;
        if (size() != list.size())
            return false;
        for (int i = 0; i != size(); ++i)
            if (conditions[i] != list.conditions[i])
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        int ch = conditions[0].hashCode();
        for (int i = 1; i != size(); ++i) {
            ch *= i;
            ch += conditions[i].hashCode();
        }
        return 72 + ch;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(10 * size()).append("[ ");
        for (AbstractCondition c : conditions)
            sb.append(c.getIntroduction()).append(", ");
        sb.replace(sb.length() - 2, sb.length(), " ]");
        return sb.toString();
    }
    private final AbstractCondition[] conditions;
}
