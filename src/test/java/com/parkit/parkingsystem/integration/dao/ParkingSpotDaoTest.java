package com.parkit.parkingsystem.integration.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDaoTest {

    private static TicketDao ticketDao;

    private static ParkingSpotDao parkingSpotDao;

    private static ParkingSpot parkingSpot;

    private static Ticket ticket;

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    @BeforeAll
    private static void setUp() {
        ticketDao = new TicketDao();
        ticketDao.setDataBaseConfig(dataBaseTestConfig);
    }

    @BeforeEach
    private void before() {
        ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDE");
        ticket.setInTime(new Date());
        parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        ticket.setParkingSpot(parkingSpot);
        DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
        parkingSpotDao = new ParkingSpotDao();

    }

    @AfterAll
    private static void tearDown() {

    }

    @Test
    public void getNextAvailableSlotTest() {
        ticketDao.saveTicket(ticket);
        int result = parkingSpotDao.getNextAvailableSlot(ParkingType.CAR);

        Assertions.assertEquals(1, result);

    }

    @Test
    public void updateParkingTest() {
        ticketDao.saveTicket(ticket);
        boolean result = parkingSpotDao.updateParking(parkingSpot);

        Assertions.assertTrue(result);
    }

    @Test
    public void notNextAvailableSpotTest() {
        ParkingSpotDao parkingSpotDAO = new ParkingSpotDao();
        final int availableSlot = parkingSpotDAO.getNextAvailableSlot(null);
        parkingSpotDAO.getNextAvailableSlot(null);

        Assertions.assertEquals(-1, availableSlot);
    }
}
