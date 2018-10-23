/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import entities.RoomRate;
import entities.RoomType;

/**
 *
 * @author CaiYuqian
 */
public class RoomSearchResult {
    private RoomType roomType;
    private RoomRate roomRate;
    private Integer amountAvailable = 0;

    public RoomSearchResult(RoomType roomType, RoomRate roomRate, Integer amountAvailable) {
        this.roomType = roomType;
        this.roomRate = roomRate;
        this.amountAvailable = amountAvailable;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public RoomRate getRoomRate() {
        return roomRate;
    }

    public void setRoomRate(RoomRate roomRate) {
        this.roomRate = roomRate;
    }

    public Integer getAmountAvailable() {
        return amountAvailable;
    }

    public void setAmountAvailable(Integer amountAvailable) {
        this.amountAvailable = amountAvailable;
    }
    
    
}
