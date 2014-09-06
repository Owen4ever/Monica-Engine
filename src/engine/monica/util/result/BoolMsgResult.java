/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package engine.monica.util.result;

import java.util.function.Supplier;

public final class BoolMsgResult {

    public BoolMsgResult(boolean success, String msg) {
        this.success = success;
        this.message = msg;
    }
    public final boolean success;
    public final String message;

    public static BoolMsgResult make(Supplier<Boolean> success, Supplier<String> msg) {
        return new BoolMsgResult(success.get(), msg.get());
    }
}
