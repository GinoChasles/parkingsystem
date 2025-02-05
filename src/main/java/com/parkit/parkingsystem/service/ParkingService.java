package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class ParkingService {

    /**
     * Déclaration logger.
     */
    private static final Logger LOGGER = LogManager.getLogger("ParkingService");

    /**
     * Déclaration du service de calcul de tarif.
     */
    private static FareCalculatorService fareCalculatorService =
            new FareCalculatorService();
    /**
     * Déclaration du lecteur de console.
     */
    private final InputReaderUtil inputReaderUtil;

    /**
     * Initialisation place de parking.
     */
    private final ParkingSpotDAO parkingSpotDAO;

    /**
     * Initialisation d'un ticket.
     */
    private final TicketDAO ticketDAO;

    /**
     * Initialisation du service.
     * @param inputReader
     * @param parkingSpot
     * @param ticket
     */
    public ParkingService(final InputReaderUtil inputReader,
                          final ParkingSpotDAO parkingSpot,
                          final TicketDAO ticket) {
        this.inputReaderUtil = inputReader;
        this.parkingSpotDAO = parkingSpot;
        this.ticketDAO = ticket;
    }

    /**
     * Entrée d'un véhicule.
     */
    public void processIncomingVehicle() {
        try {
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if (parkingSpot != null && parkingSpot.getId() > 0) {
                String vehicleRegNumber = getVehichleRegNumber();
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);
                //allot this parking space and mark it's availability as false


                Ticket ticket = new Ticket();
                LocalDateTime inTime = LocalDateTime.now();
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER,
                // PRICE, IN_TIME, OUT_TIME)
                //ticket.setId(ticketID);
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                ticketDAO.saveTicket(ticket);
                System.out.println("Generated Ticket and saved in DB");
                System.out.println("Please park your vehicle in spot number:"
                        + parkingSpot.getId());
                System.out.println("Recorded in-time for vehicle number:"
                        + vehicleRegNumber + " is:" + inTime);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to process incoming vehicle", e);
        }
    }

    private String getVehichleRegNumber() throws Exception {
        System.out.println("Please type the vehicle registration "
                + "number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    /**
     * Récupère une place de parkink s'il y a encore de la place.
     * @return une place de parking
     */
    public ParkingSpot getNextParkingNumberIfAvailable() {
        int parkingNumber = 0;
        ParkingSpot parkingSpot = null;
        try {
            ParkingType parkingType = getVehichleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if (parkingNumber > 0) {
                parkingSpot =
                        new ParkingSpot(parkingNumber, parkingType, true);
            } else {
                throw new Exception("Error fetching parking number from DB."
                        + " Parking slots might be full");
            }
        } catch (IllegalArgumentException ie) {
            LOGGER.error("Error parsing user input for type of vehicle", ie);
        } catch (Exception e) {
            LOGGER.error("Error fetching next available parking slot", e);
        }
        return parkingSpot;
    }

    private ParkingType getVehichleType() {
        System.out.println("Please select vehicle type from menu");
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch (input) {
            case 1:
                return ParkingType.CAR;
            case 2:
                return ParkingType.BIKE;
            default:
                System.out.println("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");

        }
    }

    /**
     * Quitter le parking.
     */
    public void processExitingVehicle() {
        try {
            String vehicleRegNumber = getVehichleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
            LocalDateTime outTime = LocalDateTime.now();
            ticket.setOutTime(outTime);
            fareCalculatorService.calculateFare(ticket);
            if (ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);
                System.out.println("Please pay the parking fare:"
                        + ticket.getPrice());
                System.out.println("Recorded out-time for vehicle number:"
                        + ticket.getVehicleRegNumber() + " is:" + outTime);
            } else {
                System.out.println("Unable to update ticket information."
                        + " Error occurred");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to process exiting vehicle", e);
        }
    }
}
