/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.Reservation;
import entities.Room;
import entities.RoomType;
import enumerations.RoomStatusEnum;
import exceptions.CreateNewRoomException;
import exceptions.DeleteRoomException;
import exceptions.RoomNotFoundException;
import exceptions.RoomTypeNotFoundException;
import exceptions.UpdateRoomException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
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
        try{
            Room room = (Room)query.getSingleResult();
            room.getCurrentOccupancy();
            room.getRoomType();
            return room;
        } catch(NoResultException | NonUniqueResultException ex) {
            throw new RoomNotFoundException("Room with room number " + roomNumber + " does not exist or is not in use!");
        }
    }

    // retrieve all room records 
    @Override
    public List<Room> retrieveAllRooms() {
        Query query = em.createQuery("SELECT r FROM Room r ORDER BY r.roomnumber");
        List<Room> rooms = query.getResultList();
        for(Room room: rooms){
            room.getRoomType();
        }
        return rooms;
    }

    // change the status of the room
    // set the currentOccupancy of the room
    @Override
    public void updateRoom(@NotNull Room room) throws UpdateRoomException {
        Room roomToBeUpdated = em.find(Room.class, room.getRoomId());
        if (roomToBeUpdated != null) {
            if (!roomToBeUpdated.getRoomStatus().equals(room.getRoomStatus())) {
                roomToBeUpdated.setRoomStatus(room.getRoomStatus());
            }
            if (!roomToBeUpdated.getRoomType().equals(room.getRoomType())) {
                roomToBeUpdated.setRoomType(room.getRoomType());
            }
            if (!roomToBeUpdated.getCurrentOccupancy().equals(room.getCurrentOccupancy())) {
                roomToBeUpdated.setCurrentOccupancy(room.getCurrentOccupancy());
            }
        }else{
            throw new UpdateRoomException("Room does not exist!");
        }
    }

    @Override
    public void deleteRoom(@NotNull Integer roomNumber) throws DeleteRoomException {
        Query query = em.createQuery("SELECT r FROM Room WHERE r.roomNumber = :inRoomNumber AND r.enabled = TRUE");
        query.setParameter("inRoomNumber", roomNumber);
        try{
            Room room = (Room)query.getSingleResult();
            // check whether the room is in use
            // check its currentOccupancy
            if(room.getCurrentOccupancy() != null){
                throw new DeleteRoomException("Room deletion failed: the room is currently used by a customer!");
            }
            // check whether it has any associated reservation(s)
            Query queryReservation = em.createQuery("SELECT r FROM Reservation r JOIN r.reservationLineItems.roomNights rn WHERE r.status = 'ALLOCATED' AND rn.room.roomNumber = :inRoomNumber");
            queryReservation.setParameter("inRoomNumber", roomNumber);
            List<Reservation> associatedReservations = (List<Reservation>)queryReservation.getResultList();
            if(associatedReservations != null && associatedReservations.size() > 0){
                throw new DeleteRoomException("Room deletion failed: the room is associated with successful reservation(s)!");
            } else {
                room.setEnabled(false);
            }
        }catch(NoResultException | NonUniqueResultException ex){
            throw new DeleteRoomException("Room deletion failed: " + ex.getMessage() + " or is not in use!");
        }
    }
    
    
}
