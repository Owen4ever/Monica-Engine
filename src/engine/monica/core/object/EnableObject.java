/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.monica.core.object;

import engine.monica.core.element.ElementSystem;

public abstract class EnableObject implements EnableObjectInterface {

    @Override
    public boolean enable(Body body) { return true; }

    @Override
    public boolean enable(Ability ability) { return true; }

    @Override
    public boolean enable(Career career) { return true; }

    @Override
    public boolean enable(ElementSystem system) { return true; }

    @Override
    public boolean enable(Item item) { return true; }

    @Override
    public boolean enable(Race race) { return true; }

    @Override
    public boolean enable(Role role) { return true; }
}
