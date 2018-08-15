package com.walmart.exercise.ticketservice.adapter.service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * ReservationIdGenerator generates unique seat hold ID.
 */
public class ReservationIdGenerator implements UniqueIdGenerator{

    private AtomicInteger reservationId = new AtomicInteger(1); // Start the reservation ID with an initial number 1

    /**
     * The function generates reservation Id for ticketing service.
     * Program restart will not maintain the uniqueness of the reservation ID.
     * The number start with positive integer value of 1 and limit to a maximum value of 2,147,483,647 (inclusive).
     *
     * @return A unique number sequentially.
     */
    public int generate(){
        return reservationId.getAndIncrement();
    }
}
