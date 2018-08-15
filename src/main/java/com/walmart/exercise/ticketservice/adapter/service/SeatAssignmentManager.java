package com.walmart.exercise.ticketservice.adapter.service;

import com.walmart.exercise.ticketservice.domain.Seat;
import com.walmart.exercise.ticketservice.domain.SeatHold;
import com.walmart.exercise.ticketservice.domain.SeatStatus;
import com.walmart.exercise.ticketservice.domain.SeatTracker;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * SeatAssignmentManager manages seating arrangements for a venue.
 */
public class SeatAssignmentManager {

    private int holdTime;
    private SeatTracker seatTracker;

    private Map<Integer, SeatHold> seatHoldMap;

    private Map<String,SeatHold> reservedSeatMap;
    private ScheduledExecutorService expireService;
    private final Object expirationLock = new Object();

    private UniqueIdGenerator seatHoldIdGenerator;
    private UniqueIdGenerator reservationIdGenerator;

    /**
     * Identifies expired seat holds and clears the hold map and update the seat status to be available for hold again.
     * The method would run as executor service on it's own with fixed time delays(configurable).
     */
    private Runnable runnable = () -> {
        Map<Integer,SeatHold> expiredSeatsMap = seatHoldMap.entrySet()
                .stream()
                .filter(seatHoldEntry -> seatHoldEntry.getValue().getCreatedDateTime().isBefore(LocalDateTime.now().minusSeconds(holdTime)))
                .collect(Collectors.toMap(seatHoldEntry -> seatHoldEntry.getKey(),seatHoldEntry -> seatHoldEntry.getValue()));

        expiredSeatsMap.entrySet().stream().forEach(expiredSeatHold ->
            {
                SeatHold removedSeatHold;
                // Acquire expiration lock before expiring the entries from the seat hold map
                synchronized (expirationLock) {
                    removedSeatHold = seatHoldMap.remove(expiredSeatHold.getKey());
                }
                // execute it on condition if the key was removed as it may have been reserved by another thread.
                if(null != removedSeatHold) {
                    expiredSeatHold.getValue().getHoldSeats()
                            .forEach(seat -> seatTracker.updateSeatStatus(seat, SeatStatus.AVAILABLE));
                }
            });
    };

    /**
     * Seat assignment manager maintains the seat hold map and reserved seats map.
     * The executor service will spun a single threaded scheduler to clear the expired seats in seat hold map.
     * @param seatTracker
     * @param holdTime  time a seat hold will be alive for reservation.
     */
    public SeatAssignmentManager(SeatTracker seatTracker, int holdTime) {
        this.seatTracker = seatTracker;
        // Default the hold time to 120 seconds in case the hold time is not legitimate.
        this.holdTime = (holdTime >= 0) ? holdTime : 120;
        expireService = Executors
                .newSingleThreadScheduledExecutor();
        expireService.scheduleWithFixedDelay(runnable,10,10, TimeUnit.MILLISECONDS);
        seatHoldMap = new ConcurrentHashMap<>();
        reservedSeatMap = new ConcurrentHashMap<>();

        seatHoldIdGenerator = new SeatHoldIdGenerator();
        reservationIdGenerator = new ReservationIdGenerator();
    }

    /**
     * Reserves the seats for a customer and mark the seats for a seat hold as reserved.
     *
     * @param seatHoldId the seat hold identifier
     * @param customerEmail the email address of the customer to which the
    seat hold is assigned
     * @return a reservation confirmation code
     */

    String reserveSeat(int seatHoldId,String customerEmail){
        SeatHold seatHold;
        synchronized (expirationLock) {
             seatHold = seatHoldMap.get(seatHoldId);
             if(seatHold == null || !seatHold.getCustomerEmail().equalsIgnoreCase(customerEmail)) {
                 return null;
             }
             seatHoldMap.remove(seatHoldId);
        }

        seatHold.getHoldSeats().stream().forEach(seat -> seatTracker.updateSeatStatus(seat, SeatStatus.RESERVED));
        String reservationId = String.valueOf(reservationIdGenerator.generate());
        reservedSeatMap.put(reservationId, seatHold); // Store the reservation Id and seat hold to the reservation seat map
        return reservationId;
    }

    /**
     * Check for the available seat, sort them by rows and columns.
     * Select the first @numSeats requested by the customer and mark the status as HOLD.
     * The operation is synchronized to avoid multiple threads holding the same seats.
     *
     * @param numSeats the number of seats to find and hold
     * @param customerEmail unique identifier for the customer
     * @return a SeatHold object identifying the specific seats and related
    information
     */
     synchronized SeatHold findAndHold(int numSeats, String customerEmail){
        // clear the expired seat holds if the seat availability is critical or should reflect the real time state.
        List<Seat> availableSeats = seatTracker.availableSeats();

        if(numSeats > availableSeats.size()){
            return null;
        }

        List<Seat> seats = new ArrayList<>();
        Iterator<Seat> seatIterator = sortByRowAndColumn(availableSeats).iterator();

        for (int i = 0; i < numSeats; i++) {
            Seat seat = seatIterator.next();
            seatTracker.updateSeatStatus(seat, SeatStatus.HOLD);
            // create a defensive copy for the caller
            seats.add(new Seat(seat));
        }

        SeatHold seatHold = new SeatHold(seatHoldIdGenerator.generate(),customerEmail,seats);
        seatHoldMap.put(seatHold.getSeatHoldId(),seatHold); // add the seat hold to the seat hold map.
        return seatHold;
    }

    /**
     * Sort the seats from best(row 0, column 0) to worst (rowMax, columnMax).
     * @return list of sorted seats
     */
    private List<Seat> sortByRowAndColumn(List<Seat> availableSeats){
        return availableSeats.stream()
                .sorted(Comparator.comparing(Seat::getRowNum).thenComparing(Seat::getColumnNum))
                .collect(toList());
    }

}
