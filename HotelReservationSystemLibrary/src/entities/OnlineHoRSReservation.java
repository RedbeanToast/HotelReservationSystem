/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import enumerations.ReservationStatusEnum;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class OnlineHoRSReservation extends Reservation {

    private static final long serialVersionUID = 1L;

    public OnlineHoRSReservation(RegisteredGuest registeredGuest, BigDecimal amount, GregorianCalendar checkIn, GregorianCalendar checkOut, GregorianCalendar madeDate, ReservationStatusEnum status, List<ReservationLineItem> reservationLineItems) {
        super(registeredGuest, amount, checkIn, checkOut, madeDate, status, reservationLineItems);
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
