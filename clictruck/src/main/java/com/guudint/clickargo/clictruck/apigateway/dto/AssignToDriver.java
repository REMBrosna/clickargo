package com.guudint.clickargo.clictruck.apigateway.dto;
/**
 * @author Brosna
 * @version 2.0
 * @since 1/6/2025
 */
public class AssignToDriver {
    private String driverId;
    private String truckId;

    public AssignToDriver() {
    }

    public AssignToDriver(String driverId, String truckId) {
        this.driverId = driverId;
        this.truckId = truckId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getTruckId() {
        return truckId;
    }

    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }
}
