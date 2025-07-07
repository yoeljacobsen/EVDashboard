
package com.example.evdashboard;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView socTextView;
    private TextView rangeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        socTextView = findViewById(R.id.soc_text_view);
        rangeTextView = findViewById(R.id.range_text_view);

        // This activity is primarily a placeholder for launching the Android Auto experience.
        // EV data will be displayed on the car's head unit.
        socTextView.setText("Connect to Android Auto");
        rangeTextView.setText("");
    }
}
