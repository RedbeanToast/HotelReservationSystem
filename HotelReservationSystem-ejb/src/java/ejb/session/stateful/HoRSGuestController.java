/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import ejb.session.stateless.ReservationControllerLocal;
import entities.OnlineHoRSReservation;
import entities.RegisteredGuest;
import entities.Reservation;
import entities.ReservationLineItem;
import entities.RoomNight;
import entities.RoomRate;
import entities.RoomType;
import exceptions.AddReservationLineItemException;
import exceptions.CreateReservationException;
import exceptions.GuestNotFoundException;
import exceptions.GuestNotLoggedInException;
import exceptions.InvalidLoginCredentialsException;
import exceptions.ReservationNotFoundException;
import exceptions.RetrieveReservationException;
import exceptions.SearchHotelRoomsException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
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
@Remote(HoRSGuestControllerRemote.class)
public class HoRSGuestController implements HoRSGuestControllerRemote {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    @EJB
    private ReservationControllerLocal reservationControllerLocal;
    
    private List<ReservationLineItem> reservationLineItems;
    private GregorianCalendar checkInDate;
    private GregorianCalendar checkOutDate;
    private BigDecimal totalAmount;
    

    public HoRSGuestController() {
        initialiseState();
    }

    private void initialiseState(){
        reservationLineItems = new ArrayList<>();
        checkInDate = null;
        checkOutDate = null;
        totalAmount = new BigDecimal(0);
    }
    
    @Override
    public RegisteredGuest registerGuest(@NotNull RegisteredGuest registeredGuest){
        em.persist(registeredGuest);
        em.flush();
        
        return registeredGuest;
    }
    
    @Override
    public RegisteredGuest loginGuest(@NotNull String email, @NotNull String password) throws GuestNotFoundException, InvalidLoginCredentialsException {
        RegisteredGuest registeredGuest = retrieveRegisteredGuestByEmail(email);
        if(registeredGuest.getPassword().equals(password)){
            return registeredGuest;
        }else{
            throw new InvalidLoginCredentialsException("Wrong password!");
        }
    }
    
    @Override
    public List<OnlineHoRSReservation> retrieveAllOnlineHoRSReservations(@NotNull String email) throws GuestNotLoggedInException, RetrieveReservationException {
        return reservationControllerLocal.retrieveAllOnlineHoRSReservationByEmail(email);
    }
    
    @Override 
    public OnlineHoRSReservation retrieveOnlineHoRSReservationDetailsByReservationId(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = reservationControllerLocal.retrieveReservationById(reservationId);
        if(reservation instanceof OnlineHoRSReservation){
            return (OnlineHoRSReservation)reservation;
        }else{
            throw new ReservationNotFoundException("There is no online HoRS reservation with id " + reservationId + "!");
        }
    } 
    
    // search hotel rooms across all room types based on the checkin date and checkout date
    // each RoomSearchResult should represent only one room type
    // the returning information of room types should be complete
    @Override
    public List<RoomSearchResult> searchHotelRooms(@NotNull GregorianCalendar checkInDate, @NotNull GregorianCalendar checkOutDate) throws SearchHotelRoomsException {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        List<RoomSearchResult> roomSearchResults = reservationControllerLocal.searchHotelRooms(checkInDate, checkOutDate, "OnlineHoRSReservation");
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
    public OnlineHoRSReservation checkOutReservation(@NotNull String email) throws CreateReservationException {
        OnlineHoRSReservation reservation = reservationControllerLocal.createNewOnlineHoRSReservation(reservationLineItems, email, checkInDate, checkOutDate, totalAmount);
        initialiseState();
        return reservation;
    }

    private RegisteredGuest retrieveRegisteredGuestByEmail(@NotNull String email) throws GuestNotFoundException {
        Query query = em.createQuery("SELECT rg FROM RegisteredGuest rg WHERE rg.email = :inEmail");
        query.setParameter("inEmail", email);
        try{
            RegisteredGuest registeredGuest = (RegisteredGuest)query.getSingleResult();
            return registeredGuest;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new GuestNotFoundException("There is no guest registered with email " + email + "!");
        }
    }
}
