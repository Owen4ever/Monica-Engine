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

public final class RoleGender {

    public RoleGender(String gender, int id) {
        this.gender = gender;
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != getClass())
            return false;
        RoleGender s = (RoleGender) obj;
        return id == s.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return gender + "(" + id + ")";
    }
    private final String gender;
    private final int id;

    public static final RoleGender MALE = new RoleGender("Male", 0);
    public static final RoleGender FEMALE = new RoleGender("Female", 1);
}
