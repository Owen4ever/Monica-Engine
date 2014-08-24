/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.monica.core.input;

public interface Shortcut {

    int getControlKey();

    default boolean hasControlKey() {
        return getControlKey() == ControlKey.NONE;
    }

    String getInputType();

    interface ControlKey {

        int NONE = 0b0000;
        int CTRL_L = 0b0001;
        int CTRL_R = 0b0010;
        int SHIFT_L = 0b0011;
        int SHIFT_R = 0b0100;
        int ALT_L = 0b0101;
        int ALT_R = 0b0110;
        int WIN_L = 0b0111;
        int WIN_R = 0b1000;
        int META = 0b1001;
    }
}
