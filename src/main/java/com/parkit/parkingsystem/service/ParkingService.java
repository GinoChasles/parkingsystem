package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import java.util.Date;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service du parking.
 */
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
  private final ParkingSpotDao parkingSpotDao;
  /**
   * Initialisation d'un ticket.
   */
  private final TicketDao ticketDao;
  /**
   * Initialisation du service.
   *
   * @param inputReader reader
   * @param parkingSpot parkingspot
   * @param ticket ticket
   *
   */

  public ParkingService(final InputReaderUtil inputReader,
                        final ParkingSpotDao parkingSpot,
                        final TicketDao ticket) {
    this.inputReaderUtil = inputReader;
    this.parkingSpotDao = parkingSpot;
    this.ticketDao = ticket;
  }

  /**
   * Entrée d'un véhicule.
   */

  public void processIncomingVehicle() {
    try {
      ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
      if (parkingSpot != null && parkingSpot.getId() > 0) {
        String vehicleRegNumber = getVehichleRegNumber();
        final int countTicketsBySameRegNumber = ticketDao
                 .countTicketsByVehicleRegNumber(vehicleRegNumber);
        final int discount = 5;
        parkingSpot.setAvailable(false);
        parkingSpotDao.updateParking(parkingSpot);
        Ticket ticket = new Ticket();
        Date inTime = new Date();
        ticket.setId(ticket.getId());
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(0);
        ticket.setInTime(inTime);
        ticket.setOutTime(null);
        if (countTicketsBySameRegNumber > 0) {
          ticket.setDiscount(discount);
        } else {
          ticket.setDiscount(0);
        }
        ticketDao.saveTicket(ticket);
        System.out.println("Generated Ticket and saved in DB");
        if (countTicketsBySameRegNumber > 0) {
          System.out.println("Welcome back! As a recurring user of "
                     + "our parking lot, you'll benefit"
                     + " from a 5% discount.");
        }
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
   *
   * @return une place de parking
   *
   */

  public ParkingSpot getNextParkingNumberIfAvailable() {
    int parkingNumber = 0;
    ParkingSpot parkingSpot = null;
    try {
      ParkingType parkingType = getVehichleType();
      parkingNumber = parkingSpotDao.getNextAvailableSlot(parkingType);
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

  public ParkingType getVehichleType() {
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
      Ticket ticket = ticketDao.getTicket(vehicleRegNumber);
      //      Date outTime = new Date();
      Date outTime = new Date(ticket.getInTime().getTime()
              + new Date(1000 * 7652).getTime());
      if (outTime.before(ticket.getInTime())
            || outTime.equals(ticket.getInTime())) {
        outTime = new Date(ticket.getInTime().getTime()
                + new Date(1000).getTime());
      }
      ticket.setOutTime(outTime);
      fareCalculatorService.calculateFare(ticket);
      if (ticketDao.updateTicket(ticket)) {
        ParkingSpot parkingSpot = ticket.getParkingSpot();
        parkingSpot.setAvailable(true);
        parkingSpotDao.updateParking(parkingSpot);
        System.out.println("Please pay the parking fare:"
                 + (double) Math.round(ticket.getPrice() * 100.0) / 100.0);
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
