/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.monica.core.object;

import engine.monica.core.element.ElementSystem;

public interface Ability extends EnableObjectInterface, NamesGetter {
    
    String getID();

    ElementSystem[] getEnabledElementSystems();

    Race[] getEnabledRaces();

    Career[] getEnabledCareers();
}
