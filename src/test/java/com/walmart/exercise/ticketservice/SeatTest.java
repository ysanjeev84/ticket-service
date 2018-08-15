package com.walmart.exercise.ticketservice.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SeatTest {

    @Test
    public void validateSeatStateAndId(){
        Seat seat = new Seat(10,5);
        assertEquals(10,seat.getRowNum());
        assertEquals(5,seat.getColumnNum());
        assertEquals("10-5",seat.getId());
        assertEquals(SeatStatus.AVAILABLE,seat.getStatus());
    }
}
