/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import enumerations.ReservationStatusEnum;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class OnlinePartnerReservation extends Reservation {

    private static final long serialVersionUID = 1L;
    
    @NotNull
    @ManyToOne(optional=false)
    @JoinColumn(nullable=false)
    private Partner partner;
    @NotNull
    @ManyToOne(optional=false)
    @JoinColumn(nullable=false)
    private Guest guest;
    

    public OnlinePartnerReservation() {
        
    }

    public OnlinePartnerReservation(BigDecimal amount, Date checkIn, Date checkOut, Date madeDate, ReservationStatusEnum status, List<ReservationLineItem> reservationLineItems, Partner partner, Guest guest) {
        super(amount, checkIn, checkOut, madeDate, status, reservationLineItems);
        this.partner = partner;
        this.guest = guest;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
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
        if (!(object instanceof OnlinePartnerReservation)) {
            return false;
        }
        OnlinePartnerReservation other = (OnlinePartnerReservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.OnlinePartnerReservation[ id=" + reservationId + " ]";
    }
    
}
