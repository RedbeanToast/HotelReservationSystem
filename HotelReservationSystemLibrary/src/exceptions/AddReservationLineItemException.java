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
public class AddReservationLineItemException extends Exception {

    /**
     * Creates a new instance of <code>AddReservationLineItemException</code>
     * without detail message.
     */
    public AddReservationLineItemException() {
    }

    /**
     * Constructs an instance of <code>AddReservationLineItemException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public AddReservationLineItemException(String msg) {
        super(msg);
    }
}
