package com.walmart.exercise.ticketservice.adapter.service;

/**
 * UniqueIdGenerator generate a unique ID.
 */
public interface UniqueIdGenerator {

    /**
     * Generates unique id
     * @return unique number with integer limits
     */
    int generate();
}
