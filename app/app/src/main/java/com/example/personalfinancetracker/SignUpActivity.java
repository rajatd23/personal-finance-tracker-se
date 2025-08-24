package com.example.personalfinancetracker;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class SignUpActivity extends AppCompatActivity {
    private EditText etUsername, etEmail, etPassword;
    private Button btnSignUp;
    private RequestQueue requestQueue;
    private TextView tvreturnloginPage;

    private static final String SIGNUP_URL = Constants.BASE_URL+"signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvreturnloginPage = findViewById(R.id.tvreturnloginPage);
        btnSignUp = findViewById(R.id.btnSignUp);

        requestQueue = Volley.newRequestQueue(this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate(etUsername.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString())){
                    signUpUser();
                }
            }
        });
        tvreturnloginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
    private boolean validate(String username, String email, String password) {
        if(username.isEmpty() ||email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please Fill all The required Fields!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }
    private void signUpUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("username", username);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SIGNUP_URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();

                            if (response.has("message") && response.getString("message").equals("User created successfully")) {
//                                loginUser(username, password); // Automatically log in after signup
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                finish();
                            } else if (response.has("message") && response.getString("message").equals("Username or email already exists")) {
                                Toast.makeText(SignUpActivity.this, "User Already Exist", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Check if the error has a status code
                int statusCode = error.networkResponse != null ? error.networkResponse.statusCode : -1;

                if (statusCode == 400) {
                    Toast.makeText(SignUpActivity.this, "Username or email already exists", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Sign up failed with status code: " + statusCode, Toast.LENGTH_LONG).show();
                }
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void loginUser(String username, String password) {
        // Similar logic as in LoginActivity to log in the user after signup.
        // This method can be implemented similarly to avoid code duplication.
        // For brevity's sake, it's not repeated here.

        // After successful login:
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(MainActivity.KEY_IS_LOGGED_IN, true);
        editor.apply();

        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
        finish();
    }
}
