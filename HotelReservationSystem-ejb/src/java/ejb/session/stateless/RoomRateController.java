/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.RoomRate;
import exceptions.DeleteRoomRateFailedException;
import exceptions.RoomRateNotFoundException;
import exceptions.UpdateRoomRateFailedException;
import java.util.Calendar;
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
@Local(RoomRateControllerLocal.class)
@Remote(RoomRateControllerRemote.class)
public class RoomRateController implements RoomRateControllerRemote, RoomRateControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public RoomRate createNewRoomRate(@NotNull RoomRate roomRate) {
        em.persist(roomRate);
        em.flush();

        return roomRate;
    }

    public RoomRate retrieveRoomRateById(Long rateId) throws RoomRateNotFoundException {
        RoomRate roomRate = em.find(RoomRate.class, rateId);

        if (roomRate == null) {
            throw new RoomRateNotFoundException("Room rate does not exist!");
        } else {
            return roomRate;
        }
    }
    
    public RoomRate retrieveWalkinRoomRateByRoomTypeName(String roomTypeName) throws RoomRateNotFoundException {
        Query query = em.createQuery("SELECT rr From RoomRates rr WHERE rr.rateType = 'PUBLISHED' AND rr.roomType.name = :inRoomTypeName");
        query.setParameter("inRoomTypeName", roomTypeName);
        try{
            RoomRate roomRate = (RoomRate)query.getSingleResult();
            return roomRate;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomRateNotFoundException("Published room rate for room type " + roomTypeName + " does not exist!");
        }
    }
    
    public List<RoomRate> retrieveAllRoomRates() {
        Query query = em.createQuery("SELECT rr From RoomRates rr");
        List<RoomRate> roomRates = (List<RoomRate>)query.getResultList();
        
        return roomRates;
    }

    public void updateRoomRateDetails(RoomRate roomRate) throws UpdateRoomRateFailedException {
        RoomRate roomRateToBeUpdated = em.find(RoomRate.class, roomRate.getRateId());

        if (roomRate != null) {
            if (!roomRateToBeUpdated.getRateType().equals(roomRate.getRateType())) {
                roomRateToBeUpdated.setRateType(roomRate.getRateType());
            }
            if (!roomRateToBeUpdated.getRatePerNight().equals(roomRate.getRatePerNight())) {
                roomRateToBeUpdated.setRatePerNight(roomRate.getRatePerNight());
            }
            Calendar newStartDate = roomRate.getValidityPeriodStart();
            Calendar newEndDate = roomRate.getValidityPeriodEnd();
            if (newStartDate.after(newEndDate) || newEndDate.before(Calendar.getInstance())) {
                throw new UpdateRoomRateFailedException("Room rate update failed: the new dates for start and end of the room rate are invalid!");
            } else {
                if (!roomRateToBeUpdated.getValidityPeriodStart().equals(roomRate.getValidityPeriodStart())) {
                    roomRateToBeUpdated.setValidityPeriodStart(roomRate.getValidityPeriodStart());
                }
                if (!roomRateToBeUpdated.getValidityPeriodEnd().equals(roomRate.getValidityPeriodEnd())) {
                    roomRateToBeUpdated.setValidityPeriodEnd(roomRate.getValidityPeriodEnd());
                }
            }
        }
    }
    
    public void deleteRoomRate(Long rateId) throws DeleteRoomRateFailedException {
        try{
            RoomRate roomRate = retrieveRoomRateById(rateId);
            // check whether the room rate is currently in use
            // assumption: a room rate is in use when its applied room type has allocated reservation(s)
            if(roomRate.getRoomType().getAllocatedReservations().size() > 0){
                throw new DeleteRoomRateFailedException("Delete room rate failed: the room rate is currently associated with allocated reservation(s)!");
            } else {
                roomRate.setEnabled(false);
            }
        } catch (RoomRateNotFoundException ex) {
            throw new DeleteRoomRateFailedException("Delete room rate failed: " + ex.getMessage());
        }
    }
}
