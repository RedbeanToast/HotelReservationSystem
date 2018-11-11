/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.RoomType;
import exceptions.DeleteRoomTypeException;
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

    @Override
    public RoomType createNewRoomType(@NotNull RoomType roomType) {
        em.persist(roomType);
        em.flush();
        
        return roomType;
    }
    
    @Override
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
    
    @Override
    public List<RoomType> retrieveAllRoomTypes() {
        Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.enabled = TRUE ORDER BY rt.name");
        List<RoomType> roomTypes = query.getResultList();
        return roomTypes;
    }
    
    // Change the basic information of a room type
    @Override
    public void updateRoomType(@NotNull RoomType roomType) throws RoomTypeNotFoundException {
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
        }else{
            throw new RoomTypeNotFoundException("Room type does not exist!");
        }
    }
    
    @Override
    public void deleteRoomTypeByName(@NotNull String name) throws DeleteRoomTypeException {
        try{
            RoomType roomType = retrieveRoomTypeByName(name);
            // check if there is any room under the room type
            if(roomType.getRooms().size() > 0){
                throw new DeleteRoomTypeException("Room type deletion failed: the room type is currently in use!");
            } else {
                // check if there is any room rate applied to this room type
                Query query = em.createQuery("SELECT rr FROM RoomRate WHERE rr.roomType.roomTypeId = :inRoomTypeId");
                query.setParameter("inRoomTypeId", roomType.getRoomTypeId());
                if(!query.getResultList().isEmpty()){
                    throw new DeleteRoomTypeException("Room type deletion failed: there is room rate associated with the room type currently!");
                }

                roomType.setEnabled(false);
            }
        } catch (RoomTypeNotFoundException ex) {
            throw new DeleteRoomTypeException("Room type deletion failed: " + ex.getMessage());
        }
    }
}
