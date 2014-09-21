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

import engine.monica.core.property.PropertyList;
import engine.monica.core.world.Area;
import engine.monica.core.world.Map;
import engine.monica.core.world.World;
import engine.monica.util.FinalPair;
import engine.monica.util.Vector;
import engine.monica.util.result.BoolMsgResult;

public interface RaceFactory<V extends Vector<V>> extends Factory {

    RoleGender[] getEnabledGenders();

    boolean enableGender(RoleGender gender);

    Race<V> randomRace();

    Race<V> randomRace(World world, Map<V> map, Area<V> area);

    Race<V> getRaceForRole(World world, Map<V> map, Area<V> area,
            Names names, RoleGender gender, RoleBirth birth, PropertyList properties);

    Role<V> randomRole();

    Role<V> randomRole(Names names);

    Role<V> randomRole(World world, Map<V> map, Area<V> area, Names names);

    Role<V> randomRole(World world, Map<V> map, Area<V> area, RoleBirth birth);

    Role<V> randomRole(World world, Map<V> map, Area<V> area,
            Names names, RoleGender gender, RoleBirth birth, PropertyList properties);

    Role<V> randomRole(World world, Map<V> map, Area<V> area,
            Names names, RoleGender gender, RoleBirth birth, Bloodline<V> bloodline,
            PropertyList properties);

    FinalPair<BoolMsgResult, Role<V>> bearChild(Role<V>... parents);

    FinalPair<BoolMsgResult, Role<V>> bearChild(World world, Map<V> map, Area<V> area,
            RoleBirth birth, Role<V>... parents);

    FinalPair<BoolMsgResult, Role<V>> bearChild(World world, Map<V> map, Area<V> area,
            Names names, RoleGender gender, RoleBirth birth, Bloodline<V> bloodline,
            PropertyList properties, Role<V>... parents);

    int bearNeedParentCount();

    boolean canBear(Role<V>... parents);

    boolean canPlayerBear();

    Role<V> bearPlayer(Names names, RoleGender gender, RoleBirth birth, PropertyList properties);
}
