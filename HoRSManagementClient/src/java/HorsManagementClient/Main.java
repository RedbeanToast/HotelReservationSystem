/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HorsManagementClient;

import ejb.session.stateful.WalkInReservationControllerRemote;
import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;

import javax.ejb.EJB;

/**
 *
 * @author zhangruichun
 */

public class Main {
    
    @EJB 
    private static EmployeeControllerRemote employeeControllerRemote;
    @EJB
    private static RoomControllerRemote roomControllerRemote;
    @EJB 
    private static RoomRateControllerRemote roomRateControllerRemote;
    @EJB 
    private static RoomTypeControllerRemote roomTypeControllerRemote;
    @EJB
    private static WalkInReservationControllerRemote walkInReservationControllerRemote;
    @EJB
    private static ReservationControllerRemote reservationControllerRemote;
    
    
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(employeeControllerRemote,roomControllerRemote,roomRateControllerRemote,roomTypeControllerRemote, walkInReservationControllerRemote, reservationControllerRemote);
	mainApp.runApp();
    }
}
