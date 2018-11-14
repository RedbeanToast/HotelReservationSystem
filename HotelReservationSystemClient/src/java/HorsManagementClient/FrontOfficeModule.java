/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HorsManagementClient;

import ejb.session.stateful.HoRSGuestControllerRemote;
import ejb.session.stateful.WalkInReservationControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entities.Guest;
import entities.OnlineHoRSReservation;
import entities.PeakRoomRate;
import entities.PromotionRoomRate;
import entities.Reservation;
import entities.ReservationLineItem;
import entities.Room;
import entities.RoomAllocationExceptionReport;
import entities.RoomNight;
import entities.RoomType;
import entities.WalkInReservation;
import enumerations.JobRoleEnum;
import enumerations.ReservationStatusEnum;
import enumerations.RoomStatusEnum;
import exceptions.CreateReservationException;
import exceptions.GuestNotFoundException;
import exceptions.GuestNotLoggedInException;
import exceptions.RetrieveReservationException;
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
import javax.validation.constraints.NotNull;
import utilities.RoomSearchResult;

/**
 *
 * @author zhangruichun
 */
public class FrontOfficeModule {

    private WalkInReservationControllerRemote walkInReservationControllerRemote;
    private ReservationControllerRemote reservationControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    //overloaded constructor

    public FrontOfficeModule() {

    }

    public FrontOfficeModule(WalkInReservationControllerRemote walkInReservationControllerRemote, ReservationControllerRemote reservationControllerRemote,RoomTypeControllerRemote roomTypeControllerRemote, RoomControllerRemote roomControllerRemote) {
        this.walkInReservationControllerRemote = walkInReservationControllerRemote;
        this.reservationControllerRemote = reservationControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
    }

