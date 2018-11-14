/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HorsManagementClient;

import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entities.NormalRoomRate;
import entities.PeakRoomRate;
import entities.PromotionRoomRate;
import entities.PublishedRoomRate;
import entities.Room;
import entities.RoomAllocationExceptionReport;
import entities.RoomRate;
import entities.RoomType;
import enumerations.RoomStatusEnum;
import exceptions.CreateNewRoomException;
import exceptions.DeleteRoomException;
import exceptions.DeleteRoomRateException;
import exceptions.RoomNotFoundException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeNotFoundException;
import exceptions.UpdateRoomException;
import exceptions.UpdateRoomRateException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import exceptions.DeleteRoomTypeException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.function.Function;
import javax.ejb.EJB;
import javax.validation.Validation;
/**
 *
 * @author zhangruichun
 */

public class HotelOperationModule {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    @EJB
    private RoomTypeControllerRemote roomTypeControllerRemote;
    @EJB
    private RoomControllerRemote roomControllerRemote;
    @EJB
    private RoomRateControllerRemote roomRateControllerRemote;
    @EJB
    private ReservationControllerRemote reservationControllerRemote;
    
    public HotelOperationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    
    

    public HotelOperationModule(RoomTypeControllerRemote roomTypeControllerRemote, RoomControllerRemote roomControllerRemote, RoomRateControllerRemote roomRateControllerRemote) {
        this();
        
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
    }

    
    

    public void menuOperationManager() {
        Scanner sc = new Scanner(System.in);
        int response;
        while (true) {
            System.out.println("*** Hors Management System:: Hotel Operation:: Operation Manager ***");
            System.out.println("1. Create New Room Type");
            System.out.println("2. View Room Type Details");
            System.out.println("3. View All Room Types");
            System.out.println("-------------------------");
            System.out.println("4. Create New Room");
            System.out.println("5. Update Room");
            System.out.println("6. Delete Room");
            System.out.println("7. View All Rooms");
            System.out.println("-------------------------");
            System.out.println("8. View Room Allocation Exception Report");
            System.out.println("-------------------------");
            System.out.println("9. Back\n");

            response = 0;

            OUTER:
            while (response < 1 || response > 9) {
                System.out.println("> ");
                response = sc.nextInt();
                switch (response) {
                    case 1:
                        doCreateNewRoomType();
                        break;
                    case 2:
                        doViewRoomTypeDetails();
                        break;
                    case 3:
                        doViewAllRoomTypes();
                        break;
                    case 4:
                        doCreateNewRoom();
                        break;
                    case 5:
                        doUpdateRoom();
                        break;
                    case 6:
                        doDeleteRoom();
                        break;
                    case 7:
                        doViewAllRooms();
                        break;
                    case 8:
                        doViewRoomAllocationExceptionReport();
                        break;
                    case 9:
                        break OUTER;
                    default:
                        System.out.println("Invalid option, please try again");
                        break;
                }
            }
            if (response == 9) {
                break;
            }
        }
    }

    //7. create new room type 
    private void doCreateNewRoomType() {
        Scanner sc = new Scanner(System.in);
        RoomType newRoomType = new RoomType();
        List<String> amenities = new ArrayList<>();

        System.out.println("*** Hors Management System:: Hotel Operation:: Operation Manager:: Create New Room Type ***");
        System.out.print("Enter Name>");
        newRoomType.setName(sc.nextLine().trim());

        System.out.print("Description>");
        newRoomType.setDescription(sc.nextLine().trim());

        System.out.print("Enter Size>");
        newRoomType.setSize(sc.nextBigDecimal());

        System.out.print("Enter number of beds>");
        newRoomType.setNumOfBeds(sc.nextInt());

        System.out.print("Enter capacity>");
        newRoomType.setCapacity(sc.nextInt());
        sc.nextLine();

        System.out.print("Enter amenities(with space between 2 amenities)>");
        while (sc.hasNext()) {
            amenities.add(sc.next());
        }
        System.out.println("1. Enable it");
        System.out.println("2. Do not Enable it");
        int response;
        boolean enable = false;
	response = sc.nextInt();
        if (response == 1) {
            enable = true;
        } else if (response == 2) {
            enable = false;
        }
        newRoomType.setEnabled(enable);

        Set<ConstraintViolation<RoomType>> constraintViolations = validator.validate(newRoomType);

        if (constraintViolations.isEmpty()) {
            
                newRoomType = roomTypeControllerRemote.createNewRoomType(newRoomType);

                System.out.println("New room type created successfully!: " + newRoomType.getRoomTypeId() + "\n");
            
        } else {
            showInputDataValidationErrorsForRoomType(constraintViolations);
        }
    }

