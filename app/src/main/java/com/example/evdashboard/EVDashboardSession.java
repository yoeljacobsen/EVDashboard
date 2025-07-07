
package com.example.evdashboard;

import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.car.app.Screen;
import androidx.car.app.Session;
import androidx.car.app.hardware.CarHardwareManager;
import androidx.car.app.hardware.info.CarInfo;
import androidx.car.app.hardware.info.EnergyLevel;
import androidx.car.app.hardware.common.OnCarDataAvailableListener;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EVDashboardSession extends Session {

    private static final String TAG = "EVDashboardSession";

    private CarInfo mCarInfo;
    private OnCarDataAvailableListener<EnergyLevel> mEnergyLevelListener;
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private String mSoc = "--%";
    private String mRange = "-- km";
    private EVDashboardScreen mEVDashboardScreen;
    private boolean mIsConnectedToCar = false;

    public EVDashboardSession() {
        Log.d(TAG, "EVDashboardSession constructor called.");
        getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                Log.d(TAG, "onCreate lifecycle event.");
                mIsConnectedToCar = true;
                CarHardwareManager carHardwareManager = getCarContext().getCarService(CarHardwareManager.class);
                mCarInfo = carHardwareManager.getCarInfo();

                mEnergyLevelListener = new OnCarDataAvailableListener<EnergyLevel>() {
                    @Override
                    public void onCarDataAvailable(@NonNull EnergyLevel energyLevel) {
                        Log.d(TAG, "onCarDataAvailable: EnergyLevel received.");
                        if (energyLevel.getFuelPercent().getStatus() == androidx.car.app.hardware.common.CarValue.STATUS_SUCCESS) {
                            mSoc = String.format("%.1f%%", energyLevel.getFuelPercent().getValue());
                            Log.d(TAG, "SoC: " + mSoc);
                        } else {
                            mSoc = "--%";
                            Log.d(TAG, "SoC not available. Status: " + energyLevel.getFuelPercent().getStatus());
                        }

                        if (energyLevel.getRangeRemainingMeters().getStatus() == androidx.car.app.hardware.common.CarValue.STATUS_SUCCESS) {
                            mRange = String.format("%.1f km", energyLevel.getRangeRemainingMeters().getValue() / 1000.0f);
                            Log.d(TAG, "Range: " + mRange);
                        } else {
                            mRange = "-- km";
                            Log.d(TAG, "Range not available. Status: " + energyLevel.getRangeRemainingMeters().getStatus());
                        }

                        if (mEVDashboardScreen != null) {
                            mEVDashboardScreen.invalidate();
                        }
                    }
                };
                mCarInfo.addEnergyLevelListener(mExecutor, mEnergyLevelListener);
                Log.d(TAG, "EnergyLevel listener added.");
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                Log.d(TAG, "onDestroy lifecycle event.");
                mIsConnectedToCar = false;
                if (mCarInfo != null) {
                    mCarInfo.removeEnergyLevelListener(mEnergyLevelListener);
                    Log.d(TAG, "EnergyLevel listener removed.");
                }
            }
        });
    }

    @NonNull
    @Override
    public Screen onCreateScreen(@NonNull Intent intent) {
        mEVDashboardScreen = new EVDashboardScreen(getCarContext(), this);
        return mEVDashboardScreen;
    }

    public String getSoc() {
        return mSoc;
    }

    public String getRange() {
        return mRange;
    }

    public boolean isConnectedToCar() {
        return mIsConnectedToCar;
    }
}
