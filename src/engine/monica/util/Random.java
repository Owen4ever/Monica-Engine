/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.monica.util;

public interface Random<T> {

    void setSeed(int seed);

    T next();
}
