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

import engine.monica.util.StringID;
import java.util.Set;

public final class World implements ConfigInterface {

    public World(ConfigInterface c) {
        if (c == null)
            throw new NullPointerException("The world config is null.");
        config = c;
    }

    @Override
    public <T> void set(StringID key, T value) {
        config.set(key, value);
    }

    @Override
    public <T> T get(StringID key) {
        return config.get(key);
    }

    @Override
    public boolean containKey(StringID id) {
        return config.containKey(id);
    }

    @Override
    public boolean remove(StringID key) {
        return config.remove(key);
    }

    @Override
    public void clearConfig() {
        config.clearConfig();
    }

    @Override
    public Set<StringID> keySet() {
        return config.keySet();
    }
    private final ConfigInterface config;
}