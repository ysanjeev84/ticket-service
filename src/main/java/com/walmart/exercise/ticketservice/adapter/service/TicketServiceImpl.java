package com.walmart.exercise.ticketservice.adapter.service;

import com.walmart.exercise.ticketservice.domain.SeatHold;
import com.walmart.exercise.ticketservice.domain.SeatTracker;
import com.walmart.exercise.ticketservice.domain.TicketService;
import com.walmart.exercise.ticketservice.domain.TicketServiceException;

/**
 * TicketServiceImpl provides implementation for TicketService.
 */
public class TicketServiceImpl implements TicketService{

    private final SeatTracker seatTracker;
    private SeatAssignmentManager seatAssignmentManager;

    public TicketServiceImpl(SeatTracker seatTracker, int holdTime) throws TicketServiceException{
        if(seatTracker == null){
            throw new TicketServiceException("Venue can't be null");
        }
        this.seatTracker = seatTracker;
        seatAssignmentManager = new SeatAssignmentManager(seatTracker,holdTime);
    }

    @Override
    public int numSeatsAvailable()
    { // clear the expired seat holds if the seat availability is critical or should reflect the real time state.
        return seatTracker.numAvailableSeat();
    }

    @Override
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        if(numSeats > seatTracker.numTotalSeats() || numSeats <= 0 || customerEmail == null) {
            return null;
        }
        return seatAssignmentManager.findAndHold(numSeats,customerEmail);
    }

    @Override
    public String reserveSeats(int seatHoldId, String customerEmail){
        if(customerEmail == null){
            return null;
        }
        return seatAssignmentManager.reserveSeat(seatHoldId,customerEmail);
    }

}
