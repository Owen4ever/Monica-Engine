/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.monica.core.org;

import engine.monica.core.object.NamesGetter;
import engine.monica.core.object.Role;
import engine.monica.util.result.BoolMsgResult;

public interface Organization extends NamesGetter {

    Role[] getOwners();

    Role[] getMembers();

    BoolMsgResult addMember(Role role);

    BoolMsgResult removeMemeber(Role role);

    String getDescribtion();
}
