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
public class CreateReservationException extends Exception {

    /**
     * Creates a new instance of <code>CreateReservationException</code> without
     * detail message.
     */
    public CreateReservationException() {
    }

    /**
     * Constructs an instance of <code>CreateReservationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateReservationException(String msg) {
        super(msg);
    }
}
