/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import entities.OnlineHoRSReservation;
import entities.RegisteredGuest;
import entities.ReservationLineItem;
import exceptions.CreateReservationException;
import exceptions.GuestNotFoundException;
import exceptions.GuestNotLoggedInException;
import exceptions.InvalidLoginCredentialsException;
import exceptions.ReservationNotFoundException;
import exceptions.RetrieveReservationException;
import exceptions.SearchHotelRoomsException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.validation.constraints.NotNull;
import utilities.RoomSearchResult;

/**
 *
 * @author CaiYuqian
 */

public interface HoRSGuestControllerRemote {
    RegisteredGuest registerGuest(@NotNull RegisteredGuest registeredGuest);
    RegisteredGuest loginGuest(@NotNull String email, @NotNull String password) throws GuestNotFoundException, InvalidLoginCredentialsException;
    List<OnlineHoRSReservation> retrieveAllOnlineHoRSReservations(@NotNull String email) throws GuestNotLoggedInException, RetrieveReservationException;
    OnlineHoRSReservation retrieveOnlineHoRSReservationDetailsByReservationId(Long reservationId) throws ReservationNotFoundException;
    List<RoomSearchResult> searchHotelRooms(@NotNull GregorianCalendar checkInDate, @NotNull GregorianCalendar checkOutDate) throws SearchHotelRoomsException;
    BigDecimal addReservationLineItem(@NotNull ReservationLineItem reservationLineItem);
    OnlineHoRSReservation checkOutReservation(@NotNull String email) throws CreateReservationException;
}
