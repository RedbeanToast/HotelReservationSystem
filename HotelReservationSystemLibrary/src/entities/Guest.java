/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.sun.istack.internal.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import static javax.swing.text.StyleConstants.Size;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class Guest implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestId;
    @NotNull
    @Size(min=1, max=64)
    @Column(unique = true)
    private String email;
    @NotNull
    @Size(min=1, max=16)
    private String firstName;
    @NotNull
    @Size(min=1, max=16)
    private String lastName;
    @NotNull
    @Size(min=6, max=20)
    private String phoneNumber;
    @NotNull
    @Size(min=1, max=20)
    @Column(unique = true)
    private String identificationNumber;
    @NotNull
    @Size(min=6, max=20)
    private String password;
    
    @OneToMany
    private List<OnlineHoRSReservation> onlineHoRSReservations = new ArrayList<OnlineHoRSReservation>();

    public Guest() {
        
    }

    public Guest(String email, String firstName, String lastName, String phoneNumber, String identificationNumber, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.identificationNumber = identificationNumber;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<OnlineHoRSReservation> getOnlineHoRSReservations() {
        return onlineHoRSReservations;
    }

    public void setOnlineHoRSReservations(List<OnlineHoRSReservation> onlineHoRSReservations) {
        this.onlineHoRSReservations = onlineHoRSReservations;
    }

    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (guestId != null ? guestId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the guestId fields are not set
        if (!(object instanceof Guest)) {
            return false;
        }
        Guest other = (Guest) object;
        if ((this.guestId == null && other.guestId != null) || (this.guestId != null && !this.guestId.equals(other.guestId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Guest[ id=" + guestId + " ]";
    }
    
}