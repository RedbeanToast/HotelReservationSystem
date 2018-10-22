/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entities.Employee;
import exceptions.EmployeeNotFoundException;
import exceptions.InvalidLoginCredentialsException;
import javax.ejb.Remote;

/**
 *
 * @author CaiYuqian
 */
public interface EmployeeControllerRemote {
    Employee login(String userName, String password) throws EmployeeNotFoundException, InvalidLoginCredentialsException;
}
