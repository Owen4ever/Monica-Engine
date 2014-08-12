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

package engine.monica.core.element;

import engine.monica.util.condition.ProviderType;
import static engine.monica.util.condition.ProviderType.newID;

public interface ElementProviderType {

    /* Constants */
    ProviderType PTYPE_ELEMENT_TYPE
            = new ProviderType(newID("MonicaEngine$ProviderType$Element$Type"));
    ProviderType PTYPE_ELEMENT_WEIGHT
            = new ProviderType(newID("MonicaEngine$ProviderType$Element$Weight"));
    ProviderType PTYPE_ELEMENT_COUNT
            = new ProviderType(newID("MonicaEngine$ProviderType$Element$Count"));
    ProviderType PTYPE_ELEMENT_ISBASED
            = new ProviderType(newID("MonicaEngine$ProviderType$Element$IsBased"));
    ProviderType PTYPE_ELEMENT_ISCOMBINED
            = new ProviderType(newID("MonicaEngine$ProviderType$Element$IsCombined"));
    ProviderType PTYPE_ELEMENT_COMBINEDTYPE
            = new ProviderType(newID("MonicaEngine$ProviderType$Element$CombinedType"));
}
