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

package game.engine.monica.util.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public abstract class SingleCondition {

    public abstract int count();

    public abstract ProviderType[] getConditionTypes();

    public abstract boolean match(Provider... ps);

    public static SingleCondition newSingleCondition(ConditionInterface condition,
            ProviderType... types) {
        if (condition.count() != types.length)
            throw new ConditionInitializeException("The count of"
                    + " conditions does not equal to"
                    + " the length of condition types.");
        if (condition.count() == 1)
            return new SingleCondition() {
                @Override
                public int count() {
                    return 1;
                }

                @Override
                public ProviderType[] getConditionTypes() {
                    return types;
                }

                @Override
                public boolean match(Provider... ps) {
                    return ((OneConditionInterface) condition).match(ps[0]);
                }
            };
        else if (condition.count() == 2)
            return new SingleCondition() {
                @Override
                public int count() {
                    return 2;
                }

                @Override
                public ProviderType[] getConditionTypes() {
                    return types;
                }

                @Override
                public boolean match(Provider... ps) {
                    return ((TwoConditionInterface) condition).match(ps[0], ps[1]);
                }
            };
        else if (condition.count() == 3)
            return new SingleCondition() {
                @Override
                public int count() {
                    return 3;
                }

                @Override
                public ProviderType[] getConditionTypes() {
                    return types;
                }

                @Override
                public boolean match(Provider... ps) {
                    return ((ThreeConditionInterface) condition).match(ps[0], ps[1], ps[2]);
                }
            };
        else
            return new SingleCondition() {
                @Override
                public int count() {
                    return condition.count();
                }

                @Override
                public ProviderType[] getConditionTypes() {
                    return types;
                }

                @Override
                public boolean match(Provider... ps) {
                    return ((OtherConditionInterface) condition).match(ps);
                }
            };
    }

    public static SingleCondition combineConditions(SingleCondition... cs) {
        final Stream<SingleCondition> stream
                = Arrays.asList(cs).stream();
        final ArrayList<ProviderType> typeList = stream
                .reduce(new ArrayList<ProviderType>(), null, null);
        return new SingleCondition() {
            @Override
            public int count() {
                return count;
            }

            @Override
            public ProviderType[] getConditionTypes() {
                return types;
            }

            @Override
            public boolean match(Provider... ps) {
                if (count != ps.length)
                    return false;
                for (int i = 0, j = 0, size = ps.length; i < size; ++j) {
                    switch (conditions[j].count()) {
                        case 1:
                            if (!conditions[j].match(ps[0]))
                                return false;
                            ++i;
                            break;
                        case 2:
                            if (!conditions[j].match(ps[0], ps[1]))
                                return false;
                            i += 2;
                            break;
                        case 3:
                            if (!conditions[j].match(ps[0], ps[1], ps[2]))
                                return false;
                            i += 3;
                            break;
                        default:
                            int c = conditions[j].count();
                            Provider[] v = new Provider[c];
                            for (int index = 0; index < c; ++index)
                                v[index] = ps[i + index];
                            if (!conditions[j].match(v))
                                return false;
                            i += c;
                            break;
                    }
                }
                return true;
            }
            private final int count = stream
                    .mapToInt(SingleCondition::count).sum();
            private final ProviderType[] types
                    = typeList.toArray(new ProviderType[typeList.size()]);
            private final SingleCondition[] conditions = cs;
        };
    }
}
