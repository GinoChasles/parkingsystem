package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ParkingSpotDAO {
    /**
     * Initialisation du logger.
     */
    private static final Logger LOGGER = LogManager.getLogger("ParkingSpotDAO");

    /**
     * Connection à la base de données.
     */
    private DataBaseConfig dataBaseConfig = new DataBaseConfig();

    /**
     * Récupère la prochaine place disponible.
     * @param parkingType
     * @return result la prochaine place disponible
     */
    public int getNextAvailableSlot(final ParkingType parkingType) {
        Connection con = null;
        int result = -1;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
            ps.setString(1, parkingType.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        } catch (Exception ex) {
            LOGGER.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return result;
    }

    /**
     * Met à jour la disponibilité de la place.
     * @param parkingSpot
     * @return updateRowCount à 1, la place n'est donc plus disponible
     */
    public boolean updateParking(final ParkingSpot parkingSpot) {
        //update the availability fo that parking slot
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
            ps.setBoolean(1, parkingSpot.isAvailable());
            ps.setInt(2, parkingSpot.getId());
            int updateRowCount = ps.executeUpdate();
            dataBaseConfig.closePreparedStatement(ps);
            return (updateRowCount == 1);
        } catch (Exception ex) {
            LOGGER.error("Error updating parking info", ex);
            return false;
        } finally {
            dataBaseConfig.closeConnection(con);
        }
    }

    /**
     * Getter de la config.
     * @return config
     */
    public DataBaseConfig getDataBaseConfig() {
        return dataBaseConfig;
    }

    /**
     * Setter config.
     * @param dataBase
     */
    public void setDataBaseConfig(final DataBaseConfig dataBase) {
        this.dataBaseConfig = dataBase;
    }
}
