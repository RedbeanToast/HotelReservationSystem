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
public class UpgradeRoomTypeNotFound extends Exception {

    /**
     * Creates a new instance of <code>UpgradeRoomTypeNotFound</code> without
     * detail message.
     */
    public UpgradeRoomTypeNotFound() {
    }

    /**
     * Constructs an instance of <code>UpgradeRoomTypeNotFound</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpgradeRoomTypeNotFound(String msg) {
        super(msg);
    }
}
