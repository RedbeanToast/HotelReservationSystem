/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import ejb.session.stateless.ReservationControllerLocal;
import entities.OnlinePartnerReservation;
import entities.ReservationLineItem;
import entities.RoomNight;
import exceptions.CreateReservationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */
@Stateful
public class PartnerCheckoutController implements PartnerCheckoutControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    @EJB
    private ReservationControllerLocal reservationControllerLocal;
    
    private List<ReservationLineItem> reservationLineItems;
    private GregorianCalendar checkInDate;
    private GregorianCalendar checkOutDate;
    private BigDecimal totalAmount;

    public PartnerCheckoutController() {
        initialiseState();
    }
    
    @Remove
    public void remove()
    {        
    }
    
    private void initialiseState(){
        reservationLineItems = new ArrayList<>();
        checkInDate = null;
        checkOutDate = null;
        totalAmount = new BigDecimal(0);
    }
    
    @Override
    public BigDecimal addReservationLineItem(@NotNull ReservationLineItem reservationLineItem) {  
        reservationLineItems.add(reservationLineItem);
        
        // calculate the subtotal amount
        BigDecimal subTotal = new BigDecimal(0);
        for(RoomNight roomNight: reservationLineItem.getRoomNights()){
            subTotal.add(roomNight.getRoomRate().getRatePerNight());
        }
        totalAmount.add(subTotal);
        return subTotal;
    }
    
    @Override
    public OnlinePartnerReservation checkOutReservation(@NotNull String partnerName, @NotNull String identificationNumber,
            @NotNull String firstName, @NotNull String lastName, @NotNull String phoneNumber) 
            throws CreateReservationException {
        OnlinePartnerReservation reservation = reservationControllerLocal.createNewOnlinePartnerReservation(reservationLineItems, 
                partnerName, checkInDate, checkOutDate, totalAmount, identificationNumber, firstName, lastName, phoneNumber);
        initialiseState();
        
        return reservation;
    }
}
