package com.github.troyhighschoolband.band_uniform_tracking_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;

import static com.github.troyhighschoolband.Constants.*;

public class DataActivity extends AppCompatActivity {

    public static final JSONParser parser = new JSONParser();

    private EditText barcodeNumber;
    private EditText name;
    private EditText itemType;
    private EditText itemSize;
    private EditText itemNumber;

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
            Button backButton = (Button) findViewById(R.id.backButton);
            Button submitButton = (Button) findViewById(R.id.submitButton);
            errorLog = (TextView) findViewById(R.id.errorLog);

            if (bn != -1) {
                barcodeNumber.setText(String.valueOf(bn));
                loadData(bn);
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
                    }
                    catch (NumberFormatException nfe) {
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
            throw e;
        }
    }

    private void storeData(long barcodeNumber, String name, String itemType, int itemNumber,
                              String itemSize) {
        Data data = new Data(barcodeNumber, name, itemType, itemNumber, itemSize);
        new StoreTask().execute(data);
    }

    private void loadData(long barcodeNumber) {
        new LoadTask().execute(barcodeNumber);
    }


    private class Data {
        private boolean worked;
        private long barcodeNumber;
        private String name;
        private String itemType;
        private int itemNumber;
        private String itemSize;

        private Data() {
            this(false, -1, null, null, -1, null);
        }
        private Data(boolean w) {
            this(w, -1, null, null, -1, null);
        }
        private Data(long bn, String n, String it, int in, String is) {
            this(true, bn, n, it, in, is);
        }
        private Data(boolean w, long bn, String n, String it, int in, String is) {
            worked = w;
            barcodeNumber = bn;
            name = n;
            itemType = it;
            itemNumber = in;
            itemSize = is;
        }
    }

    private class StoreTask extends AsyncTask<Data, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            errorLog.setText("Storing data...");
        }

        @Override
        protected Boolean doInBackground(Data... params) {
            try {
                Data data = params[0];
                long barcodeNumber = data.barcodeNumber;
                URL url = new URL("https", "sheets.googleapis.com", "/v4/spreadsheets/" +
                        spreadsheetId + "/values/A3:A?majorDimension=COLUMNS&key=" + apiKey);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                if (conn.getResponseCode() != 200) {
                    throw new Exception("Unwanted response code: "+conn.getResponseCode());
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new BufferedInputStream(conn.getInputStream())));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                JSONArray values = (JSONArray) ((JSONObject) parser.parse(builder.toString())
                ).get("values");
                Object[] col = ((JSONArray) values.toArray()[0]).toArray();
                int lineNumber = -1;
                for (int i = 0; i < col.length; i++) {
                    if (Long.parseLong((String) col[i]) == barcodeNumber) {
                        lineNumber = i + 3;
                    }
                }
                if (lineNumber == -1) {
                    lineNumber = col.length + 3;
                }
                JSONObject send = new JSONObject();
                send.put("range", "'Barcode Scanner'!A"+lineNumber);
                send.put("majorDimension", "ROWS");
                JSONArray row = new JSONArray();
                row.add(String.valueOf(barcodeNumber));
                row.add(String.valueOf(System.currentTimeMillis()/1000));
                row.add(data.name);
                row.add(data.itemType);
                row.add(String.valueOf(data.itemNumber));
                row.add(data.itemSize);
                send.put("values", row);
                url = new URI("https", "sheets.googleapis.com", "/v4/spreadsheets/" + spreadsheetId +
                              "/values/'Barcode Scanner'!A" + lineNumber +
                              "?valueInputOption=USER_ENTERED&key=" + apiKey).toURL();
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authentication", "Basic "+OAuthToken);
                conn.setRequestMethod("PUT");
                DataOutputStream put = new DataOutputStream(conn.getOutputStream());
                put.writeBytes(send.toJSONString());
                if(conn.getResponseCode()/100 != 2) {
                    reader = new BufferedReader(new InputStreamReader(
                            new BufferedInputStream(conn.getErrorStream())));
                    builder = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    throw new Exception(builder.toString() + "\t\t" +
                                        String.valueOf(conn.getResponseCode()));
                }
                return true;
            }
            catch(Exception e) {
                Log.e("buta", "exception", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                errorLog.setText("Successfully stored");
            }
            else {
                errorLog.setText("An error occurred. Please try again later.");
            }
        }
    }

    private class LoadTask extends AsyncTask<Long, Void, Data> {
        @Override
        protected void onPreExecute() {
            errorLog.setText("Loading...");
        }

        @Override
        protected Data doInBackground(Long... params) {
            try {
                long barcodeNumber = params[0];
                URL url = new URL("https", "sheets.googleapis.com", "/v4/spreadsheets/" +
                        spreadsheetId + "/values/A3:A?majorDimension=COLUMNS&key=" + apiKey);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                if(conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            new BufferedInputStream(conn.getInputStream())));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    JSONArray values = (JSONArray) ((JSONObject) parser.parse(builder.toString())
                    ).get("values");
                    Object[] col = ((JSONArray)values.toArray()[0]).toArray();
                    int lineNumber = -1;
                    for(int i=0; i<col.length; i++) {
                        if(Long.parseLong((String)col[i])==barcodeNumber) {
                            lineNumber = i + 3;
                        }
                    }
                    if(lineNumber == -1) {
                        return new Data(true);
                    }
                    else {
                        //the barcode was found
                        url = new URL("https", "sheets.googleapis.com", "/v4/spreadsheets/" +
                                spreadsheetId + "/values/A" + lineNumber + ":" + lineNumber +
                                "?key="+apiKey);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.connect();
                        if(conn.getResponseCode() == 200) {
                            reader = new BufferedReader(new InputStreamReader(
                                    new BufferedInputStream(conn.getInputStream())));
                            builder = new StringBuilder();
                            while ((line = reader.readLine()) != null) {
                                builder.append(line);
                            }
                            try {
                                values = (JSONArray) (((JSONObject) parser.parse(builder.toString()))
                                                      .get("values"));
                            }
                            catch(ClassCastException cce) {
                                throw new Exception(builder.toString());
                            }
                            Object[] data = ((JSONArray)values.toArray()[0]).toArray();
                            if (Long.parseLong((String)data[0]) != barcodeNumber) {
                                throw new AssertionError("Incorrect barcode returned from the Google Sheets");
                            }
                            return new Data(Long.parseLong((String) data[0]), (String) data[2],
                                    (String) data[3], Integer.parseInt((String) data[4]),
                                    (String) data[5]);
                        }
                    }
                }
                else {
                    Log.d("buta: http_response", String.valueOf(conn.getResponseCode()));
                }
            }
            catch(IndexOutOfBoundsException ioobe) {
                Log.e("buta", "exception", ioobe);
                throw new IllegalArgumentException(ioobe);
            }
            catch(Exception e) {
                Log.e("buta", "exception", e);
            }
            return new Data();
        }

        @Override
        protected void onPostExecute(Data data) {
            if (data.worked && data.barcodeNumber != -1) {
                name.setText(data.name);
                itemType.setText(data.itemType);
                itemSize.setText(data.itemSize);
                itemNumber.setText(String.valueOf(data.itemNumber));
                errorLog.setText("Success loading from database.");
            }
            else {
                if(data.worked) {
                    errorLog.setText("This barcode has not been scanned.");
                }
                else {
                    errorLog.setText("An unknown error occurred while loading from the database.");
                }
            }
        }
    }
}
