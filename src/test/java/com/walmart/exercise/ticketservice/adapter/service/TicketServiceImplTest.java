package com.walmart.exercise.ticketservice.adapter.service;

import com.walmart.exercise.ticketservice.domain.Seat;
import com.walmart.exercise.ticketservice.domain.SeatHold;
import com.walmart.exercise.ticketservice.domain.SeatTracker;
import com.walmart.exercise.ticketservice.domain.TicketServiceException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;
import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.*;

public class TicketServiceImplTest {

    private TicketServiceImpl subject;
    SeatHoldIdGenerator seatHoldIdGenerator;
    Map<Integer,SeatHold> reservationMap;
    private Set<String> seatIdSet;
    boolean duplicate = false;

    @Test
    public void findAndHoldSeats_numOfSeatAvailable_returnsValidNumber() throws TicketServiceException {
        SeatTracker seatTracker = new SeatTracker(5, 5);
        subject = new TicketServiceImpl(seatTracker, 100);
        String customerEmail = "test234@gmail.com";

        subject.findAndHoldSeats(6, customerEmail);
        Assert.assertEquals(19, subject.numSeatsAvailable());
        subject.findAndHoldSeats(5, customerEmail);
        subject.findAndHoldSeats(7, customerEmail);
        subject.findAndHoldSeats(4, customerEmail);
        subject.findAndHoldSeats(3, customerEmail);
        Assert.assertEquals(0, subject.numSeatsAvailable());
    }

    @Test
    public void findAndHoldSeats_emailNull_returnsNull() throws TicketServiceException{
        SeatTracker seatTracker = new SeatTracker(5,5);
        subject = new TicketServiceImpl(seatTracker,100);
        assertNull(subject.findAndHoldSeats(5,null));
    }

    @Test
    public void findAndHoldSeats_badInput_returnsNull() throws TicketServiceException{
        SeatTracker seatTracker = new SeatTracker(4,4);
        subject = new TicketServiceImpl(seatTracker,10);
        String customerEmail = "test345@gmail.com";
        assertNull(subject.findAndHoldSeats(5,null));
        assertNull(subject.findAndHoldSeats(0,"abc@test.com"));
        assertNull(subject.findAndHoldSeats(20,"abc@test.com"));
        assertEquals(1,subject.findAndHoldSeats(6,customerEmail).getSeatHoldId());
        assertEquals(2,subject.findAndHoldSeats(6,customerEmail).getSeatHoldId());
        assertNull(subject.findAndHoldSeats(5,customerEmail));
    }

    @Test
    public void findAndHoldSeats_validInput_returnsValidId() throws TicketServiceException{
        SeatTracker seatTracker = new SeatTracker(4,4);
        subject = new TicketServiceImpl(seatTracker,10);
        String customerEmail = "test456@gmail.com";
        assertEquals(1,subject.findAndHoldSeats(6,customerEmail).getSeatHoldId());
        assertEquals(2,subject.findAndHoldSeats(6,customerEmail).getSeatHoldId());
    }

    @Test
    public void findAndHoldSeats_validInput_holdBestAvailableSeats() throws TicketServiceException{
        SeatTracker seatTracker = new SeatTracker(10,5);
        subject = new TicketServiceImpl(seatTracker,100);
        assertEquals(50,subject.numSeatsAvailable());
        SeatHold seatHold = subject.findAndHoldSeats(4,"test123@gmail.com");
        assertNotNull(seatHold);
        List<Seat> seats = seatHold.getHoldSeats().stream().collect(toList());
        assertEquals(4,seats.size());
        assertEquals(1,seatHold.getSeatHoldId());
        assertEquals(seats.get(0).getRowNum(),0);
        assertEquals(seats.get(0).getColumnNum(),0);
        assertEquals(seats.get(1).getRowNum(),0);
        assertEquals(seats.get(1).getColumnNum(),1);
        assertEquals(seats.get(2).getRowNum(),0);
        assertEquals(seats.get(2).getColumnNum(),2);
        assertEquals(seats.get(3).getRowNum(),0);
        assertEquals(seats.get(3).getColumnNum(),3);
    }

