package com.github.troyhighschoolband.band_uniform_tracking_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.zxing.IntentIntegrator;
import com.google.zxing.IntentResult;

public class StarterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String barcodeNumberName = "barcode_number";
    private long barcodeNumber;

    private Button scanButton, manualButton;

    private EditText manualInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);

        scanButton = (Button) findViewById(R.id.manualButton);
        manualButton = (Button) findViewById(R.id.scanButton);

        manualInput = (EditText) findViewById(R.id.manualInput);
    }

    public void onClick(View v){
        if(v.getId() == R.id.scanButton){
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
        }
        else{
            if(v.getId() == R.id.manualButton){
                barcodeNumber = Long.parseLong(manualInput.getText().toString());
                goToDataActivity();
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid Input Setting. TBH I don't know how you got here.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(result != null){
            barcodeNumber = Long.parseLong(result.getContents());
            goToDataActivity();
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(), "No Scan Data Received", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void goToDataActivity() {
        Intent intent = new Intent(this, DataActivity.class);
        intent.putExtra(barcodeNumberName, barcodeNumber);
        startActivity(intent);
    }
}