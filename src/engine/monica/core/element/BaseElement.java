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

/**
 * A child class of {@code AbstractElement} and all the base elements are the
 * instance of {@code BaseElement} such as {@code water}, {@code fire},
 * {@code ice}, {@code air}, {@code electricity}.
 */
public class BaseElement extends AbstractElement {

    private static final long serialVersionUID = 46274354281733422L;

    public BaseElement(String systemId,
            String id, String name, int turnToEnergy) {
        super(systemId, id, name, turnToEnergy);
    }

    @Override
    public final boolean isCombined() {
        return false;
    }
}
