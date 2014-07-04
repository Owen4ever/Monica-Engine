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

package game.engine.monica.core.engine;

public class EngineThread extends Thread {

    public EngineThread(EngineRunnable r) {
        this(TG_UNGROUPED, r);
    }

    public EngineThread(EngineRunnable r, String name) {
        this(TG_UNGROUPED, r, name);
    }

    public EngineThread(EngineThreadGroup g, EngineRunnable r) {
        this(g, r, "Eternal World - Unname Thread " + getCurrentThreadCount());
    }

    public EngineThread(EngineThreadGroup g, EngineRunnable r, String name) {
        super(g, r, name);
    }

    public static void sleepWithoutException(long delay) {
        try {
            sleep(delay);
        } catch (InterruptedException ex) {
        }
    }

    public static void sleepWithoutException(long delay, int nano) {
        try {
            sleep(delay, nano);
        } catch (InterruptedException ex) {
        }
    }

    public static void sleepAndNeedWakeUp(long delay) throws InterruptedException {
        CoreEngine.engineThreadRouser.addNeedWakeUpThread(Thread.currentThread());
        sleep(delay);
        CoreEngine.engineThreadRouser.removeWakeUpThread(Thread.currentThread());
    }

    private static int threadCount = 0;

    private static synchronized int getCurrentThreadCount() {
        return threadCount++;
    }

    private static final EngineThreadGroup TG_UNGROUPED = new EngineThreadGroup("Ungrouped");
}
