/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class PromotionRoomRate extends RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @NotNull
    private Date validityPeriodStart;
    @NotNull
    private Date validityPeriodEnd;

    public PromotionRoomRate() {
    }

    public PromotionRoomRate(Date validityPeriodStart, Date validityPeriodEnd, BigDecimal ratePerNight, Boolean enabled, RoomType roomType) {
        super(ratePerNight, enabled, roomType);
        this.validityPeriodStart = validityPeriodStart;
        this.validityPeriodEnd = validityPeriodEnd;
    }

    public Date getValidityPeriodStart() {
        return validityPeriodStart;
    }

    public void setValidityPeriodStart(Date validityPeriodStart) {
        this.validityPeriodStart = validityPeriodStart;
    }

    public Date getValidityPeriodEnd() {
        return validityPeriodEnd;
    }

    public void setValidityPeriodEnd(Date validityPeriodEnd) {
        this.validityPeriodEnd = validityPeriodEnd;
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
        if (!(object instanceof PromotionRoomRate)) {
            return false;
        }
        PromotionRoomRate other = (PromotionRoomRate) object;
        if ((this.rateId == null && other.rateId != null) || (this.rateId != null && !this.rateId.equals(other.rateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.PromotionRoomRate[ id=" + rateId + " ]";
    }
    
}
