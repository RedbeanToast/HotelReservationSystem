/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.RoomRate;
import exceptions.DeleteRoomRateFailedException;
import exceptions.RoomRateNotFoundException;
import exceptions.UpdateRoomRateFailedException;
import javax.ejb.Remote;

/**
 *
 * @author CaiYuqian
 */

public interface RoomRateControllerRemote {
    RoomRate createNewRoomRate(RoomRate roomRate);
    RoomRate retrieveRoomRateById(Long rateId) throws RoomRateNotFoundException;
    void updateRoomRateDetails(RoomRate roomRate) throws UpdateRoomRateFailedException;
    void deleteRoomRate(Long rateId) throws DeleteRoomRateFailedException;
}
