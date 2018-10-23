/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.Reservation;
import entities.RoomRate;
import entities.RoomType;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeNotFoundException;
import exceptions.SearchAvailableRoomsFailedException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import utilities.RoomSearchResult;

/**
 *
 * @author CaiYuqian
 */
@Stateless
public class ReservationController implements ReservationControllerRemote, ReservationControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;
    private RoomRateControllerLocal roomRateControllerLocal;

    public RoomSearchResult walkinSearchRoomsAvailableByRoomType(@NotNull Calendar checkInDate, @NotNull Calendar checkOutDate, @NotNull String roomTypeName) throws SearchAvailableRoomsFailedException {
        if (checkInDate.after(checkOutDate)) {
            throw new SearchAvailableRoomsFailedException("Room search failed: checkin date cannot be after the checkout data!");
        }

        try {
            RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeByName(roomTypeName);
            int numOfRoomsAvailable = roomType.getRooms().size();
            for (Reservation reservation : roomType.getAllocatedReservations()) {
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
                for (Reservation reservation : roomType.getAllocatedReservations()) {
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

    public void checkInGuest(Long reservationId, String identificationNumber) {

    }
}
