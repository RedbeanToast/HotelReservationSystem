/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.Guest;
import entities.OnlineHoRSReservation;
import exceptions.GuestNotFoundException;
import exceptions.InvalidLoginCredentialsException;
import exceptions.OnlineHoRSReservationNotFoundException;
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
import javax.validation.constraints.Size;

/**
 *
 * @author CaiYuqian
 */
@Stateless
@Local(HoRSGuestControllerLocal.class)
@Remote(HoRSGuestControllerRemote.class)
public class HoRSGuestController implements HoRSGuestControllerRemote, HoRSGuestControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public Guest registerGuest(@NotNull Guest guest){
        em.persist(guest);
        em.flush();
        
        return guest;
    }
    
    public Guest loginGuest(@NotNull @Size(min=1, max=64) String email, @NotNull @Size(min=6, max=20) String password) throws InvalidLoginCredentialsException {
        try{
            Guest guest = retrieveGuestByEmail(email);
            if(!guest.getPassword().equals(password)){
                throw new InvalidLoginCredentialsException("Login failed: the password does not match!");
            }
            return guest;
        }catch(GuestNotFoundException ex){
            throw new InvalidLoginCredentialsException("Login failed: " + ex.getMessage());
        }
    }
    
    public Guest retrieveGuestByEmail(@NotNull @Size(min=1, max=64) String email) throws GuestNotFoundException {
        Query query = em.createQuery("SELECT g FROM Guest g WHERE g.email = :inEmail");
        query.setParameter("inEmail", email);
        try{
            Guest guest = (Guest)query.getSingleResult();
            return guest;
        } catch(NoResultException | NonUniqueResultException ex) {
            throw new GuestNotFoundException("Guest not found: " + ex.getMessage());
        }
    }
    
    public OnlineHoRSReservation retrieveOnlineHoRSReservationDetailsByReservationId(@NotNull Long reservationId) throws OnlineHoRSReservationNotFoundException {
        Query query = em.createQuery("SELECT ohr FROM OnlineHoRSReservation ohr WHERE ohr.reservationid = :inReservationId");
        query.setParameter("inReservationId", reservationId);
        try{
            OnlineHoRSReservation reservation = (OnlineHoRSReservation)query.getSingleResult();
            return reservation;
        } catch(NoResultException | NonUniqueResultException ex) {
            throw new OnlineHoRSReservationNotFoundException("Online HoRS reservation not found: " + ex.getMessage());
        }
    }
    
    public List<OnlineHoRSReservation> retrieveAllOnlineHoRSReservationsByEmail(@NotNull @Size(min=1, max=64) String email){
        Query query = em.createQuery("SELECT ohr FROM OnlineHoRSReservation ohr WHERE ohr.guest.email = :inEmail");
        query.setParameter("inEmail", email);
        List<OnlineHoRSReservation> reservations = (List<OnlineHoRSReservation>)query.getResultList();
        
        return reservations;
    }
}