    //8. View Room Type Details 
    private void doViewRoomTypeDetails() {
        Scanner sc = new Scanner(System.in);
        int response;

        //retrieve room type by name 
        System.out.println("*** Hors Management System:: Hotel Operation:: Operation Manager:: View Room Type Details ***\n");
        System.out.print("Enter room type name> ");
        String roomTypeName = sc.nextLine().trim();

        try {
            RoomType currentRoomType;
            currentRoomType = roomTypeControllerRemote.retrieveRoomTypeByName(roomTypeName);

            System.out.println("Room Type Id: " + currentRoomType.getRoomTypeId());
            System.out.println("Room Name: " + currentRoomType.getName());
            System.out.println("Room Description: " + currentRoomType.getDescription());
            System.out.println("Room Size: " + currentRoomType.getSize());
            System.out.println("Room Number of Beds: " + currentRoomType.getNumOfBeds());
            System.out.println("Room Capacity: " + currentRoomType.getCapacity());

            System.out.println("Room amenities: ");
            List<String> list = currentRoomType.getAmenities();
            int num;
            for(String item: list){
                System.out.print(item);
            }

            System.out.println("Room Type Enabled: ");
            if (currentRoomType.getEnabled()) {
                System.out.print("Yes");
            } else {
                System.out.print("No");
            }

            System.out.println("--------------------------------");
            System.out.println("1: Update Room Type");
            System.out.println("2: Delete Room Type");
            System.out.println("3: Back\n");
            System.out.print("> ");

            response = sc.nextInt();

            if (response == 1) {
                doUpdateRoomType(currentRoomType);
            } else if (response == 2) {
                doDeleteRoomType(currentRoomType);
            }
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while retrieving product: " + ex.getMessage() + "\n");
        }

    }
    //9. Update Room Type 

