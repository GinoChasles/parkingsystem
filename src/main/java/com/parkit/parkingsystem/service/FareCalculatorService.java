package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;
import java.time.LocalDateTime;

public class FareCalculatorService {
    /**
     * Calcul du tarif.
     * @param ticket
     */
    public void calculateFare(final Ticket ticket) {
        if ((ticket.getOutTime() == null)
        || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
            throw new IllegalArgumentException(
            "Out time provided is incorrect:"
            + ticket.getOutTime().toString());
        }

        LocalDateTime inHour = ticket.getInTime();
        LocalDateTime outHour = ticket.getOutTime();

        // TODO: Some tests are failing here.
        // Need to check if this logic is correct
        double duration = (double)Duration.between(inHour,outHour).toMinutes()/60;

        if (duration <= 0.5) {
            ticket.setPrice(0.0);
        } else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR:
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                    break;
                case BIKE:
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                    break;
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
    }
}
