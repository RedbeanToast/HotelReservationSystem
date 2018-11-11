/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import entities.RoomNight;
import entities.RoomRate;
import entities.RoomType;
import java.util.List;

/**
 *
 * @author CaiYuqian
 */
public class RoomSearchResult {
    private RoomType roomType;
    // a collection is used here since during a single hotel stay different room rates may apply 
    private List<RoomNight> roomNights;
    private Integer amountAvailable = 0;

    public RoomSearchResult(RoomType roomType, List<RoomNight> roomNights, Integer amountAvailable) {
        this.roomType = roomType;
        this.roomNights = roomNights;
        this.amountAvailable = amountAvailable;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public List<RoomNight> getRoomNights() {
        return roomNights;
    }

    public void setRoomNights(List<RoomNight> roomNights) {
        this.roomNights = roomNights;
    }

    public Integer getAmountAvailable() {
        return amountAvailable;
    }

    public void setAmountAvailable(Integer amountAvailable) {
        this.amountAvailable = amountAvailable;
    }
    
    
}
