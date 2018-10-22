/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.Room;
import entities.RoomType;
import enumerations.RoomStatusEnum;
import exceptions.CreateNewRoomFailedException;
import exceptions.RoomTypeNotFoundException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
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

    public Room createNewRoom(@NotNull Integer roomNumber, @NotNull String roomTypeName) throws CreateNewRoomFailedException {
        try{
            RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeByName(roomTypeName);
            Room room = new Room(roomNumber, RoomStatusEnum.valueOf("AVAILABLE"), true, roomType);
            em.persist(room);

            return room;
        } catch (RoomTypeNotFoundException ex) {
            throw new CreateNewRoomFailedException("New room creation failed: " + ex.getMessage());
        }
    }
    
    public List<Room> retrieveAllRooms() {
        Query query = em.createQuery("SELECT r FROM Room r ORDER BY r.roomnumber");
        List<Room> rooms = (List<Room>)query.getResultList();
        return rooms;
    }
}
