/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import enumerations.ReservationStatusEnum;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

/**
 *
 * @author CaiYuqian
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long reservationId;
    @NotNull
    @Digits(integer=6, fraction=2)
    protected BigDecimal amount;
    @NotNull
    @Future
    protected Date checkIn;
    @NotNull
    @Future
    protected Date checkOut;
    @NotNull
    protected Date madeDate;
    @NotNull
    @Enumerated(EnumType.STRING)
    protected ReservationStatusEnum status;
    @NotNull
    protected Boolean isUpgraded;
    
    @ManyToOne
    protected Room room;
    @ManyToOne
    protected RoomRate roomRate;

    public Reservation() {
        
    }

    public Reservation(BigDecimal amount, Date checkIn, Date checkOut, Date madeDate, ReservationStatusEnum status, Boolean isUpgraded, Room room, RoomRate roomRate) {
        
        this();
        
        this.amount = amount;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.madeDate = madeDate;
        this.status = status;
        this.isUpgraded = isUpgraded;
        this.room = room;
        this.roomRate = roomRate;
    } 

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Date checkIn) {
        this.checkIn = checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Date checkOut) {
        this.checkOut = checkOut;
    }

    public Date getMadeDate() {
        return madeDate;
    }

    public void setMadeDate(Date madeDate) {
        this.madeDate = madeDate;
    }

    public ReservationStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ReservationStatusEnum status) {
        this.status = status;
    }

    public Boolean getIsUpgraded() {
        return isUpgraded;
    }

    public void setIsUpgraded(Boolean isUpgraded) {
        this.isUpgraded = isUpgraded;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public RoomRate getRoomRate() {
        return roomRate;
    }

    public void setRoomRate(RoomRate roomRate) {
        this.roomRate = roomRate;
    }
    
    public Long getReservationId() {
        return reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Reservation[ id=" + reservationId + " ]";
    }
    
}
