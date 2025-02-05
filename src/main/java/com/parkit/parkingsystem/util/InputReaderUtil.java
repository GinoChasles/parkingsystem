package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 *
 */
public class InputReaderUtil {

    /**
     * Déclaration du scanner, entrée console.
     */
    private static final Scanner SCAN = new Scanner(System.in);
    /**
     * Déclaration logger.
     */
    private static final Logger LOGGER =
            LogManager.getLogger("InputReaderUtil");

    /**
     * méthode pour lire la valeur entrée dans la console.
     * @return input
     */
    public int readSelection() {
        try {
            int input = Integer.parseInt(SCAN.nextLine());
            return input;
        } catch (Exception e) {
            LOGGER.error("Error while reading user input from Shell", e);
            System.out.println("Error reading input. "
                    + "Please enter valid number for proceeding further");
            return -1;
        }
    }

    /**
     * Lire la plaque d'immatriculation saisie.
     * @return la plaque
     * @throws Exception
     */
    public String readVehicleRegistrationNumber() throws Exception {
        try {
            String vehicleRegNumber = SCAN.nextLine();
            if (vehicleRegNumber == null
                    || vehicleRegNumber.trim().length() == 0) {
                throw new IllegalArgumentException("Invalid input provided");
            }
            return vehicleRegNumber;
        } catch (Exception e) {
            LOGGER.error("Error while reading user input from Shell", e);
            System.out.println("Error reading input. Please enter a"
                    + " valid string for vehicle registration number");
            throw e;
        }
    }


}
