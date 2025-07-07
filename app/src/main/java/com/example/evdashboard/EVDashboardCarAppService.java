
package com.example.evdashboard;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.car.app.CarAppService;
import androidx.car.app.Session;
import androidx.car.app.validation.HostValidator;

public class EVDashboardCarAppService extends CarAppService {

    private static final String TAG = "EVDashboardCarAppService";

    @NonNull
    @Override
    public HostValidator createHostValidator() {
        Log.d(TAG, "createHostValidator called.");
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR;
    }

    @NonNull
    @Override
    public Session onCreateSession() {
        Log.d(TAG, "onCreateSession called.");
        return new EVDashboardSession();
    }
}
