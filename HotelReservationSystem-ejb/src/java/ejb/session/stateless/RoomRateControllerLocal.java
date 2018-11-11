/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.RoomRate;
import entities.RoomType;
import exceptions.SearchRoomRateException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */

public interface RoomRateControllerLocal {
    RoomRate searchPrevailingRoomRate(@NotNull GregorianCalendar date, RoomType roomType, String reservationType) throws SearchRoomRateException;
}
