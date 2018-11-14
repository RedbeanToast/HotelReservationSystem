/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HorsReservationClient;

import ejb.session.stateful.HoRSGuestControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entities.OnlineHoRSReservation;
import entities.RegisteredGuest;
import entities.Reservation;
import entities.ReservationLineItem;
import entities.RoomNight;
import entities.RoomType;
import exceptions.CreateReservationException;
import exceptions.GuestNotLoggedInException;
import exceptions.ReservationNotFoundException;
import exceptions.RetrieveReservationException;
import exceptions.RoomTypeNotFoundException;
import exceptions.SearchHotelRoomsException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.RoomSearchResult;

/**
 *
 * @author zhangruichun
 */
public class HolidayReservation {
    private HoRSGuestControllerRemote hoRSGuestControllerRemote;
    private ReservationControllerRemote reservationControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private RoomControllerRemote roomControllerRemote;

    public HolidayReservation() {
    }

    public HolidayReservation(HoRSGuestControllerRemote hoRSGuestControllerRemote, ReservationControllerRemote reservationControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, RoomControllerRemote roomControllerRemote) {
        this.hoRSGuestControllerRemote = hoRSGuestControllerRemote;
        this.reservationControllerRemote = reservationControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
    }
    
    
    void doSearchHotelRoom(RegisteredGuest registerGuest) {
        System.out.println("Welcome to Hors Reservation System:: Search Hotel Room");

        Scanner sc = new Scanner(System.in);
        int response;
        int availableAmount;
        int num;
        int roomNeed;

        System.out.println("***Hotel Management System:: Front Office:: Walk In Search Room");

        System.out.print("Enter Check In Date: ");
        System.out.print("Enter year (YYYY): ");
        Integer yearIn = sc.nextInt();
        System.out.print("Enter month (MM): ");
        Integer monthInt = sc.nextInt();
        System.out.print("Enter date (DD): ");
        Integer dateIn = sc.nextInt();
        GregorianCalendar checkInDate = new GregorianCalendar(yearIn, monthInt, dateIn);

        System.out.print("Enter Check Out Date: ");
        System.out.print("Enter year (YYYY): ");
        Integer yearOut = sc.nextInt();
        System.out.print("Enter month (MM): ");
        Integer monthOut = sc.nextInt();
        System.out.print("Enter date (DD): ");
        Integer dateOut = sc.nextInt();
        GregorianCalendar checkOutDate = new GregorianCalendar(yearOut, monthOut, dateOut);

        String roomType;
        System.out.print("Enter Number of Person> ");
        num = sc.nextInt();
        BigDecimal total = null;
        List<RoomSearchResult> roomSearchResults;
        try {
            roomSearchResults = hoRSGuestControllerRemote.searchHotelRooms(checkInDate, checkOutDate);
            Reservation reservation = new Reservation() {
            };
            List<ReservationLineItem> reservationLineItems = null;
            for (RoomSearchResult roomSearchResult : roomSearchResults) {

                System.out.println("Room Type: " + roomSearchResult.getRoomType());
                List<RoomNight> roomNights = roomSearchResult.getRoomNights();
                System.out.println("Room Night: ");
                for (RoomNight roomNight : roomNights) {
                    GregorianCalendar date = roomNight.getDate();
                    System.out.print(date.get(Calendar.DATE));
                }

                System.out.println("Available Amount: " + roomSearchResult.getAmountAvailable());
                System.out.print("Do you want to reserve this room search result> 1. Yes> 2.No>");
                int r = sc.nextInt();
                if (r == 1) {
                    ReservationLineItem item = doReserveHotelRoom(roomSearchResult);
                    reservationLineItems.add(item);
                    BigDecimal subtotal = hoRSGuestControllerRemote.addReservationLineItem(item);
                    total = total.add(subtotal);
                }
            }

            try {
                reservation = hoRSGuestControllerRemote.checkOutReservation(registerGuest.getEmail());
            } catch (CreateReservationException ex) {
                System.out.print(ex.getMessage());
            }
            System.out.print("Total cost: " + total);
        } catch (SearchHotelRoomsException ex) {
            System.out.print(ex.getMessage());
        }

    }

    private ReservationLineItem doReserveHotelRoom(RoomSearchResult roomSearchResult){
        Scanner sc = new Scanner(System.in);
        ReservationLineItem reservationLineItem = null;
        System.out.print("Reservation Room Type Name> ");
        String name = sc.nextLine().trim();
            try{
                RoomType roomType = roomTypeControllerRemote.retrieveRoomTypeByName(name);
                reservationLineItem.setIntendedRoomType(roomType);
            }catch(RoomTypeNotFoundException ex){
                System.out.println("Room Type does not exist, please try again");
            }
        System.out.print("Reservation Room Amount> ");
        int amount = sc.nextInt();
        reservationLineItem.setNumOfRooms(amount);
        
        return reservationLineItem;
    }

    void doViewReservationDetails(RegisteredGuest registerGuest) {
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        System.out.println("Welcome to Hors Reservation System:: View Reservation Details");
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter Reservation Id>");
        Long id = sc.nextLong();
        OnlineHoRSReservation reservation;
        try {
            reservation = hoRSGuestControllerRemote.retrieveOnlineHoRSReservationDetailsByReservationId(id);
            System.out.println("Reservation Amount: " + reservation.getAmount());
            System.out.println("Check in date: " + outputDateFormat.format(reservation.getCheckIn()));
            System.out.println("Check out date: " + outputDateFormat.format(reservation.getCheckOut()));
            System.out.println("Reservation Made date" + outputDateFormat.format(reservation.getMadeDate()));
            System.out.println("Reservation Status" + reservation.getStatus());
        } catch (ReservationNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

    }

    void doViewAllReservations(RegisteredGuest registerGuest) {
        List<OnlineHoRSReservation> list;
        try {
            list = hoRSGuestControllerRemote.retrieveAllOnlineHoRSReservations(registerGuest.getEmail());
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (OnlineHoRSReservation reservation : list) {
                System.out.println("Reservation Amount: " + reservation.getAmount());
                System.out.println("Check in date: " + outputDateFormat.format(reservation.getCheckIn()));
                System.out.println("Check out date: " + outputDateFormat.format(reservation.getCheckOut()));
                System.out.println("Reservation Made date" + outputDateFormat.format(reservation.getMadeDate()));
                System.out.println("Reservation Status" + reservation.getStatus());
            }
        } catch (GuestNotLoggedInException ex) {
            System.out.println(ex.getMessage());
        } catch (RetrieveReservationException ex) {
            System.out.println(ex.getMessage());
        }

    }
}
