/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel.ws;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 *
 * @author CaiYuqian
 */
public class RemoteRoomNight implements Serializable {
    private GregorianCalendar date;
    private Long roomRateId;

    public RemoteRoomNight(GregorianCalendar date, Long roomRateId) {
        this.date = date;
        this.roomRateId = roomRateId;
    }

    public GregorianCalendar getDate() {
        return date;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    public Long getRoomRateId() {
        return roomRateId;
    }

    public void setRoomRateId(Long roomRateId) {
        this.roomRateId = roomRateId;
    }
    
    
}
