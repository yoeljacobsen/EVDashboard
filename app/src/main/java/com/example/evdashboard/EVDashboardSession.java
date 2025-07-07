
package com.example.evdashboard;

import android.content.Intent;
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

    private CarInfo mCarInfo;
    private OnCarDataAvailableListener<EnergyLevel> mEnergyLevelListener;
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private String mSoc = "--%";
    private String mRange = "-- km";
    private EVDashboardScreen mEVDashboardScreen;

    public EVDashboardSession() {
        getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                CarHardwareManager carHardwareManager = getCarContext().getCarService(CarHardwareManager.class);
                mCarInfo = carHardwareManager.getCarInfo();

                mEnergyLevelListener = new OnCarDataAvailableListener<EnergyLevel>() {
                    @Override
                    public void onCarDataAvailable(@NonNull EnergyLevel energyLevel) {
                        mSoc = String.format("%.1f%%", energyLevel.getFuelPercent().getValue());
                        mRange = String.format("%.1f km", energyLevel.getRangeRemainingMeters().getValue() / 1000.0f);
                        if (mEVDashboardScreen != null) {
                            mEVDashboardScreen.invalidate();
                        }
                    }
                };
                mCarInfo.addEnergyLevelListener(mExecutor, mEnergyLevelListener);
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                if (mCarInfo != null) {
                    mCarInfo.removeEnergyLevelListener(mEnergyLevelListener);
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
}
