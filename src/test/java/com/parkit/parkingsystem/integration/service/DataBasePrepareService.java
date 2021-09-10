package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class DataBasePrepareService {

    DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    public void clearDataBaseEntries() {
        Connection connection = null;
        try{
            connection = dataBaseTestConfig.getConnection();

            //set parking entries to available
            connection.prepareStatement("update parking set available = true").execute();

            //clear ticket entries;
            connection.prepareStatement("truncate table ticket").execute();

        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }

    public boolean ticketExistsForVehicle(final String vehicleRegNumber) {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();
            final PreparedStatement ps = connection.prepareStatement(
                    "select count(*) as number "
                    + "from ticket where ticket.VEHICLE_REG_NUMBER = ?");
            ps.setString(1, vehicleRegNumber);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("number") > 0;
            } else {
                throw new NoSuchElementException("First enter");
            }
        }  catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }

    public boolean slotAvailable(final int parkingNumber) {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();
            final PreparedStatement ps = connection.prepareStatement(
                    "select * from parking "
                            + "where PARKING_NUMBER = ?");
            ps.setInt(1, parkingNumber);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("available") > 0;
            } else {
                throw new NoSuchElementException("No place avaible");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }

    public boolean checkFareAndOutTimeInDb(final String vehicleRegNumber) {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();
            final PreparedStatement ps = connection.prepareStatement(
                    "select count(*) as number from ticket"
                    + " where VEHICLE_REG_NUMBER = ?"
                    + " and price is not null"
                    + " and out_time is not null"
            );
            ps.setString(1, vehicleRegNumber);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("number") > 0;
            } else {
                throw new NoSuchElementException("Not already sorting");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }
}
