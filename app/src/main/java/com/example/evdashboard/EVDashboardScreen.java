
package com.example.evdashboard;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;
import androidx.car.app.model.Action;
import androidx.car.app.hardware.CarHardwareManager;
import androidx.car.app.hardware.info.CarInfo;
import androidx.car.app.hardware.info.EnergyLevel;
import androidx.car.app.hardware.common.CarValue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EVDashboardScreen extends Screen {

    private final CarInfo mCarInfo;
    private final Executor mExecutor = Executors.newSingleThreadExecutor();

    private String mBatteryLevel = "N/A";

    public EVDashboardScreen(CarContext carContext) {
        super(carContext);
        CarHardwareManager carHardwareManager = carContext.getCarService(CarHardwareManager.class);
        mCarInfo = carHardwareManager.getCarInfo();
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        mCarInfo.addEnergyLevelListener(mExecutor, energyLevel -> {
            CarValue<Float> batteryPercent = energyLevel.getBatteryPercent();
            if (batteryPercent.getStatus() == CarValue.STATUS_SUCCESS) {
                mBatteryLevel = String.format("%.1f%%", batteryPercent.getValue());
            } else {
                mBatteryLevel = "N/A";
            }
            invalidate(); // Redraw the screen with updated data
        });

        Row socRow = new Row.Builder().setTitle("State of Charge").addText(mBatteryLevel).build();

        Pane pane = new Pane.Builder()
                .addRow(socRow)
                .build();

        return new PaneTemplate.Builder(pane)
                .setHeaderAction(getHeaderAction())
                .setTitle("EV Dashboard")
                .build();
    }

    private Action getHeaderAction() {
        return Action.APP_ICON;
    }
}
