/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.Employee;
import entities.Guest;
import entities.OnlineHoRSReservation;
import entities.OnlinePartnerReservation;
import entities.Partner;
import entities.RegisteredGuest;
import entities.Reservation;
import entities.ReservationLineItem;
import entities.Room;
import entities.RoomAllocationExceptionReport;
import entities.RoomNight;
import entities.RoomType;
import entities.WalkInReservation;
import enumerations.AllocationExceptionTypeEnum;
import enumerations.ReservationStatusEnum;
import exceptions.CreateReservationException;
import exceptions.InsufficientNumOfRoomForAllocationException;
import exceptions.PartnerNotFoundException;
import exceptions.ReservationNotFoundException;
import exceptions.RetrieveReservationException;
import exceptions.SearchHotelRoomsException;
import exceptions.SearchRoomRateException;
import exceptions.UpgradeRoomTypeNotFound;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
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
    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;
    @EJB
    private RoomControllerLocal roomControllerLocal;
    @EJB
    private PartnerControllerLocal partnerControllerLocal;

    @Override
    public List<OnlineHoRSReservation> retrieveAllOnlineHoRSReservationByEmail(@NotNull String email) throws RetrieveReservationException {
        List<OnlineHoRSReservation> onlineHoRSReservations = new ArrayList<>();
        Query query = em.createQuery("SELECT rg FROM RegisteredGuest rg WHERE rg.email = :inEmail");
        query.setParameter("inEmail", email);
        try{
            RegisteredGuest registeredGuest = (RegisteredGuest)query.getSingleResult();
            query = em.createQuery("SELECT r FROM OnlineHoRSReservation r WHERE r.guest.guestId = :inGuestId ORDER BY r.reservationId DESC");
            query.setParameter("inGuestId", registeredGuest.getGuestId());
            onlineHoRSReservations = query.getResultList();
            return onlineHoRSReservations;
        }catch(NoResultException | NonUniqueResultException ex){
            throw new RetrieveReservationException("Online guest with email " + email + " does not exist!");
        }
    }
    
    @Override
    public List<OnlinePartnerReservation> retrieveAllOnlinePartnerReservationByName(@NotNull String name) throws RetrieveReservationException {
        List<OnlinePartnerReservation> onlinePartnerReservations = new ArrayList<>();
        Query query = em.createQuery("SELECT p FROM Partner p WHERE p.name = :inName");
        query.setParameter("inName", name);
        try{
            Partner partner = (Partner)query.getSingleResult();
            query = em.createQuery("SELECT r FROM OnlinePartnerReservation r WHERE r.partner.name = :inName ORDER BY r.reservationId DESC");
            query.setParameter("inName", partner.getName());
            onlinePartnerReservations = query.getResultList();
            return onlinePartnerReservations;
        }catch(NoResultException | NonUniqueResultException ex){
            throw new RetrieveReservationException("Partner " + name + " does not exist!");
        }
    }
    
    @Override
    public Reservation retrieveReservationById(Long reservationId) throws ReservationNotFoundException {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if(reservation == null){
            throw new ReservationNotFoundException("Reservation with ID " + reservationId + " does not exist!");
        }
        for(ReservationLineItem reservationLineItem: reservation.getReservationLineItems()){
            for(RoomNight roomNight: reservationLineItem.getRoomNights()){
                roomNight.getRoom();
            }
        }
        return reservation;
    }
  
    // The reservation type is needed when searching the room rate(s)
    // search hotel rooms acroos ALL room types
    // the returning information of room types should be complete
    @Override
    public List<RoomSearchResult> searchHotelRooms(@NotNull GregorianCalendar checkInDate, @NotNull GregorianCalendar checkOutDate, @NotNull String reservatonType) throws SearchHotelRoomsException {
        List<RoomSearchResult> roomSearchResults = new ArrayList<RoomSearchResult>();

        // retrieve all room types
        Query query = em.createQuery("SELECT rt FROM RoomType rt ORDER BY rt.size ASC");
        List<RoomType> roomTypes = query.getResultList();
        
        // search all available rooms for each room type, if such room exists, search the corresponding prevailing room rate,
        //     and add it into a RoomSearchResult
        for(RoomType roomType: roomTypes){
            // get all the rooms under the room type
            List<Room> rooms = roomType.getRooms();
            Integer availableAmount = rooms.size();
            
            // check all the room nights associated with each room and update the availableAmount accordingly
            for (Room room : rooms) {
                for (ReservationLineItem successfulReservation : room.getSuccessfulReservations()) {
                    for (RoomNight roomNight : successfulReservation.getRoomNights()) {
                        GregorianCalendar nightDate = roomNight.getDate();
                        if (nightDate.compareTo(checkInDate) >= 0 && nightDate.compareTo(checkOutDate) < 0) {
                            availableAmount--;
                            break;
                        }
                    }
                }
            }
            
            if(availableAmount == 0){
                break;
            }else{
                // there are rooms available during the period under this room type, proceed to search the prevailing room rate for each day within the period
                try{
                    List<RoomNight> roomNights = new ArrayList<>();
                    GregorianCalendar startDate = new GregorianCalendar();
                    startDate.set(checkInDate.YEAR, checkInDate.MONTH, checkInDate.DATE);
                    while(startDate.compareTo(checkOutDate) <= 0){
                        RoomNight roomNight = new RoomNight(startDate, roomRateControllerLocal.searchPrevailingRoomRate(startDate, roomType, reservatonType));
                        roomNights.add(roomNight);
                        startDate.add(Calendar.DATE, 1);
                    }
                    roomSearchResults.add(new RoomSearchResult(roomType, roomNights, availableAmount));
                }catch(SearchRoomRateException ex){
                    throw new SearchHotelRoomsException(ex.getMessage());
                }
            }
        }
        
        return roomSearchResults;
    }
    
    // have to persist all the reservation line items together with their room nights
    @Override
    public OnlineHoRSReservation createNewOnlineHoRSReservation(@NotNull List<ReservationLineItem> reservationLineItems, @NotNull String email, @NotNull GregorianCalendar checkInDate, @NotNull GregorianCalendar checkOutDate, @NotNull BigDecimal totalAmount) throws CreateReservationException {
        
        // retrieve the registered guest
        Query query = em.createQuery("SELECT rg FROM RegisteredGuest rg WHERE rg.email = :inEmail");
        query.setParameter("inEmail", email);
        try{
            RegisteredGuest guest = (RegisteredGuest)query.getSingleResult();
            // create new reservation object
            OnlineHoRSReservation reservation = new OnlineHoRSReservation(guest, totalAmount, checkInDate, checkOutDate, new GregorianCalendar(), ReservationStatusEnum.SUCCESS, reservationLineItems);
            guest.getReservations().add(reservation);
            // persist all associated relationship attributes
            em.persist(reservation);
            for(ReservationLineItem reservationLineItem: reservationLineItems){
                reservationLineItem.setReservation(reservation);
                em.persist(reservationLineItem);
                for(RoomNight roomNight: reservationLineItem.getRoomNights()){
                    em.persist(roomNight);
                }
            }
            
            em.flush();
            return reservation;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CreateReservationException("There is no guest registered with email " + email + "!");
        }  
    }
    
    @Override
    public WalkInReservation createNewWalkInReservation(@NotNull List<ReservationLineItem> reservationLineItems, @NotNull Long guestId, @NotNull Long employeeId, @NotNull GregorianCalendar checkOutDate, @NotNull BigDecimal totalAmount) throws CreateReservationException {
        // check whether the guest exists
        Guest guest = em.find(Guest.class, guestId);
        if(guest == null){
            throw new CreateReservationException("Guest does not exist!");
        }
        // retrieve employee
        Employee employee = em.find(Employee.class, employeeId);
        if(employee == null){
            throw new CreateReservationException("Employee does not exist!");
        }
        // create new reservation object
        WalkInReservation reservation = new WalkInReservation(guest, totalAmount, new GregorianCalendar(), checkOutDate, new GregorianCalendar(), ReservationStatusEnum.SUCCESS, reservationLineItems, employee);
        employee.getWalkInReservations().add(reservation);
        guest.getReservations().add(reservation);
        // persist all associated relationship attributes
        em.persist(reservation);
        for (ReservationLineItem reservationLineItem : reservationLineItems) {
            reservationLineItem.setReservation(reservation);
            em.persist(reservationLineItem);
            for (RoomNight roomNight : reservationLineItem.getRoomNights()) {
                em.persist(roomNight);
            }
        }

        em.flush();
        return reservation;
        
    }
    
    @Override
    public OnlinePartnerReservation createNewOnlinePartnerReservation(@NotNull List<ReservationLineItem> reservationLineItems, @NotNull String partnerName, 
            @NotNull GregorianCalendar checkInDate, @NotNull GregorianCalendar checkOutDate, @NotNull BigDecimal totalAmount, 
            @NotNull String identificationNumber, @NotNull String firstName, @NotNull String lastName, @NotNull String phoneNumber)
            throws CreateReservationException
    {
        
        // check whether the guest exists
        Query query = em.createQuery("SELECT g FROM Guest g WHERE g.identificationNumber = :inIdentificationNumber");
        query.setParameter("inIdentificationNumber", identificationNumber);
        Guest guest = (Guest)query.getSingleResult();
        if(guest == null){
            // create a new guest
            guest = new Guest(firstName, lastName, identificationNumber, phoneNumber);
            em.persist(guest);
            em.flush();
        }
        // retrieve partner
        try {
            Partner partner = partnerControllerLocal.retrievePartnerByName(partnerName);

            OnlinePartnerReservation reservation = new OnlinePartnerReservation(guest, totalAmount, checkInDate, checkOutDate, new GregorianCalendar(), ReservationStatusEnum.SUCCESS, reservationLineItems, partner);
            partner.getOnlinePartnerReservations().add(reservation);
            guest.getReservations().add(reservation);
            // persist all associated relationship attributes
            em.persist(reservation);
            for (ReservationLineItem reservationLineItem : reservationLineItems) {
                reservationLineItem.setReservation(reservation);
                em.persist(reservationLineItem);
                for (RoomNight roomNight : reservationLineItem.getRoomNights()) {
                    em.persist(roomNight);
                }
            }

            em.flush();

            // nullify the relationship attributes to avoid cyclic reference in web service
            em.detach(reservation);
            reservation.setPartner(null);
            reservation.setGuest(null);
            reservation.getReservationLineItems().clear();
            return reservation;
        }catch(PartnerNotFoundException ex){
            throw new CreateReservationException(ex.getMessage());
        }

        
    }

    // The reservations returned are only for valid check-in on the CURRENT date.
    @Override
    public List<Reservation> retrieveValidReservationsByGuestIdentificationNumber(@NotNull String identificationNumber) {
        List<Reservation> validReservations = new ArrayList<Reservation>();
        
        // Walkin reservations
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.guest.identificationNumber = :inIdentificationNumber AND r.checkIn = :today");
        query.setParameter("inIdentificationNumber", identificationNumber);
        query.setParameter("today", Calendar.getInstance(), TemporalType.DATE);
        validReservations = query.getResultList();
        
        return validReservations;
    }
    
    @Override
    public List<RoomAllocationExceptionReport> retrieveRoomAllocationExceptionReports() {
        Query query = em.createQuery("SELECT r FROM RoomAllocationExceptionReport r ORDER BY r.reportId ASC");
        return query.getResultList();
    }
    
    @Schedule(hour="2")
    private void allocateRoomsToCurrentDayReservations(){

        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.status = :inStatus AND r.checkIn = :today ORDER BY r.madeDate ASC");
        query.setParameter("inStatus", ReservationStatusEnum.SUCCESS.toString());
        query.setParameter("today", Calendar.getInstance(), TemporalType.DATE);
        List<Reservation> reservations = query.getResultList();
        
        // assign the reservations to rooms one by one, generate room allocation exception report if necessary
        // assumption: a guest cannot change his/her room during the stay
        for(Reservation reservation: reservations){
            for(ReservationLineItem reservationLineItem: reservation.getReservationLineItems()){
                // note: there may be more than one room in the reservation line item,
                // note:    and some of them are not able to be allocated -> need to check separately
                try{
                    List<Room> availableRooms = roomControllerLocal.searchAvailableRoomsForAllocation(reservationLineItem.getIntendedRoomType().getRoomTypeId(), reservationLineItem.getNumOfRooms(), reservationLineItem.getReservation().getCheckOut());
                    query = em.createQuery("SELECT rn FROM RoomNight rn WHERE rn.reservationLineItem.reservationLineItemId = :inReservationLineItemId ORDER BY rn.date ASC");
                    query.setParameter("inReservationLineItemId", reservationLineItem.getReservationLineItemId());
                    List<RoomNight> roomNights = query.getResultList();
                    // assign rooms to roomnight randomly 
                    int numOfRooms = reservationLineItem.getNumOfRooms();
                    int numOfNights = roomNights.size() / numOfRooms;
                    for(int roomNo = 0; roomNo < numOfRooms; roomNo++){
                        for(int roomNightNo = 0; roomNightNo < roomNights.size(); roomNightNo += numOfRooms){
                            roomNights.get(roomNightNo).setRoom(availableRooms.get(roomNo));
                            availableRooms.get(roomNo).getSuccessfulReservations().add(reservationLineItem);
                        }
                    }
                    reservation.setStatus(ReservationStatusEnum.ALLOCATED);
                }catch(InsufficientNumOfRoomForAllocationException ex){
                    // check whether upgrade is possible
                    try {
                        RoomType upgradeRoomType = roomTypeControllerLocal.retrieveUpgradeRoomType(reservationLineItem.getIntendedRoomType());
                        try {
                            List<Room> availableRooms = roomControllerLocal.searchAvailableRoomsForAllocation(upgradeRoomType.getRoomTypeId(), reservationLineItem.getNumOfRooms(), reservationLineItem.getReservation().getCheckOut());
                            query = em.createQuery("SELECT rn FROM RoomNight rn WHERE rn.reservationLineItem.reservationLineItemId = :inReservationLineItemId ORDER BY rn.date ASC");
                            query.setParameter("inReservationLineItemId", reservationLineItem.getReservationLineItemId());
                            List<RoomNight> roomNights = query.getResultList();
                            // assign rooms to roomnight randomly 
                            int numOfRooms = reservationLineItem.getNumOfRooms();
                            int numOfNights = roomNights.size() / numOfRooms;
                            for (int roomNo = 0; roomNo < numOfRooms; roomNo++) {
                                for (int roomNightNo = 0; roomNightNo < roomNights.size(); roomNightNo += numOfRooms) {
                                    roomNights.get(roomNightNo).setRoom(availableRooms.get(roomNo));
                                    availableRooms.get(roomNo).getSuccessfulReservations().add(reservationLineItem);
                                }
                            }
                            reservation.setStatus(ReservationStatusEnum.ALLOCATED);
                            // generate room allocation exception report
                            RoomAllocationExceptionReport report = new RoomAllocationExceptionReport();
                            report.setExceptionType(AllocationExceptionTypeEnum.UPGRADED);
                            report.setReservationLineItem(reservationLineItem);
                            em.persist(report);
                        } catch (InsufficientNumOfRoomForAllocationException ex1) {
                            // upgrade room type exists but there are insufficient rooms for the upgrade room type
                            // generate room allocation exception report
                            RoomAllocationExceptionReport report = new RoomAllocationExceptionReport();
                            report.setExceptionType(AllocationExceptionTypeEnum.UPGRADEFAILED);
                            report.setReservationLineItem(reservationLineItem);
                            em.persist(report);
                            reservation.setStatus(ReservationStatusEnum.FAILED);
                        }
                    }catch(UpgradeRoomTypeNotFound ex2){
                        // no upgrade room type available
                        // generate room allocation exception report
                        RoomAllocationExceptionReport report = new RoomAllocationExceptionReport();
                        report.setExceptionType(AllocationExceptionTypeEnum.UPGRADEFAILED);
                        report.setReservationLineItem(reservationLineItem);
                        em.persist(report);
                        reservation.setStatus(ReservationStatusEnum.FAILED);
                    }
                }
            }
        }
    }
}
