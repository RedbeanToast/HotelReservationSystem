/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import enumerations.RoomStatusEnum;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import static javax.swing.text.StyleConstants.Size;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    @NotNull
    @Size(min=4, max=4)
    private Integer roomNumber;
    @NotNull
    @Enumerated(EnumType.STRING)
    private RoomStatusEnum roomStatus;
    @NotNull
    private Boolean enabled;
    
    @ManyToOne
    private RoomType roomType;
    @OneToMany(mappedBy = "room")
    private List<Reservation> reservations = new ArrayList<Reservation>();
    private Reservation currentOccupancy;

    public Room() {
        
    }

    public Room(Integer roomNumber, RoomStatusEnum roomStatus, Boolean enabled, RoomType roomType) {
        
        this();
        
        this.roomNumber = roomNumber;
        this.roomStatus = roomStatus;
        this.enabled = enabled;
        this.roomType = roomType;
    }

    public Long getRoomId() {
        return roomId;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomStatusEnum getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatusEnum roomStatus) {
        this.roomStatus = roomStatus;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Reservation getCurrentOccupancy() {
        return currentOccupancy;
    }

    public void setCurrentOccupancy(Reservation currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomNumber != null ? roomNumber.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomId fields are not set
        if (!(object instanceof Room)) {
            return false;
        }
        Room other = (Room) object;
        if ((this.roomNumber == null && other.roomNumber != null) || (this.roomNumber != null && !this.roomNumber.equals(other.roomNumber))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Room[ id=" + roomNumber + " ]";
    }
    
}
