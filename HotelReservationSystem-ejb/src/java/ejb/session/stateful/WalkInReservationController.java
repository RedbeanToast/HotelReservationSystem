/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import ejb.session.stateless.ReservationControllerLocal;
import entities.Guest;
import entities.ReservationLineItem;
import entities.RoomNight;
import entities.WalkInReservation;
import exceptions.CreateNewGuestException;
import exceptions.CreateReservationException;
import exceptions.GuestNotFoundException;
import exceptions.SearchHotelRoomsException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import utilities.RoomSearchResult;

/**
 *
 * @author CaiYuqian
 */
@Stateful
@Remote(WalkInReservationControllerRemote.class)
public class WalkInReservationController implements WalkInReservationControllerRemote {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    @EJB
    private ReservationControllerLocal reservationControllerLocal;
    
    private List<ReservationLineItem> reservationLineItems;
    private GregorianCalendar checkOutDate;
    private BigDecimal totalAmount;

    public WalkInReservationController() {
        initialiseState();
    }

    private void initialiseState(){
        reservationLineItems = new ArrayList<>();
        checkOutDate = null;
        totalAmount = new BigDecimal(0);
    }
    
    @Override
    public Guest createNewGuest(Guest guest) throws CreateNewGuestException {
        // search if there is alreayd a guest with the identification number
        String identificationNum = guest.getIdentificationNumber();
        Query query = em.createQuery("SELECT g FROM Guest g WHERE g.identificationNumber = :inIdentificationNum");
        query.setParameter("inIdentification", identificationNum);
        if(query.getSingleResult() != null){
            throw new CreateNewGuestException("Guest with identification number "+ identificationNum + " already exists!");
        }
        
        em.persist(guest);
        em.flush();
        return guest;
    }
    
    @Override
    public Guest retrieveGuestByIdentificationNumber(@NotNull String identificationNumber) throws GuestNotFoundException {
        Query query = em.createQuery("SELECT g FROM Guest g WHERE g.identificationNumber = :inIdentificationNum");
        try{
            query.setParameter("inIdentification", identificationNumber);
            Guest guest = (Guest)query.getSingleResult();
            return guest;
        }catch(NoResultException| NonUniqueResultException ex){
            throw new GuestNotFoundException("Guest with identification number "+ identificationNumber + " already exists!");
        }
        
    }
    
    // search hotel rooms across all room types based on the checkin date and checkout date
    // each RoomSearchResult should represent only one room type
    // the returning information of room types should be complete
    @Override
    public List<RoomSearchResult> searchHotelRooms(@NotNull GregorianCalendar checkOutDate) throws SearchHotelRoomsException {
        GregorianCalendar checkInDate = new GregorianCalendar();
        this.checkOutDate = checkOutDate;
        List<RoomSearchResult> roomSearchResults = reservationControllerLocal.searchHotelRooms(checkInDate, checkOutDate, "WalkInReservation");
        // lazy fetching all the information about the room type
        for(RoomSearchResult roomSearchResult: roomSearchResults){
            roomSearchResult.getRoomType().getAmenities().size();
        }
        return roomSearchResults;
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
    public WalkInReservation checkOutReservation(@NotNull Long guestId, @NotNull Long employeeId) throws CreateReservationException {
        WalkInReservation reservation = reservationControllerLocal.createNewWalkInReservation(reservationLineItems, guestId, employeeId, checkOutDate, totalAmount);
        initialiseState();
        return reservation;
    }
}
