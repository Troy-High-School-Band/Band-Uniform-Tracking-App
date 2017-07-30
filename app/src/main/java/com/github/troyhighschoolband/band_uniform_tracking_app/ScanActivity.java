package com.github.troyhighschoolband.band_uniform_tracking_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        final TextView manual = (TextView) findViewById(R.id.manualInput);
        Button submit = (Button) findViewById(R.id.submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDataActivity(Long.parseLong(manual.getText().toString()));
            }
        });
    }

    private void goToDataActivity(long barcodeNumber) {
        Intent intent = new Intent(this, DataActivity.class);
        intent.putExtra("barcode_number", barcodeNumber);
        startActivity(intent);
    }
}
