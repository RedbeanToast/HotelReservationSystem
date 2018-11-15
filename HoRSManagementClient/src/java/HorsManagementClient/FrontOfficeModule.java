/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HorsManagementClient;

import ejb.session.stateful.WalkInReservationControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entities.Employee;
import entities.Guest;
import entities.Reservation;
import entities.ReservationLineItem;
import entities.Room;
import entities.RoomAllocationExceptionReport;
import entities.RoomNight;
import entities.RoomType;
import entities.WalkInReservation;
import enumerations.ReservationStatusEnum;
import enumerations.RoomStatusEnum;
import exceptions.CreateNewGuestException;
import exceptions.CreateReservationException;
import exceptions.GuestNotFoundException;
import exceptions.RoomNotFoundException;
import exceptions.RoomTypeNotFoundException;
import exceptions.SearchHotelRoomsException;
import exceptions.UpdateRoomException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import javax.ejb.EJB;
import utilities.RoomSearchResult;

/**
 *
 * @author zhangruichun
 */
public class FrontOfficeModule {

    @EJB
    private WalkInReservationControllerRemote walkInReservationControllerRemote;
    @EJB
    private ReservationControllerRemote reservationControllerRemote;
    @EJB
    private RoomTypeControllerRemote roomTypeControllerRemote;
    @EJB
    private RoomControllerRemote roomControllerRemote;
    
    private Employee currentEmployee;
    private Guest guest;

    public FrontOfficeModule(WalkInReservationControllerRemote walkInReservationControllerRemote, ReservationControllerRemote reservationControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, RoomControllerRemote roomControllerRemote, Employee currentEmployee) {
        this.walkInReservationControllerRemote = walkInReservationControllerRemote;
        this.reservationControllerRemote = reservationControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.currentEmployee = currentEmployee;
    }

