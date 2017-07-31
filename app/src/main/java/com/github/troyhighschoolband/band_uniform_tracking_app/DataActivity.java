package com.github.troyhighschoolband.band_uniform_tracking_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DataActivity extends AppCompatActivity {

    private EditText barcodeNumber;
    private EditText name;
    private EditText itemType;
    private EditText itemSize;
    private EditText itemNumber;

    private Button backButton;
    private Button submitButton;

    private TextView errorLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        try {
            Intent intent = getIntent();
            long bn = intent.getLongExtra(StarterActivity.barcodeNumberName, -1);

            barcodeNumber = (EditText) findViewById(R.id.BarcodeNumberInput);
            name = (EditText) findViewById(R.id.ItemUserInput);
            itemType = (EditText) findViewById(R.id.ItemTypeInput);
            itemSize = (EditText) findViewById(R.id.ItemSizeInput);
            itemNumber = (EditText) findViewById(R.id.ItemNumberInput);
            backButton = (Button) findViewById(R.id.backButton);
            submitButton = (Button) findViewById(R.id.submitButton);
            errorLog = (TextView) findViewById(R.id.errorLog);

            if (bn != -1) {
                barcodeNumber.setText(String.valueOf(bn));
            }

            Data loaded = loadData(bn);
            if (loaded.worked) {
                name.setText(loaded.name);
                itemType.setText(loaded.itemType);
                itemSize.setText(loaded.itemSize);
                itemNumber.setText(loaded.itemNumber);
            }

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        storeData(Long.parseLong(barcodeNumber.getText().toString()),
                                  name.getText().toString(), itemType.getText().toString(),
                                  Integer.parseInt(itemNumber.getText().toString()),
                                  itemSize.getText().toString());
                    } catch (NumberFormatException nfe) {
                        errorLog.setText("Invalid number format.");
                    }
                }
            });
        }
        catch(Exception e) {
            errorLog.setText("");
            for(StackTraceElement line:e.getStackTrace()) {
                errorLog.setText(errorLog.getText().toString()+line.toString()+'\n');
            }
        }
    }

    /**
     * Stores the given data into the database
     *
     * @return If storing worked, true. Otherwise, false.
     */
    private boolean storeData(long barcodeNumber, String name, String itemType, int itemNumber,
                              String itemSize) {
        return false;
    }

    private Data loadData(long barcodeNumber) {
        return new Data();
    }


    public class Data {
        public boolean worked;
        public long barcodeNumber;
        public String name;
        public String itemType;
        public int itemNumber;
        public String itemSize;

        public Data() {
            this(false, 0, null, null, 0, null);
        }

        public Data(long bn, String n, String it, int in, String is) {
            this(true, bn, n, it, in, is);
        }

        public Data(boolean w, long bn, String n, String it, int in, String is) {
            worked = w;
            barcodeNumber = bn;
            name = n;
            itemType = it;
            itemNumber = in;
            itemSize = is;
        }
    }
}
