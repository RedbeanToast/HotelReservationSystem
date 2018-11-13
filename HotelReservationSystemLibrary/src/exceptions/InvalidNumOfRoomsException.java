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
public class InvalidNumOfRoomsException extends Exception {

    /**
     * Creates a new instance of <code>InvalidNumOfRoomsException</code> without
     * detail message.
     */
    public InvalidNumOfRoomsException() {
    }

    /**
     * Constructs an instance of <code>InvalidNumOfRoomsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidNumOfRoomsException(String msg) {
        super(msg);
    }
}
