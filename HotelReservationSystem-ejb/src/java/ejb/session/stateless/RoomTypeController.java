/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.RoomType;
import exceptions.DeleteRoomTypeFailedException;
import exceptions.RoomTypeNotFoundException;
import java.util.List;
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
@Local(RoomTypeControllerLocal.class)
@Remote(RoomTypeControllerRemote.class)
public class RoomTypeController implements RoomTypeControllerRemote, RoomTypeControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public RoomType createNewRoomType(@NotNull RoomType roomType) {
        em.persist(roomType);
        em.flush();
        
        return roomType;
    }
    
    public RoomType retrieveRoomTypeByName(@NotNull String name) throws RoomTypeNotFoundException {
        Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.name = :inName");
        query.setParameter("inName", name);
        try{
            RoomType roomType = (RoomType)query.getSingleResult();
            return roomType;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomTypeNotFoundException("Room type " + name + " does not exist!");
        }
    }
    
    public List<RoomType> retrieveRoomTypesByCapacity(Integer capacityRequired) {
        Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.enabled = TRUE AND rt.capacity >= :inCapacityRequired BY rt.name");
        query.setParameter("inCapacityRequired", capacityRequired);
        List<RoomType> roomTypes = (List<RoomType>)query.getResultList();
        return roomTypes;
    }
    
    public List<RoomType> retrieveAllRoomTypes() {
        Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.enabled = TRUE ORDER BY rt.name");
        List<RoomType> roomTypes = (List<RoomType>)query.getResultList();
        return roomTypes;
    }
    
    // Change the basic information of a room type
    public void updateRoomType(@NotNull RoomType roomType) {
        RoomType roomTypeToBeUpdated = em.find(RoomType.class, roomType.getRoomTypeId());
        if(roomTypeToBeUpdated != null){
            if(!roomType.getName().equals(roomTypeToBeUpdated.getName())){
                roomTypeToBeUpdated.setName(roomType.getName());
            }
            if(!roomType.getDescription().equals(roomTypeToBeUpdated.getDescription())){
                roomTypeToBeUpdated.setDescription(roomType.getDescription());
            }
            if(!roomType.getSize().equals(roomTypeToBeUpdated.getSize())){
                roomTypeToBeUpdated.setSize(roomType.getSize());
            }
            if(!roomType.getNumOfBeds().equals(roomTypeToBeUpdated.getNumOfBeds())){
                roomTypeToBeUpdated.setNumOfBeds(roomType.getNumOfBeds());
            }
            if(!roomType.getCapacity().equals(roomTypeToBeUpdated.getCapacity())){
                roomTypeToBeUpdated.setCapacity(roomType.getCapacity());
            }
            if(!roomType.getAmenities().equals(roomTypeToBeUpdated.getAmenities())){
                roomTypeToBeUpdated.setAmenities(roomType.getAmenities());
            }
        }
    }
    
    public void deleteRoomTypeByName(@NotNull String name) throws DeleteRoomTypeFailedException {
        try{
            RoomType roomType = retrieveRoomTypeByName(name);
            if(roomType.getRooms().size() > 0){
                throw new DeleteRoomTypeFailedException("Room type deletion failed: the room type is currently in use!");
            } else {
                // if there is no room under this roomType, then there must not be reservation with a room rate that is applied to this room type
                // since such a room does not exist, hence, no room rate is applied to this room type currently
                roomType.setEnabled(false);
            }
        } catch (RoomTypeNotFoundException ex) {
            throw new DeleteRoomTypeFailedException("Room type deletion failed: " + ex.getMessage());
        }
    }
}
