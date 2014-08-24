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

package engine.monica.core.map;

import engine.monica.core.element.AbstractElement;
import static engine.monica.util.Lock.*;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ElementConcentration {

    public ElementConcentration() {
    }

    public void addConcentration(AbstractElement e, double concentration) {
        if (e == null)
            throw new NullPointerException("The element is null.");
        if (concentration < 0d)
            throw new ConcentrationValueException("The concentration"
                    + " is less than 0.");
        getWriteLock(lock);
        RET(lock, () -> {
            vals.put(e, new ConcentrationValue(concentration));
            count++;
            total = -1d;
            average = -1d;
        });
    }

    public void setConcentration(AbstractElement e, double concentration) {
        if (e == null)
            throw new NullPointerException("The element is null.");
        if (concentration < 0d)
            throw new ConcentrationValueException("The concentration"
                    + " is less than 0.");
        getWriteLock(lock);
        RET(lock, () -> {
            vals.get(e).setVal(concentration);
            count++;
            total = -1d;
            average = -1d;
        });
    }

    public double getConcentration(AbstractElement e) {
        ConcentrationValue c = vals.get(e);
        return c != null ? c.val : 0d;
    }

    public Set<AbstractElement> getElements() {
        return vals.keySet();
    }

    public int getElementCount() {
        return count;
    }

    public double getTotal() {
        if (total < 0d)
            total = vals.values()
                    .stream().mapToDouble(ConcentrationValue::getVal).sum();
        return total;
    }

    public double getAverage() {
        if (average < 0d)
            average = getTotal() / vals.size();
        return average;
    }

    public static void average(ElementConcentration c1, ElementConcentration c2,
            ElementConcentrationAverager a) {
        average(c1, c2, a, false);
    }

    public static void average(ElementConcentration c1, ElementConcentration c2,
            ElementConcentrationAverager a, boolean needLock) {
        if (needLock) {
            getWriteLock(c1.lock);
            getWriteLock(c2.lock);
            RET(c1.lock, () -> {
                RET(c2.lock, () -> {
                    a.average(c1, c2);
                });
            });
        } else
            a.average(c1, c2);
    }
    private int count = 0;
    private final HashMap<AbstractElement, ConcentrationValue> vals
            = new HashMap<>();
    private transient double total = -1d, average = -1d;
    private transient final ReentrantReadWriteLock lock
            = new ReentrantReadWriteLock();

    private static final class ConcentrationValue {

        public ConcentrationValue(double val) {
            this.val = val;
        }

        public final double getVal() {
            return val;
        }

        public final void setVal(double val) {
            this.val = val;
        }
        public double val = 0d;
    }
}
