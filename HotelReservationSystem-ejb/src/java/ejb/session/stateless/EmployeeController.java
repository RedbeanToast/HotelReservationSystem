/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.Employee;
import exceptions.EmployeeNotFoundException;
import exceptions.InvalidLoginCredentialsException;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */
@Stateless
@Local(EmployeeControllerLocal.class)
@Remote(EmployeeControllerRemote.class)
public class EmployeeController implements EmployeeControllerRemote, EmployeeControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public EmployeeController() {
    }

    public Employee login(@NotNull String userName, @NotNull String password) throws EmployeeNotFoundException, InvalidLoginCredentialsException {
        Employee employee = retrieveEmployeeByUserName(userName);
        if(employee.getPassword().equals(password)){
            return employee;
        } else {
            throw new InvalidLoginCredentialsException("Wrong password!");
        }      
    }
   
    public Employee createNewEmployee(@NotNull Employee employee) {
        em.persist(employee);
        em.flush();
        
        return employee;
    }
    
    public Employee retrieveEmployeeByUserName(@NotNull String userName) throws EmployeeNotFoundException {
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.username = :inUserName");
        query.setParameter("inUserName", userName);
        try{
            Employee employee = (Employee)query.getSingleResult();
            return employee;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new EmployeeNotFoundException("Employee " + userName + " does not exist!");
        }
    }
    
    public List<Employee> retrieveAllEmployees() {
        Query query = em.createQuery("SELECT e FROM Employee e");
        List<Employee> employees = (List<Employee>)query.getResultList();
        return employees;
    }
}
