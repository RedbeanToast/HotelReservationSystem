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
public class DeleteRoomTypeException extends Exception {

    /**
     * Creates a new instance of <code>DisableRoomTypeFailedException</code>
     * without detail message.
     */
    public DeleteRoomTypeException() {
    }

    /**
     * Constructs an instance of <code>DisableRoomTypeFailedException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteRoomTypeException(String msg) {
        super(msg);
    }
}
