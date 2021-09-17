package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DbConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Ticket et bdd.
 */
public class TicketDao {

  /**
   * Initialisation logger.
   */

  private static final Logger LOGGER = LogManager.getLogger("TicketDAO");

  /**
   * Connection à la base de données.
   */

  private DataBaseConfig dataBaseConfig = new DataBaseConfig();

  /**
   * Déclaration d'une constante d'index 1.
   */

  private static final int INDEX_1 = 1;

  /**
   * Déclaration d'une constante d'index 2.
   */

  private static final int INDEX_2 = 2;

  /**
   * Déclaration d'une constante d'index 3.
   */

  private static final int INDEX_3 = 3;

  /**
   * Déclaration d'une constante d'index 4.
   */

  private static final int INDEX_4 = 4;

  /**
   * Déclaration d'une constante d'index 5.
   */

  private static final int INDEX_5 = 5;
  /**
   * Déclaration d'une constante d'index 6.
   */

  private static final int INDEX_6 = 6;

  /**
   * Getter de la config.
   *
   * @return config
   *
   */

  public DataBaseConfig getDataBaseConfig() {
    return dataBaseConfig;
  }
  /**
   * Setter config.
   *
   * @param dataBase db
   *
   */

  public void setDataBaseConfig(final DataBaseConfig dataBase) {
    this.dataBaseConfig = dataBase;
  }

  /**
   * Sauvegarde du ticket.
   *
   * @param ticket ticket
   *
   * @return exécution du script sql de sauvegarde
   *
   */

  public boolean saveTicket(final Ticket ticket) {
    Connection con = null;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps =
                con.prepareStatement(DbConstants.SAVE_TICKET);
      //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
      //ps.setInt(1,ticket.getId());
      ps.setInt(INDEX_1, ticket.getParkingSpot().getId());
      ps.setString(INDEX_2, ticket.getVehicleRegNumber());
      ps.setDouble(INDEX_3, ticket.getPrice());
      ps.setTimestamp(INDEX_4,
                    new Timestamp(ticket.getInTime().getTime()));
      ps.setTimestamp(INDEX_5, (ticket.getOutTime() == null)
                    ? null : (new Timestamp(ticket.getOutTime().getTime())));
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
   *
   * @param vehicleRegNumber plaque
   *
   * @return le ticket
   *
   */

  public Ticket getTicket(final String vehicleRegNumber) {
    Connection con = null;
    Ticket ticket = null;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps = con.prepareStatement(DbConstants.GET_TICKET);
      //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
      ps.setString(INDEX_1, vehicleRegNumber);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        ticket = new Ticket();
        ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(INDEX_1),
            ParkingType.valueOf(rs.getString(INDEX_6)), false);
        ticket.setParkingSpot(parkingSpot);
        ticket.setId(rs.getInt(INDEX_2));
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(rs.getDouble(INDEX_3));
        ticket.setInTime(rs.getTimestamp(4));
        ticket.setOutTime(rs.getTimestamp(5));
        //TODO uderstand why generated error
        //                ticket.setInTime(rs.getTimestamp(INDEX_4).toDate());
        //                ticket.setOutTime(rs.getTimestamp(INDEX_5).toDate());
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
   *
   * @param ticket ticket
   *
   * @return true si le ticket a été mis a jour
   *
   */
  public boolean updateTicket(final Ticket ticket) {
    Connection con = null;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps =
              con.prepareStatement(DbConstants.UPDATE_TICKET);
      ps.setDouble(INDEX_1, ticket.getPrice());
      ps.setTimestamp(INDEX_2,
              new Timestamp(ticket.getOutTime().getTime()));
      ps.setInt(INDEX_3, ticket.getId());
      ps.execute();
      return true;
    } catch (Exception ex) {
      LOGGER.error("Error saving ticket info", ex);
    } finally {
      dataBaseConfig.closeConnection(con);
    }
    return false;
  }

  /**
   * Count the number of ticket for a vehicleRegNumer.
   *
   * @param vehicleRegNumber plaque
   *
   * @return int
   *
   */
  public int countTicketsByVehicleRegNumber(final String vehicleRegNumber) {
    Connection con = null;
    int result = 0;
    try {
      con = dataBaseConfig.getConnection();
      PreparedStatement ps =
              con.prepareStatement(DbConstants.CHECK_REGNUMBER);
      ps.setString(INDEX_1, vehicleRegNumber);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        result = rs.getInt(INDEX_1);
      }
      dataBaseConfig.closeResultSet(rs);
      dataBaseConfig.closePreparedStatement(ps);
    } catch (Exception ex) {
      LOGGER.error("Error counting tickets by VehicleRegNumber", ex);
    } finally {
      dataBaseConfig.closeConnection(con);
    }
    return result;
  }
}
