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
import java.util.Calendar;
import javax.persistence.Column;
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
    @Column(name = "checkIn", columnDefinition="DATETIME")
    protected Calendar checkIn;
    @NotNull
    @Future
    @Column(name = "checkOut", columnDefinition="DATETIME")
    protected Calendar checkOut;
    @NotNull
    @Column(name = "madeDate", columnDefinition="DATETIME")
    protected Calendar madeDate;
    @NotNull
    @Enumerated(EnumType.STRING)
    protected ReservationStatusEnum status;
    @NotNull
    protected Boolean isUpgraded;
    protected Room roomAllocated;
    protected RoomType intendedRoomType;

    @ManyToOne
    protected RoomRate roomRate;
    @ManyToOne
    protected RoomType allocatedRoomType;

    public Reservation() {
        
    }

    public Reservation(BigDecimal amount, Calendar checkIn, Calendar checkOut, Calendar madeDate, ReservationStatusEnum status, Boolean isUpgraded, RoomType intendedRoomType, RoomRate roomRate) {
        
        this();
        
        this.amount = amount;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.madeDate = madeDate;
        this.status = status;
        this.isUpgraded = isUpgraded;
        this.intendedRoomType = intendedRoomType;
        this.roomRate = roomRate;
    } 

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Calendar getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Calendar checkIn) {
        this.checkIn = checkIn;
    }

    public Calendar getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Calendar checkOut) {
        this.checkOut = checkOut;
    }

    public Calendar getMadeDate() {
        return madeDate;
    }

    public void setMadeDate(Calendar madeDate) {
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

    public RoomRate getRoomRate() {
        return roomRate;
    }

    public void setRoomRate(RoomRate roomRate) {
        this.roomRate = roomRate;
    }

    public Room getRoomAllocated() {
        return roomAllocated;
    }

    public void setRoomAllocated(Room roomAllocated) {
        this.roomAllocated = roomAllocated;
    }

    public RoomType getAllocatedRoomType() {
        return allocatedRoomType;
    }

    public void setAllocatedRoomType(RoomType allocatedRoomType) {
        this.allocatedRoomType = allocatedRoomType;
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
