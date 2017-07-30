package com.github.troyhighschoolband.band_uniform_tracking_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
    }

    /**
     *
     * @param barcodeNumber The number on the barcode
     * @param type
     * @param itemNumber
     * @param size
     * @return If storing worked
     */
    private boolean storeData(long barcodeNumber, ItemType type, int itemNumber, String size) {
        return false;
    }
}
