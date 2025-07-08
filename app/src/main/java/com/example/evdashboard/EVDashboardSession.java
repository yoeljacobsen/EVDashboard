
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
    private boolean mIsMockData = false;
    private int mCarAppApiLevel = 0;
    private int mPhoneApiLevel = android.os.Build.VERSION.SDK_INT;
    private String mAppVersion = "";

    public EVDashboardSession() {
        Log.d(TAG, "EVDashboardSession constructor called.");
        getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                try {
                    mAppVersion = getCarContext().getPackageManager().getPackageInfo(getCarContext().getPackageName(), 0).versionName;
                } catch (android.content.pm.PackageManager.NameNotFoundException e) {
                    mAppVersion = "N/A";
                }
                Log.d(TAG, "DefaultLifecycleObserver onCreate called.");
                Log.d(TAG, "onCreate lifecycle event.");
                mIsConnectedToCar = true;
                try {
                    CarHardwareManager carHardwareManager = getCarContext().getCarService(CarHardwareManager.class);
                    mCarInfo = carHardwareManager.getCarInfo();
                    Log.d(TAG, "CarInfo retrieved: " + (mCarInfo != null ? mCarInfo.toString() : "null"));
                    mIsMockData = false;
                    mCarAppApiLevel = getCarContext().getCarAppApiLevel();
                    Log.d(TAG, "Car App API Level: " + mCarAppApiLevel);

                    if (mCarInfo instanceof androidx.car.app.hardware.info.ProjectedCarInfo) {
                        Log.w(TAG, "Running on Android Auto (ProjectedCarInfo). Using mock data for EnergyLevel.");
                        mIsMockData = true;
                        MockCarInfoProvider mockProvider = new MockCarInfoProvider();
                        EnergyLevel mockEnergyLevel = mockProvider.getMockEnergyLevel();
                        mSoc = String.format("%.1f%%", mockEnergyLevel.getBatteryPercent().getValue()); // Use battery percent from mock for SoC
                        mRange = String.format("%.1f km", mockEnergyLevel.getRangeRemainingMeters().getValue() / 1000.0f);
                        Log.d(TAG, "Mock SoC: " + mSoc + ", Mock Range: " + mRange);
                    } else if (mCarInfo != null) {
                        mEnergyLevelListener = new OnCarDataAvailableListener<EnergyLevel>() {
                            @Override
                            public void onCarDataAvailable(@NonNull EnergyLevel energyLevel) {
                                Log.d(TAG, "onCarDataAvailable: EnergyLevel received. BatteryPercent: " + energyLevel.getBatteryPercent().getStatus() + ", RangeRemainingMeters: " + energyLevel.getRangeRemainingMeters().getStatus());
                                if (energyLevel.getBatteryPercent().getStatus() == androidx.car.app.hardware.common.CarValue.STATUS_SUCCESS) {
                                    mSoc = String.format("%.1f%%", energyLevel.getBatteryPercent().getValue());
                                    Log.d(TAG, "SoC (Battery): " + mSoc);
                                } else {
                                    mSoc = "--%";
                                    Log.d(TAG, "SoC not available. Status: " + energyLevel.getBatteryPercent().getStatus());
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
                    } else {
                        Log.w(TAG, "CarInfo is null. EnergyLevel listener not added.");
                    }
                } catch (IllegalStateException e) {
                    Log.e(TAG, "CarHardwareManager not available: " + e.getMessage() + ". Using mock data.");
                    mIsMockData = true;
                    // Use mock data when CarHardwareManager is not available
                    MockCarInfoProvider mockProvider = new MockCarInfoProvider();
                    EnergyLevel mockEnergyLevel = mockProvider.getMockEnergyLevel();
                    mSoc = String.format("%.1f%%", mockEnergyLevel.getBatteryPercent().getValue());
                    mRange = String.format("%.1f km", mockEnergyLevel.getRangeRemainingMeters().getValue() / 1000.0f);
                    Log.d(TAG, "Mock SoC: " + mSoc + ", Mock Range: " + mRange);
                }
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

    public boolean isMockData() {
        return mIsMockData;
    }

    public int getCarAppApiLevel() {
        return mCarAppApiLevel;
    }

    public int getPhoneApiLevel() {
        return mPhoneApiLevel;
    }

    public String getAppVersion() {
        return mAppVersion;
    }

    
}
