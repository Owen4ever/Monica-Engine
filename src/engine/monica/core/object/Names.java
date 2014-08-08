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

import engine.monica.util.SimpleArrayList;
import java.util.HashSet;
import java.util.Set;

public final class Names {

    public Names() {
        this("Nameless");
    }

    public Names(String name, String... nickName) {
        setDisplayName(name);
        if (nickName.length > 0) {
            SimpleArrayList<String> ns = new SimpleArrayList<>(nickName);
            ns.forEach(n -> {
                checkName(n);
            });
            nickNames.addAll(ns);
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String name) {
        checkName(name);
        this.displayName = name;
    }

    public Set<String> getNickNames() {
        return nickNames;
    }

    public void addNickName(String name) {
        checkName(name);
        nickNames.add(name);
    }

    public void removeNickName(String name) {
        nickNames.remove(name);
    }
    private String displayName;
    private final HashSet<String> nickNames = new HashSet<>();

    private static void checkName(String name) {
        if (name == null || name.isEmpty())
            throw new NullPointerException("The name is null.");
    }
}
