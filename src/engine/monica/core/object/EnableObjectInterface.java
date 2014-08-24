/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.monica.core.object;

import engine.monica.core.element.ElementSystem;

public interface EnableObjectInterface {

    boolean enable(Body body);

    boolean enable(Ability ability);

    boolean enable(Career career);

    boolean enable(ElementSystem system);

    boolean enable(Item item);

    boolean enable(Race race);

    boolean enable(Role role);
}
