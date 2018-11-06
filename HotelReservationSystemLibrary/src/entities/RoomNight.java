/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    private Date date;

    public RoomNight() {
        
    }

    public RoomNight(Date date) {
        
        this();
        
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getRoomNightId() {
        return roomNightId;
    }

    public void setRoomNightId(Long roomNightId) {
        this.roomNightId = roomNightId;
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
