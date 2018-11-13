/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HorsManagementClient;

import ejb.session.stateful.WalkInReservationControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import entities.Guest;
import entities.PeakRoomRate;
import entities.Reservation;
import entities.ReservationLineItem;
import entities.RoomAllocationExceptionReport;
import entities.RoomNight;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import utilities.RoomSearchResult;

/**
 *
 * @author zhangruichun
 */
public class FrontOfficeModule {

    private WalkInReservationControllerRemote walkInReservationControllerRemote;
    private ReservationControllerRemote reservationControllerRemote;

    
    //overloaded constructor

    public FrontOfficeModule() {

    }

    public FrontOfficeModule(WalkInReservationControllerRemote walkInReservationControllerRemote, ReservationControllerRemote reservationControllerRemote) {
        this.walkInReservationControllerRemote = walkInReservationControllerRemote;
        this.reservationControllerRemote = reservationControllerRemote;
    }

    public void menuGuestRelationOfficer() {
        Scanner sc = new Scanner(System.in);
        int response = 0;
        while (true) {
            System.out.println("***Hotel Management System:: Front Office");
            System.out.println("1. Walk in search room");
            System.out.println("2. Check in guest");
            System.out.println("3. Check out guest");
            System.out.println("4. Back\n");
            response = 0;

            while (response < 1 || response > 4) {
                System.out.print(">");
                response = sc.nextInt();
                if (response == 1) {
                    doWalkInSearchRoom();
                } else if (response == 2) {
                    doCheckInGuest();
                } else if (response == 3) {
                    doCheckOutGuest();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again \n");
                }
            }
            if (response == 4) {
                break;
            }
        }
    }

    private void doWalkInSearchRoom() throws ParseException {

        Scanner sc = new Scanner(System.in);
        int response;
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        GregorianCalendar checkInDate= new GregorianCalendar();
        GregorianCalendar checkOutDate = new GregorianCalendar();
        int availableAmount;
        int num;
        int roomNeed;
        

        System.out.println("***Hotel Management System:: Front Office:: Walk In Search Room");
        System.out.print("Enter Check In Date (dd/mm/yyyy)> ");
        Date date1 = inputDateFormat.parse(sc.nextLine().trim());
        checkInDate.setTime(date1);
                    
        System.out.print("Enter Check Out Date (dd/mm/yyyy)> ");
        Date date2 = inputDateFormat.parse(sc.nextLine().trim());
        checkInDate.setTime(date2);
        
        System.out.print("Enter Number of Person> ");
        num = sc.nextInt();

        List<RoomSearchResult> roomSearchResults = walkInReservationControllerRemote.searchHotelRoom(checkInDate, checkOutDate);
        for (RoomSearchResult roomSearchResult : roomSearchResults) {
            availableAmount = roomSearchResult.getAvailableAmount();
            roomNeed = num / (roomSearchResult.getRoomType().getCapacity()) + 1;
            if (roomNeed <= availableAmount) {
                System.out.println("Room Type: " + roomSearchResult.getRoomType());
                List<RoomNight> roomNights = roomSearchResul.getRoomNight();
                System.out.println("Room Night: ");
                for (RoomNight roomNight : roomNights) {
                    Date date = roomNight.getDate();
                    System.out.print(outputDateFormat.format(date));
                }
                System.out.println("Available Amount: " + roomSearchResult.getAvailableAmount());
                System.out.println("1. Make Reservation");
                response = sc.nextInt();
                if (response == 1) {
                    doMakeReservation(roomSearchResult, roomNeed, checkInDate, checkOutDate);
                }
            }
        }
    }

