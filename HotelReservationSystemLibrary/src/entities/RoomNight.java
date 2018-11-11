/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class RoomNight implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomNightId;
    @NotNull
    private GregorianCalendar date;
    
    @NotNull
    @JoinColumn(nullable=false)
    @ManyToOne(optional=false)
    private ReservationLineItem reservationLineItem;
    @ManyToOne
    private Room room;
    @OneToOne
    private RoomAllocationExceptionReport roomAllocationExceptionReport;
    @NotNull
    @JoinColumn(nullable=false)
    @ManyToOne(optional=false)
    private RoomRate roomRate;
    
    public RoomNight() {
        
    }

    public RoomNight(GregorianCalendar date, RoomRate roomRate) {
        
        this();
        
        this.date = date;
        this.roomRate = roomRate;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public RoomAllocationExceptionReport getRoomAllocationExceptionReport() {
        return roomAllocationExceptionReport;
    }

    public void setRoomAllocationExceptionReport(RoomAllocationExceptionReport roomAllocationExceptionReport) {
        this.roomAllocationExceptionReport = roomAllocationExceptionReport;
    }

    public RoomRate getRoomRate() {
        return roomRate;
    }

    public void setRoomRate(RoomRate roomRate) {
        this.roomRate = roomRate;
    }

    public Long getRoomNightId() {
        return roomNightId;
    }

    public void setRoomNightId(Long roomNightId) {
        this.roomNightId = roomNightId;
    }

    public ReservationLineItem getReservationLineItem() {
        return reservationLineItem;
    }

    public void setReservationLineItem(ReservationLineItem reservationLineItem) {
        this.reservationLineItem = reservationLineItem;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomNightId != null ? roomNightId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomNightId fields are not set
        if (!(object instanceof RoomNight)) {
            return false;
        }
        RoomNight other = (RoomNight) object;
        if ((this.roomNightId == null && other.roomNightId != null) || (this.roomNightId != null && !this.roomNightId.equals(other.roomNightId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.RoomNight[ id=" + roomNightId + " ]";
    }
    
}
