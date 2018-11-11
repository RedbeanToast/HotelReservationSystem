/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.NormalRoomRate;
import entities.PeakRoomRate;
import entities.PromotionRoomRate;
import entities.PublishedRoomRate;
import entities.Room;
import entities.RoomRate;
import entities.RoomType;
import exceptions.DeleteRoomRateException;
import exceptions.RoomRateNotFoundException;
import exceptions.SearchRoomRateException;
import exceptions.UpdateRoomRateException;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
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

    @Override
    public RoomRate createNewRoomRate(@NotNull RoomRate roomRate) {
        em.persist(roomRate);
        em.flush();

        return roomRate;
    }

    @Override
    public RoomRate retrieveRoomRateById(@NotNull Long rateId) throws RoomRateNotFoundException {
        RoomRate roomRate = em.find(RoomRate.class, rateId);

        if (roomRate == null) {
            throw new RoomRateNotFoundException("Room rate does not exist!");
        } else {
            roomRate.getRoomType();
            return roomRate;
        }
    }
    
    @Override
    public List<RoomRate> retrieveAllRoomRates() {
        Query query = em.createQuery("SELECT rr From RoomRates rr");
        List<RoomRate> roomRates = query.getResultList();
        for(RoomRate roomRate: roomRates){
            roomRate.getRoomType();
        }
        return roomRates;
    }

    // search the prevailing room rate based on the different criteria given
    @Override
    public RoomRate searchPrevailingRoomRate(@NotNull GregorianCalendar date, RoomType roomType, String reservationType) throws SearchRoomRateException {
        if(reservationType.equals("WalkInReservation")){
            // always use the published rate
            Query query = em.createQuery("SELECT prr FROM PublishedRoomRate prr WHERE prr.roomType = :inRoomType");
            query.setParameter("inRoomType", roomType);
            try{
                PublishedRoomRate publishedRoomRate = (PublishedRoomRate)query.getSingleResult(); 
                return publishedRoomRate;
            }catch(NoResultException | NonUniqueResultException ex){
                throw new SearchRoomRateException("No published room rate exists for room type " + roomType.getName() + " !");
            }
        }else{
            // applicable to both OnlineHoRSReservation as well as OnlinePartnerReservation
            // search the available normal, promotion and peak rate for the room type
            // normal room rate
            NormalRoomRate normalRoomRate = null;
            Query query = em.createQuery("SELECT nrr FROM NormalRoomRate nrr WHERE nrr.roomType = :inRoomType");
            query.setParameter("inRoomType", roomType);
            normalRoomRate = (NormalRoomRate)query.getSingleResult();
            
            // peak room rate
            PeakRoomRate peakRoomRate = null;
            query = em.createQuery("SELECT peakrr FROM PeakRoomRate peakrr WHERE peakrr.roomType = :inRoomType");
            query.setParameter("inRoomType", roomType);
            // there may be more than one peak room rate under the room type hence the result is a collection
            List<PeakRoomRate> peakRoomRates = query.getResultList();
            if(peakRoomRates != null && peakRoomRates.size() > 0){
                BigDecimal maxRate = new BigDecimal(0);
                // check whether the search date is included in the validity dates
                for(PeakRoomRate rate: peakRoomRates){
                    for(GregorianCalendar validityDate: rate.getValidityDates()){
                        if(validityDate.YEAR == date.YEAR && validityDate.MONTH == date.MONTH && validityDate.DATE == date.DATE){
                            if(rate.getRatePerNight().compareTo(maxRate) > 0){
                                maxRate = rate.getRatePerNight();
                                peakRoomRate = rate;
                                break;
                            }
                        }
                    }
                }
            }
            
            // promotion room rate
            PromotionRoomRate promotionRoomRate = null;
            query = em.createQuery("SELECT promorr FROM PromotionRoomRate promorr WHERE promorr.roomType = :inRoomType AND promorr.validityPeriodStart <= :inDate AND promorr.validityPeriodEnd >= :inDate");
            query.setParameter("inRoomType", roomType);
            query.setParameter("inDate", date);
            // there may be more than one promotion room rate under the room type hence the result is a collection
            List<PromotionRoomRate> promotionRoomRates = query.getResultList();
            if (promotionRoomRates != null && promotionRoomRates.size() > 0) {
                BigDecimal minRate = promotionRoomRates.get(0).getRatePerNight();
                for (PromotionRoomRate rate : promotionRoomRates) {
                    if (rate.getRatePerNight().compareTo(minRate) < 0) {
                        minRate = rate.getRatePerNight();
                        promotionRoomRate = rate;
                        break;
                    }
                }
            }
            
            // choose the prevailing room rate according to the business rules
            if(promotionRoomRate != null && peakRoomRate != null){
                if(normalRoomRate != null){
                    return promotionRoomRate;
                }else{
                    return peakRoomRate;
                }
            }else if(promotionRoomRate != null){
                return promotionRoomRate;
            }else if(peakRoomRate != null){
                return peakRoomRate;
            }else if(normalRoomRate != null){
                return normalRoomRate;
            }else{
                throw new SearchRoomRateException("No normal rate exists for the room type " + roomType.getName() + " !");
            }
        }
    }
    
    @Override
    public void updateRoomRateDetails(RoomRate roomRate) throws UpdateRoomRateException {
        RoomRate roomRateToBeUpdated = em.find(RoomRate.class, roomRate.getRateId());

        if (roomRate != null) {
            // update basic information
            if(!roomRateToBeUpdated.getRatePerNight().equals(roomRate.getRatePerNight())){
                roomRateToBeUpdated.setRatePerNight(roomRate.getRatePerNight());
            }
            
            // update specific information based on particular room rate type
            if(roomRateToBeUpdated instanceof PeakRoomRate){
                PeakRoomRate newRoomRate = (PeakRoomRate)roomRate;
                PeakRoomRate roomRateUpdated = (PeakRoomRate)roomRateToBeUpdated;
                if(!roomRateUpdated.getValidityDates().equals(newRoomRate.getValidityDates())){
                    roomRateUpdated.setValidityDates(newRoomRate.getValidityDates());
                }
            }
            if(roomRateToBeUpdated instanceof PromotionRoomRate){
                PromotionRoomRate newRoomRate = (PromotionRoomRate)roomRate;
                PromotionRoomRate roomRateUpdated = (PromotionRoomRate)roomRateToBeUpdated;
                if(!roomRateUpdated.getValidityPeriodStart().equals(newRoomRate.getValidityPeriodStart())){
                    // check whether the new validity start date is before the new end date
                    if(newRoomRate.getValidityPeriodStart().compareTo(newRoomRate.getValidityPeriodEnd()) < 0){
                        roomRateUpdated.setValidityPeriodStart(newRoomRate.getValidityPeriodStart());
                        if(!roomRateUpdated.getValidityPeriodEnd().equals(newRoomRate.getValidityPeriodEnd())){
                            roomRateUpdated.setValidityPeriodEnd(newRoomRate.getValidityPeriodEnd());
                        }
                    }else{
                        throw new UpdateRoomRateException("The new start date of validity period is invalid!");
                    }
                    
                }
            }
        }else{
            throw new UpdateRoomRateException("Room rate update failed: room rate does not exist!"); 
        }
    }
    
    // check whether the room rate is currently in use
    // assumption: a room rate is in use when there are rooms that have room nights that are currently associated
    //   with a room type under this room rate; or the room nights are reserved and also associated with this room rate
    @Override
    public void deleteRoomRate(Long rateId) throws DeleteRoomRateException {
        try{
            RoomRate roomRate = retrieveRoomRateById(rateId);
            
            Query query = em.createQuery("SELECT r FROM Room r JOIN r.successfulReservations sr JOIN sr.roomNights rn WHERE rn.roomRate = :inRoomRate");
            query.setParameter("inRateId", rateId);
            List<Room> rooms = query.getResultList();
            if(rooms != null && rooms.size() > 0){
                throw new DeleteRoomRateException("Delete room rate failed: there are reservations currently associated with this room rate!");
            }else{
                roomRate.setEnabled(false);
            }
        } catch (RoomRateNotFoundException ex) {
            throw new DeleteRoomRateException("Delete room rate failed: " + ex.getMessage());
        }
    }
}
