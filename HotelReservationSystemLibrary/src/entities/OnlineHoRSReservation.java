/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import enumerations.ReservationStatusEnum;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class OnlineHoRSReservation extends Reservation {

    private static final long serialVersionUID = 1L;
    
    @ManyToOne
    private Guest guest;

    public OnlineHoRSReservation(BigDecimal amount, Date checkIn, Date checkOut, Date madeDate, ReservationStatusEnum status, Boolean isUpgraded, Room room, Guest guest) {
        super(amount, checkIn, checkOut, madeDate, status, isUpgraded, room);
        this.guest = guest;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OnlineHoRSReservation)) {
            return false;
        }
        OnlineHoRSReservation other = (OnlineHoRSReservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.OnlineHoRSReservation[ id=" + reservationId + " ]";
    }
    
}
