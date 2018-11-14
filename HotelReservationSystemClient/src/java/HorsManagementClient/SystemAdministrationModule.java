/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HorsManagementClient;

import ejb.session.stateless.EmployeeControllerRemote;
import entities.Employee;
import entities.Partner;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import enumerations.JobRoleEnum;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
/**
 *
 * @author zhangruichun
 */
public class SystemAdministrationModule {

    private EmployeeControllerRemote employeeControllerRemote;
    private PartnerControllerRemote partnerControllerRemote;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public SystemAdministrationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    

   
    

    public SystemAdministrationModule(EmployeeControllerRemote employeeControllerRemote, PartnerControllerRemote partnerControllerRemote,ValidatorFactory validatorFactory, Validator validator) {
        this();
        this.employeeControllerRemote = employeeControllerRemote;
        this.partnerControllerRemote = partnerControllerRemote;
    }

    /**
     *
     * 
     */
    public void menuSystemAdministration(){
        Scanner sc = new Scanner(System.in);
        int response;
        while (true) {
            System.out.println("***Hors Management System:: System Administration ***");
            System.out.println("1. Create New Employee");
            System.out.println("2. View All Employees");
            System.out.println("----------------------");
            System.out.println("3. Create New Partner");
            System.out.println("4. View All Partners");
            System.out.println("----------------------");
            System.out.println("5. Back\n");

            response = 0;

            OUTER:
            while (response < 1 || response > 5) {
                System.out.print("> ");
                response = sc.nextInt();
                switch (response) {
                    case 1:
                        doCreateEmployee();
                        break;
                    case 2:
                        doViewAllEmployees();
                        break;
                    case 3:
                        doCreateNewPartner();
                        break;
                    case 4:
                        doViewAllPartners();
                        break;
                    case 5:
                        break OUTER;
                    default:
                        System.out.println("Invalid option, please try again \n");
                        break;
                }
            }
            if (response == 5) {
                break;
            }
        }
    }

    //3. Create New Employee
    private void doCreateEmployee() {
        Scanner sc = new Scanner(System.in);
        Employee newEmployee = new Employee();
        int response;

        System.out.println("*** Hors Management System::System Administration::Create New Employee ***");
        System.out.print("Enter First name>");
        newEmployee.setFirstName(sc.nextLine().trim());
        System.out.print("Enter Last name>");
        newEmployee.setLastName(sc.nextLine().trim());

        while (true) {
            System.out.println("Select Job Role:");
            System.out.println("1. System Administrator");
            System.out.println("2. Operation Manager");
            System.out.println("3. Sales Manager");
            System.out.println("4. Guest Officer\n");

            response = sc.nextInt();

            //set job role
            if (response >= 1 && response <= 4) {
                newEmployee.setJobRole(JobRoleEnum.values()[response - 1]);
                break;
            } else {
                System.out.println("Invalid option, please try again \n");
            }
        }
        sc.nextLine();
        System.out.print("Enter Username");
        newEmployee.setUserName(sc.nextLine().trim());
        System.out.println("Enter Password");
        newEmployee.setPassword(sc.nextLine().trim());

        Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(newEmployee);

        if (constraintViolations.isEmpty()) {
            newEmployee = employeeControllerRemote.createNewEmployee(newEmployee);
            System.out.println("New Staff created successfully, employee ID is: "+newEmployee.getEmployeeId());
        }else{
                showInputDataValidationErrorsForEmployee(constraintViolations);
        }

    }
        //4. View All Employees
    private void doViewAllEmployees() {
        Scanner sc = new Scanner(System.in);

        System.out.println("***Hors Management System:: System Administration:: View All Staffs");

        List<Employee> employees = employeeControllerRemote.retrieveAllEmployees();

        employees.forEach((employee) -> {
            System.out.println("Employee ID: " + employee.getEmployeeId() + "First Name: " + employee.getFirstName() + "Last Name: " + employee.getLastName() + "Job Role: " + employee.getJobRole().toString() + "Username: " + employee.getUserName() + "Password: " + employee.getPassword());
        });

        System.out.println("Press any key to continue...>");
        sc.nextLine();
    }

    //5. Create New Partner
    private void doCreateNewPartner() {
        Scanner sc = new Scanner(System.in);
        System.out.println("***Hors Management System:: System Administration:: Create new partner");
        Partner partner = new Partner();
        System.out.print("Enter partner username:");
        partner.setName(sc.nextLine().trim());
        System.out.print("Enter partner password;");
        partner.setPassword(sc.nextLine().trim());

        Set<ConstraintViolation<Partner>> constraintViolations = validator.validate(partner);

        if (constraintViolations.isEmpty()) {
            partner = partnerControllerRemote.createNewPartner(partner);
            System.out.println("New partner created successfully");
        } else {
            showInputDataValidationErrorsForPartner(constraintViolations);
        }

    }
    //6. View All partners

    private void doViewAllPartners() {
        Scanner sc = new Scanner(System.in);

        System.out.println("***Hors Management System:: System Administration:: View All Partners");

        List<Partner> partners = partnerControllerRemote.retrieveAllPartners();

        partners.forEach((partner) -> {
            System.out.println("Partner ID:" + partner.getPartnerId() + "Partner Name: " + partner.getName() + "Password: " + partner.getPassword());
        });

        System.out.println("Press any key to continue...>");
        sc.nextLine();
    }
    
    private void showInputDataValidationErrorsForEmployee(Set<ConstraintViolation<Employee>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        constraintViolations.forEach((constraintViolation) -> {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        });

        System.out.println("\nPlease try again......\n");
    }    
    
    private void showInputDataValidationErrorsForPartner(Set<ConstraintViolation<Partner>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        constraintViolations.forEach((constraintViolation) -> {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        });

        System.out.println("\nPlease try again......\n");
    }    
}


