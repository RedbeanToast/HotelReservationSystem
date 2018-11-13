/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.LateCheckoutExceptionReport;
import entities.Reservation;
import entities.Room;
import entities.RoomNight;
import entities.RoomType;
import enumerations.ReservationStatusEnum;
import enumerations.RoomStatusEnum;
import exceptions.CreateNewRoomException;
import exceptions.DeleteRoomException;
import exceptions.InsufficientNumOfRoomForAllocationException;
import exceptions.RoomNotFoundException;
import exceptions.RoomTypeNotFoundException;
import exceptions.UpdateRoomException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */
@Stateless
@Local(RoomControllerLocal.class)
@Remote(RoomControllerRemote.class)
public class RoomController implements RoomControllerRemote, RoomControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    private TimerService timerService;

    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;

    @Override
    public Room createNewRoom(@NotNull Integer roomNumber, @NotNull String roomTypeName) throws CreateNewRoomException {
        try {
            RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeByName(roomTypeName);
            Room room = new Room(roomNumber, RoomStatusEnum.valueOf("AVAILABLE"), true, roomType);
            room.setRoomType(roomType);
            roomType.getRooms().add(room);
            em.persist(room);
            em.flush();
            return room;
        } catch (RoomTypeNotFoundException ex) {
            throw new CreateNewRoomException("New room creation failed: " + ex.getMessage());
        }
    }

    @Override
    public Room retrieveRoomByRoomNumber(Integer roomNumber) throws RoomNotFoundException {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomNumber = :inRoomNumber AND r.enabled = TRUE");
        query.setParameter("inRoomNumber", roomNumber);
        try {
            Room room = (Room) query.getSingleResult();
            room.getCurrentOccupancy();
            room.getRoomType();
            return room;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomNotFoundException("Room with room number " + roomNumber + " does not exist or is not in use!");
        }
    }

    // retrieve all room records 
    @Override
    public List<Room> retrieveAllRooms() {
        Query query = em.createQuery("SELECT r FROM Room r ORDER BY r.roomnumber");
        List<Room> rooms = query.getResultList();
        for (Room room : rooms) {
            room.getRoomType();
        }
        return rooms;
    }

    // change the status of the room
    // set the currentOccupancy of the room
    @Override
    public void updateRoom(@NotNull Room room, @NotNull Boolean isBusinessRelated) throws UpdateRoomException {
        Room roomToBeUpdated = em.find(Room.class, room.getRoomId());
        if (roomToBeUpdated != null) {
            if (!isBusinessRelated) {
                // related to internal operations, such as changing room type
                if (!roomToBeUpdated.getRoomType().equals(room.getRoomType())) {
                    roomToBeUpdated.setRoomType(room.getRoomType());
                }
            } else {
                // related to business operations (check in or checkout)
                if (!roomToBeUpdated.getCurrentOccupancy().equals(room.getCurrentOccupancy())) {
                    // the room is preparing for either checkin or checkout
                    if (roomToBeUpdated.getCurrentOccupancy() != null) {
                        // the room is previously occupied 
                        if (room.getCurrentOccupancy() == null) {
                            // the room is ready for checkout
                            // do all the relationship-associated activities
                            roomToBeUpdated.getSuccessfulReservations().remove(roomToBeUpdated.getCurrentOccupancy());
                            roomToBeUpdated.getCurrentOccupancy().getReservation().setStatus(ReservationStatusEnum.COMPLETED);
                            // assumption: if a guest checks out early, the following room nights are considered checked out also
                            for(RoomNight roomNight: roomToBeUpdated.getCurrentOccupancy().getRoomNights()){
                                if(roomNight.getRoom().getRoomNumber().equals(roomToBeUpdated.getRoomNumber())){
                                    roomNight.setRoom(null);
                                }
                            }
                            // the room can proceed to checkout and cleaning
                            roomToBeUpdated.setCurrentOccupancy(room.getCurrentOccupancy());
                            if (room.getRoomStatus().equals(RoomStatusEnum.CLEANING)) {
                                roomToBeUpdated.setRoomStatus(room.getRoomStatus());
                                // set a timer for one-hour cleaning
                                long duration = 3600000;
                                TimerConfig timerConfig = new TimerConfig();
                                timerConfig.setInfo(room);
                                Timer timer = timerService.createSingleActionTimer(duration, timerConfig);
                            } else {
                                throw new UpdateRoomException("Cannot update room: new current occupancy does not match the new status!");
                            }
                        } else {
                            throw new UpdateRoomException("Cannot update room: too early for checkin! The room is currently occupied!");
                        }
                    } else { // previously current occupancy is null
                        // someone wants to check in
                        // check whether the cleaning has been done
                        if (roomToBeUpdated.getRoomStatus().equals(RoomStatusEnum.CLEANING)) {
                            throw new UpdateRoomException("Cannot update room: too early for checkin! The room is currently under cleaning!");
                        }
                        if (roomToBeUpdated.getRoomStatus().equals(RoomStatusEnum.AVAILABLE)) {
                            // set a timer to track the correct checkout timing if a checkout is allowed
                            roomToBeUpdated.setCurrentOccupancy(room.getCurrentOccupancy());
                            GregorianCalendar checkOutDate = roomToBeUpdated.getCurrentOccupancy().getReservation().getCheckOut();
                            ScheduleExpression schedule = new ScheduleExpression();
                            schedule.year(checkOutDate.YEAR);
                            schedule.month(checkOutDate.MONTH);
                            schedule.dayOfMonth(checkOutDate.YEAR);
                            schedule.hour(12);
                            TimerConfig timerConfig = new TimerConfig();
                            // set the timer's info to be the room night that has the latest date
                            RoomNight roomNight = roomToBeUpdated.getCurrentOccupancy().getRoomNights().get(0);
                            for (RoomNight rn : roomToBeUpdated.getCurrentOccupancy().getRoomNights()) {
                                if (rn.getDate().compareTo(roomNight.getDate()) > 0) {
                                    roomNight = rn;
                                }
                            }
                            timerConfig.setInfo(roomNight);
                            Timer timer = timerService.createCalendarTimer(schedule, timerConfig);
                        }else{
                            throw new UpdateRoomException("Cannot update room: The room is not available for the moment!");
                        }
                    }
                } else {
                    // two current occupancies are the same
                    if (roomToBeUpdated.getCurrentOccupancy() == null) {
                        throw new UpdateRoomException("The room is not occupied in the first place!");
                    } else {
                        // guest wants to prolong the checkout 
                        // check whether a late checkout can be done
                        Query query = em.createQuery("SELECT li.reservationLineItemId AS reservationLineItemId, rn.date AS date FROM Room r JOIN r.successfulReservations li JOIN li.roomNights rn WHERE rn.room.roomNumber = :inRoomNumber ORDER BY rn.date ASC");
                        query.setParameter("inRoomNumber", room.getRoomNumber());
                        Object[] result = (Object[]) query.setMaxResults(1).getSingleResult();
                        Long potentialCrashReservationLineItemId = (Long) result[0];
                        Calendar potentialCrashDate = (Calendar) result[1];
                        GregorianCalendar supposedCheckOutDate = roomToBeUpdated.getCurrentOccupancy().getReservation().getCheckOut();
                        if (!potentialCrashReservationLineItemId.equals(roomToBeUpdated.getCurrentOccupancy().getReservationLineItemId())) {
                            if (potentialCrashDate.YEAR == supposedCheckOutDate.YEAR && potentialCrashDate.MONTH == supposedCheckOutDate.MONTH && potentialCrashDate.DATE == supposedCheckOutDate.DATE) {
                                // crash found, need to generate a late check out exception report
                                throw new UpdateRoomException("Cannot update room: there is another reservation allocated to this room on the day of checkout!");
                            } else {
                                // can proceed to late checkout
                                if (room.getRoomStatus().equals(RoomStatusEnum.PROLONGEDOCCUPIED)) {
                                    roomToBeUpdated.setRoomStatus(room.getRoomStatus());
                                } else {
                                    throw new UpdateRoomException("Cannot update room: wrong input status!");
                                }
                            }
                        }
                        roomToBeUpdated.setRoomStatus(room.getRoomStatus());
                    }
                }
            }
        } else {
            throw new UpdateRoomException("Room does not exist!");
        }
    }

    @Override
    public void deleteRoom(@NotNull Integer roomNumber) throws DeleteRoomException {
        Query query = em.createQuery("SELECT r FROM Room WHERE r.roomNumber = :inRoomNumber AND r.enabled = TRUE");
        query.setParameter("inRoomNumber", roomNumber);
        try {
            Room room = (Room) query.getSingleResult();
            // check whether the room is in use
            // check its currentOccupancy
            if (room.getCurrentOccupancy() != null) {
                throw new DeleteRoomException("Room deletion failed: the room is currently used by a customer!");
            }
            // check whether it has any associated reservation(s)
            Query queryReservation = em.createQuery("SELECT r FROM Reservation r JOIN r.reservationLineItems.roomNights rn WHERE r.status = 'ALLOCATED' AND rn.room.roomNumber = :inRoomNumber");
            queryReservation.setParameter("inRoomNumber", roomNumber);
            List<Reservation> associatedReservations = (List<Reservation>) queryReservation.getResultList();
            if (associatedReservations != null && associatedReservations.size() > 0) {
                throw new DeleteRoomException("Room deletion failed: the room is associated with successful reservation(s)!");
            } else {
                room.setEnabled(false);
            }
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new DeleteRoomException("Room deletion failed: " + ex.getMessage() + " or is not in use!");
        }
    }
    
    // since allocation happens on the current day, so the start date must be today
    @Override
    public List<Room> searchAvailableRoomsForAllocation(@NotNull Long roomTypeID, @NotNull Integer numOfRoomsRequired, @NotNull GregorianCalendar checkOutDate) throws InsufficientNumOfRoomForAllocationException {
        // retrieve all the rooms under the room type
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomType.roomTypeId = :inRoomTypeId");
        query.setParameter("inRoomTypeId", roomTypeID);
        List<Room> allRooms = query.getResultList();
        GregorianCalendar today = new GregorianCalendar();
        
        List<Room> availableRooms = new ArrayList<>();
        for(Room room: allRooms){
            query = em.createQuery("SELECT r3 FROM Room r1 JOIN r1.successfulReservations r2 JOIN r2.reservation r3 WHERE r3.status = :inAllocated ORDER BY r3.checkIn, r3.checkOut ASC");
            // only need to compare the reservations that have already been allocated to room(s)
            // because First Come Fisrt Serve for SUCCESSFUL reseravtions
            query.setParameter("inAllocated", ReservationStatusEnum.ALLOCATED.toString());
            List<Reservation> reservations = query.getResultList();
            // compare the latest reservation dates with the pending reservation
            for(Reservation reservation: reservations){
                GregorianCalendar checkIn = reservation.getCheckIn();
                GregorianCalendar checkOut = reservation.getCheckOut();
                if(checkIn.compareTo(today) < 0){
                    if(checkOut.compareTo(today) <= 0){
                        availableRooms.add(room);
                        break;
                    }else{
                        break;
                    }
                } else if(checkIn.compareTo(today) == 0){
                    break;
                } else if(checkIn.compareTo(today) > 0){
                    if(checkIn.compareTo(checkOutDate) >= 0){
                        availableRooms.add(room);
                        break;
                    } else {
                        break;
                    }
                }
            }
        }
        if(availableRooms.size() >= numOfRoomsRequired){
            return availableRooms;
        }else{
            throw new InsufficientNumOfRoomForAllocationException("Not enough room for allocation!");
        }
    }

    @Override
    public List<LateCheckoutExceptionReport> retrieveLateCheckoutExceptionReports() {
        Query query = em.createQuery("SELECT lce FROM LateCheckoutExceptionReport lce");
        List<LateCheckoutExceptionReport> reports = query.getResultList();
        return reports;
    }

    @Timeout
    private void Timeout(Timer timer) {
        if (timer.getInfo() instanceof RoomNight) {
            RoomNight roomNight = (RoomNight) timer.getInfo();
            Room room = roomNight.getRoom();
            if (room.getRoomStatus().equals(RoomStatusEnum.OCCUPIED) && room.getCurrentOccupancy().getReservationLineItemId().equals(roomNight.getReservationLineItem().getReservationLineItemId())) {
                // the guest should check out by now and s/he did not indicate to prolong the stay
                // check whether this room is allocated to another reservation
                Query query = em.createQuery("SELECT li.reservationLineItemId AS reservationLineItemId, rn.date AS date FROM Room r JOIN r.successfulReservations li JOIN li.roomNights rn WHERE rn.room.roomNumber = :inRoomNumber ORDER BY rn.date ASC");
                query.setParameter("inRoomNumber", room.getRoomNumber());
                Object[] result = (Object[]) query.setMaxResults(1).getSingleResult();
                Long potentialCrashReservationLineItemId = (Long) result[0];
                Calendar potentialCrashDate = (Calendar) result[1];
                GregorianCalendar today = new GregorianCalendar();
                if (!potentialCrashReservationLineItemId.equals(roomNight.getReservationLineItem().getReservationLineItemId())) {
                    if (potentialCrashDate.YEAR == today.YEAR && potentialCrashDate.MONTH == today.MONTH && potentialCrashDate.DATE == today.DATE) {
                        // crash found, need to generate a late check out exception report
                        LateCheckoutExceptionReport lateCheckoutExceptionReport = new LateCheckoutExceptionReport(room, new GregorianCalendar());
                        em.persist(lateCheckoutExceptionReport);
                    }
                }
            }
        } else if (timer.getInfo() instanceof Room) {
            Room room = (Room) timer.getInfo();
            room.setRoomStatus(RoomStatusEnum.AVAILABLE);
        }
    }
}
