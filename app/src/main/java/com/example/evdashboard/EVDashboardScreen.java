package com.example.evdashboard;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.Pane;
import androidx.car.app.model.PaneTemplate;
import androidx.car.app.model.Row;
import androidx.car.app.model.Template;
import androidx.car.app.model.Action;


public class EVDashboardScreen extends Screen {

    private EVDashboardSession mSession;

    public EVDashboardScreen(CarContext carContext, EVDashboardSession session) {
        super(carContext);
        mSession = session;
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        String soc = mSession.getSoc();
        String range = mSession.getRange();

        Row socRow = new Row.Builder().setTitle("SoC").addText(soc).build();
        Row rangeRow = new Row.Builder().setTitle("Range").addText(range).build();
        Row mockDataRow = new Row.Builder().setTitle("Mock Data").addText(mSession.isMockData() ? "Yes" : "No").build();
        Row carApiLevelRow = new Row.Builder().setTitle("Car API Level").addText(String.valueOf(mSession.getCarAppApiLevel())).build();
        Row phoneApiLevelRow = new Row.Builder().setTitle("Phone API Level").addText(String.valueOf(mSession.getPhoneApiLevel())).build();
        Row appVersionRow = new Row.Builder().setTitle("App Version").addText(mSession.getAppVersion()).build();

        Pane pane = new Pane.Builder()
                .addRow(socRow)
                .addRow(rangeRow)
                .addRow(mockDataRow)
                .addRow(carApiLevelRow)
                .addRow(phoneApiLevelRow)
                .addRow(appVersionRow)
                .addAction(
                        new Action.Builder()
                                .setTitle("Refresh")
                                .setOnClickListener(this::invalidate)
                                .build())
                .build();

        return new PaneTemplate.Builder(pane)
                .setHeaderAction(Action.APP_ICON)
                .build();
    }
}
