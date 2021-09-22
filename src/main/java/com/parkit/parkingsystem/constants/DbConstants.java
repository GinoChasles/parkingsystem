package com.parkit.parkingsystem.constants;

/**
 * SQL request for bdd.
 */
public final class DbConstants {
  /**
   * requête SQL pour afficher les places disponibles.
   */
  public static final String GET_NEXT_PARKING_SPOT =
      "select min(PARKING_NUMBER) from parking "
      + "where AVAILABLE = true and TYPE = ?";

  /**
   * requête SQL pour mettre à jour une place de parking utilisée.
   */
  public static final String UPDATE_PARKING_SPOT =
      "update parking set available = ? where PARKING_NUMBER = ?";

  /**
   * requête SQL pour sauvegarder un ticket.
   */
  public static final String SAVE_TICKET =
      "insert into ticket(PARKING_NUMBER, "
      + "VEHICLE_REG_NUMBER, PRICE,"
      + " IN_TIME, OUT_TIME) values(?,?,?,?,?)";

  /**
   * requête SQL pour mettre à jour le prix d'un ticket.
   */
  public static final String UPDATE_TICKET =
      "update ticket set PRICE=?, OUT_TIME=? where ID=?";

  /**
   * requête SQL pour obtenir le ticket.
   */
  public static final String GET_TICKET =
      "select t.PARKING_NUMBER, t.ID, t.PRICE, "
      + "t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,"
      + "parking p where p.parking_number = "
      + "t.parking_number and t.VEHICLE_REG_NUMBER="
      + "? order by t.IN_TIME desc limit 1";

  /**
   * requête SQL pour vérifier si la plaque
   * du véhicule est déjà enregistré dans la bdd.
   */
  public static final String CHECK_REGNUMBER =
      "select count(*) from ticket where"
      + " ticket.VEHICLE_REG_NUMBER = ? ";

  private DbConstants() {
  }
}
