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
public class UnableToCreateRemoteCheckoutControllerException extends Exception {

    /**
     * Creates a new instance of
     * <code>UnableToCreateRemoteCheckoutControllerException</code> without
     * detail message.
     */
    public UnableToCreateRemoteCheckoutControllerException() {
    }

    /**
     * Constructs an instance of
     * <code>UnableToCreateRemoteCheckoutControllerException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UnableToCreateRemoteCheckoutControllerException(String msg) {
        super(msg);
    }
}