    public void menuGuestRelationOfficer() throws ParseException {
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

    private void doWalkInSearchRoom() throws ParseException, SearchHotelRoomsException, GuestNotFoundException {

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
        
                    
        System.out.print("Enter Check Out Date (dd/mm/yyyy)> ");
        Date date2 = inputDateFormat.parse(sc.nextLine().trim());
        checkOutDate.setTime(date2);
        
        System.out.print("Enter Number of Person> ");
        num = sc.nextInt();

        List<RoomSearchResult> roomSearchResults = walkInReservationControllerRemote.searchHotelRooms(checkOutDate);
        for (RoomSearchResult roomSearchResult : roomSearchResults) {
            
            System.out.println("Room Type: " + roomSearchResult.getRoomType());
            List<RoomNight> roomNights = roomSearchResult.getRoomNights();
            System.out.println("Room Night: ");
            for (RoomNight roomNight : roomNights) {
                GregorianCalendar date1 = roomNight.getDate();
                System.out.print(date1.get(Calendar.DATE));
            }
            System.out.println("Available Amount: " + roomSearchResult.getAmountAvailable());       
        }
        System.out.print("Are you a registered guest?> 1. Yes>2. No");
        response = sc.nextInt();
        Guest guest = new Guest();
        if(response == 1){
            System.out.print("Enter Guest Identification Number>");
            try{
                guest = walkInReservationControllerRemote.retrieveGuestByIdentificationNumber(sc.nextLine().trim());
            }catch(GuestNotFoundException ex){
                System.out.print("Guest Not Found");
            }     
        }else if(response == 2){
            System.out.print("Enter first name> ");
            guest.setFirstName(sc.nextLine().trim());
            System.out.print("Enter last name> ");
            guest.setLastName(sc.nextLine().trim());
            System.out.print("Enter phone number> ");
            guest.setPhoneNumber(sc.nextLine().trim());
            System.out.print("Enter Identification Number> ");
            guest.setIdentificationNumber(sc.nextLine().trim());
        }
        //create new reservation line item 
        WalkInReservation reservation;
        BigDecimal total = null; 
        reservation = new WalkInReservation() {};
        List<ReservationLineItem> reservationLineItems = null;
        System.out.println("Enter Employee Id");
        Long employeeId = sc.nextLong();
        while(true){
            ReservationLineItem reservationLineItem =null;
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
            
            System.out.print("Do you want to reserve another room type? >1. Yes>2. No");
            int check;
            check = sc.nextInt();
            if(check == 2){
                break;
            }
            reservationLineItems.add(reservationLineItem);
            BigDecimal subTotal = walkInReservationControllerRemote.addReservationLineItem(reservationLineItem);
            total = total.add(subTotal);           
        }
        try{
            reservation = reservationControllerRemote.createNewWalkInReservation(reservationLineItems,guest.getGuestId(),employeeId,checkOutDate,total);
        }catch(CreateReservationException ex){
            System.out.print("Error occurs when create new reservation"+ex.getMessage());
        }
    }

    
     
    private void doCheckInGuest() {
        System.out.println("***Hotel Management System:: Front Office:: Check In Guest");
        Scanner sc = new Scanner(System.in);
        //for online reservation 
        String id;
        System.out.print("Enter guest ID ");
        id = sc.nextLine().trim();
        
        List<Reservation> reservations = reservationControllerRemote.retrieveValidReservationsByGuestIdentificationNumber(id);
         
        for (Reservation reservation : reservations) {
            List<ReservationLineItem> items = reservation.getReservationLineItems();
            for(ReservationLineItem item: items){
                List<RoomNight> roomNights = item.getRoomNights();
                RoomAllocationExceptionReport report = reservationControllerRemote.retrieveRoomAllocationExceptionReportByLineItemId(item.getReservationLineItemId());
                if(report!=null){
                    if ("Upgrade".equals(report.getExceptionType().toString())) {
                        System.out.print("RoomNight: " + report.getRoomNight().getDate().toString() + "has been upgraded" + report.getRoomNight().getRoomNumber());
                    } else {
                        System.out.print("RoomNight: " + report.getRoomNight().getDate().toString() + "has not allocate a room for you");
                    }
                }
                System.out.print("Do you want to check in this reservationLineItem? >1.Yes >2.No");
                int check = sc.nextInt();
                if(check == 1){
                    for(RoomNight roomNight: roomNights){
                        Room room = roomNight.getRoom();
                        room.setRoomStatus(RoomStatusEnum.values()[1]);
                        room.setCurrentOccupancy(item);
                        try{
                            roomControllerRemote.updateRoom(room,true);
                        }catch(UpdateRoomException ex){
                            System.out.print("Error occurs when update room exception"+ex.getMessage());
                        }
                        
                    }
                    
                }else{
                    System.out.print("Not check in");
                }
            }
            int k = 0;
            for(ReservationLineItem item: items){
                List<RoomNight> nights = item.getRoomNights();
                for(RoomNight night:nights){
                    if(!night.getRoom().getRoomStatus().toString().equals("OCCUPIED")){
                        k =1;
                        break;
                    }
                }
                if(k==1){
                    break;
                }
            }
            if(k==0){
                reservation.setStatus(ReservationStatusEnum.SUCCESS);
            }
        }
    }

    private void doCheckOutGuest() {
        System.out.println("***Hotel Management System:: Front Office:: Check Out Guest");
        String id;
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter guest identification number> ");
        id = sc.nextLine().trim();

        List<Reservation> reservations = reservationControllerRemote.retrieveAllReservationsByGuestIdentificationNumber(id);
        for (Reservation reservation : reservations) {
            List<ReservationLineItem> items = reservation.getReservationLineItems();
            for (ReservationLineItem item : items) {
                System.out.println("1. Check Out> ");
                System.out.println("2. Apply for Late Check Out> ");
                int response = sc.nextInt();
                if(response == 1){
                    List<RoomNight> roomNights = item.getRoomNights();
                    for(RoomNight roomNight: roomNights){
                        Room room = roomNight.getRoom();
                        room.setRoomStatus(RoomStatusEnum.values()[4]);
                        room.setCurrentOccupancy(null);
                        try{
                            roomControllerRemote.updateRoom(room,true);
                        }catch(UpdateRoomException ex){
                            System.out.print("Error occurs when update room exception"+ex.getMessage());
                        }
                    }
                }else if(response == 2){
                    List<RoomNight> roomNights = item.getRoomNights();
                    for(RoomNight roomNight: roomNights){
                        Room room = roomNight.getRoom();
                        room.setRoomStatus(RoomStatusEnum.values()[3]);
                        
                        try{
                            roomControllerRemote.updateRoom(room,true);
                        }catch(UpdateRoomException ex){
                            System.out.print("Error occurs when update room exception"+ex.getMessage());
                        }
                    }
                }
            }
        }
        
        int k = 0;
        for(Reservation reservation: reservations){
            List<ReservationLineItem> items = reservation.getReservationLineItems();
            for(ReservationLineItem item: items){
                List<RoomNight> nights = item.getRoomNights();
                for(RoomNight night:nights){
                    if(!night.getRoom().getRoomStatus().toString().equals("CLEANING")){
                        k =1;
                        break;
                    }
                }
                if(k==1){
                    break;
                }
            }
            if(k==0){
                reservation.setStatus(ReservationStatusEnum.COMPLETED);
            }
        }
            

        
       
    }

    
}