    @Test
    public void findAndHoldSeats_expire_holdBestAvailableSeats() throws InterruptedException, TicketServiceException{
        SeatTracker seatTracker = new SeatTracker(10,10);
        subject = new TicketServiceImpl(seatTracker,1);
        subject.findAndHoldSeats(3,"test123@gmail.com");
        try {
            sleep(2000);
        }catch (InterruptedException e){
            throw new InterruptedException("Thread interrupted in sleep "+e.getMessage());
        }
        assertEquals(100,subject.numSeatsAvailable());
        SeatHold seatHold = subject.findAndHoldSeats(2,"test123@gmail.com");
        assertNotNull(seatHold);
        List<Seat> seats = seatHold.getHoldSeats().stream().collect(toList());
        assertEquals(2,seats.size());
        assertEquals(2,seatHold.getSeatHoldId());
        assertEquals(seats.get(0).getRowNum(),0);
        assertEquals(seats.get(0).getColumnNum(),0);
        assertEquals(seats.get(1).getRowNum(),0);
        assertEquals(seats.get(1).getColumnNum(),1);
    }

    @Test
    public void holdAndReserve_expire_reserveBestAvailableSeats() throws InterruptedException, TicketServiceException{
        SeatTracker seatTracker = new SeatTracker(10,10);
        subject = new TicketServiceImpl(seatTracker,2);
        SeatHold seatHold1 = subject.findAndHoldSeats(3,"a.test3@gmail.com");
        SeatHold secondReq = subject.findAndHoldSeats(90, "b.test3@gmail.com");
        assertNull(subject.reserveSeats(secondReq.getSeatHoldId(),"invalidemail@mail.com"));
        assertEquals("1",subject.reserveSeats(secondReq.getSeatHoldId(),"b.test3@gmail.com"));
        try {
            sleep(3000);
        }catch (InterruptedException e){
            throw new InterruptedException("Thread interrupted in sleep "+e.getMessage());
        }

        assertNull(subject.reserveSeats(seatHold1.getSeatHoldId(),"a.test3@gmail.com"));
        assertEquals(10, subject.numSeatsAvailable());
        SeatHold thirdReq = subject.findAndHoldSeats(5,"a.test3@gmail.com");
        assertEquals(3,thirdReq.getSeatHoldId());
        List<Seat> thirdReqSeats = thirdReq.getHoldSeats().stream().collect(toList());
        assertEquals(5,thirdReqSeats.size());
        assertEquals(0, thirdReqSeats.get(0).getRowNum());
        assertEquals(0, thirdReqSeats.get(0).getColumnNum());
        assertEquals(9, thirdReqSeats.get(4).getRowNum());
        assertEquals(4, thirdReqSeats.get(4).getColumnNum());
    }


    @Test
    public void holdAndReserve_multipleThreads_validateNoDuplicateSeatAllocation() throws TicketServiceException, InterruptedException{
        SeatTracker seatTracker = new SeatTracker(100,100);
        reservationMap = new ConcurrentHashMap<>();
        seatIdSet = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        seatHoldIdGenerator = new SeatHoldIdGenerator();
        subject = new TicketServiceImpl(seatTracker,2);
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for(int i=0;i<100;i++){
            executorService.execute(runnable);
        }
        Thread.sleep(1500);
        Assert.assertFalse(duplicate);
        Assert.assertEquals(10000,  seatIdSet.size());
        Assert.assertEquals(100,reservationMap.keySet().stream().distinct().count());
    }

    Runnable runnable = ()->{
        SeatHold seatHold = subject.findAndHoldSeats(100,"test"+seatHoldIdGenerator.generate()+"@gmail.com");
        seatHold.getHoldSeats().stream().map(seat -> seat.getId()).forEach(seatId -> {
            if(seatIdSet.contains(seatId)){duplicate =true;}
            seatIdSet.add(seatId);
        });
        subject.reserveSeats(seatHold.getSeatHoldId(),seatHold.getCustomerEmail());
        reservationMap.put(seatHold.getSeatHoldId(),seatHold);
    };

}

