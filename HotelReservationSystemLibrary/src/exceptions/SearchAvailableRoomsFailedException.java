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
public class SearchAvailableRoomsFailedException extends Exception {

    /**
     * Creates a new instance of
     * <code>InvalidSearchingInformationException</code> without detail message.
     */
    public SearchAvailableRoomsFailedException() {
    }

    /**
     * Constructs an instance of
     * <code>InvalidSearchingInformationException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public SearchAvailableRoomsFailedException(String msg) {
        super(msg);
    }
}
