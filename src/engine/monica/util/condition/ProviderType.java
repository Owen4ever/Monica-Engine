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

package engine.monica.util.condition;

import engine.monica.core.engine.CoreEngine;
import engine.monica.util.AlreadyExistsInContainerException;
import engine.monica.util.StringID;
import java.util.HashMap;

public final class ProviderType {

    public ProviderType(StringID id) {
        if (id == null)
            throw new NullPointerException("The id is null.");
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return id.equals(((ProviderType) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode() + 1;
    }

    @Override
    public String toString() {
        return getClass().getName() + id;
    }
    private final StringID id;

    public static StringID newStringID(String sid) {
        if (sid == null || sid.isEmpty())
            throw new NullPointerException("The sid is null.");
        if (sids.containsKey(sid))
            throw new AlreadyExistsInContainerException("The sid has already"
                    + " put into the map.");
        StringID id = new StringID(sid);
        sids.put(sid, id);
        return id;
    }

    public static StringID getStringID(String sid) {
        if (sid == null || sid.isEmpty())
            throw new NullPointerException("The sid is null.");
        return sids.get(sid);
    }
    private static final HashMap<String, StringID> sids
            = new HashMap<>(CoreEngine.getDefaultQuantily(), 0.4f);
}
