package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

public class ParkingSpot {
    /**
     * Numéro de place.
     */
    private int number;

    /**
     * Définis le type de véhicule.
     */
    private ParkingType parkingType;

    /**
     * Boolean qui défini si la place est disponible.
     */
    private boolean isAvailable;

    /**
     * Public constructor.
     * @param placeNumber
     * @param vehiculeType
     * @param available
     */
    public ParkingSpot(final int placeNumber,
                       final ParkingType vehiculeType,
                       final boolean available) {
        this.number = placeNumber;
        this.parkingType = vehiculeType;
        this.isAvailable = available;
    }

    /**
     * Getter du numéro de la place.
     * @return number
     */
    public int getId() {
        return number;
    }

    /**
     * Setter du numéro de la place.
     * @param id
     */
    public void setId(final int id) {
        this.number = id;
    }

    /**
     * Getter du type de place.
     * @return moto ou voiture
     */
    public ParkingType getParkingType() {
        return parkingType;
    }

    /**
     * Setter du type de véhicule.
     * @param vehiculeType
     */
    public void setParkingType(final ParkingType vehiculeType) {
        this.parkingType = vehiculeType;
    }

    /**
     * getter de la disponibilité de la place.
     * @return si la place est disponible ou non.
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * Setter de la disponibilité de la place.
     * @param available
     */
    public void setAvailable(final boolean available) {
        isAvailable = available;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParkingSpot that = (ParkingSpot) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return number;
    }
}
