/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import entities.OnlineHoRSReservation;
import entities.OnlinePartnerReservation;
import entities.ReservationLineItem;
import exceptions.CreateReservationException;
import java.math.BigDecimal;
import javax.ejb.Local;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */
@Local
public interface PartnerCheckoutControllerLocal {
    BigDecimal addReservationLineItem(@NotNull ReservationLineItem reservationLineItem);
    
    OnlinePartnerReservation checkOutReservation(@NotNull String partnerName, @NotNull String identificationNumber,
            @NotNull String firstName, @NotNull String lastName, @NotNull String phoneNumber) 
            throws CreateReservationException;
}
