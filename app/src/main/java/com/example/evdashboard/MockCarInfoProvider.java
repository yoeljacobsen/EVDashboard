package com.example.evdashboard;

import androidx.annotation.NonNull;
import androidx.car.app.hardware.info.EnergyLevel;
import androidx.car.app.hardware.common.CarValue;

/**
 * A mock implementation of CarInfo and EnergyLevel for testing purposes when
 * CarHardwareManager is not available.
 */
public class MockCarInfoProvider {

    private float mMockFuelPercent = 50.0f; // Default mock SoC
    private float mMockBatteryPercent = 50.0f; // Default mock Battery SoC
    private float mMockRangeMeters = 200000.0f; // Default mock Range (200 km)

    public EnergyLevel getMockEnergyLevel() {
        return new EnergyLevel.Builder()
                .setFuelPercent(new CarValue<>(mMockFuelPercent, System.currentTimeMillis(), 0))
                .setBatteryPercent(new CarValue<>(mMockBatteryPercent, System.currentTimeMillis(), 0))
                .setRangeRemainingMeters(new CarValue<>(mMockRangeMeters, System.currentTimeMillis(), 0))
                .build();
    }

    /**
     * Sets the mock fuel percentage.
     *
     * @param fuelPercent The fuel percentage to set (0-100).
     */
    public void setMockFuelPercent(float fuelPercent) {
        mMockFuelPercent = fuelPercent;
    }

    /**
     * Sets the mock battery percentage.
     *
     * @param batteryPercent The battery percentage to set (0-100).
     */
    public void setMockBatteryPercent(float batteryPercent) {
        mMockBatteryPercent = batteryPercent;
    }

    /**
     * Sets the mock range in kilometers.
     *
     * @param rangeKm The range in kilometers to set.
     */
    public void setMockRangeKm(float rangeKm) {
        mMockRangeMeters = rangeKm * 1000.0f;
    }
}
