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
import java.time.LocalDateTime;
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
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.minusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(45);//45 minutes parking time should give 3/4th parking fare
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(45);//45 minutes parking time should give 3/4th parking fare
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (0.75 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusDays(1);//24 hours parking time should give 24 * parking fare per hour
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithLessOfThirtyMinutes() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(30);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( 0 , ticket.getPrice());
    }
    @Test
    public void calculateFareBikeWithLessOfThirtyMinutes() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusMinutes(30);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( 0 , ticket.getPrice());
    }
    @Test
    public void calculateCarWithFivePourcentDiscount() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusHours(1);
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
    public void calculateCarWithFivePourcentDiscountForRandomTime() {
        final double hourInMinutes = 60;
        Random r = new Random();
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusDays(r.nextInt(365)).plusHours(r.nextInt(24)).plusMinutes(r.nextInt(60));
        double duration = (double) Duration.between(
                inTime, outTime).toMinutes() / hourInMinutes;
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setVehicleRegNumber("AA123AA");
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setDiscount(5);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( duration * Fare.CAR_RATE_PER_HOUR - (5 * (duration * Fare.CAR_RATE_PER_HOUR) / 100) , ticket.getPrice());
    }
    @Test
    public void calculateBikeWithFivePourcentDiscount() {
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusHours(1);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setVehicleRegNumber("112233");
        ticket.setDiscount(5);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( Fare.BIKE_RATE_PER_HOUR - (5 * Fare.BIKE_RATE_PER_HOUR / 100), ticket.getPrice());
    }

    @Test
    public void calculateBikeWithFivePourcentDiscountForRandomTime() {
        final double hourInMinutes = 60;
        Random r = new Random();
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusDays(r.nextInt(365)).plusHours(r.nextInt(24)).plusMinutes(r.nextInt(60));
        double duration = (double) Duration.between(
                inTime, outTime).toMinutes() / hourInMinutes;
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setVehicleRegNumber("112233");
        ticket.setDiscount(5);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( duration * Fare.BIKE_RATE_PER_HOUR - (5 * (duration * Fare.BIKE_RATE_PER_HOUR )/ 100), ticket.getPrice());
    }

    @Test
    public void calculateBikeForRandomTime() {
        final double hourInMinutes = 60;
        Random r = new Random();
        LocalDateTime inTime = LocalDateTime.now();
        LocalDateTime outTime = inTime.plusDays(r.nextInt(365)).plusHours(r.nextInt(24)).plusMinutes(r.nextInt(60));
        double duration = (double) Duration.between(
                inTime, outTime).toMinutes() / hourInMinutes;
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setVehicleRegNumber("112233");
        ticket.setDiscount(0);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( duration * Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
    }
}
