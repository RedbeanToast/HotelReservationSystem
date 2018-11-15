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
import entities.Employee;
import exceptions.EmployeeNotFoundException;
import java.util.Scanner;
import exceptions.InvalidLoginCredentialsException;

import java.text.ParseException;
import javax.ejb.EJB;
/**
 *
 * @author zhangruichun
 */

public class MainApp {
    
    private EmployeeControllerRemote employeeControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private WalkInReservationControllerRemote walkInReservationControllerRemote;
    private ReservationControllerRemote reservationControllerRemote;
    
    
    
    private FrontOfficeModule frontOfficeModule;
    
    private HotelOperationModule hotelOperationModule;
    
    private SystemAdministrationModule systemAdministrationModule;
    
    private Employee currentEmployee;
    
    public MainApp(){
        
    }

    public MainApp(EmployeeControllerRemote employeeControllerRemote, RoomControllerRemote roomControllerRemote, 
            RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote,
            WalkInReservationControllerRemote walkInReservationControllerRemote, ReservationControllerRemote reservationControllerRemote) {
        this.employeeControllerRemote = employeeControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.walkInReservationControllerRemote = walkInReservationControllerRemote;
        this.reservationControllerRemote = reservationControllerRemote;
        
        
    }
    
    public void runApp() {
        Scanner sc = new Scanner(System.in);
	int response;
        currentEmployee = new Employee();
	while(true){
            System.out.println("***Welcome to Hotel Reservation System***");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            OUTER:
            while (response <1 ||response >2) {
                System.out.print("> ");
                response = sc.nextInt();
                switch (response) {
                    case 1:
                        try{
                            doEmployeeLogin();
                            System.out.println("Login Successful");
                            frontOfficeModule = new FrontOfficeModule(walkInReservationControllerRemote, 
                            reservationControllerRemote, roomTypeControllerRemote, roomControllerRemote, currentEmployee);
                            systemAdministrationModule = new SystemAdministrationModule(employeeControllerRemote);
                            hotelOperationModule = new HotelOperationModule(roomTypeControllerRemote, roomControllerRemote, roomRateControllerRemote);
                            
                            menuMain();
                        }catch(InvalidLoginCredentialsException | EmployeeNotFoundException ex){
                            System.out.println(ex.getMessage());
                        }   
                        break;
                    case 2:
                        break OUTER;
                    default:
                        System.out.println("Invalid option, please try again");
                        break;
                }
            }
            if(response == 2){
                break;
            }
        }

    }

    //1. employee login
    private void doEmployeeLogin() throws InvalidLoginCredentialsException, EmployeeNotFoundException{
	Scanner sc = new Scanner(System.in);
	String username;
	String password;

	System.out.println("*** HorsManagementSystem::employee Login ***");
	System.out.print("Please enter your username: ");
	username = sc.nextLine().trim();

	System.out.print("Please enter your password: ");
	password = sc.nextLine().trim();

	
        try{
            currentEmployee = new Employee();
            if(username.length() > 0 && password.length() > 0){
		currentEmployee = employeeControllerRemote.login(username, password);
			
            }else{
		throw new InvalidLoginCredentialsException("Missing login credential!");
            }
        }catch(EmployeeNotFoundException ex){
            System.out.println("Employee does not found");
        }
	
    }

    
    //2. employee logout
    private void menuMain(){
	Scanner sc = new Scanner(System.in);
	int response = 0;

	System.out.println("***HorsManagement ***");
	System.out.println("You are login as::" + currentEmployee.getJobRole().toString() + " " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName());
	System.out.println("1. Continue ");
	System.out.println("2. Logout ");
	while(response < 1 || response > 2){
		System.out.print("> ");
		response = sc.nextInt();
		if(response == 1){
                    switch (currentEmployee.getJobRole().toString()) {
                        case "SYSTEMADMIN":
                            systemAdministrationModule.menuSystemAdministration(currentEmployee);
                            break;
                        case "OPERATIONMANAGER":
                            hotelOperationModule.menuOperationManager();
                            break;
                        case "SALESMANAGER":
                            hotelOperationModule.menuSalesManager();
                            break;
                        default:
                            frontOfficeModule.menuGuestRelationOfficer();
                            break;
                    }
        	}else{
                    break;
                }
	}
    }
}
