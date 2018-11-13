/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel.ws;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author CaiYuqian
 */
public class RemoteReservationLineItem implements Serializable {
    private List<RemoteRoomNight> roomNights;
    private Integer numOfRooms;
    private Long intendedRoomTypeId;

    public RemoteReservationLineItem(List<RemoteRoomNight> roomNights, Integer numOfRooms, Long intendedRoomTypeId) {
        this.roomNights = roomNights;
        this.numOfRooms = numOfRooms;
        this.intendedRoomTypeId = intendedRoomTypeId;
    }

    public List<RemoteRoomNight> getRoomNights() {
        return roomNights;
    }

    public void setRoomNights(List<RemoteRoomNight> roomNights) {
        this.roomNights = roomNights;
    }

    public Integer getNumOfRooms() {
        return numOfRooms;
    }

    public void setNumOfRooms(Integer numOfRooms) {
        this.numOfRooms = numOfRooms;
    }

    public Long getIntendedRoomTypeId() {
        return intendedRoomTypeId;
    }

    public void setIntendedRoomTypeId(Long intendedRoomTypeId) {
        this.intendedRoomTypeId = intendedRoomTypeId;
    }
    
}
