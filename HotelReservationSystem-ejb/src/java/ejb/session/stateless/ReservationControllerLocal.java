/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.Guest;
import entities.OnlineHoRSReservation;
import entities.OnlinePartnerReservation;
import entities.Reservation;
import entities.ReservationLineItem;
import entities.WalkInReservation;
import exceptions.CreateReservationException;
import exceptions.ReservationNotFoundException;
import exceptions.RetrieveReservationException;
import exceptions.SearchHotelRoomsException;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;
import javax.validation.constraints.NotNull;
import utilities.RoomSearchResult;

/**
 *
 * @author CaiYuqian
 */

public interface ReservationControllerLocal {
    List<OnlineHoRSReservation> retrieveAllOnlineHoRSReservationByEmail(@NotNull String email) throws RetrieveReservationException;
    List<OnlinePartnerReservation> retrieveAllOnlinePartnerReservationByName(@NotNull String name) throws RetrieveReservationException;
    Reservation retrieveReservationById(Long reservationId) throws ReservationNotFoundException;
    List<RoomSearchResult> searchHotelRooms(@NotNull GregorianCalendar checkInDate, @NotNull GregorianCalendar checkOutDate, @NotNull String reservatonType) throws SearchHotelRoomsException;
    OnlineHoRSReservation createNewOnlineHoRSReservation(@NotNull List<ReservationLineItem> reservationLineItems, @NotNull String email, @NotNull GregorianCalendar checkInDate, @NotNull GregorianCalendar checkOutDate, @NotNull BigDecimal totalAmount) throws CreateReservationException;
    WalkInReservation createNewWalkInReservation(@NotNull List<ReservationLineItem> reservationLineItems, @NotNull Long guestId, @NotNull Long employeeId, @NotNull GregorianCalendar checkOutDate, @NotNull BigDecimal totalAmount) throws CreateReservationException;
    OnlinePartnerReservation createNewOnlinePartnerReservation(@NotNull OnlinePartnerReservation reservation, @NotNull String partnerName);
}
