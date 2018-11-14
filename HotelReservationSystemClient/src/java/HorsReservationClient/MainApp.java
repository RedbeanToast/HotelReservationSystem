/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HorsReservationClient;

import ejb.session.stateful.HoRSGuestControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import entities.Guest;
import entities.RegisteredGuest;
import exceptions.GuestNotFoundException;
import exceptions.InvalidLoginCredentialsException;
import exceptions.SearchHotelRoomsException;
import java.text.ParseException;
import java.util.Scanner;

/**
 *
 * @author zhangruichun
 */
public class MainApp {
    private ReservationControllerRemote reservationControllerRemote;
    private HoRSGuestControllerRemote hoRSGuestControllerRemote;
    private HolidayReservation HolidayReservation;
    
    private RegisteredGuest guest;
    private RegisteredGuest registerGuest;
    
    public MainApp() {
    }

    public MainApp(ReservationControllerRemote reservationControllerRemote, HoRSGuestControllerRemote hoRSGuestControllerRemote) {
        this.reservationControllerRemote = reservationControllerRemote;
        this.hoRSGuestControllerRemote = hoRSGuestControllerRemote;
        
    }
    
    public void runApp() throws GuestNotFoundException, InvalidLoginCredentialsException {
        Scanner sc = new Scanner(System.in);
        int response = 0;

        while (true) {
            System.out.println("***Hors Reservation System***");
            System.out.print("1. Login> ");
            System.out.print("2. Register> ");
            System.out.print("3. Exit\n");

            response = sc.nextInt();
            response = 0;
            while (response < 1 | response > 3) {
                if (response == 1) {
                    try {
                        doLogin();
                        System.out.println("Login successgul");
                        doMenu();
                    } catch (GuestNotFoundException ex) {
                        System.out.println("Guest Not Found: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    doRegister();
                    System.out.println("Register Successfully");
                    System.out.println("1. Login>");
                    int r = sc.nextInt();
                    if(r == 1){
                        doLogin();
                    }else{
                        response = 3;
                        break;
                    }
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again");
                }
            }
            if (response == 3) {
                break;
            }
        }
    }
    
    private void doLogin() throws GuestNotFoundException, InvalidLoginCredentialsException {
        Scanner sc = new Scanner(System.in);
        String email = "";
        String password = "";
        System.out.println("***Hors Reservation System:: Login***");
        System.out.print("Enter email> ");
        email = sc.nextLine().trim();
        System.out.print("Enter password> ");
        password = sc.nextLine().trim();
        try{
           registerGuest = hoRSGuestControllerRemote.loginGuest(email, password);
        }catch(GuestNotFoundException ex){
            System.out.println("Guest can not found"+ex.getMessage());
        }
            
        
    }

    private void doRegister() {
        Scanner sc = new Scanner(System.in);
        String password;
        
        System.out.print("Enter first name> ");
        guest.setFirstName(sc.nextLine().trim());
        System.out.print("Enter last name> ");
        guest.setLastName(sc.nextLine().trim());
        System.out.print("Enter phone number> ");
        guest.setPhoneNumber(sc.nextLine().trim());
        System.out.print("Enter identification number> ");
        guest.setIdentificationNumber(sc.nextLine().trim());
        System.out.print("Enter email>");
        guest.setEmail(sc.nextLine().trim());
        System.out.print("Enter password>");
        password = sc.nextLine().trim();
        System.out.print("Please verify your password>");
        if (password == sc.nextLine().trim()) {
            guest.setPassword(password);
        } else {
            System.out.println("Password does not match");
        }

        guest = hoRSGuestControllerRemote.registerGuest(guest);

    }

    private void doMenu() throws ParseException, SearchHotelRoomsException {
        Scanner sc = new Scanner(System.in);
        int response = 0;
        while (true) {
            System.out.println("Welcome to Hors Reservation System");
            System.out.println("1. Search Hotel Room");
            System.out.println("2. View My Reservation Details");
            System.out.println("3. View All My Reservations");
            System.out.println("4. exit\n");

            response = 0;
            while (response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();
                if (response == 1) {
                    HolidayReservation.doSearchHotelRoom(RegisteredGuest registerGuest);
                } else if (response == 2) {
                    HolidayReservation.doViewReservationDetails(RegisteredGuest registerGuest);
                } else if (response == 3) {
                    HolidayReservation.doViewAllReservations(RegisteredGuest registerGuest);
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid input, please enter again");
                }
            }
            if (response == 4) {
                break;
            }
        }
    }
}
