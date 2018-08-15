package com.walmart.exercise.ticketservice.domain;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SeatHoldTest {

    @Test
    public void seatHoldTest(){
        List<Seat> seats = new ArrayList<>(Arrays.asList(new Seat(1,1),
                new Seat(1,2), new Seat(1,3)));
        SeatHold seatHold = new SeatHold(0, "abc@test.com", seats);
        assertEquals(0, seatHold.getSeatHoldId());
        assertEquals("abc@test.com", seatHold.getCustomerEmail());
        assertEquals(seatHold.getHoldSeats().size(),seats.size());
        assertTrue(seats.containsAll(seatHold.getHoldSeats()));
    }

}
