package com.walmart.exercise.ticketservice.domain;

/**
 * Seat's status
 * - AVAILABLE: Available for reservation or hold.
 * - HOLD: Temporary hold, can go to available on expire or reserved on reservation.
 * - RESERVED: Reserved after maintaining a hold.
 */
public enum SeatStatus {
    AVAILABLE, HOLD, RESERVED
}
