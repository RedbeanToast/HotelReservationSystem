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
public class CreateNewGuestException extends Exception {

    /**
     * Creates a new instance of <code>CreateNewGuestException</code> without
     * detail message.
     */
    public CreateNewGuestException() {
    }

    /**
     * Constructs an instance of <code>CreateNewGuestException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNewGuestException(String msg) {
        super(msg);
    }
}
