/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.RoomType;
import exceptions.DeleteRoomTypeException;
import exceptions.RoomTypeNotFoundException;
import java.util.List;
import javax.ejb.Remote;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */
public interface RoomTypeControllerRemote {
    RoomType createNewRoomType(@NotNull RoomType roomType);
    RoomType retrieveRoomTypeByName(@NotNull String name) throws RoomTypeNotFoundException;
    List<RoomType> retrieveAllRoomTypes();
    void updateRoomType(@NotNull RoomType roomType) throws RoomTypeNotFoundException;
    void deleteRoomTypeByName(@NotNull String name) throws DeleteRoomTypeException;
}
