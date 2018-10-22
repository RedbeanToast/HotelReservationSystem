/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import enumerations.ReservationStatusEnum;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import static javax.swing.text.StyleConstants.Size;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class OnlinePartnerReservation extends Reservation {

    private static final long serialVersionUID = 1L;
    @NotNull
    @Size(min=1, max=16)
    private String guestFirstName;
    @Size(min=1, max=16)
    private String guestLastName;
    @NotNull
    @Size(min=1, max=20)
    private String identificationNumber;
    @NotNull
    @Size(min=6, max=20)
    private String phoneNumber;
    
    @ManyToOne
    private Partner partner;

    public OnlinePartnerReservation() {
        
    }

    public OnlinePartnerReservation(String guestFirstName, String guestLastName, String identificationNumber, String phoneNumber, BigDecimal amount, Date checkIn, Date checkOut, Date madeDate, ReservationStatusEnum status, Boolean isUpgraded, Room room, RoomRate roomRate, Partner partner) {
        super(amount, checkIn, checkOut, madeDate, status, isUpgraded, room, roomRate);
        this.guestFirstName = guestFirstName;
        this.guestLastName = guestLastName;
        this.identificationNumber = identificationNumber;
        this.phoneNumber = phoneNumber;
        this.partner = partner;
    }

    public String getGuestFirstName() {
        return guestFirstName;
    }

    public void setGuestFirstName(String guestFirstName) {
        this.guestFirstName = guestFirstName;
    }

    public String getGuestLastName() {
        return guestLastName;
    }

    public void setGuestLastName(String guestLastName) {
        this.guestLastName = guestLastName;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
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
