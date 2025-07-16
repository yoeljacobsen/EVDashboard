
package com.example.evdashboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private TextView socTextView;
    private TextView rangeTextView;
    private Button copyLogsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        socTextView = findViewById(R.id.soc_text_view);
        rangeTextView = findViewById(R.id.range_text_view);
        copyLogsButton = findViewById(R.id.copy_logs_button);

        // This activity is primarily a placeholder for launching the Android Auto experience.
        // EV data will be displayed on the car's head unit.
        socTextView.setText("Connect to Android Auto");
        rangeTextView.setText("");

        copyLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyLogsToClipboard();
            }
        });
        
	    checkAndRequestFuelPermission();
    }


    // Define a constant for the permission request code.
    // This integer is used to identify the result in the onRequestPermissionsResult callback.
    private static final int REQUEST_CAR_FUEL_PERMISSION = 101;

    @Override
    protected void onResume() {
        super.onResume();
        // Check the permission status every time the activity is resumed.
        // This ensures the UI is updated if the user grants the permission and returns to the app.
        checkAndRequestFuelPermission();
    }

    private void checkAndRequestFuelPermission() {
        // The specific permission string for car fuel data.
        final String permission = "com.google.android.gms.permission.CAR_FUEL";

        // Check if the permission has already been granted.
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, you can proceed to access fuel level data.
            System.out.println("CAR_FUEL permission is already granted.");
        } else {
            // Permission has not been granted, so we must request it from the user.
            System.out.println("CAR_FUEL permission not granted. Requesting...");
            ActivityCompat.requestPermissions(
                this, // The current activity context.
                new String[]{permission}, // The array of permissions to request.
                REQUEST_CAR_FUEL_PERMISSION // The request code to identify this request.
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the result corresponds to our fuel permission request.
        if (requestCode == REQUEST_CAR_FUEL_PERMISSION) {
            // Check if the permission was granted.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // The user granted the permission.
                System.out.println("User granted the CAR_FUEL permission.");
            } else {
                // The user denied the permission.
                // You should handle this case gracefully, perhaps by disabling the feature
                // that requires this permission.
                System.out.println("User denied the CAR_FUEL permission.");
            }
        }
    }


    private void copyLogsToClipboard() {
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
                log.append("\n");
            }

            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("EVDashboard Logs", log.toString());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "Logs copied to clipboard", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error copying logs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
