package com.example.personalfinancetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    static final String PREFS_NAME = "MyPrefsFile";
    static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String CHECK_AUTH_URL = Constants.BASE_URL+"check_auth";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        checkAuthentication();
    }

    private void checkAuthentication() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean isLoggedIn = settings.getBoolean(KEY_IS_LOGGED_IN, false);

        if (isLoggedIn) {
            Toast.makeText(MainActivity.this, "Logging in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        } else {
            goToLoginActivity();
        }
    }

    private void goToLoginActivity() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}
