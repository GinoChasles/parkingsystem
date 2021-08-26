package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;

public class Ticket {
    /**
     * id du ticket.
     */
    private int id;

    /**
     * Information sur la place de parking.
     */
    private ParkingSpot parkingSpot;

    /**
     * Numéro de plaque du véhicule.
     */
    private String vehicleRegNumber;

    /**
     * Prix du ticket.
     */
    private double price;

    /**
     * Date d'arrivée du véhicule.
     */
    private LocalDateTime inTime;

    /**
     * Date de sortie du véhicule.
     */
    private LocalDateTime outTime;

    /**
     * Récupère l'id du ticket.
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set de l'id du ticket.
     * @param idTicket
     */
    public void setId(final int idTicket) {
        this.id = idTicket;
    }

    /**
     * Getter de la place de parking.
     * @return place de parking
     */
    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    /**
     * Setter de la place de parking.
     * @param vehiculeSpot
     */
    public void setParkingSpot(final ParkingSpot vehiculeSpot) {
        this.parkingSpot = vehiculeSpot;
    }

    /**
     * Getter de la plaque d'immatriculation du véhicule.
     * @return la plaque d'immatriculation
     */
    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    /**
     * Setter de la plaque d'immatriculation du véhicule.
     * @param vehicleNumber
     */
    public void setVehicleRegNumber(final String vehicleNumber) {
        this.vehicleRegNumber = vehicleNumber;
    }

    /**
     * Getter du prix du ticket.
     * @return le prix
     */
    public double getPrice() {
        return price;
    }

    /**
     * Setter du prix du ticket.
     * @param priceTicket
     */
    public void setPrice(final double priceTicket) {
        this.price = priceTicket;
    }

    /**
     * Getter de la date d'arrivée.
     * @return la date d'arrivée
     */
    public LocalDateTime getInTime() {
        return inTime;
    }

    /**
     * Date d'entrée du véhicule.
     * @param enterTime
     */
    public void setInTime(final LocalDateTime enterTime) {
        this.inTime = enterTime;
    }

    /**
     * Date de sortie du véhicule.
     * @return date de sortie
     */
    public LocalDateTime getOutTime() {
        return outTime;
    }

    /**
     * Setter de la date de sortie du véhicule du parking.
     * @param quitTime
     */
    public void setOutTime(final LocalDateTime quitTime) {
        this.outTime = quitTime;
    }
}
