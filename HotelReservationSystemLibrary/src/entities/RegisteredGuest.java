/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class RegisteredGuest extends Guest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(unique=true)
    private String email;
    @NotNull
    @Size(min=6, max=25)
    private String password;

    public RegisteredGuest() {
        
    }

    public RegisteredGuest(String email, String password, String firstName, String lastName, String phoneNumber, String identificationNumber) {
        super(firstName, lastName, phoneNumber, identificationNumber);
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (guestId != null ? guestId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RegisteredGuest)) {
            return false;
        }
        RegisteredGuest other = (RegisteredGuest) object;
        if ((this.guestId == null && other.guestId != null) || (this.guestId != null && !this.guestId.equals(other.guestId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.RegisteredGuest[ id=" + guestId + " ]";
    }
    
}
