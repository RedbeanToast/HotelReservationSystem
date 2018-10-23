/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.RoomRate;
import exceptions.RoomRateNotFoundException;
import javax.ejb.Local;

/**
 *
 * @author CaiYuqian
 */

public interface RoomRateControllerLocal {
    RoomRate retrieveWalkinRoomRateByRoomTypeName(String roomTypeName) throws RoomRateNotFoundException;
}
