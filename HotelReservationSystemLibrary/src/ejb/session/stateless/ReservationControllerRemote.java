/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.Reservation;
import entities.WalkInReservation;
import exceptions.SearchAvailableRoomsFailedException;
import java.util.Calendar;
import java.util.List;
import javax.ejb.Remote;
import javax.validation.constraints.NotNull;
import utilities.RoomSearchResult;

/**
 *
 * @author CaiYuqian
 */

public interface ReservationControllerRemote {
    RoomSearchResult walkinSearchRoomsAvailableByRoomType(@NotNull Calendar checkInDate, @NotNull Calendar checkOutDate, @NotNull String roomTypeName) throws SearchAvailableRoomsFailedException;
    List<RoomSearchResult> walkinSearchRoomsAvailableByCapacity(@NotNull Integer capacityRequired, @NotNull Calendar checkInDate, @NotNull Calendar checkOutDate) throws SearchAvailableRoomsFailedException;
    Reservation reserveRoom(Reservation reservation);
    List<Reservation> retrieveValidReservationsByGuestIdentificationNumber(String identificationNumber);
}
