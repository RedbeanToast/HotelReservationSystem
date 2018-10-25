/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.OnlineHoRSReservation;
import entities.OnlinePartnerReservation;
import entities.Reservation;
import entities.Room;
import entities.RoomRate;
import entities.RoomType;
import entities.WalkInReservation;
import enumerations.RoomStatusEnum;
import exceptions.CheckoutFailedException;
import exceptions.ReservationNotFoundException;
import exceptions.RoomNotFoundException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeNotFoundException;
import exceptions.SearchAvailableRoomsFailedException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import utilities.RoomSearchResult;

/**
 *
 * @author CaiYuqian
 */
@Stateless
@Local(ReservationControllerLocal.class)
@Remote(ReservationControllerRemote.class)
public class ReservationController implements ReservationControllerRemote, ReservationControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;
    private RoomRateControllerLocal roomRateControllerLocal;
    private RoomControllerLocal roomControllerLocal;

    public RoomSearchResult walkinSearchRoomsAvailableByRoomType(@NotNull Calendar checkInDate, @NotNull Calendar checkOutDate, @NotNull String roomTypeName) throws SearchAvailableRoomsFailedException {
        if (checkInDate.after(checkOutDate)) {
            throw new SearchAvailableRoomsFailedException("Room search failed: checkin date cannot be after the checkout data!");
        }

        try {
            RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeByName(roomTypeName);
            int numOfRoomsAvailable = roomType.getRooms().size();
            for (Reservation reservation : roomType.getSuccessfulReservations()) {
                //compare the checkin date
                if (reservation.getCheckIn().before(checkInDate)) {
                    if (reservation.getCheckOut().after(checkInDate)) {
                        numOfRoomsAvailable--;
                    }
                } else if (reservation.getCheckIn().equals(checkInDate)) {
                    numOfRoomsAvailable--;
                } else { // reservation's checkin date is after this checkin date
                    if (checkOutDate.after(reservation.getCheckIn())) {
                        numOfRoomsAvailable--;
                    }
                }
            }

            // after have the rooms available during that period, retrieve the published room rate for that room type
            try {
                RoomRate publishedRoomRate = roomRateControllerLocal.retrieveWalkinRoomRateByRoomTypeName(roomTypeName);
                return new RoomSearchResult(roomType, publishedRoomRate, numOfRoomsAvailable);
            } catch (RoomRateNotFoundException ex) {
                throw new SearchAvailableRoomsFailedException("Room search failed: " + ex.getMessage());
            }

        } catch (RoomTypeNotFoundException ex) {
            throw new SearchAvailableRoomsFailedException("Room search failed: " + ex.getMessage());
        }

    }

    public List<RoomSearchResult> walkinSearchRoomsAvailableByCapacity(@NotNull Integer capacityRequired, @NotNull Calendar checkInDate, @NotNull Calendar checkOutDate) throws SearchAvailableRoomsFailedException {
        if (checkInDate.after(checkOutDate)) {
            throw new SearchAvailableRoomsFailedException("Room search failed: checkin date cannot be after the checkout data!");
        }

        List<RoomSearchResult> roomSearchResults = new ArrayList<RoomSearchResult>();
        // retrieve all available room types that have the capacity required
        List<RoomType> roomTypes = roomTypeControllerLocal.retrieveRoomTypesByCapacity(capacityRequired);
        for (RoomType roomType : roomTypes) {
            try {
                int numOfRoomsAvailable = roomType.getRooms().size();
                for (Reservation reservation : roomType.getSuccessfulReservations()) {
                    //compare the checkin date
                    if (reservation.getCheckIn().before(checkInDate)) {
                        if (reservation.getCheckOut().after(checkInDate)) {
                            numOfRoomsAvailable--;
                        }
                    } else if (reservation.getCheckIn().equals(checkInDate)) {
                        numOfRoomsAvailable--;
                    } else { // reservation's checkin date is after this checkin date
                        if (checkOutDate.after(reservation.getCheckIn())) {
                            numOfRoomsAvailable--;
                        }
                    }
                }
                
                RoomRate publishedRoomRate = roomRateControllerLocal.retrieveWalkinRoomRateByRoomTypeName(roomType.getName());
                roomSearchResults.add(new RoomSearchResult(roomType, publishedRoomRate, numOfRoomsAvailable));
            } catch (RoomRateNotFoundException ex) {
                throw new SearchAvailableRoomsFailedException("Room search failed: " + ex.getMessage());
            }
        }
        // todo: sorting based on published rate
        return roomSearchResults;
    }

    // To make a reservation, the room type and room rate must exist in advance otherwise the reservation cannot be made
    // By default, when a reservation is initiated, isUpgraded = false, roomAllocated and allocatedRoomType are null
    // important: a certain room type's associated reservations are SUCCESSFUL reservations
    public Reservation reserveRoom(Reservation reservation) {
        em.persist(reservation);
        em.flush();
        
        return reservation;
    }
    
    public Reservation retrieveReservationById(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if(reservation == null){
            throw new ReservationNotFoundException("Reservation with ID " + reservationId + " does not exist!");
        }
        return reservation;
    }
    
    // The reservations returned are only for valid check-in on the CURRENT date.
    public List<Reservation> retrieveValidReservationsByGuestIdentificationNumber(String identificationNumber) {
        List<Reservation> validReservations = new ArrayList<Reservation>();
        Calendar currentTime = Calendar.getInstance();
            
        // Walkin reservations
        Query walkInReservationQuery = em.createQuery("SELECT wir FROM WalkInReservation wir WHERE wir.identificationNumber = :inIdentificationNumber AND wir.roomAllocated IS NOT NULL");
        walkInReservationQuery.setParameter("inIdentificationNumber", identificationNumber);
        List<WalkInReservation> allWalkInReservations = (List<WalkInReservation>)walkInReservationQuery.getResultList();
        for(WalkInReservation walkInReservation: allWalkInReservations){
            walkInReservation.getRoomAllocated();
        }
        // OnlineHoRSReservations
        Query onlineHoRSReservationQuery = em.createQuery("SELECT ohr FROM OnlineHoRSReservation ohr WHERE ohr.guest.identificationNumber = :inIdentificationNumber AND ohr.roomAllocated IS NOT NULL");
        onlineHoRSReservationQuery.setParameter("inIdentificationNumber", identificationNumber);
        List<OnlineHoRSReservation> allOnlineHoRSReservations = (List<OnlineHoRSReservation>)onlineHoRSReservationQuery.getResultList();
        for(OnlineHoRSReservation onlineHoRSReservation: allOnlineHoRSReservations){
            onlineHoRSReservation.getRoomAllocated();
        }
        // OnlinePartnerReservations
        Query onlinePartnerReservationQuery = em.createQuery("SELECT opr FROM WalkInReservation opr WHERE opr.identificationNumber = :inIdentificationNumber AND opr.roomAllocated IS NOT NULL");
        onlinePartnerReservationQuery.setParameter("inIdentificationNumber", identificationNumber);
        List<OnlinePartnerReservation> allOnlinePartnerReservations = (List<OnlinePartnerReservation>)walkInReservationQuery.getResultList();
        for(OnlinePartnerReservation onlinePartnerReservation: allOnlinePartnerReservations){
            onlinePartnerReservation.getRoomAllocated();
        }
        
        validReservations.addAll(allWalkInReservations);
        validReservations.addAll(allOnlineHoRSReservations);
        validReservations.addAll(allOnlinePartnerReservations);
        
        // Remain those whose checkInDate is today
        Iterator itr = validReservations.iterator();
        while(itr.hasNext()){
            Reservation reservation = (Reservation)itr.next();
            Calendar supposedCheckInDate = reservation.getCheckIn();
            if(currentTime.get(Calendar.YEAR) != supposedCheckInDate.get(Calendar.YEAR) || currentTime.get(Calendar.MONTH) != supposedCheckInDate.get(Calendar.MONTH) 
                   || currentTime.get(Calendar.DATE) != supposedCheckInDate.get(Calendar.DATE)){
                itr.remove();
            } else {
                // currentTime is the same as checkinDate, check whether it's 2 pm
                if(currentTime.get(Calendar.HOUR_OF_DAY) < 14 && !reservation.getRoomAllocated().getRoomStatus().equals(RoomStatusEnum.valueOf("AVAILABLE"))){
                    itr.remove();
                }
            }
        }
        
        return validReservations;
    }
    

}
