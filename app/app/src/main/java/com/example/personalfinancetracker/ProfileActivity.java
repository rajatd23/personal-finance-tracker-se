package com.example.personalfinancetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import android.content.SharedPreferences;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail, tvMonthlyBudget, tvTargetExpense;
    private EditText etUsername, etEmail, etMonthlyBudget, etTargetExpense;
    private Button btnEdit, btnSave;
    private RequestQueue requestQueue;
    private static final String PROFILE_URL = Constants.BASE_URL + "get_user_profile/";
    private static final String UPDATE_PROFILE_URL = Constants.BASE_URL + "update_user_profile/";
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvMonthlyBudget = findViewById(R.id.tvMonthlyBudget);
        tvTargetExpense = findViewById(R.id.tvTargetExpense);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etMonthlyBudget = findViewById(R.id.etMonthlyBudget);
        etTargetExpense = findViewById(R.id.etTargetExpense);

        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);

        requestQueue = Volley.newRequestQueue(this);

        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        userId = settings.getString("user_id", "");

        loadUserProfile();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToEditMode();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void loadUserProfile() {
        if (!userId.isEmpty()) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, PROFILE_URL + userId, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                updateUIWithProfileData(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ProfileActivity.this, "Error parsing profile data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                        }
                    });

            requestQueue.add(jsonObjectRequest);
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUIWithProfileData(JSONObject data) throws JSONException {
        String username = data.getString("username");
        String email = data.getString("email");
        double monthlyBudget = data.getDouble("monthly_budget");
        double targetExpense = data.getDouble("target_expense");

        tvUsername.setText("Username: " + username);
        tvEmail.setText("Email: " + email);
        tvMonthlyBudget.setText("Monthly Income: $" + monthlyBudget);
        tvTargetExpense.setText("Target Expense: $" + targetExpense);

        etUsername.setText(username);
        etEmail.setText(email);
        etMonthlyBudget.setText(String.valueOf(monthlyBudget));
        etTargetExpense.setText(String.valueOf(targetExpense));
    }

    private void switchToEditMode() {
        tvUsername.setVisibility(View.GONE);
        tvEmail.setVisibility(View.GONE);
        tvMonthlyBudget.setVisibility(View.GONE);
        tvTargetExpense.setVisibility(View.GONE);

        etUsername.setVisibility(View.VISIBLE);
        etEmail.setVisibility(View.VISIBLE);
        etMonthlyBudget.setVisibility(View.VISIBLE);
        etTargetExpense.setVisibility(View.VISIBLE);

        btnEdit.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
    }

    private void saveUserProfile() {
        JSONObject updatedProfile = new JSONObject();
        try {
            updatedProfile.put("username", etUsername.getText().toString());
            updatedProfile.put("email", etEmail.getText().toString());
            updatedProfile.put("monthly_budget", Double.parseDouble(etMonthlyBudget.getText().toString()));
            updatedProfile.put("target_expense", Double.parseDouble(etTargetExpense.getText().toString()));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparing data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, UPDATE_PROFILE_URL + userId, updatedProfile,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            loadUserProfile(); // Reload the profile data
                            switchToViewMode();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void switchToViewMode() {
        tvUsername.setVisibility(View.VISIBLE);
        tvEmail.setVisibility(View.VISIBLE);
        tvMonthlyBudget.setVisibility(View.VISIBLE);
        tvTargetExpense.setVisibility(View.VISIBLE);

        etUsername.setVisibility(View.GONE);
        etEmail.setVisibility(View.GONE);
        etMonthlyBudget.setVisibility(View.GONE);
        etTargetExpense.setVisibility(View.GONE);

        btnEdit.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.GONE);
    }
}
