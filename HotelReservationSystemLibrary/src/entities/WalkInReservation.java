/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import entities.Employee;
import entities.Guest;
import entities.Reservation;
import entities.ReservationLineItem;
import javax.validation.constraints.NotNull;
import enumerations.ReservationStatusEnum;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author CaiYuqian
 */
@Entity
public class WalkInReservation extends Reservation {

    private static final long serialVersionUID = 1L;
    @NotNull
    @ManyToOne(optional=false)
    @JoinColumn(nullable=false)
    private Employee employee;
    
    public WalkInReservation() {
        
    }

    public WalkInReservation(Guest guest, BigDecimal amount, GregorianCalendar checkIn, GregorianCalendar checkOut, GregorianCalendar madeDate, ReservationStatusEnum status, List<ReservationLineItem> reservationLineItems, Employee employee) {
        super(guest, amount, checkIn, checkOut, madeDate, status, reservationLineItems);
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
 
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WalkInReservation)) {
            return false;
        }
        WalkInReservation other = (WalkInReservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.WalkInReservation[ id=" + reservationId + " ]";
    }
    
}
