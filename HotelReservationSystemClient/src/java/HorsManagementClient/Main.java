/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HorsManagementClient;

import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import exceptions.EmployeeNotFoundException;
import java.text.ParseException;

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
    
    
    public static void main(String[] args) throws EmployeeNotFoundException, ParseException{
        MainApp mainApp = new MainApp(employeeControllerRemote,roomControllerRemote,roomRateControllerRemote,roomTypeControllerRemote);
	mainApp.runApp();
    }
}
