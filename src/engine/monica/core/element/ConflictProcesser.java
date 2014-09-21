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

import engine.monica.core.world.Area;
import engine.monica.core.world.Map;
import engine.monica.util.FinalPair;

public interface ConflictProcesser {

    /**
     * @return FinalPair(AbstractElement, Integer): p1,
     * FinalPair(AbstractElement, Integer): p2
     */
    FinalPair<FinalPair<AbstractElement, Integer>, FinalPair<AbstractElement, Integer>>
            conflict(FinalPair<AbstractElement, Integer> p1,
                    FinalPair<AbstractElement, Integer> p2, Map m, Area a);
}
