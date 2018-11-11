/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import enumerations.JobRoleEnum;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;
    @NotNull
    @Size(min=1, max=16)
    @Column(length=16)
    private String firstName;
    @NotNull
    @Size(min=1, max=16)
    @Column(length=16)
    private String lastName;
    @NotNull
    @Enumerated(EnumType.STRING)
    private JobRoleEnum jobRole;
    @NotNull
    @Size(min=6, max=20)
    @Column(unique=true, length=20)
    private String userName;
    @NotNull
    @Size(min=6, max=20)
    @Column(unique=true, length=20)
    private String password;
    
    @OneToMany(mappedBy="employee")
    private List<WalkInReservation> walkInReservations;

    public Employee() {
        
    }

    public Employee(String firstName, String lastName, JobRoleEnum jobRole, String userName, String password) {
        
        this();
        
        this.firstName = firstName;
        this.lastName = lastName;
        this.jobRole = jobRole;
        this.userName = userName;
        this.password = password;
        this.walkInReservations = new ArrayList<WalkInReservation>();
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public JobRoleEnum getJobRole() {
        return jobRole;
    }

    public void setJobRole(JobRoleEnum jobRole) {
        this.jobRole = jobRole;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<WalkInReservation> getWalkInReservations() {
        return walkInReservations;
    }

    public void setWalkInReservations(List<WalkInReservation> walkInReservations) {
        this.walkInReservations = walkInReservations;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (employeeId != null ? employeeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the employeeId fields are not set
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee other = (Employee) object;
        if ((this.employeeId == null && other.employeeId != null) || (this.employeeId != null && !this.employeeId.equals(other.employeeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Employee[ id=" + employeeId + " ]";
    }
    
}
