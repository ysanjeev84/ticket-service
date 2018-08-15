package com.walmart.exercise.ticketservice.domain;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Seat Tracker class manages the seats and their status.
 */
public final class SeatTracker {

    private final Map<String,Seat> seats;
    private final int rows;
    private final int columns;

    /**
     * Creates a seat tracker and initialize the seats
     * @param rows
     * @param columns
     */
    public SeatTracker(int rows,int columns){
        this.rows = rows;
        this.columns = columns;
        // Future state: the seats capacity can be increased dynamically as we add more seats.
        seats = new HashMap<>(rows*columns);
        init();
    }

    /**
     * Initialize the seats assuming(Important) every row has the same number of seats.
     */
    private void init(){
        for(int i=0;i<rows;i++){
            for(int j=0;j<columns;j++){
                Seat seat = new Seat(i,j);
                seats.put(seat.getId(),seat);
            }
        }
    }

    /**
     * Gets seats(including status and location).
     *
     * @return collection of seats
     */
    private synchronized Collection<Seat> getSeats() {
        return Collections.unmodifiableCollection(seats.values());
    }

    /**
     * Change the status of a given seat to some status and update counters.
     * @param seat  seat to change the status of the given seat
     * @param status seat status to change to
     */
    public synchronized void updateSeatStatus(Seat seat, SeatStatus status) {
        Seat seatTrackerSeat = seats.get(seat.getId());
        if(status.equals(seatTrackerSeat.getStatus()) || SeatStatus.RESERVED.equals(seatTrackerSeat.getStatus())
                || (SeatStatus.AVAILABLE.equals(seatTrackerSeat.getStatus()) && SeatStatus.RESERVED.equals(seat.getStatus()))){
            // Possible race condition here // throw error
            System.err.println("Possible Race Condition here");
            return;
        }
        seatTrackerSeat.setStatus(status);
    }

    /**
     * Get the list of seats with status as Available.
     * @return list of available seats.
     */
    public List<Seat> availableSeats(){
        return availableSeatStream()
                .collect(toList());
    }

    /**
     * Get the stream of available seats.
     *
     * @return stream of seats
     */
    public Stream<Seat> availableSeatStream(){
        return getSeats().parallelStream().filter(seat -> SeatStatus.AVAILABLE.equals(seat.getStatus()));
    }

    public int numAvailableSeat(){
        return (int)availableSeatStream().count();
    }
    public int numTotalSeats(){
        return rows*columns;
    }

}
