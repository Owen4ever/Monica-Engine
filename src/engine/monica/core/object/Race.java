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

package engine.monica.core.object;

import engine.monica.core.graphics.GraphicObject;
import engine.monica.core.property.PropertyList;
import engine.monica.util.Vector;
import java.util.Arrays;

public final class Race<V extends Vector<V>> implements NamesGetter {

    public Race(String id, Names name, RoleGender[] enableGender,
            PropertyList defaultProperties, Talent[] talents,
            GraphicObject<V>[] objs) {
        this(id, name, enableGender, defaultProperties, talents, null, objs);
    }

    public Race(String id, Names name, RoleGender[] enableGender,
            PropertyList defaultProperties, Talent[] talents,
            Body defaultBody, GraphicObject<V>[] objs) {
        if (id == null)
            throw new NullPointerException("The id is null.");
        if (name == null)
            throw new NullPointerException("The name is null.");
        if (enableGender == null || enableGender.length == 0)
            throw new NullPointerException("The gender which the race enables is null.");
        for (RoleGender s : enableGender)
            if (s == null)
                throw new NullPointerException("The gender which the race enables is null.");
        if (defaultProperties == null)
            throw new NullPointerException("The array of default properties is null.");
        if (talents == null || talents.length == 0)
            throw new NullPointerException("The talent is null.");
        for (Talent t : talents)
            if (t == null)
                throw new NullPointerException("The talent is null.");
        this.id = id;
        this.names = name;
        this.enableGender = enableGender;
        this.defaultProperties = defaultProperties;
        this.talents = talents;
        hasDefaultBody = defaultBody != null;
        body = defaultBody;
        raceObjects = objs;
    }

    public String getID() {
        return id;
    }

    @Override
    public Names getNames() {
        return names;
    }

    public RoleGender[] getEnableGender() {
        return enableGender;
    }

    public PropertyList getDefaultProperties() {
        return defaultProperties;
    }

    public Talent[] getTalents() {
        return talents;
    }

    public boolean hasDefaultBody() {
        return hasDefaultBody;
    }

    public Body getDefaultBody() {
        return body;
    }

    public GraphicObject<V>[] getRaceObjects() {
        return raceObjects;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != getClass())
            return false;
        Race r = (Race) obj;
        return id.equals(r.id);
    }

    @Override
    public int hashCode() {
        return 138 + id.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName()
                + " [ ID = " + id
                + ", Name = " + names.getDisplayName()
                + ", Enable Genders = " + Arrays.toString(enableGender)
                + ", Default Body = "
                + (hasDefaultBody ? body.getNames().getDisplayName() : "null")
                + " ]";
    }
    private final String id;
    private final Names names;
    private final RoleGender[] enableGender;
    private final PropertyList defaultProperties;
    private final Talent[] talents;
    private final boolean hasDefaultBody;
    private final Body body;
    private final GraphicObject<V>[] raceObjects;
}
