/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import enumerations.AllocationExceptionTypeEnum;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class RoomAllocationExceptionReport implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;
    @NotNull
    @Enumerated(EnumType.STRING)
    private AllocationExceptionTypeEnum exceptionType;
    
    @OneToOne(mappedBy="roomAllocationExceptionReport")
    @NotNull
    private RoomNight roomNight;

    public RoomAllocationExceptionReport() {
        
    }

    public RoomAllocationExceptionReport(AllocationExceptionTypeEnum exceptionType, RoomNight roomNight) {
        
        this();
        
        this.exceptionType = exceptionType;
        this.roomNight = roomNight;
    }

    public AllocationExceptionTypeEnum getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(AllocationExceptionTypeEnum exceptionType) {
        this.exceptionType = exceptionType;
    }

    public RoomNight getRoomNight() {
        return roomNight;
    }

    public void setRoomNight(RoomNight roomNight) {
        this.roomNight = roomNight;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reportId != null ? reportId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reportId fields are not set
        if (!(object instanceof RoomAllocationExceptionReport)) {
            return false;
        }
        RoomAllocationExceptionReport other = (RoomAllocationExceptionReport) object;
        if ((this.reportId == null && other.reportId != null) || (this.reportId != null && !this.reportId.equals(other.reportId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.RoomAllocationExceptionReport[ id=" + reportId + " ]";
    }
    
}
