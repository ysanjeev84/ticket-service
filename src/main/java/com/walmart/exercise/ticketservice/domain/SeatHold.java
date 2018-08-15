package com.walmart.exercise.ticketservice.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * SeatHold is a list of seat(s), seat hold ID and the customer information for which a customer has a hold or reservation.
 */
public class SeatHold {

    private final int seatHoldId;
    private final List<Seat> holdSeats;
    private final String customerEmail;
    private final LocalDateTime createdDateTime;

    /**
     * Creates a SeatHold object. Set the seat hold create date time to current date time.
     *
     * @param id
     * @param customerEmail
     * @param seats
     */
    public SeatHold(int id, String customerEmail, List<Seat> seats){
        this.seatHoldId = id;
        this.customerEmail = customerEmail;
        this.holdSeats = seats;
        this.createdDateTime = LocalDateTime.now();
    }

    public int getSeatHoldId() {
        return seatHoldId;
    }

    public Collection<Seat> getHoldSeats() {
        return Collections.unmodifiableCollection(holdSeats);
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public LocalDateTime getCreatedDateTime(){
        return LocalDateTime.from(createdDateTime);
    }

}
