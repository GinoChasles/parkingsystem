package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class App {
    /**
     * Logger de l'application.
     */
    private static final Logger LOGGER = LogManager.getLogger("App");

    /**
     * Lancement de l'application.
     *
     * @param args
     *
     */
    public static void main(final String[] args) {
        LOGGER.info("Initializing Parking System");
        InteractiveShell.loadInterface();
    }

    private App() {
    }
}
