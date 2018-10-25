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
public class CheckoutFailedException extends Exception {

    /**
     * Creates a new instance of <code>CheckoutFailedException</code> without
     * detail message.
     */
    public CheckoutFailedException() {
    }

    /**
     * Constructs an instance of <code>CheckoutFailedException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CheckoutFailedException(String msg) {
        super(msg);
    }
}
