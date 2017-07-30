package com.github.troyhighschoolband.band_uniform_tracking_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ScanActivity extends AppCompatActivity {

    public static final String barcodeNumberName = "barcode_number";

    private EditText manualInput;

    private Button submit;

    private TextView errorLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        manualInput = (EditText) findViewById(R.id.manualInput);
        submit = (Button) findViewById(R.id.submitButton);
        errorLog = (TextView) findViewById(R.id.errorLog);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    goToDataActivity(Long.parseLong(manualInput.getText().toString()));
                }
                catch(NumberFormatException nfe) {
                    goToDataActivity(-1L);
                }
                /*catch(Exception e) {
                    errorLog.setText("");
                    for(StackTraceElement line:e.getStackTrace()) {
                        errorLog.setText(errorLog.getText().toString()+line.toString()+'\n');
                    }
                }*/
            }
        });
    }

    private void goToDataActivity(long barcodeNumber) {
        Intent intent = new Intent(this, DataActivity.class);
        intent.putExtra(barcodeNumberName, barcodeNumber);
        startActivity(intent);
    }
}
