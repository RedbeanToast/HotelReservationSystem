/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.OnlineHoRSReservation;
import entities.OnlinePartnerReservation;
import entities.Partner;
import entities.Reservation;
import exceptions.InvalidLoginCredentialsException;
import exceptions.PartnerNotFoundException;
import exceptions.ReservationNotFoundException;
import exceptions.RetrieveReservationException;
import exceptions.SearchHotelRoomsException;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
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
@Stateless
@Local(PartnerControllerLocal.class)
public class PartnerController implements PartnerControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    @EJB
    private ReservationControllerLocal reservationControllerLocal;

    public PartnerController() {
    }

    @Override
    public Partner loginPartner(@NotNull String name, @NotNull String password) throws PartnerNotFoundException, InvalidLoginCredentialsException {
        Partner partner = retrievePartnerByName(name);
        if (partner.getPassword().equals(password)) {
            return partner;
        } else {
            throw new InvalidLoginCredentialsException("Wrong password!");
        }
    }
    
    @Override
    public List<OnlinePartnerReservation> retrieveAllOnlinePartnerReservations(@NotNull String name) throws RetrieveReservationException {
        return reservationControllerLocal.retrieveAllOnlinePartnerReservationByName(name);
    }
    
    @Override 
    public OnlinePartnerReservation retrieveOnlinePartnerReservationDetailsByReservationId(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = reservationControllerLocal.retrieveReservationById(reservationId);
        if(reservation instanceof OnlinePartnerReservation){
            return (OnlinePartnerReservation)reservation;
        }else{
            throw new ReservationNotFoundException("There is no online partner reservation with id " + reservationId + "!");
        }
    } 
    
    // search hotel rooms across all room types based on the checkin date and checkout date
    // each RoomSearchResult should represent only one room type
    // the returning information of room types should be complete
    @Override
    public List<RoomSearchResult> searchHotelRooms(@NotNull GregorianCalendar checkInDate, @NotNull GregorianCalendar checkOutDate) throws SearchHotelRoomsException {
        List<RoomSearchResult> roomSearchResults = reservationControllerLocal.searchHotelRooms(checkInDate, checkOutDate, "OnlinePartnerReservation");
        // lazy fetching all the information about the room type
        for(RoomSearchResult roomSearchResult: roomSearchResults){
            roomSearchResult.getRoomType().getAmenities().size();
        }
        return roomSearchResults;
    }
    
    // because online partners will use web services to do the room reservation, only stateless session bean can be used.
    // hence, parameter for the check out can only be the whole reservation object
    public Reservation checkOutReservation(@NotNull OnlinePartnerReservation reservation, @NotNull String partnerName){
        return reservationControllerLocal.createNewOnlinePartnerReservation(reservation, partnerName);
    }
    
    private Partner retrievePartnerByName(@NotNull String name) throws PartnerNotFoundException{
        Query query = em.createQuery("SELECT p FROM Partner p WHERE p.name = :inName");
        query.setParameter("inName", name);
        try{
            Partner partner = (Partner)query.getSingleResult();
            return partner;
        }catch(NoResultException | NonUniqueResultException ex){
            throw new PartnerNotFoundException("Partner " + name + " does not exist!");
        }
    }
}
