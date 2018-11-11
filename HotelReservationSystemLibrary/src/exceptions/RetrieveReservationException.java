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
public class RetrieveReservationException extends Exception {

    /**
     * Creates a new instance of <code>RetrieveReservationException</code>
     * without detail message.
     */
    public RetrieveReservationException() {
    }

    /**
     * Constructs an instance of <code>RetrieveReservationException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public RetrieveReservationException(String msg) {
        super(msg);
    }
}
