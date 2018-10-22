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
public class Partner implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId;
    @NotNull
    @Size(min=6, max=20)
    @Column(unique = true)
    private String name;
    @NotNull
    @Size(min=6, max=20)
    private String password;

    @OneToMany(mappedBy = "partner")
    private List<OnlinePartnerReservation> onlinePartnerReservations = new ArrayList<OnlinePartnerReservation>();
    
    public Partner() {
        
    }

    public Partner(String name, String password) {
        this();
        
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<OnlinePartnerReservation> getOnlinePartnerReservations() {
        return onlinePartnerReservations;
    }

    public void setOnlinePartnerReservations(List<OnlinePartnerReservation> onlinePartnerReservations) {
        this.onlinePartnerReservations = onlinePartnerReservations;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (partnerId != null ? partnerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the partnerId fields are not set
        if (!(object instanceof Partner)) {
            return false;
        }
        Partner other = (Partner) object;
        if ((this.partnerId == null && other.partnerId != null) || (this.partnerId != null && !this.partnerId.equals(other.partnerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Partner[ id=" + partnerId + " ]";
    }
    
}