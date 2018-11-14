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
import entities.Employee;
import exceptions.EmployeeNotFoundException;
import java.util.Scanner;
import exceptions.InvalidLoginCredentialsException;

import java.text.ParseException;
/**
 *
 * @author zhangruichun
 */

public class MainApp {
    private EmployeeControllerRemote employeeControllerRemote;
    
    private RoomControllerRemote roomControllerRemote;
    
    private RoomRateControllerRemote roomRateControllerRemote;
    
    private RoomTypeControllerRemote roomTypeControllerRemote;
    
    
    
    private FrontOfficeModule frontOfficeModule;
    
    private HotelOperationModule hotelOperationModule;
    
    private SystemAdministrationModule systemAdministrationModule;
    
    private Employee currentEmployee;
    
    public MainApp(){
        
    }

    public MainApp(EmployeeControllerRemote employeeControllerRemote, RoomControllerRemote roomControllerRemote, RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote) {
        this.employeeControllerRemote = employeeControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        
    }
    
    public void runApp() throws EmployeeNotFoundException,ParseException{
        Scanner sc = new Scanner(System.in);
	int response;

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
                            doEnployeeLogin();
                            System.out.println("Login Successful");
                            
                            //need to add on
                            frontOfficeModule = new FrontOfficeModule();
                            systemAdministrationModule = new SystemAdministrationModule();
                            hotelOperationModule = new HotelOperationModule();
                            
                            menuMain();
                        }catch(InvalidLoginCredentialsException ex){
                            System.out.println("Invalid login credential: "+ ex.getMessage() + "\n");
                        }   break;
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
    private void doEnployeeLogin() throws InvalidLoginCredentialsException, EmployeeNotFoundException{
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
    private void menuMain() throws ParseException{
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
                            systemAdministrationModule.menuSystemAdministration();
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
