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
public class GuestNotLoggedInException extends Exception {

    /**
     * Creates a new instance of <code>GuestNotLoggedInException</code> without
     * detail message.
     */
    public GuestNotLoggedInException() {
    }

    /**
     * Constructs an instance of <code>GuestNotLoggedInException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public GuestNotLoggedInException(String msg) {
        super(msg);
    }
}
