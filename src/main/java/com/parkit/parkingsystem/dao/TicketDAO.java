package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TicketDAO {

    /**
     * Initialisation logger.
     */
    private static final Logger LOGGER = LogManager.getLogger("TicketDAO");

    /**
     * Connection à la base de données.
     */
    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    /**
     * Déclaration d'une constante d'index 1.
     */
    private final int index1 = 1;
    /**
     * Déclaration d'une constante d'index 2.
     */
    private final int index2 = 2;
    /**
     * Déclaration d'une constante d'index 3.
     */
    private final int index3 = 3;
    /**
     * Déclaration d'une constante d'index 4.
     */
    private final int index4 = 4;
    /**
     * Déclaration d'une constante d'index 5.
     */
    private final int index5 = 5;
    /**
     * Déclaration d'une constante d'index 6.
     */
    private final int index6 = 6;

    /**
     * Sauvegarde du ticket.
     * @param ticket
     * @return exécution du script sql de sauvegarde de
     * ticket dans la base de données
     */
    public boolean saveTicket(final Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(DBConstants.SAVE_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            //ps.setInt(1,ticket.getId());
            ps.setInt(index1, ticket.getParkingSpot().getId());
            ps.setString(index2, ticket.getVehicleRegNumber());
            ps.setDouble(index3, ticket.getPrice());
            ps.setTimestamp(index4,
                    Timestamp.valueOf(ticket.getInTime()));
            ps.setTimestamp(index5, (ticket.getOutTime() == null)
                    ? null : (Timestamp.valueOf(
                    ticket.getOutTime())));
            return ps.execute();
        } catch (Exception ex) {
            LOGGER.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
            return false;
        }
    }

    /**
     * Getter du ticket depuis la base de données.
     * @param vehicleRegNumber
     * @return le ticket
     */
    public Ticket getTicket(final String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(index1, vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(index1),
                        ParkingType.valueOf(rs.getString(index6)), false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(index2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(index3));
                ticket.setInTime(rs.getTimestamp(index4).toLocalDateTime());
                ticket.setOutTime(rs.getTimestamp(index5).toLocalDateTime());
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        } catch (Exception ex) {
            LOGGER.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
            return ticket;
        }
    }

    /**
     * Update du ticket dans la base de donnée.
     * @param ticket
     * @return true si le ticket a été mis a jour
     */
    public boolean updateTicket(final Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(index1, ticket.getPrice());
            ps.setTimestamp(index2,
                    Timestamp.valueOf(ticket.getOutTime()));
            ps.setInt(index3, ticket.getId());
            ps.execute();
            return true;
        } catch (Exception ex) {
            LOGGER.error("Error saving ticket info", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }
}
