/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.monica.core.map;

@FunctionalInterface
public interface ElementConcentrationAverager {

    void average(ElementConcentration c1, ElementConcentration c2);
}
