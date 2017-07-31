package com.github.troyhighschoolband.band_uniform_tracking_app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import java.io.IOException;

import static com.github.troyhighschoolband.Constants.*;

public class GoogleSignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope("https://www.googleapis.com/auth/spreadsheets"))
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
        if(requestCode == resultCode) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                try {
                    OAuthToken = GoogleAuthUtil.getToken(this, account.getAccount(),
                                                    "https://www.googleapis.com/auth/spreadsheets");
                }
                catch (IOException | GoogleAuthException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult cr) {
        finish();
    }
}
