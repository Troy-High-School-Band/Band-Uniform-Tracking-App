package com.github.troyhighschoolband.band_uniform_tracking_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class DataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        Intent intent = getIntent();
        long bn = intent.getLongExtra(ScanActivity.barcodeNumberName, -1);

        TextView barcodeNumber = (TextView) findViewById(R.id.BarcodeNumberInput);
        TextView name = (TextView) findViewById(R.id.ItemUserInput);
        TextView itemType = (TextView) findViewById(R.id.ItemTypeInput);
        TextView itemSize = (TextView) findViewById(R.id.ItemSizeInput);
        TextView itemNumber = (TextView) findViewById(R.id.ItemNumberInput);
        Button backButton = (Button) findViewById(R.id.backButton);
        Button submitButton = (Button) findViewById(R.id.submitButton);

        barcodeNumber.setText(String.valueOf(bn));
        Data loaded = loadData(bn);
        if (loaded.worked) {
            name.setText(loaded.name);
            itemType.setText(loaded.itemType);
            itemSize.setText(loaded.itemSize);
            itemNumber.setText(loaded.itemNumber);
        }
    }

    /**
     * Stores the given data into the database
     *
     * @return If storing worked
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
