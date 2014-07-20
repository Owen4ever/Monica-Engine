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

import engine.monica.core.engine.CoreEngine;
import engine.monica.util.LinkedPointer;
import java.util.HashMap;

public final class WorldFactory {

    public WorldFactory() {
    }

    public World createBy(ConfigInterface c) {
        World w = new World(c);
        pointer = pointer.linkNew();
        worlds.put(pointer, w);
        return w;
    }

    public World getWorld(int index) {
        return worlds.get(new LinkedPointer(index));
    }
    private final HashMap<LinkedPointer, World> worlds
            = new HashMap<>(CoreEngine.getDefaultQuantily(), 0.1f);
    private LinkedPointer pointer = LinkedPointer.first();
}
