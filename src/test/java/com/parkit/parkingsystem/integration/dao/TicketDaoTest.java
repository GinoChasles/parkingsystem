package com.parkit.parkingsystem.integration.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class TicketDaoTest {


    private static TicketDao ticketDao;

    private static Ticket ticket;

    private static DataBasePrepareService dataBasePrepareService;

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

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
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        ticket.setParkingSpot(parkingSpot);
        dataBasePrepareService = new DataBasePrepareService();
    }

    @AfterAll
    private static void tearDown() {

    }

    @Test
    public void saveTicketTest() {
        ticketDao.saveTicket(ticket);
        Ticket result = ticketDao.getTicket("ABCDE");

        Assertions.assertNotNull(result);
    }

    @Test
    public void getTicketTest() {
        ticketDao.saveTicket(ticket);
        Ticket result = ticketDao.getTicket("ABCDE");

        Assertions.assertEquals(result.getVehicleRegNumber(), ticket.getVehicleRegNumber());
    }

    @Test
    public void updateTicketTest() {
        ticketDao.saveTicket(ticket);
        Date outTime = new Date(ticket.getInTime().getTime() + new Date(3600 * 1000).getTime());
        Ticket updatedTicket = ticketDao.getTicket("ABCDE");
        updatedTicket.setOutTime(outTime);
        FareCalculatorService fareCalculatorService = new FareCalculatorService();
        fareCalculatorService.calculateFare(updatedTicket);
        ticketDao.updateTicket(updatedTicket);

        Assertions.assertTrue(ticketDao.updateTicket(updatedTicket));
        Assertions.assertNotEquals(updatedTicket.getPrice(), 0.0);
    }

    @Test
    public void countTicketsByVehicleRegNumberTest() {
        dataBasePrepareService.clearDataBaseEntries();
        ticketDao.saveTicket(ticket);
        int result = ticketDao.countTicketsByVehicleRegNumber("ABCDE");

        Assertions.assertEquals(result,1 );
    }

    @Test
    public void errorToFetchNextSlotTest() {
        dataBaseTestConfig = null;
        try {
            ticketDao.saveTicket(ticket);
        } catch (Exception ex) {
            Assertions.fail("Error fetching next available slot", ex);
        }
    }
}
