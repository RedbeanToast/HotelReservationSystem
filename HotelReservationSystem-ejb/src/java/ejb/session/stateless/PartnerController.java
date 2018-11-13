/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.OnlinePartnerReservation;
import entities.Partner;
import entities.Reservation;
import entities.ReservationLineItem;
import entities.RoomType;
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
        List<OnlinePartnerReservation> reservations = reservationControllerLocal.retrieveAllOnlinePartnerReservationByName(name);
        // nullify the bidirectional relationship attributes to avoid cyclic reference in web service
        // the information returned will only be the online reservations not associated with anything
        // room nights are excluded
        for(OnlinePartnerReservation reservation: reservations){
            em.detach(reservation);
            reservation.getReservationLineItems().clear();
            reservation.setPartner(null);
            reservation.setGuest(null);
        }
        
        return reservations;
    }
    
    @Override 
    public OnlinePartnerReservation retrieveOnlinePartnerReservationDetailsByReservationId(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = reservationControllerLocal.retrieveReservationById(reservationId);
        if(reservation instanceof OnlinePartnerReservation){
            OnlinePartnerReservation reservationFound =  (OnlinePartnerReservation)reservation;
            em.detach(reservationFound);
            // nullify the bidirection relationship attributes to avoid cyclic reference issue in web service
            for(ReservationLineItem reservationLineItem: reservationFound.getReservationLineItems()){
                reservationLineItem.setReservation(null);
                reservationLineItem.getRoomNights().clear();
            }
            return reservationFound;
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
            RoomType roomType = roomSearchResult.getRoomType();
            roomType.getAmenities().size();
            em.detach(roomType);
            roomType.setRooms(null);
            
        }
        return roomSearchResults;
    }
    
    @Override
    public Partner retrievePartnerByName(@NotNull String name) throws PartnerNotFoundException {
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
