/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.Guest;
import entities.OnlineHoRSReservation;
import exceptions.InvalidLoginCredentialsException;
import exceptions.OnlineHoRSReservationNotFoundException;
import java.util.List;
import javax.ejb.Remote;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author CaiYuqian
 */

public interface HoRSGuestControllerRemote {
    Guest registerGuest(@NotNull Guest guest);
    Guest loginGuest(@NotNull @Size(min=1, max=64) String email, @NotNull @Size(min=6, max=20) String password) throws InvalidLoginCredentialsException;
    List<OnlineHoRSReservation> retrieveAllOnlineHoRSReservationsByEmail(@NotNull @Size(min=1, max=64) String email);
    OnlineHoRSReservation retrieveOnlineHoRSReservationDetailsByReservationId(@NotNull Long reservationId) throws OnlineHoRSReservationNotFoundException;
}
