package com.example.personalfinancetracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText etDate, etDescription, etAmount, etGroupSize;
    private RadioGroup rgTransactionType;
    private Spinner spinnerCategory;
    private Button btnSubmit;
    private Calendar myCalendar;
    private static final String ADD_TRANSACTION_URL = Constants.BASE_URL+"add_transaction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);
        etAmount = findViewById(R.id.etAmount);
        etGroupSize = findViewById(R.id.etGroupSize);
        rgTransactionType = findViewById(R.id.rgTransactionType);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSubmit = findViewById(R.id.btnSubmit);

        myCalendar = Calendar.getInstance();

        // Set up date picker
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        etDate.setOnClickListener(v -> new DatePickerDialog(AddTransactionActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        // Set up category spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set up radio group listener
        rgTransactionType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbGroup) {
                etGroupSize.setVisibility(View.VISIBLE);
            } else {
                etGroupSize.setVisibility(View.GONE);
            }
        });

        btnSubmit.setOnClickListener(v -> submitTransaction());
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void submitTransaction() {
        String date = etDate.getText().toString();
        String description = etDescription.getText().toString();
        String amount = etAmount.getText().toString();
        String type = rgTransactionType.getCheckedRadioButtonId() == R.id.rbPersonal ? "Personal" : "Group";
        String category = spinnerCategory.getSelectedItem().toString();
        String groupSize = etGroupSize.getText().toString();

        if (date.isEmpty() || description.isEmpty() || amount.isEmpty() ||
                (type.equals("Group") && groupSize.isEmpty())) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve user_id from SharedPreferences
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        String user_id = settings.getString("user_id", null);

        if (user_id == null) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create JSON object with transaction data
        JSONObject transactionData = new JSONObject();
        try {
            transactionData.put("date", date);
            transactionData.put("description", description);
            transactionData.put("amount", amount);
            transactionData.put("type", type);
            transactionData.put("category", category);
            transactionData.put("user_id", user_id);  // Add user_id to transaction data

            if (type.equals("Group")) {
                transactionData.put("group_size", groupSize);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating transaction data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                ADD_TRANSACTION_URL,
                transactionData,
                response -> {
                    Toast.makeText(AddTransactionActivity.this, "Transaction added successfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    startActivity(new Intent(AddTransactionActivity.this, TransactionsActivity.class));
                    finish();
                },
                error -> {
                    String errorMessage = "Error submitting transaction";
                    if (error.networkResponse != null) {
                        errorMessage += ": " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(AddTransactionActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

}