    public void menuGuestRelationOfficer() {
        Scanner sc = new Scanner(System.in);
        int response;
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

        Date checkInDate = new Date();
        Date checkOutDate = new Date();
        int availableAmount;
        int num;
        int roomNeed;
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/m/y");
        
        System.out.println("***Hotel Management System:: Front Office:: Walk In Search Room");

        System.out.print("Enter Check Out Date: ");
        checkOutDate = dateFormat.parse(sc.nextLine());
        
        

        System.out.print("Enter Number of Person> ");
        num = sc.nextInt();

        try {
            List<RoomSearchResult> roomSearchResults = walkInReservationControllerRemote.searchHotelRooms(checkOutDate);
            for (RoomSearchResult roomSearchResult : roomSearchResults) {
                
                System.out.println("Room Type: " + roomSearchResult.getRoomType());
                List<RoomNight> roomNights = roomSearchResult.getRoomNights();
                System.out.println("Room Night: ");
                for (RoomNight roomNight : roomNights) {
                    Date date1 = roomNight.getDate();
                    System.out.print(dateFormat.format(date1));
                }
                System.out.println("Available Amount: " + roomSearchResult.getAmountAvailable());
            }
            
            guest = new Guest();
            while (guest.getGuestId() != null) {
                System.out.println("1. Make reservation for a guest registered in the system");
                System.out.println("2. Make reservation for a new guest");
                response = sc.nextInt();
                sc.nextLine();
                if (response == 1) {
                    System.out.print("Enter Guest Identification Number>");
                    try {
                        guest = walkInReservationControllerRemote.retrieveGuestByIdentificationNumber(sc.nextLine().trim());
                    } catch (GuestNotFoundException ex) {
                        System.out.println(ex.getMessage() + " Please try again!");
                    }
                } else if (response == 2) {
                    System.out.print("Enter first name> ");
                    guest.setFirstName(sc.nextLine().trim());
                    System.out.print("Enter last name> ");
                    guest.setLastName(sc.nextLine().trim());
                    System.out.print("Enter phone number> ");
                    guest.setPhoneNumber(sc.nextLine().trim());
                    System.out.print("Enter Identification Number> ");
                    guest.setIdentificationNumber(sc.nextLine().trim());
                    try {
                        guest = walkInReservationControllerRemote.createNewGuest(guest);
                    } catch (CreateNewGuestException ex) {
                        System.out.println(ex.getMessage() + " Please try again!");
                    }
                }
            }
            //create new reservation line item 
            WalkInReservation reservation;
            BigDecimal total = null;
            reservation = new WalkInReservation() {
            };
            List<ReservationLineItem> reservationLineItems = null;
            System.out.println("Enter Employee Id");
            Long employeeId = sc.nextLong();
            while (true) {
                ReservationLineItem reservationLineItem = null;
                System.out.print("Reservation Room Type Name> ");
                String name = sc.nextLine().trim();
                try {
                    RoomType roomType = roomTypeControllerRemote.retrieveRoomTypeByName(name);
                    reservationLineItem.setIntendedRoomType(roomType);
                } catch (RoomTypeNotFoundException ex) {
                    System.out.println("Room Type does not exist, please try again");
                }
                System.out.print("Reservation Room Amount> ");
                int amount = sc.nextInt();
                reservationLineItem.setNumOfRooms(amount);

                System.out.print("Do you want to reserve another room type? >1. Yes>2. No");
                int check;
                check = sc.nextInt();
                if (check == 2) {
                    break;
                }
                reservationLineItems.add(reservationLineItem);
                BigDecimal subTotal = walkInReservationControllerRemote.addReservationLineItem(reservationLineItem);
                total = total.add(subTotal);
            }

            try {
                reservation = walkInReservationControllerRemote.checkOutReservation(guest.getGuestId(), currentEmployee.getEmployeeId());
            } catch (CreateReservationException ex) {
                System.out.print("An Error occurred when creating a new reservation" + ex.getMessage());
            }
        } catch (SearchHotelRoomsException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void doCheckInGuest() {
        System.out.println("***Hotel Management System:: Front Office:: Check In Guest");
        Scanner sc = new Scanner(System.in);
        //for online reservation 
        String id;
        System.out.print("Enter guest ID ");
        id = sc.nextLine().trim();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/m/y");
        List<Reservation> reservations = reservationControllerRemote.retrieveValidReservationsByGuestIdentificationNumber(id);

        for (Reservation reservation : reservations) {
            List<ReservationLineItem> items = reservation.getReservationLineItems();
            for (ReservationLineItem item : items) {
                List<RoomNight> roomNights = item.getRoomNights();
                RoomAllocationExceptionReport report = reservationControllerRemote.retrieveRoomAllocationExceptionReportByLineItemId(item.getReservationLineItemId());
                if (report != null) {
                    if ("Upgrade".equals(report.getExceptionType().toString())) {
                        System.out.print("Check In Date> ");
                        System.out.print(dateFormat.format(report.getReservationLineItem().getReservation().getCheckIn()));
                        
                        System.out.println("");
            
                        System.out.print("Check Out Date> ");
                        System.out.print(dateFormat.format(report.getReservationLineItem().getReservation().getCheckOut()));
                        //System.out.print("Year> "+report.getReservationLineItem().getReservation().getCheckOut().get(Calendar.YEAR)+"> ");
                        //System.out.print("Month> " + report.getReservationLineItem().getReservation().getCheckOut().get(Calendar.MONTH)+"> ");
                        //System.out.print("Day> " + report.getReservationLineItem().getReservation().getCheckOut().get(Calendar.DAY_OF_MONTH)+"> ");
            
                        System.out.println("");
            
                        System.out.println("Exception Type: "+report.getExceptionType().toString());
                        System.out.println("Reservation Id: "+report.getReservationLineItem().getReservation().getReservationId());
                        System.out.print("\n");
                        System.out.print(report.getReservationLineItem().getReservation().getReservationId() + "has been upgraded");
                    } else {
                        System.out.print("Check In Date> ");
                        System.out.print(dateFormat.format(report.getReservationLineItem().getReservation().getCheckIn()));
                        //System.out.print("Year> "+report.getReservationLineItem().getReservation().getCheckIn().get(Calendar.YEAR)+"> ");
                        //System.out.print("Month> " + report.getReservationLineItem().getReservation().getCheckIn().get(Calendar.MONTH)+"> ");
                        //System.out.print("Day> " + report.getReservationLineItem().getReservation().getCheckIn().get(Calendar.DAY_OF_MONTH)+"> ");
            
                        System.out.println("");
            
                        System.out.print("Check Out Date> ");
                        System.out.print(dateFormat.format(report.getReservationLineItem().getReservation().getCheckOut()));
                        //System.out.print("Year> "+report.getReservationLineItem().getReservation().getCheckOut().get(Calendar.YEAR)+"> ");
                        //System.out.print("Month> " + report.getReservationLineItem().getReservation().getCheckOut().get(Calendar.MONTH)+"> ");
                        //System.out.print("Day> " + report.getReservationLineItem().getReservation().getCheckOut().get(Calendar.DAY_OF_MONTH)+"> ");
            
                        System.out.println("");
            
                        System.out.println("Exception Type: "+report.getExceptionType().toString());
                        System.out.println("Reservation Id: "+report.getReservationLineItem().getReservation().getReservationId());
                        System.out.print("\n");
                        System.out.print(report.getReservationLineItem().getReservation().getReservationId() + "has not allocate a room for you");
                    }
                }
                System.out.print("Do you want to check in this reservationLineItem? >1.Yes >2.No");
                int check = sc.nextInt();
                if (check == 1) {
                    for (RoomNight roomNight : roomNights) {
                        Room room = roomNight.getRoom();
                        room.setRoomStatus(RoomStatusEnum.values()[1]);
                        room.setCurrentOccupancy(item);
                        try {
                            roomControllerRemote.updateRoom(room, true);
                        } catch (UpdateRoomException ex) {
                            System.out.print("Error occurs when update room exception" + ex.getMessage());
                        }

                    }

                } else {
                    System.out.print("Not check in");
                }
            }
            int k = 0;
            for (ReservationLineItem item : items) {
                List<RoomNight> nights = item.getRoomNights();
                for (RoomNight night : nights) {
                    if (!night.getRoom().getRoomStatus().toString().equals("OCCUPIED")) {
                        k = 1;
                        break;
                    }
                }
                if (k == 1) {
                    break;
                }
            }
            if (k == 0) {
                reservation.setStatus(ReservationStatusEnum.SUCCESS);
            }
        }
    }

    private void doCheckOutGuest() {
        System.out.println("***Hotel Management System:: Front Office:: Check Out Guest");
        int roomNumber;
        int response = 0;
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Enter Room Number you want to check out> ");
        roomNumber = sc.nextInt();
        
        System.out.print("1. Check Out >");
        System.out.print("2. Apply For Late Check Out> ");
        System.out.print("3. Exit");
        response = sc.nextInt();
        Room room;
        try{
            room = roomControllerRemote.retrieveRoomByRoomNumber(roomNumber);
            while(true){
                if(response == 1){
                    room.setRoomStatus(RoomStatusEnum.values()[4]);
                    room.setCurrentOccupancy(null);
                }else if(response == 2){
                    room.setRoomStatus(RoomStatusEnum.values()[3]);
                }else if(response == 3){
                    break;
                }else{
                    System.out.print("Invalid input");
                }
            }
            try{
                roomControllerRemote.updateRoom(room, Boolean.TRUE);
            }catch(UpdateRoomException ex){
                System.out.print("Error occurs when update room "+ ex.getMessage());
            }
        }catch(RoomNotFoundException ex){
            System.out.print("Room cannot be found " + ex.getMessage());
        }
        
        

    }

}
