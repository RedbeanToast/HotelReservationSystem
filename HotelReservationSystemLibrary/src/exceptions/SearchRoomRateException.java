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
public class SearchRoomRateException extends Exception {

    /**
     * Creates a new instance of <code>SearchRoomRateException</code> without
     * detail message.
     */
    public SearchRoomRateException() {
    }

    /**
     * Constructs an instance of <code>SearchRoomRateException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public SearchRoomRateException(String msg) {
        super(msg);
    }
}
