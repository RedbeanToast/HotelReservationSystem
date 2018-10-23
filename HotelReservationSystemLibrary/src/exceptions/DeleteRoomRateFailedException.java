/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author CaiYuqian
 */
public class DeleteRoomRateFailedException extends Exception {

    /**
     * Creates a new instance of <code>DeleteRoomRateFailed</code> without
     * detail message.
     */
    public DeleteRoomRateFailedException() {
    }

    /**
     * Constructs an instance of <code>DeleteRoomRateFailed</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteRoomRateFailedException(String msg) {
        super(msg);
    }
}
