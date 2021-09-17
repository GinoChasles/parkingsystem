package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar() {
//        Calendar cal = Calendar.getInstance();
//        long timeinSecs = cal.getTimeInMillis();
//        Date inTime = cal.getTime();
////       cal.add(Calendar.HOUR_OF_DA1Y, 1);
//        Date outTime = new Date (timeinSecs + (60*60*1000));

        Date hour = new Date(3600*1000);
        Date inTime = new Date();
        Date outTime = new Date(inTime.getTime() + hour.getTime());

        System.out.println(inTime);
        System.out.println(outTime);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike() {
        Date hour = new Date(3600*1000);
        Date inTime = new Date();
        Date outTime = new Date(inTime.getTime() + hour.getTime());
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType() {
        Date hour = new Date(3600*1000);
        Date inTime = new Date();
        Date outTime = new Date(inTime.getTime() + hour.getTime());
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        Date hour = new Date(3600*1000);
        Date inTime = new Date();
        Date outTime = new Date(inTime.getTime() - hour.getTime());
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        Date addTime = new Date(2700*1000);
        Date inTime = new Date();
        Date outTime = new Date(inTime.getTime() + addTime.getTime());
       //45 minutes parking time should give 3/4th parking fare
        System.out.println(inTime);
        System.out.println(outTime);

        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        Date addTime = new Date(2700*1000);
        Date inTime = new Date();
        Date outTime = new Date(inTime.getTime() + addTime.getTime());//45 minutes parking time should give 3/4th parking fare
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        Date addTime = new Date(3600*1000*24);
        Date inTime = new Date();
        Date outTime = new Date(inTime.getTime() + addTime.getTime());//24 hours parking time should give 24 * parking fare per hour
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithLessOfThirtyMinutes() {
        Date addTime = new Date(1800*1000);
        Date inTime = new Date();
        Date outTime = new Date(inTime.getTime() + addTime.getTime());
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( 0 , ticket.getPrice());
    }
    @Test
    public void calculateFareBikeWithLessOfThirtyMinutes() {
        Date addTime = new Date(1800*1000);
        Date inTime = new Date();
        Date outTime = new Date(inTime.getTime() + addTime.getTime());
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( 0 , ticket.getPrice());
    }
    @Test
    public void calculateCarWithFivePourcentDiscount() {
        Date hour = new Date(3600*1000);
        Date inTime = new Date();
        Date outTime = new Date(inTime.getTime() + hour.getTime());
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setVehicleRegNumber("AA123AA");
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setDiscount(5);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( Fare.CAR_RATE_PER_HOUR - (5 * Fare.CAR_RATE_PER_HOUR / 100) , ticket.getPrice());
    }

    @Test
    public void calculateBikeWithFivePourcentDiscount() {
        Date hour = new Date(3600*1000);
        Date inTime = new Date();
        Date outTime = new Date(inTime.getTime() + hour.getTime());
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setVehicleRegNumber("112233");
        ticket.setDiscount(5);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( Fare.BIKE_RATE_PER_HOUR - (5 * Fare.BIKE_RATE_PER_HOUR / 100), ticket.getPrice());
    }
}