    private void doMakeReservation(RoomSearchResult roomSearchResult, int roomNeed, Date checkInDate, Date checkOutDate) {
        int response = 0;
        Scanner sc = new Scanner(System.in);
        Reservation reservation = new Reservation();
        //check in date is date????? 
        reservation.setCheckIn(checkInDate);
        reservation.setMadeDate(checkInDate);
        reservation.setCheckOur(checkOurDate);
        reservation.setStatus("COMPLETED");
        reservation.setRoomType(roomSearchResult.getRoomType());
        reservation.setNumOfRooms(roomNeed);

        Guest guest = new Guest();
        System.out.print("Are you a registered guest? 1. yes 2. No");
        response = sc.nextInt();
        if (response == 1) {
            System.out.print("Enter guest email> ");
            guest = guestControllerRemote.retrieveGuestByEmail(sc.nextLine().trim());
        } else if (response == 2) {
            System.out.print("Enter guest first name> ");
            guest.setFirstName(sc.nextLine().trim());
            System.out.print("Enter guest last name> ");
            guest.setLastName(sc.nextLine().trim());
            System.out.print("Enter guest phone number> ");
            guest.setPhoneNumber(sc.nextLine().trim());
            System.out.print("Enter guest identification number> ");
            guest.setIdentificationNumnber(sc.nextLine().trim());

            guest = guestControllerRemote.createNewGuest(guest);
        }

        reservation = ReservationControllerRemote.createNewWalkinReservation(reservation, guest.getGuestId());

        System.out.print("1. Confirm to make payment> ");
        System.out.print("2. Back \n");
        System.out.print("> ");
        int confirm = 0;
        confirm = sc.nextInt();
        if (confirm == 1) {
            System.out.println("\nTotal Amount Payable is " + reservation.getAmount());
        } else {
            System.out.println("payment does not confirm");
        }

    }

    private void doCheckInGuest() {
        Calender today = Calender.getInstance();
        System.out.println("***Hotel Management System:: Front Office:: Check In Guest");
        String identificationNumber;
        System.out.print("Enter guest identification number");
        identificationNumber = sc.nextLine().trim();
        List<Reservation> allAeservations = reservationControllerRemote.retrieveAllReservationsByGuestIdentificationNumber();
        for (Reservation reservation : allReservations) {
            if (reservation.checkInDate().toString().equals(today.toString())) {
                doCheckIn(reservation, guest);
            }
        }
    }

    private void doCheckInReservation(Reservation reservation, Guest guest) {
        List<ReservationLineItem> items = reservation.getReservationLineItem();
        for (ReservationLineItem item : items) {
            List<RoomNight> roomNights = item.getRoomNight();
            for (RoomNight roomNight : roomNights) {
                if (roomNight.getRoomAllocationExceptionReport() != null) {
                    RoomAllocationExceptionReport roomAllocationExceptionReport = roomNight.getRoomAllocationExceptionReport();
                    //upgrade already
                    if (roomAllocationExceptionReport.getExceptionType() == "Upgrade") {
                        System.out.print("RoomNight: " + roomNight.getDate().toString() + "has been upgraded" + roomAllocationExceptionReport.getRoomNight().getRoomNumber());
                    } else {
                        System.out.print("RoomNight: " + roomNight.getDate().toString() + "has not allocate a room for you");
                    }
                } else {
                    item.getReservation().setStatus("SUCCESS");

                    //current occupancy
                }
            }
            System.out.print("Your room type: " + item.getRoomType() + "is room number: " + item.getRoom().getRoomNumber());
        }
    }

    private void doCheckOutGuest() {
        Calender today = Calender.getInstance();
        System.out.println("***Hotel Management System:: Front Office:: Check Out Guest");
        String id;
        System.out.print("Enter guest identification number> ");
        List<Reservation> allAeservations = reservationControllerRemote.retrieveAllReservationsByGuestIdentificationNumber();
        for (Reservation reservation : allReservations) {
            if (reservation.checkInDate().toString().equals(today.toString())) {
                doCheckOut(reservation, guest);
            }
        }
    }

    private void doCheckOutreservation() {
        List<ReservationLineItem> items = reservation.getReservationLineItem();
        for (ReservationLineItem item : items) {
            item.getReservation().setStatus("COMPLETED");
            System.out.print("Your room type: " + item.getRoomType() + "check out");
        }
    }
}
