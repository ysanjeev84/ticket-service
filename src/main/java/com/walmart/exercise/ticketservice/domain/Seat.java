package com.walmart.exercise.ticketservice.domain;

/**
 * Seat has a location row number and column number which uniquely identifies a seat.
 * At a given time, the seat will have one status.
 */
public final class Seat {

    private final String id;
    private final int rowNum;
    private final int columnNum;
    private SeatStatus status;

    public Seat(int rowNum,int columnNum){
        this.rowNum = rowNum;
        this.columnNum = columnNum;
        // The seat ID can be used to uniquely identify a seat on multiple floors without a breaking change.
        this.id = this.rowNum + "-" + this.columnNum;
        // Mark the seat as available on seat creation.
        status = SeatStatus.AVAILABLE;
    }

    public Seat(Seat seat){
        this.rowNum = seat.rowNum;
        this.columnNum = seat.columnNum;
        this.id = seat.id;
        this.status = seat.status;
    }

    public String getId(){return this.id;}

    public int getRowNum() {
        return rowNum;
    }

    public int getColumnNum() {
        return columnNum;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

}
