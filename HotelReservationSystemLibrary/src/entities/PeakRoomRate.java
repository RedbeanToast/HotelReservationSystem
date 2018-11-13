/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class PeakRoomRate extends RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotNull
    @ElementCollection
    private List<GregorianCalendar> validityDates = new ArrayList<>();

    public PeakRoomRate() {
    }

    public PeakRoomRate(BigDecimal ratePerNight, Boolean enabled, RoomType roomType) {
        super(ratePerNight, enabled, roomType);
    }

    public List<GregorianCalendar> getValidityDates() {
        return validityDates;
    }

    public void setValidityDates(List<GregorianCalendar> validityDates) {
        this.validityDates = validityDates;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rateId != null ? rateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PeakRoomRate)) {
            return false;
        }
        PeakRoomRate other = (PeakRoomRate) object;
        if ((this.rateId == null && other.rateId != null) || (this.rateId != null && !this.rateId.equals(other.rateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.PeakRoomRate[ id=" + rateId + " ]";
    }
    
}
