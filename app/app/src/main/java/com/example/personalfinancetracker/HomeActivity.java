package com.example.personalfinancetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class HomeActivity extends AppCompatActivity {

    private Button btnTransactions, btnProfile, btnCalender, btnLogout, btnDashboard;
    private RequestQueue requestQueue;
    private static final String LOGOUT_URL = Constants.BASE_URL+"logout";
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome); // Initialize the TextView for welcome message
        btnTransactions = findViewById(R.id.btnTransactions);
        btnProfile = findViewById(R.id.btnProfile);
        btnCalender = findViewById(R.id.btnButton3);
        btnLogout = findViewById(R.id.btnLogout);

        requestQueue = Volley.newRequestQueue(this);

        // Retrieve username from SharedPreferences and set welcome message
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        String username = settings.getString("username", "User"); // Default to "User" if not found
        tvWelcome.setText("Welcome " + username); // Set the welcome message

        btnDashboard = findViewById(R.id.btnDashboard);

        // Set up other button click listeners

        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, DashboardActivity.class));
            }
        });

        btnTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, TransactionsActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });
        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, RecurringTransactionActivity.class));
            }
        });
    }

    private void logoutUser() {
        // Make a request to the server to log out
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, LOGOUT_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (message.equals("Logged out successfully")) {
                                // Clear login state from SharedPreferences
                                SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean(MainActivity.KEY_IS_LOGGED_IN, false);
                                editor.remove("username"); // Clear stored username
                                editor.apply();

                                // Navigate back to LoginActivity
                                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                                startActivity(intent);
                                finish(); // Close HomeActivity
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "Error parsing logout response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Logout failed";
                        if (error.networkResponse != null) {
                            errorMessage += ": " + error.networkResponse.statusCode;
                        } else if (error.getMessage() != null) {
                            errorMessage += ": " + error.getMessage();
                        }
                        Toast.makeText(HomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers; // Add any necessary headers here
            }
        };

        requestQueue.add(jsonObjectRequest); // Add request to queue
    }
}
