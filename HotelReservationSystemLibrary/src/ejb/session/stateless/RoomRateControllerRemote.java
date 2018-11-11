/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.RoomRate;
import exceptions.DeleteRoomRateException;
import exceptions.RoomRateNotFoundException;
import exceptions.UpdateRoomRateException;
import java.util.List;
import javax.ejb.Remote;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */

public interface RoomRateControllerRemote {
    RoomRate createNewRoomRate(@NotNull RoomRate roomRate);
    RoomRate retrieveRoomRateById(@NotNull Long rateId) throws RoomRateNotFoundException;
    List<RoomRate> retrieveAllRoomRates();
    void updateRoomRateDetails(RoomRate roomRate) throws UpdateRoomRateException;
    void deleteRoomRate(Long rateId) throws DeleteRoomRateException;
}
