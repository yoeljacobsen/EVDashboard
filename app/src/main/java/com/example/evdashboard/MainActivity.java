
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
