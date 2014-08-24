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

package engine.monica.core.engine;

import engine.monica.core.element.ElementCalculator;
import engine.monica.core.element.ElementConcentrationCalculator;
import engine.monica.core.element.ElementCountSet;
import engine.monica.core.element.ElementList;
import engine.monica.core.element.ElementProviderType;
import engine.monica.core.graphics.GraphicObject;
import engine.monica.core.input.InputConstants;
import engine.monica.core.map.ElementConcentrationAverager;
import engine.monica.util.FinalPair;

/**
 * Concentrate all the constants which are available to public.
 */
public interface EngineConstants extends
        ElementProviderType,
        InputConstants {

    ElementList ELEMENT_LIST_NULL = new ElementList();
    ElementCountSet ELEMENT_COUNT_SET_NULL = new ElementCountSet();
    GraphicObject[] ARRAY_GRAPHIC_OBJECT_NULL = new GraphicObject[0];
    ElementConcentrationCalculator ELEMENT_CONCENTRATION_CALC_DEFAULT
            = (p, map, area) -> {
                double per
                = area.getElementConcentration().getConcentration(p.first)
                / area.getElementConcentration().getTotal();
                if (per >= .8)
                    return new FinalPair<>(p.first, p.last << 1);
                else if (per >= .68)
                    return new FinalPair<>(p.first, (p.last * 3) >> 1);
                else if (per >= .56)
                    return new FinalPair<>(p.first, (p.last * 10) >> 3);
                else
                    return p;
            };
    @SuppressWarnings("unchecked")
    ElementCalculator ELEMENT_CALC_DEFAULT
            = (p1, p2, map, area, c) -> {
                int i = p1.last - p2.last;
                if (i > 0)
                    return new FinalPair[]{new FinalPair<>(p1.first, i), new FinalPair<>(p2.first, -p2.last)};
                else if (i < 0)
                    return new FinalPair[]{new FinalPair<>(p2.first, i), new FinalPair<>(p1.first, -p1.last)};
                else
                    return null;
            };

    ElementConcentrationAverager ELEMENT_CONCENTRATION_AVERAGER_DEFAULT
            = (c1, c2) -> {
                c1.getElements().parallelStream().forEach(e1 -> {
                    double dc1 = c1.getConcentration(e1);
                    double dc2 = c2.getConcentration(e1);
                    if (dc2 == 0d)
                        c2.setConcentration(e1, dc1 / 10);
                    else
                        c2.setConcentration(e1, (dc1 + dc2) / 2);
                });
                c2.getElements().parallelStream().forEach(e2 -> {
                    double dc1 = c1.getConcentration(e2);
                    if (dc1 == 0d)
                        c2.setConcentration(e2, c2.getConcentration(e2) / 10);
                });
            };
}
