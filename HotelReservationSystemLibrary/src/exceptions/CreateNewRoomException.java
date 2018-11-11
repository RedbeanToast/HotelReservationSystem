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
public class CreateNewRoomException extends Exception {

    /**
     * Creates a new instance of <code>CreateNewRoomFailedException</code>
     * without detail message.
     */
    public CreateNewRoomException() {
    }

    /**
     * Constructs an instance of <code>CreateNewRoomFailedException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNewRoomException(String msg) {
        super(msg);
    }
}
