package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;
import java.time.LocalDateTime;

public class FareCalculatorService {

    /**
     * Constante définissant à partir de quand
     * le parking n'est plus gratuit, exprimé en heure.
     */
    private static final  double DELAYFREE = 0.5;

    /**
     * Constante une heure correspond à 60minutes.
     */
    private static final double HOUR_IN_MINUTES = 60;

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
        double duration = (double) Duration.between(
                inHour, outHour).toMinutes() / HOUR_IN_MINUTES;
        final int cent = 100;

        if (duration <= DELAYFREE) {
            ticket.setPrice(0.0);
        } else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR:
                    ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR
                            - (duration * Fare.CAR_RATE_PER_HOUR
                                    * ticket.getDiscount()) / cent);
                    break;
                case BIKE:
                    ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR
                            - (duration * Fare.BIKE_RATE_PER_HOUR
                                    * ticket.getDiscount()) / cent);
                    break;
                default:
                    throw new IllegalArgumentException("Unkown Parking Type");
            }
        }
    }
}
