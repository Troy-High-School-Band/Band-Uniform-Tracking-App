package com.github.troyhighschoolband.band_uniform_tracking_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

import static com.github.troyhighschoolband.Constants.*;

public class GoogleSignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        //Toast.makeText(this, OAuthToken, Toast.LENGTH_SHORT).show();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope("//www.googleapis.com/auth/spreadsheets"))
                .build();

        GoogleApiClient client = new GoogleApiClient.Builder(this)
                                .enableAutoManage(this, this)
                                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                                .build();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(client);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if(result.isSuccess()) {
            new GetOAuthToken().execute(result.getSignInAccount());
        }
        else {
            Status s = result.getStatus();
            if(s.hasResolution()) {
                Log.i("buta: message", "can be resolved");
            }
            switch(s.getStatusCode()) {
                case GoogleSignInStatusCodes.SIGN_IN_REQUIRED:
                    Log.i("buta: message", "Sign in required.");
                    break;
                case GoogleSignInStatusCodes.NETWORK_ERROR:
                    Log.i("buta: message", "Network error.");
                    break;
                case GoogleSignInStatusCodes.SIGN_IN_FAILED:
                    Log.i("buta: message", "Sign in failed.");
                    break;
                case GoogleSignInStatusCodes.ERROR:
                    Log.i("buta: message", "Error.");
                    break;
                default:
                    Log.i("buta:message", "unknown negative status.");
            }
            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult cr) {
        finish();
    }

    private class GetOAuthToken extends AsyncTask<GoogleSignInAccount, Void, String> {
        @Override
        protected String doInBackground(GoogleSignInAccount... params) {
            GoogleSignInAccount account = params[0];
            try {
                String token = GoogleAuthUtil.getToken(GoogleSignInActivity.this, account.getAccount(),
                        "https://www.googleapis.com/auth/spreadsheets");
                Log.i("buta: message", "successful OAuth token retrieval.");
                return token;
            }
            catch (IOException | GoogleAuthException | NullPointerException e) {
                Log.e("buta", "exception", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String token) {
            if (token != null) {
                setResult(Activity.RESULT_OK);
            }
            //OAuthToken = token;
            GoogleSignInActivity.this.finish();
        }
    }
}
