/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import datamodel.ws.RemoteReservationLineItem;
import datamodel.ws.RemoteRoomNight;
import ejb.session.singleton.RemotePartnerCheckoutManager;
import ejb.session.stateful.PartnerCheckoutControllerLocal;
import ejb.session.stateless.PartnerControllerLocal;
import ejb.session.stateless.RoomRateControllerLocal;
import entities.OnlinePartnerReservation;
import entities.Partner;
import entities.ReservationLineItem;
import entities.RoomNight;
import entities.RoomRate;
import entities.RoomType;
import entity.SaleTransactionEntity;
import exceptions.CreateReservationException;
import exceptions.InvalidLoginCredentialsException;
import exceptions.InvalidNumOfRoomsException;
import exceptions.PartnerNotFoundException;
import exceptions.RemotePartnerCheckoutControllerNotFoundException;
import exceptions.ReservationNotFoundException;
import exceptions.RetrieveReservationException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeNotFoundException;
import exceptions.SearchHotelRoomsException;
import exceptions.UnableToCreateRemoteCheckoutControllerException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import utilities.RoomSearchResult;

/**
 *
 * @author CaiYuqian
 */
@WebService(serviceName = "PartnerWebService")
@Stateless
public class PartnerWebService {

    @EJB
    private PartnerControllerLocal partnerControllerLocal;
    @EJB
    private RemotePartnerCheckoutManager remotePartnerCheckoutManager;
    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;
    
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    
    
    @WebMethod(operationName = "partnerLogin")
    public Partner partnerLogin(@WebParam(name = "name") String name,
                        @WebParam(name = "password") String password) 
            throws PartnerNotFoundException, InvalidLoginCredentialsException {
        Partner partner = partnerControllerLocal.loginPartner(name, password);
        System.out.println("********** PartnerWebService.partnerLogin(): Partner " + partner.getName() + " login remotely via web service");
        return partner;
    }
    
    @WebMethod(operationName = "viewAllPartnerReservations")
    public List<OnlinePartnerReservation> viewAllPartnerReservations(
                                    @WebParam(name = "name") String name,
                                    @WebParam(name = "password") String password) 
            throws PartnerNotFoundException, InvalidLoginCredentialsException, RetrieveReservationException {
        Partner partner = partnerControllerLocal.loginPartner(name, password);
        System.out.println("********** PartnerWebService.viewAllPartnerReservations(): Partner " + partner.getName() + " search remotely via web service");
        List<OnlinePartnerReservation> results = partnerControllerLocal.retrieveAllOnlinePartnerReservations(name);
        return results;
    }
    
    @WebMethod(operationName = "viewPartnerReservationDetails")
    public OnlinePartnerReservation viewPartnerReservationDetails(
                                    @WebParam(name = "name") String name,
                                    @WebParam(name = "password") String password,
                                    @WebParam(name = "reservationId") Long reservationId) 
            throws PartnerNotFoundException, InvalidLoginCredentialsException, ReservationNotFoundException {
        Partner partner = partnerControllerLocal.loginPartner(name, password);
        System.out.println("********** PartnerWebService.viewPartnerReservationDetails(): Partner " + partner.getName() + " search remotely via web service");
        OnlinePartnerReservation reservation = partnerControllerLocal.retrieveOnlinePartnerReservationDetailsByReservationId(reservationId);
        return reservation;
    }
    
    @WebMethod(operationName = "searchHotelRooms")
    public List<RoomSearchResult> searchHotelRooms(@WebParam(name = "name") String name,
                                    @WebParam(name = "password") String password,
                                    @WebParam(name = "checkInDate") Calendar checkIn,
                                    @WebParam(name = "checkOutDate") Calendar checkOut) 
            throws PartnerNotFoundException, InvalidLoginCredentialsException, SearchHotelRoomsException {
        Partner partner = partnerControllerLocal.loginPartner(name, password);
        System.out.println("********** PartnerWebService.searchHotelRooms(): Partner " + partner.getName() + " search remotely via web service");
        GregorianCalendar checkInDate = new GregorianCalendar(checkIn.YEAR, checkIn.MONTH, checkIn.DATE);
        GregorianCalendar checkOutDate = new GregorianCalendar(checkOut.YEAR, checkOut.MONTH, checkOut.DATE);
        // checkout date must be after checkin date; for online reservation, cannot have current date being the checkin date
        if(checkInDate.compareTo(checkOutDate) >= 0 || checkInDate.compareTo(new GregorianCalendar()) <= 0){
            throw new SearchHotelRoomsException("Invalid dates for searching!");
        }
        List<RoomSearchResult> results = partnerControllerLocal.searchHotelRooms(checkInDate, checkOutDate);
        return results;
    }
    
