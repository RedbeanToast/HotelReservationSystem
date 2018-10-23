/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import enumerations.RateTypeEnum;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rateId;
    @NotNull
    @Enumerated(EnumType.STRING)
    private RateTypeEnum rateType;
    @NotNull
    @Digits(integer=4, fraction=2)
    private BigDecimal ratePerNight;
    @NotNull
    private Calendar validityPeriodStart;
    @NotNull
    @Future
    private Calendar validityPeriodEnd;
    @NotNull
    private Boolean enabled;
    
    @ManyToOne
    private RoomType roomType;

    public RoomRate() {
        
    }

    public RoomRate(RateTypeEnum rateType, BigDecimal ratePerNight, Calendar validityPeriodStart, Calendar validityPeriodEnd, Boolean enabled, RoomType roomType) {
        
        this();
        
        this.rateType = rateType;
        this.ratePerNight = ratePerNight;
        this.validityPeriodStart = validityPeriodStart;
        this.validityPeriodEnd = validityPeriodEnd;
        this.enabled = enabled;
        this.roomType = roomType;
    }
    
    public RateTypeEnum getRateType() {
        return rateType;
    }

    public void setRateType(RateTypeEnum rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public Calendar getValidityPeriodStart() {
        return validityPeriodStart;
    }

    public void setValidityPeriodStart(Calendar validityPeriodStart) {
        this.validityPeriodStart = validityPeriodStart;
    }

    public Calendar getValidityPeriodEnd() {
        return validityPeriodEnd;
    }

    public void setValidityPeriodEnd(Calendar validityPeriodEnd) {
        this.validityPeriodEnd = validityPeriodEnd;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Long getRateId() {
        return rateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rateId != null ? rateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the rateId fields are not set
        if (!(object instanceof RoomRate)) {
            return false;
        }
        RoomRate other = (RoomRate) object;
        if ((this.rateId == null && other.rateId != null) || (this.rateId != null && !this.rateId.equals(other.rateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.RoomRate[ id=" + rateId + " ]";
    }
    
}
