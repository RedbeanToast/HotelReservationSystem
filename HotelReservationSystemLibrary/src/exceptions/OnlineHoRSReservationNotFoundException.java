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
public class OnlineHoRSReservationNotFoundException extends Exception {

    /**
     * Creates a new instance of
     * <code>OnlineHoRSReservationNotFoundException</code> without detail
     * message.
     */
    public OnlineHoRSReservationNotFoundException() {
    }

    /**
     * Constructs an instance of
     * <code>OnlineHoRSReservationNotFoundException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public OnlineHoRSReservationNotFoundException(String msg) {
        super(msg);
    }
}
