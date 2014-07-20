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

package engine.monica.core.map;

import engine.monica.core.element.AbstractElement;
import java.util.concurrent.ConcurrentHashMap;
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
        while (true) {
            if (!lock.isWriteLocked() && lock.writeLock().tryLock())
                if (lock.getWriteHoldCount() != 1)
                    lock.writeLock().unlock();
                else
                    break;
        }
        vals.put(e, new ConcentrationValue(concentration));
        count++;
        lock.writeLock().unlock();
    }

    public void setConcentration(AbstractElement e, double concentration) {
        if (e == null)
            throw new NullPointerException("The element is null.");
        if (concentration < 0d)
            throw new ConcentrationValueException("The concentration"
                    + " is less than 0.");
        while (true) {
            if (!lock.isWriteLocked() && lock.writeLock().tryLock())
                if (lock.getWriteHoldCount() != 1)
                    lock.writeLock().unlock();
                else
                    break;
        }
        vals.get(e).setVal(concentration);
        count++;
        lock.writeLock().unlock();
    }

    public double getConcentration(AbstractElement e) {
        return vals.get(e).val;
    }

    public int getElementCount() {
        return count;
    }

    public double getTotal() {
        return vals.values()
                .stream().mapToDouble(ConcentrationValue::getVal).sum();
    }
    private int count = 0;
    private final ConcurrentHashMap<AbstractElement, ConcentrationValue> vals
            = new ConcurrentHashMap<>();
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
