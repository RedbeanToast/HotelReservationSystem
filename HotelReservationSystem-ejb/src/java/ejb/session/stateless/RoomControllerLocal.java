/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.Room;
import exceptions.InsufficientNumOfRoomForAllocationException;
import exceptions.RoomNotFoundException;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Local;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */

public interface RoomControllerLocal {
    Room retrieveRoomByRoomNumber(Integer roomNumber) throws RoomNotFoundException;
    List<Room> searchAvailableRoomsForAllocation(@NotNull Long roomTypeID, @NotNull Integer numOfRoomsRequired, @NotNull GregorianCalendar checkOutDate) throws InsufficientNumOfRoomForAllocationException;
}
