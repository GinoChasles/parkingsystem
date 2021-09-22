package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDao;
import com.parkit.parkingsystem.dao.TicketDao;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    private static ParkingService parkingService;

    private Ticket ticket;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @Mock
    private static ParkingSpotDao parkingSpotDAO;

    @Mock
    private static TicketDao ticketDAO;


    @BeforeEach
    private void setUpPerTest() {
        try {

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            ticket = new Ticket();
            ticket.setInTime(new Date());
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");


            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up tests");
        }
    }

    @Test
    void processExitingVehicleTest() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

        when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void getVehicleTypeCarTest() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        ParkingType type = parkingService.getVehichleType();
        Assertions.assertEquals(type, ParkingType.CAR);
    }

    @Test
    public void getVehicleTypeBikeTest() {
        when(inputReaderUtil.readSelection()).thenReturn(2);
        ParkingType type = parkingService.getVehichleType();
        Assertions.assertEquals(type, ParkingType.BIKE);
    }

    @Test
    public void getErrorVehicleTypeTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            when(inputReaderUtil.readSelection()).thenReturn(3);
            parkingService.getVehichleType();
        });
    }
    @Test
    public void getNextParkingNumberIfAvailableTest() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        parkingSpotDAO.updateParking(parkingSpot);

        final int availableSlot = new ParkingSpotDao().getNextAvailableSlot(ParkingType.CAR);
        Assertions.assertEquals(1, availableSlot);
    }

    @Test
    public void errorGetNextParkingParkingNumberTest() {


    }

    @Test
    public void processIncomingVehicleTest() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
        parkingService.processIncomingVehicle();
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }

}
