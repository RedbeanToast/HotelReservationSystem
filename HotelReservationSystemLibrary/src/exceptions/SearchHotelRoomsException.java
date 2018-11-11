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
public class SearchHotelRoomsException extends Exception {

    /**
     * Creates a new instance of <code>SearchHotelRoomsException</code> without
     * detail message.
     */
    public SearchHotelRoomsException() {
    }

    /**
     * Constructs an instance of <code>SearchHotelRoomsException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public SearchHotelRoomsException(String msg) {
        super(msg);
    }
}
