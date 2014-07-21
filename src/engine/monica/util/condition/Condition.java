/*
 * Copyright (C) 2014 Owen
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package engine.monica.util.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public abstract class Condition {

    public abstract int count();

    public abstract ProviderType[] getConditionTypes();

    public abstract boolean match(Provider... ps);

    public static Condition newSingleCondition(ConditionInterface condition,
            ProviderType... types) {
        if (condition.count() != types.length)
            throw new ConditionInitializeException("The count of"
                    + " conditions does not equal to"
                    + " the length of condition types.");
        if (condition.count() == 1)
            return new Condition() {
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
            return new Condition() {
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
            return new Condition() {
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
            return new Condition() {
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

    public static Condition combineConditions(Condition... cs) {
        final Stream<Condition> stream
                = Arrays.asList(cs).stream();
        final ArrayList<ProviderType> typeList = stream
                .reduce(new ArrayList<ProviderType>(), null, null);
        return new Condition() {
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
                    .mapToInt(Condition::count).sum();
            private final ProviderType[] types
                    = typeList.toArray(new ProviderType[typeList.size()]);
            private final Condition[] conditions = cs;
        };
    }
}
