/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.Room;
import exceptions.CreateNewRoomFailedException;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */

public interface RoomControllerRemote {
    Room createNewRoom(@NotNull Integer roomNumber, @NotNull String roomTypeName) throws CreateNewRoomFailedException;
}
