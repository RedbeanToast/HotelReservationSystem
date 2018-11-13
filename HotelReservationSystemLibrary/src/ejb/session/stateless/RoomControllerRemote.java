/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.LateCheckoutExceptionReport;
import entities.Room;
import exceptions.CreateNewRoomException;
import exceptions.DeleteRoomException;
import exceptions.RoomNotFoundException;
import exceptions.UpdateRoomException;
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */

public interface RoomControllerRemote {
    Room createNewRoom(@NotNull Integer roomNumber, @NotNull String roomTypeName) throws CreateNewRoomException;
    Room retrieveRoomByRoomNumber(Integer roomNumber) throws RoomNotFoundException;
    List<Room> retrieveAllRooms();
    void updateRoom(@NotNull Room room, @NotNull Boolean isBusinessRelated) throws UpdateRoomException;
    void deleteRoom(@NotNull Integer roomNumber) throws DeleteRoomException;
    List<LateCheckoutExceptionReport> retrieveLateCheckoutExceptionReports();
}
