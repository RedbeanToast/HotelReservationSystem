/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.RoomType;
import exceptions.RoomTypeNotFoundException;
import java.util.List;
import javax.ejb.Local;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */
public interface RoomTypeControllerLocal {
    RoomType retrieveRoomTypeByName(@NotNull String name) throws RoomTypeNotFoundException;
    List<RoomType> retrieveRoomTypesByCapacity(Integer capacityRequired);
}
