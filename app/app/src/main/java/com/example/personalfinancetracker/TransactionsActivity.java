package com.example.personalfinancetracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TransactionsActivity extends AppCompatActivity {

    private ListView listViewTransactions;
    private Button btnAddTransaction;
    private ArrayList<String> transactions;
    private ArrayAdapter<String> adapter;
    private static final String SERVER_URL = Constants.BASE_URL+"get_transactions";

    private static final int ADD_TRANSACTION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        listViewTransactions = findViewById(R.id.listViewTransactions);
        btnAddTransaction = findViewById(R.id.btnAddTransaction);

        transactions = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, transactions);
        listViewTransactions.setAdapter(adapter);

        btnAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionsActivity.this, AddTransactionActivity.class);
                startActivityForResult(intent, ADD_TRANSACTION_REQUEST);
                finish();
            }
        });
        fetchTransactions();
    }
    private void fetchTransactions() {
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        String userId = settings.getString("user_id", null);

        if (userId != null) {
            String urlWithUserId = SERVER_URL + "?user_id=" + userId;
            new FetchTransactionsTask().execute(urlWithUserId);
        } else {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TRANSACTION_REQUEST && resultCode == RESULT_OK && data != null) {
            String date = data.getStringExtra("date");
            String description = data.getStringExtra("description");
            String amount = data.getStringExtra("amount");
            String type = data.getStringExtra("type");
            String category = data.getStringExtra("category");
            String groupSize = data.getStringExtra("groupSize");

            String transactionString;
            if (type.equals("Group")) {
                transactionString = String.format("%s - %s - $%s (%s, %s, Group of %s)", date, description, amount, type, category, groupSize);
            } else {
                transactionString = String.format("%s - %s - $%s (%s, %s)", date, description, amount, type, category);
            }
            transactions.add(transactionString);
            adapter.notifyDataSetChanged();

            // Here you would typically send this data to your Flask backend
            // You can use Retrofit or Volley to make API calls
        }
    }
    private class FetchTransactionsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();
                    response = sb.toString();
                } else {
                    Log.e("FetchTransactionsTask", "HTTP error code: " + conn.getResponseCode());
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e("FetchTransactionsTask", "Error: " + e.getMessage());
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (!result.isEmpty()) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    transactions.clear();  // Clear existing items before adding new ones
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject transaction = jsonArray.getJSONObject(i);
                        String date = transaction.getString("date");
                        String description = transaction.getString("description");
                        String amount = transaction.getString("amount");
                        String type = transaction.getString("type");
                        String category = transaction.getString("category");
                        String groupSize = transaction.optString("group_size", "1");

                        String transactionString;
                        if ("Group".equals(type)) {
                            transactionString = String.format("%s - %s - $%s (%s, %s, Group of %s)", date, description, amount, type, category, groupSize);
                        } else {
                            transactionString = String.format("%s - %s - $%s (%s, %s)", date, description, amount, type, category);
                        }
                        transactions.add(transactionString);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(TransactionsActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                    Log.e("FetchTransactionsTask", "JSON Parsing error: " + e.getMessage());
                }
            } else {
                Toast.makeText(TransactionsActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
            }
        }
    }

}