    @WebMethod(operationName = "requestRemoteCheckoutController")
    public String requestRemoteCheckoutController(@WebParam(name = "name") String name, 
                                                                @WebParam(name = "password") String password) 
            throws PartnerNotFoundException, InvalidLoginCredentialsException, UnableToCreateRemoteCheckoutControllerException {
        
        Partner partner = partnerControllerLocal.loginPartner(name, password);
        System.out.println("********** PartnerWebService.serverStateRequestRemoteCheckoutController(): Partner " + partner.getName());
        
        String sessionKey = remotePartnerCheckoutManager.createNewRemotePartnerCheckoutController();
        
        if(sessionKey != null){
            return sessionKey;
        }else{
            throw new UnableToCreateRemoteCheckoutControllerException("The server is unable to create a new checkout controller to support your remote checout request at this juncture. Please try again later.");
        }
    }
    
    @WebMethod(operationName = "remoteAddItem")
    public BigDecimal remoteAddItem(@WebParam(name = "name") String name, 
                                                @WebParam(name = "password") String password,
                                                @WebParam(name = "sessionKey") String sessionKey,
                                                @WebParam(name = "remoteReservationLineItem") RemoteReservationLineItem remoteReservationLineItem)
            throws PartnerNotFoundException, InvalidLoginCredentialsException, RemotePartnerCheckoutControllerNotFoundException,
                   RoomRateNotFoundException, RoomTypeNotFoundException, InvalidNumOfRoomsException
    {
        Partner partner = partnerControllerLocal.loginPartner(name, password);
        System.out.println("********** PartnerWebService.serverStateRequestRemoteCheckoutController(): Partner " + partner.getName());
        
        PartnerCheckoutControllerLocal partnerCheckoutControllerLocal = remotePartnerCheckoutManager.retrieveRemotePartnerCheckoutController(sessionKey);
        // instantiate the reservation item with the provided information
        List<RoomNight> roomNights = new ArrayList<RoomNight>();
        for(RemoteRoomNight remoteRoomNight: remoteReservationLineItem.getRoomNights()){
            RoomRate roomRate = roomRateControllerLocal.retrieveRoomRateById(remoteRoomNight.getRoomRateId());
            roomNights.add(new RoomNight(remoteRoomNight.getDate(), roomRate));
        }
        
        RoomType intendedRoomType = em.find(RoomType.class, remoteReservationLineItem.getIntendedRoomTypeId());
        if(intendedRoomType == null){
            throw new RoomTypeNotFoundException("Required room type does not exist!");
        }
        
        Integer numOfRooms = remoteReservationLineItem.getNumOfRooms();
        if(numOfRooms <= 0){
            throw new InvalidNumOfRoomsException("Invalid number of rooms!");
        }
        
        ReservationLineItem reservationLineItem = new ReservationLineItem(numOfRooms, intendedRoomType, roomNights);
        
        return partnerCheckoutControllerLocal.addReservationLineItem(reservationLineItem);
    }

    @WebMethod(operationName = "remoteCheckOutReservation")
    public OnlinePartnerReservation remoteCheckOutReservation(@WebParam(name = "name") String name, 
                                                @WebParam(name = "password") String password,
                                                @WebParam(name = "sessionKey") String sessionKey,
                                                @WebParam(name = "identificationNumber") String identificationNumber,
                                                @WebParam(name = "firstName") String firstName,
                                                @WebParam(name = "lastName") String lastName,
                                                @WebParam(name = "phoneNumber") String phoneNumber)
            throws PartnerNotFoundException, InvalidLoginCredentialsException, RemotePartnerCheckoutControllerNotFoundException, 
             CreateReservationException
    {
        Partner partner = partnerControllerLocal.loginPartner(name, password);
        System.out.println("********** PartnerWebService.serverStateRequestRemoteCheckoutController(): Partner " + partner.getName());
        
        PartnerCheckoutControllerLocal partnerCheckoutControllerLocal = remotePartnerCheckoutManager.retrieveRemotePartnerCheckoutController(sessionKey);
        OnlinePartnerReservation reservation = partnerCheckoutControllerLocal.checkOutReservation(name, identificationNumber,
            firstName, lastName, phoneNumber);
        remotePartnerCheckoutManager.removeRemotePartnerCheckoutController(sessionKey);
        
        em.detach(reservation);
        em.detach(reservation.getPartner());
        reservation.setPartner(null);
        
        return reservation;
    }
}
