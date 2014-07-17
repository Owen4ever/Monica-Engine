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

package engine.monica.core.element;

import engine.monica.util.condition.ProviderType;
import static engine.monica.util.condition.ProviderType.newStringID;

public interface ElementProviderType {

    /* Constants */
    ProviderType PTYPE_ELEMENT_TYPE
            = new ProviderType(newStringID("MonicaEngine$ProviderType$Element$Type"));
    ProviderType PTYPE_ELEMENT_WEIGHT
            = new ProviderType(newStringID("MonicaEngine$ProviderType$Element$Weight"));
    ProviderType PTYPE_ELEMENT_COUNT
            = new ProviderType(newStringID("MonicaEngine$ProviderType$Element$Count"));
    ProviderType PTYPE_ELEMENT_ISBASED
            = new ProviderType(newStringID("MonicaEngine$ProviderType$Element$IsBased"));
    ProviderType PTYPE_ELEMENT_ISCOMBINED
            = new ProviderType(newStringID("MonicaEngine$ProviderType$Element$IsCombined"));
    ProviderType PTYPE_ELEMENT_COMBINEDTYPE
            = new ProviderType(newStringID("MonicaEngine$ProviderType$Element$CombinedType"));
}
