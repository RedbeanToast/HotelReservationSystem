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
public class InsufficientNumOfRoomForAllocationException extends Exception {

    /**
     * Creates a new instance of
     * <code>InsufficientNumOfRoomForAllocationException</code> without detail
     * message.
     */
    public InsufficientNumOfRoomForAllocationException() {
    }

    /**
     * Constructs an instance of
     * <code>InsufficientNumOfRoomForAllocationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InsufficientNumOfRoomForAllocationException(String msg) {
        super(msg);
    }
}