    private void doUpdateRoomType(RoomType roomType) {
        Scanner sc = new Scanner(System.in);
        String input;

        System.out.println("***Hors Management System:: Hotel Operation:: Operation Manager:: View Room Type Details:: Update Room Type Details *** \n");
        System.out.print("Enter Room Type Name (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            roomType.setName(input);
        }

        System.out.print("Enter Description (blank if no change)> ");
        input = sc.nextLine().trim();
        if (input.length() > 0) {
            roomType.setDescription(input);
        }

        System.out.print("Enter Size Of Room Type (blank if no change)> ");
        BigDecimal size;
        if (sc.hasNextBigDecimal()) {
            size = sc.nextBigDecimal();
            roomType.setSize(size);
        }

        System.out.print("Enter Number Of Beds (blank if no change)> ");
        int num;
        if (sc.hasNextInt()) {
            num = sc.nextInt();
            roomType.setNumOfBeds(num);
        }

        System.out.print("Enter Capacity (blank if no change)> ");
        int capacity;
        if (sc.hasNextInt()) {
            capacity = sc.nextInt();
            roomType.setCapacity(capacity);
        }

        System.out.print("Enter Amenities (blank if no change)> ");
        List<String> list = null;
        while (sc.hasNext()) {
            list.add(sc.next());
            roomType.setAmenities(list);
        }

        System.out.print("Enable room (blank if no change, enter '1' to change)>");
        int response;
        while (sc.hasNextInt()) {
            response = sc.nextInt();
            if (response == 1) {
                if (roomType.getEnabled()) {
                    roomType.setEnabled(false);
                } else {
                    roomType.setEnabled(true);
                }

            }
        }

        Set<ConstraintViolation<RoomType>> constraintViolations;
        constraintViolations = validator.validate(roomType);

        if (constraintViolations.isEmpty()) {
            try {
                roomTypeControllerRemote.updateRoomType(roomType);

                System.out.println("Room type updated successfully!" + "\n");
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("An error has occurred while updating the room type: " + ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForRoomType(constraintViolations);
        }
    }
    
    //10. Delete Room Type 
    private void doDeleteRoomType(RoomType roomType) {
        Scanner sc = new Scanner(System.in);
        int response;
        System.out.println("***Hors Management System:: Hotel Operation:: Operation Manager:: View Room Type Details:: Delete Room Type *** \n");
        System.out.println("Confirm Delete Room Type (Enter 0 to Delete):");

        response = sc.nextInt();

        if (response == 0) {
            try {
                roomTypeControllerRemote.deleteRoomTypeByName(roomType.getName());
                System.out.println("Room Type Delete Successfully");
            } catch (DeleteRoomTypeException ex) {
                System.out.println("An error has occurred while deleting staff: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.println("Room Type Not Deleted\n");
        }
    }

    //11. View All Room Types 
    private void doViewAllRoomTypes() {
        Scanner sc = new Scanner(System.in);
        System.out.println("***Hors Management System:: Hotel Operation:: Operation Manager:: View All Room Types *** \n");
        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();

        roomTypes.stream().map((roomtype) -> {
            System.out.print("Room Type ID >" + roomtype.getRoomTypeId());
            return roomtype;
        }).map((roomtype) -> {
            System.out.print("Room Type name >" + roomtype.getName());
            return roomtype;
        }).map((roomtype) -> {
            System.out.print("Room Type Description >" + roomtype.getDescription());
            return roomtype;
        }).map((roomtype) -> {
            System.out.print("Room Type Size >" + roomtype.getSize());
            return roomtype;
        }).map((roomtype) -> {
            System.out.print("Room Type Number Of Beds >" + roomtype.getNumOfBeds());
            return roomtype;
        }).map((roomtype) -> {
            System.out.print("Room Type Capacity >" + roomtype.getCapacity());
            return roomtype;
        }).map((roomtype) -> {
            System.out.print("Room Type Amenities >");
            return roomtype;
        }).map(new Function<RoomType, RoomType>() {
            @Override
            public RoomType apply(RoomType roomtype) {
                List<String> list = roomtype.getAmenities();
                list.forEach((s) -> {
                    System.out.print(s + " ");
                });
                System.out.print("Room Type Enabled >");
                return roomtype;
            }
        }).map((roomtype) -> {
            if (roomtype.getEnabled()) {
                System.out.print("Yes");
            } else {
                System.out.print("No");
            }
            return roomtype;
        }).forEachOrdered((_item) -> {
            System.out.print("\n");
        });

        System.out.print("Press any key to continue... >");
        sc.nextLine();
    }

    //12. Create new room
    private void doCreateNewRoom() {
        Scanner sc = new Scanner(System.in);
        Room room = new Room();
        RoomStatusEnum roomStatus;
        System.out.println("***Hors Management System:: Hotel Operation:: Operation Manager:: Create New Room *** \n");
        System.out.print("Enter Room Number> ");
        int roomNumber = sc.nextInt();
        
        System.out.print("Enter Room Type> ");
        String roomTypeName = sc.nextLine().trim();
        
        
        Set<ConstraintViolation<Room>> constraintViolations = validator.validate(room);

        if (constraintViolations.isEmpty()) {
            try {
                room = roomControllerRemote.createNewRoom(roomNumber,roomTypeName);

                System.out.println("New room created successfully!: " + room.getRoomId() + "\n");
            } catch (CreateNewRoomException ex) {
                System.out.println("An error has occurred while creating the new room: " + ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForRoom(constraintViolations);
        }
    }

    //13. Update room //can only update room type
    private void doUpdateRoom() {
        Scanner sc = new Scanner(System.in);
        String input;

        System.out.println("***Hors Management System:: Hotel Operation:: Operation Manager:: Update Room *** \n");
        System.out.print("Enter Room Number> ");
        int roomNumber = sc.nextInt();
        Room room = null;
        try {
            room = roomControllerRemote.retrieveRoomByRoomNumber(roomNumber);
        } catch (RoomNotFoundException ex) {
            System.out.println("Can not find a room by the given room number");
        }
        System.out.print("Enter Room Type (blank if no change)> ");
        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomTypes();
        for (RoomType roomType : roomTypes) {
            System.out.println("Room Type Id: " + roomType.getRoomTypeId());
            System.out.println("Room Name: " + roomType.getName());
            System.out.println("Room Description: " + roomType.getDescription());
            System.out.println("Room Size: " + roomType.getSize());
            System.out.println("Room Number of Beds: " + roomType.getNumOfBeds());
            System.out.println("Room Capacity: " + roomType.getCapacity());

            System.out.println("Room amenities: ");
            List<String> list = roomType.getAmenities();
            int num;
            for (String item : list) {
                System.out.print(item);
            }

            System.out.println("Room Type Enabled: ");
            if (roomType.getEnabled()) {
                System.out.print("Yes");
            } else {
                System.out.print("No");
            }

            System.out.println("Do you want to change the room type into this one? Yes: Enter 1 No: Enter2 ");
            int check = sc.nextInt();
            if (check == 1) {
                room.setRoomType(roomType);
            }
        }

        Set<ConstraintViolation<Room>> constraintViolations;
        constraintViolations = validator.validate(room);

        if (constraintViolations.isEmpty()) {
            try{
                roomControllerRemote.updateRoom(room, false);
                System.out.println("Room updated successfully!" + "\n");
            }catch(UpdateRoomException ex){
                System.out.println("An error has occurred while updating a room: " + ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForRoom(constraintViolations);
        }
    }
    


    //14. Delete room
    private void doDeleteRoom() {
        Scanner sc = new Scanner(System.in);
        String input;
        int roomNumber;
        System.out.println("***Hors Management System:: Hotel Operation:: Operation Manager:: Delete Room *** \n");
        System.out.print("Enter Room Number > ");
        roomNumber = sc.nextInt();
        System.out.print("Confirm Delete Room " + roomNumber + "> Enter 1 to confirm > ");
        int response = 0;
        if (response == 1) {
            try {
                roomControllerRemote.deleteRoom(roomNumber);
                System.out.println("Room deleted successfully \n");
            } catch (DeleteRoomException ex) {
                System.out.println("An error has occurred while deleting room: " + ex.getMessage() + "\n");
            }
        }
    }

    //15. View all rooms //can only see the room type and room number at this time
    private void doViewAllRooms() {
        Scanner sc = new Scanner(System.in);

        System.out.println("***Hors Management System:: Hotel Operation:: Operation Manager:: View All Rooms *** \n");
        List<Room> rooms = roomControllerRemote.retrieveAllRooms();
        System.out.printf("%20s%20s\n", "Room Number", "Room Type");

        for (Room room : rooms) {
            System.out.printf("%20d%20s\n", room.getRoomNumber(), room.getRoomType());
        }

        System.out.print("Press any key to continue...>");
        sc.nextLine();
    }

    //16. View room allocation exception report
    private void doViewRoomAllocationExceptionReport() {
        List<RoomAllocationExceptionReport> reports = reservationControllerRemote.retrieveRoomAllocationExceptionReports();
        
        for(RoomAllocationExceptionReport report: reports){
            System.out.print("Year> "+report.getRoomNight().getDate().get(Calendar.YEAR)+"> ");
            System.out.print("Month> " + report.getRoomNight().getDate().get(Calendar.MONTH)+"> ");
            System.out.print("Day> " + report.getRoomNight().getDate().get(Calendar.DAY_OF_MONTH)+"> ");
            System.out.println("");
            System.out.println("Exception Type: "+report.getExceptionType().toString());
            System.out.println("Reservation Id: "+report.getReservationLineItem().getReservation().getReservationId());
            System.out.print("\n");
        }
    }

    public void menuSalesManager() throws ParseException {
        Scanner sc = new Scanner(System.in);
        int response;

        while (true) {
            System.out.println("*** Hors Management System:: Hotel Operation:: Sales Manager ***");
            System.out.println("1. Create New Room Rate");
            System.out.println("2. View Room Rate Details");
            System.out.println("3. View All Room Rates");
            System.out.println("4. Back\n");
            response = 0;

            OUTER:
            while (response < 1 || response > 7) {
                System.out.println("> ");
                response = sc.nextInt();
                switch (response) {
                    case 1:
                        doCreateNewRoomRate();
                        break;
                    case 2:
                        doViewRoomRateDetails();
                        break;
                    case 3:
                        doViewAllRoomRates();
                        break;
                    case 4:
                        break OUTER;
                    default:
                        System.out.println("Invalid option, please try again");
                        break;
                }
            }

            if (response == 4) {
                break;
            }
        }
    }

    //17. Create new room rate
    private void doCreateNewRoomRate() throws ParseException {
        Scanner sc = new Scanner(System.in);
        RoomRate roomRate;
        int response;

        System.out.println("*** Hors Management System:: Hotel Operation:: Sales Manager:: Create New Room Rate***");

        System.out.print("Enter Room Rate Type>1. Published Room Rate>2. Normal Room Rate> 3. Peak Room Rate> 4. Promotion Room Rate> ");
        response = sc.nextInt();
        switch (response) {
            case 1:
                {
                    PublishedRoomRate publishedRoomRate = new PublishedRoomRate();
                    System.out.print("Enter Room Rate Room Type Name>");
                    String name = sc.nextLine().trim();
                    try{
                        publishedRoomRate.setRoomType(roomTypeControllerRemote.retrieveRoomTypeByName(name));
                    }catch(RoomTypeNotFoundException ex){
                        System.out.print("Room type does not exist");
                    }
                    
                    System.out.print("Enter Room Rate Per Night>");
                    publishedRoomRate.setRatePerNight(sc.nextBigDecimal());
                    System.out.print("Do you want to enable this room rate? > 1. Yes > 2.No");
                    int m = sc.nextInt();
                    if (m == 1) {
                        publishedRoomRate.setEnabled(true);
                    } else {
                        publishedRoomRate.setEnabled(false);
                    }       
                    
                    Set<ConstraintViolation<RoomRate>> constraintViolations = validator.validate(publishedRoomRate);
                    if (constraintViolations.isEmpty()) {
                        
                            publishedRoomRate = (PublishedRoomRate) roomRateControllerRemote.createNewRoomRate(publishedRoomRate);
                            
                            System.out.println("New published room rate created successfully!: " + publishedRoomRate.getRateId() + "\n");
                        
                    } else {
                        showInputDataValidationErrorsForRoomRate(constraintViolations);
                    }       break;
                }
            case 2:
                {
                    NormalRoomRate normalRoomRate = new NormalRoomRate();
                    System.out.print("Enter Room Rate Room Type Name>");
                    String name = sc.nextLine().trim();
                    try{
                        normalRoomRate.setRoomType(roomTypeControllerRemote.retrieveRoomTypeByName(name));
                    }catch(RoomTypeNotFoundException ex){
                        System.out.print("Room type does not exist");
                    }
                    
                    System.out.print("Enter Room Rate Per Night>");
                    normalRoomRate.setRatePerNight(sc.nextBigDecimal());
                    System.out.print("Do you want to enable this room rate? > 1. Yes > 2.No");
                    int m = sc.nextInt();
                    if (m == 1) {
                        normalRoomRate.setEnabled(true);
                    } else {
                        normalRoomRate.setEnabled(false);
                    }       
                    Set<ConstraintViolation<RoomRate>> constraintViolations = validator.validate(normalRoomRate);
                    if (constraintViolations.isEmpty()) {
                        
                            normalRoomRate = (NormalRoomRate) roomRateControllerRemote.createNewRoomRate(normalRoomRate);
                            
                            System.out.println("New normal room rate created successfully!: " + normalRoomRate.getRateId() + "\n");
                        
                    } else {
                        showInputDataValidationErrorsForRoomRate(constraintViolations);
                    }       break;
                }
            case 3:
                {
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
                    List<GregorianCalendar> validateDate = null;
                    
                    int check;
                    RoomRate peakRoomRate = new PeakRoomRate();
                    System.out.print("Enter Room Rate Room Type>");
                    String name = sc.nextLine().trim();
                    try{
                        peakRoomRate.setRoomType(roomTypeControllerRemote.retrieveRoomTypeByName(name));
                    }catch(RoomTypeNotFoundException ex){
                        System.out.print("Room type does not exist");
                    }
                                 
                    System.out.print("Enter Room Rate Per Night>");
                    peakRoomRate.setRatePerNight(sc.nextBigDecimal());
                    System.out.print("Do you want to enable this room rate? > 1. Yes > 2.No");
                    int m = sc.nextInt();
                    if (m == 1) {
                        peakRoomRate.setEnabled(true);
                    } else {
                        peakRoomRate.setEnabled(false);
                    }       
                    while (true) {
                        System.out.print("Enter Validity Date (dd/mm/yyyy)> ");
                        Date date = inputDateFormat.parse(sc.nextLine().trim());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        validateDate.add((GregorianCalendar)cal);
                        
                        System.out.print(" 1. if still need to input validity Date> 2. finish input> ");
                        check = sc.nextInt();
                        if (check == 2) {
                            break;
                        }
                    }       
                    Set<ConstraintViolation<RoomRate>> constraintViolations = validator.validate(peakRoomRate);
                    if (constraintViolations.isEmpty()) {
                        
                            peakRoomRate = (PeakRoomRate)roomRateControllerRemote.createNewRoomRate(peakRoomRate);
                            
                            System.out.println("New peak room rate created successfully!: " + peakRoomRate.getRateId() + "\n");
                       
                    } else {
                        showInputDataValidationErrorsForRoomRate(constraintViolations);
                    }       break;
                }
            case 4:
                {
                    PromotionRoomRate promotionRoomRate = new PromotionRoomRate();
                    
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
                    GregorianCalendar validityPeriodStart = new GregorianCalendar();
                    GregorianCalendar validityPeriodEnd = new GregorianCalendar();
                    System.out.print("Enter Room Rate Room Type>");
                    String name = sc.nextLine().trim();
                    try{
                        promotionRoomRate.setRoomType(roomTypeControllerRemote.retrieveRoomTypeByName(name));
                    }catch(RoomTypeNotFoundException ex){
                        System.out.print("Room Type does not exist");
                    }
                    
                    System.out.print("Enter Room Rate Per Night>");
                    promotionRoomRate.setRatePerNight(sc.nextBigDecimal());
                    System.out.print("Do you want to enable this room rate? > 1. Yes > 2.No");
                    int m = sc.nextInt();
                    if (m == 1) {
                        promotionRoomRate.setEnabled(true);
                    } else {
                        promotionRoomRate.setEnabled(false);
                    }       
                    
                    System.out.print("Enter Validity Period Start Date (dd/mm/yyyy)>");
                    Date date1 = inputDateFormat.parse(sc.nextLine().trim());
                    validityPeriodStart.setTime(date1);
                    promotionRoomRate.setValidityPeriodStart(validityPeriodStart);
                    System.out.print("Enter Validity Period End Date (dd/mm/yyyy)>");
                    
                    Date date2 = inputDateFormat.parse(sc.nextLine().trim());
                    validityPeriodEnd.setTime(date2);
                    promotionRoomRate.setValidityPeriodEnd(validityPeriodEnd);
                    Set<ConstraintViolation<RoomRate>> constraintViolations = validator.validate(promotionRoomRate);
                    if (constraintViolations.isEmpty()) {
                        
                            promotionRoomRate = (PromotionRoomRate)roomRateControllerRemote.createNewRoomRate(promotionRoomRate);
                            
                            System.out.println("New promotion room rate created successfully!: " + promotionRoomRate.getRateId() + "\n");
                        
                    } else {
                        showInputDataValidationErrorsForRoomRate(constraintViolations);
                    }       break;
                }
            default:
                System.out.println("Invalid input, please enter again");
                break;
        }
    }

    //18. View Room rate details 
    private void doViewRoomRateDetails() throws ParseException {
        Scanner sc = new Scanner(System.in);
        int response;
        System.out.println("*** Hors Management System:: Hotel Operation:: Sales Manager:: View Room Rate Details***");
        System.out.print("Enter Room Rate ID> ");
        Long id = sc.nextLong();

        try {
            RoomRate roomRate = roomRateControllerRemote.retrieveRoomRateById(id);
            if (roomRate instanceof PublishedRoomRate) {                        
                System.out.println("Room Rate ID: "+roomRate.getRateId());
                System.out.println("Room Rate Per Night: "+roomRate.getRatePerNight());
                System.out.println("Room Type: "+roomRate.getRoomType().getName());
                System.out.println("Room Enabled:");
                if(roomRate.getEnabled()){
                    System.out.print("Yes");
                }else{
                    System.out.print("No");
                }
                
                System.out.println("1. Update Room Rate");
                System.out.println("2. Delete Room Rate");
                System.out.println("3. Back\n");
                System.out.print("> ");
                response = sc.nextInt();
                if (response == 1){
                    doUpdateRoomRate(roomRate);
                } else if (response == 2) {
                    doDeleteRoomRate(roomRate);
                } else{
                    System.out.println("Invalid option, please try again");
                }
            }

            if (roomRate instanceof NormalRoomRate) {
                System.out.println("Room Rate ID: "+roomRate.getRateId());
                System.out.println("Room Rate Per Night: "+roomRate.getRatePerNight());
                System.out.println("Room Type: "+roomRate.getRoomType().getName());
                System.out.println("Room Enabled:");
                if(roomRate.getEnabled()){
                    System.out.print("Yes");
                }else{
                    System.out.print("No");
                }
               
                System.out.println("1. Update Room Rate");
                System.out.println("2. Delete Room Rate");
                System.out.println("3. Back\n");
                System.out.print("> ");
                response = sc.nextInt();
                if (response == 1) {
                    doUpdateRoomRate(roomRate);
                } else if (response == 2) {
                    doDeleteRoomRate(roomRate);
                } else {
                    System.out.println("Invalid option, please try again");
                }
            }

            if (roomRate instanceof PeakRoomRate) {
                DateFormat dateFormat = new SimpleDateFormat("y/M/d");
                
                System.out.print("Room Rate ID> ");
                System.out.print(roomRate.getRateId());
                System.out.print("\n");
                System.out.print("Room Rate Per Night> ");
                System.out.print(roomRate.getRatePerNight());
                System.out.print("\n");
                System.out.print("Room Rate Room Type> ");
                System.out.print(roomRate.getRoomType().getName());
                System.out.print("\n");
                System.out.print("Enabled> ");
                if(roomRate.getEnabled()){
                    System.out.print("Yes");
                }else{
                    System.out.print("No");
                }
                
                System.out.print("\n");
                System.out.print("Validitity Dates> ");
                List<GregorianCalendar> list = ((PeakRoomRate)roomRate).getValidityDates();
                for (GregorianCalendar date : list) {
                    System.out.print(date.get(Calendar.DATE));
                }

                System.out.println("--------------");
                System.out.println("1. Update Room Rate");
                System.out.println("2. Delete Room Rate");
                System.out.println("3. Back\n");
                System.out.print("> ");
                response = sc.nextInt();
                if (response == 1) {
                    doUpdateRoomRate(roomRate);
                } else if (response == 2) {
                    doDeleteRoomRate(roomRate);
                } else {
                    System.out.print("Invalid option,please try again");
                }

            }

            if (roomRate instanceof PromotionRoomRate) {
                DateFormat dateFormat = new SimpleDateFormat("y/M/d");
                System.out.print("Room Rate ID> ");
                System.out.print(roomRate.getRateId());
                System.out.print("\n");
                System.out.print("Room Rate Per Night> ");
                System.out.print(roomRate.getRatePerNight());
                System.out.print("\n");
                System.out.print("Room Rate Room Type> ");
                System.out.print(roomRate.getRoomType().getName());
                System.out.print("\n");
                System.out.print("Enabled> ");
                if(roomRate.getEnabled()){
                    System.out.print("Yes");
                }else{
                    System.out.print("No");
                }
                System.out.print("\n");

                System.out.print("Validitity Period Start Date> ");
                GregorianCalendar date1 = ((PromotionRoomRate)roomRate).getValidityPeriodStart();
                System.out.print(date1.get(Calendar.DATE));
                System.out.print("\n");

                System.out.print("Validitity Period End Date> ");
                GregorianCalendar date2 = ((PromotionRoomRate)roomRate).getValidityPeriodEnd();
                System.out.print(date2.get(Calendar.DATE));
                System.out.print("\n");

                System.out.println("1. Update Room Rate");
                System.out.println("2. Delete Room Rate");
                System.out.println("3. Back\n");
                System.out.print("> ");
                response = sc.nextInt();
                switch (response) {
                    case 1:
                        doUpdateRoomRate(roomRate);
                        break;
                    case 2:
                        doDeleteRoomRate(roomRate);
                        break;
                    default:
                        System.out.println("Invalid option, please try again");
                        break;
                }

            }
        } catch (RoomRateNotFoundException ex) {
            System.out.println("An error has occured while retrieving room rate: " + ex.getMessage() + "\n");
        }
    }

    //19. Update Room Rate 
    private void doUpdateRoomRate(RoomRate roomRate) throws ParseException {
        Scanner sc = new Scanner(System.in);
        String input;
        int response;
        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        System.out.println("*** Hors Management System:: Hotel Operation:: Sales Manager:: View Room Rate Details:: Update Room Rate***");
        System.out.print("Enter Room Rate Room Type Name (blank if no change)> ");
        input = sc.nextLine().trim();
        RoomType type = null;
        try{
            type = roomTypeControllerRemote.retrieveRoomTypeByName(input);
        }catch(RoomTypeNotFoundException ex){
            System.out.print("Room Type does not exist");
        }
        if (input.length() > 0) {
            roomRate.setRoomType(type);
        }
        System.out.print("Enter Room Rate per night (blank if no change)> ");
        if (sc.hasNextBigDecimal()) {
            roomRate.setRatePerNight(sc.nextBigDecimal());
        }
        System.out.print("Enter Room Enabled (blank if no change)>1. Yes> 2. No> ");
        if (sc.hasNextInt()) {
            response = sc.nextInt();
            if (response == 1) {
                roomRate.setEnabled(true);
            } else if (response == 2) {
                roomRate.setEnabled(false);
            } else {
                System.out.print("Invalid input, please try again");
            }
        }
        if (roomRate instanceof PeakRoomRate) {
            int check = 0;
            List<GregorianCalendar> validateDate = null;
            
            System.out.print("Change Validity Date (blank if no change)> 1. Change");
            check = sc.nextInt();
            if (check == 1) {
                while (true) {
                    System.out.print("Enter Validity Date (dd/mm/yyyy) (blank if no change)> ");
                    Date date = dateFormat.parse(sc.nextLine().trim());
                    Calendar cal = new GregorianCalendar();
                    cal = Calendar.getInstance();
                    cal.setTime(date);
                    validateDate.add((GregorianCalendar)cal);

                    System.out.print(" 1. if still need to input validity Date> 2. finish input> ");
                    check = sc.nextInt();
                    if (check == 2) {
                        break;
                    }
                    ((PeakRoomRate)roomRate).setValidityDates(validateDate);
                }
            }
        }
            if (roomRate instanceof PromotionRoomRate) {
                
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
                GregorianCalendar validityPeriodStart = new GregorianCalendar();
                GregorianCalendar validityPeriodEnd = new GregorianCalendar();
                
                Date startDate;
                Date endDate;
                
                
                System.out.print("Enter Validity Period Start Date (dd/mm/yyyy) (blank if no change)> ");
                if (sc.hasNext()) {
                    
                    Date date1 = inputDateFormat.parse(sc.nextLine().trim());
                    validityPeriodStart.setTime(date1);
                    ((PromotionRoomRate)roomRate).setValidityPeriodStart(validityPeriodStart);
                    
                }

                System.out.print("Enter Validity Period End Date (dd/mm/yyyy) (blank if no change)>");
                
                if (sc.hasNext()) {
                    Date date2 = inputDateFormat.parse(sc.nextLine().trim());
                    validityPeriodEnd.setTime(date2);
                    ((PromotionRoomRate)roomRate).setValidityPeriodEnd(validityPeriodEnd);
                }

            }

            Set<ConstraintViolation<RoomRate>> constraintViolations = validator.validate(roomRate);

            if (constraintViolations.isEmpty()) {
                try {
                    roomRateControllerRemote.updateRoomRateDetails(roomRate);
                    System.out.println("Room Rate updated successfully!\n");
                }  catch (UpdateRoomRateException ex) {
                    System.out.println("An error has occurred while updating room rate: " + ex.getMessage() + "\n");
                }
            } else {
                showInputDataValidationErrorsForRoomRate(constraintViolations);
            }
        }
        //20. Delete Room Rate 
    private void doDeleteRoomRate(RoomRate roomRate) {
        Scanner sc = new Scanner(System.in);
        int input = 0;
        System.out.println("*** Hors Management System:: Hotel Operation:: Sales Manager:: View Room Rate Details:: Delete Room Rate***");
        System.out.print("Confirm Delete Room Rate(Enter 1 to confirm)> " + roomRate.getRateId());
        input = sc.nextInt();
        if (input == 1) {
            try {
                roomRateControllerRemote.deleteRoomRate(roomRate.getRateId());
                System.out.println("Room Rate delete successfully");
            } catch (DeleteRoomRateException ex) {
                System.out.println("An error has occured while deleting room rate: " + ex.getMessage() + "\n");
            }
        } else {
            System.out.print("Room Rate not deleted");
        }

    }

    //21. View All Room Rates
    private void doViewAllRoomRates() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Hors Management System:: Hotel Operation:: Sales Manager:: View All Room Rates***");
        List<RoomRate> roomRates = roomRateControllerRemote.retrieveAllRoomRates();

        for (RoomRate roomRate : roomRates) {
            if (roomRate instanceof PublishedRoomRate) {
                System.out.print("Room Rate ID> ");
                System.out.print(roomRate.getRateId());
                System.out.print("\n");
                System.out.print("Room Rate Per Night> ");
                System.out.print(roomRate.getRatePerNight());
                System.out.print("\n");
                System.out.print("Room Rate Room Type> ");
                System.out.print(roomRate.getRoomType().getName());
                System.out.print("\n");
                System.out.print("Enabled> ");
                if(roomRate.getEnabled()){
                    System.out.print("Yes");
                }else{
                    System.out.print("No");
                }
                
            }

            if (roomRate instanceof NormalRoomRate) {
                System.out.print("Room Rate ID> ");
                System.out.print(roomRate.getRateId());
                System.out.print("\n");
                System.out.print("Room Rate Per Night> ");
                System.out.print(roomRate.getRatePerNight());
                System.out.print("\n");
                System.out.print("Room Rate Room Type> ");
                System.out.print(roomRate.getRoomType().getName());
                System.out.print("\n");
                System.out.print("Enabled> ");
                if(roomRate.getEnabled()){
                    System.out.print("Yes");
                }else{
                    System.out.print("No");
                }
            }

            if (roomRate instanceof PeakRoomRate) {
                DateFormat dateFormat = new SimpleDateFormat("y/M/d");
                System.out.print("Room Rate ID> ");
                System.out.print(roomRate.getRateId());
                System.out.print("\n");
                System.out.print("Room Rate Per Night> ");
                System.out.print(roomRate.getRatePerNight());
                System.out.print("\n");
                System.out.print("Room Rate Room Type> ");
                System.out.print(roomRate.getRoomType().getName());
                System.out.print("\n");
                System.out.print("Enabled> ");
                if(roomRate.getEnabled()){
                    System.out.print("Yes");
                }else{
                    System.out.print("No");
                }
                
                System.out.print("Validitity Dates> ");
                List<GregorianCalendar> list = ((PeakRoomRate)roomRate).getValidityDates();
                for (GregorianCalendar date : list) {
                    System.out.print(date.get(Calendar.DATE));
                }

            }

            if (roomRate instanceof PromotionRoomRate) {
                DateFormat dateFormat = new SimpleDateFormat("y/M/d");
                System.out.print("Room Rate ID> ");
                System.out.print(roomRate.getRateId());
                System.out.print("\n");
                System.out.print("Room Rate Per Night> ");
                System.out.print(roomRate.getRatePerNight());
                System.out.print("\n");
                System.out.print("Room Rate Room Type> ");
                System.out.print(roomRate.getRoomType().getName());
                System.out.print("\n");
                System.out.print("Enabled> ");
                if(roomRate.getEnabled()){
                    System.out.print("Yes");
                }else{
                    System.out.print("No");
                }

                System.out.print("Validitity Period Start Date> ");
                GregorianCalendar date1 = ((PromotionRoomRate)roomRate).getValidityPeriodStart();
                System.out.print(date1.get(Calendar.DATE));
                System.out.print("\n");

                System.out.print("Validitity Period End Date> ");
                GregorianCalendar date2 = ((PromotionRoomRate)roomRate).getValidityPeriodEnd();
                System.out.print(date2.get(Calendar.DATE));
                System.out.print("\n");

            }
        }
    }
    
    private void showInputDataValidationErrorsForRoomType(Set<ConstraintViolation<RoomType>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

    private void showInputDataValidationErrorsForRoom(Set<ConstraintViolation<Room>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

    private void showInputDataValidationErrorsForRoomRate(Set<ConstraintViolation<RoomRate>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }        

}

