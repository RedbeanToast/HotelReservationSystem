/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.Room;
import exceptions.RoomNotFoundException;
import javax.ejb.Local;

/**
 *
 * @author CaiYuqian
 */

public interface RoomControllerLocal {
    Room retrieveRoomByRoomNumber(Integer roomNumber) throws RoomNotFoundException;
}