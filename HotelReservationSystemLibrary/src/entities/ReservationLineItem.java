/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class ReservationLineItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationLineItemId;
    @NotNull
    @Min(1)
    private Integer numOfRooms;
    
    @OneToOne
    private RoomType intendedRoomType;
    @NotNull
    @JoinColumn(nullable=false)
    @ManyToOne(optional=false)
    private Reservation reservation;
    @OneToMany(mappedBy="reservationLineItem")
    @Size(min=1)
    private List<RoomNight> roomNights;

    public ReservationLineItem() {
    
    }

    public ReservationLineItem(Integer numOfRooms, RoomType intendedRoomType, Reservation reservation, List<RoomNight> roomNights) {
        
        this();
        
        this.numOfRooms = numOfRooms;
        this.intendedRoomType = intendedRoomType;
        this.reservation = reservation;
        this.roomNights = roomNights;
    }

    public RoomType getIntendedRoomType() {
        return intendedRoomType;
    }

    public void setIntendedRoomType(RoomType intendedRoomType) {
        this.intendedRoomType = intendedRoomType;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public List<RoomNight> getRoomNights() {
        return roomNights;
    }

    public void setRoomNights(List<RoomNight> roomNights) {
        this.roomNights = roomNights;
    }

    public Long getReservationLineItemId() {
        return reservationLineItemId;
    }

    public void setReservationLineItemId(Long reservationLineItemId) {
        this.reservationLineItemId = reservationLineItemId;
    }

    public Integer getNumOfRooms() {
        return numOfRooms;
    }

    public void setNumOfRooms(Integer numOfRooms) {
        this.numOfRooms = numOfRooms;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationLineItemId != null ? reservationLineItemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationLineItemId fields are not set
        if (!(object instanceof ReservationLineItem)) {
            return false;
        }
        ReservationLineItem other = (ReservationLineItem) object;
        if ((this.reservationLineItemId == null && other.reservationLineItemId != null) || (this.reservationLineItemId != null && !this.reservationLineItemId.equals(other.reservationLineItemId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.ReservationItem[ id=" + reservationLineItemId + " ]";
    }
    
}
