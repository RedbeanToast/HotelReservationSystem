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
public class UpdateRoomRateFailedException extends Exception {

    /**
     * Creates a new instance of <code>RoomRateUpdateFailedException</code>
     * without detail message.
     */
    public UpdateRoomRateFailedException() {
    }

    /**
     * Constructs an instance of <code>RoomRateUpdateFailedException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateRoomRateFailedException(String msg) {
        super(msg);
    }
}
