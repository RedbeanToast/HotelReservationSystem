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
public class CheckinFailedException extends Exception {

    /**
     * Creates a new instance of <code>CheckinFailedException</code> without
     * detail message.
     */
    public CheckinFailedException() {
    }

    /**
     * Constructs an instance of <code>CheckinFailedException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CheckinFailedException(String msg) {
        super(msg);
    }
}
