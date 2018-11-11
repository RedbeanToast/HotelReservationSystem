/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import entities.Guest;
import entities.ReservationLineItem;
import entities.WalkInReservation;
import exceptions.CreateNewGuestException;
import exceptions.CreateReservationException;
import exceptions.GuestNotFoundException;
import exceptions.SearchHotelRoomsException;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Remote;
import javax.validation.constraints.NotNull;
import utilities.RoomSearchResult;

/**
 *
 * @author CaiYuqian
 */

public interface WalkInReservationControllerRemote {
    Guest createNewGuest(Guest guest) throws CreateNewGuestException;
    Guest retrieveGuestByIdentificationNumber(@NotNull String identificationNumber) throws GuestNotFoundException;
    List<RoomSearchResult> searchHotelRooms(@NotNull GregorianCalendar checkOutDate) throws SearchHotelRoomsException;
    BigDecimal addReservationLineItem(@NotNull ReservationLineItem reservationLineItem);
    WalkInReservation checkOutReservation(@NotNull Long guestId, @NotNull Long employeeId) throws CreateReservationException;
}
