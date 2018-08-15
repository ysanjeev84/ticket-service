package com.walmart.exercise.ticketservice.domain;

/**
 * Ticket service specific exception.
 */
public class TicketServiceException extends Exception{
    public TicketServiceException(String message) { super(message); }
}
