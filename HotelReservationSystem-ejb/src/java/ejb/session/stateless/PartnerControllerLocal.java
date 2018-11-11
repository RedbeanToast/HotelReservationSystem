/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.OnlinePartnerReservation;
import entities.Partner;
import exceptions.InvalidLoginCredentialsException;
import exceptions.PartnerNotFoundException;
import exceptions.ReservationNotFoundException;
import exceptions.RetrieveReservationException;
import exceptions.SearchHotelRoomsException;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import utilities.RoomSearchResult;

/**
 *
 * @author CaiYuqian
 */

public interface PartnerControllerLocal {
    Partner loginPartner(@NotNull String name, @NotNull String password) throws PartnerNotFoundException, InvalidLoginCredentialsException;
    List<OnlinePartnerReservation> retrieveAllOnlinePartnerReservations(@NotNull String name) throws RetrieveReservationException;
    OnlinePartnerReservation retrieveOnlinePartnerReservationDetailsByReservationId(Long reservationId) throws ReservationNotFoundException;
    List<RoomSearchResult> searchHotelRooms(@NotNull GregorianCalendar checkInDate, @NotNull GregorianCalendar checkOutDate) throws SearchHotelRoomsException;
}
