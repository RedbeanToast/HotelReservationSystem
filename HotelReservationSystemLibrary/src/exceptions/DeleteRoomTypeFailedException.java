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
public class DeleteRoomTypeFailedException extends Exception {

    /**
     * Creates a new instance of <code>DisableRoomTypeFailedException</code>
     * without detail message.
     */
    public DeleteRoomTypeFailedException() {
    }

    /**
     * Constructs an instance of <code>DisableRoomTypeFailedException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteRoomTypeFailedException(String msg) {
        super(msg);
    }
}
