/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HorsReservationClient;

import ejb.session.stateful.HoRSGuestControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import exceptions.GuestNotFoundException;
import javax.ejb.EJB;

/**
 *
 * @author zhangruichun
 */
public class Main {

    @EJB(name = "ReservationControllerRemote ")
    private static ReservationControllerRemote reservationControllerRemote;

    @EJB(name = "HoRSGuestControllerRemote")
    private static HoRSGuestControllerRemote hoRSGuestControllerRemote;
    
    /**
     *
     * @param args
     * @throws GuestNotFoundException
     */
     
    public static void main(String[] args) throws GuestNotFoundException{
        HorsReservationClient.MainApp mainApp = new HorsReservationClient.MainApp(reservationControllerRemote,hoRSGuestControllerRemote);
	mainApp.runApp();
    }

    
    
    
    
    
    
     
    
    
    
    
    
    
    
}
