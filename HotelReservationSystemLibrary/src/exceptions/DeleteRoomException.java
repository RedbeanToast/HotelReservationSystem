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
public class DeleteRoomException extends Exception {

    /**
     * Creates a new instance of <code>RoomDeletionFailedException</code>
     * without detail message.
     */
    public DeleteRoomException() {
    }

    /**
     * Constructs an instance of <code>RoomDeletionFailedException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteRoomException(String msg) {
        super(msg);
    }
}
