package com.example.personalfinancetracker;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private static final String GET_TRANSACTIONS_URL = Constants.BASE_URL + "get_transactions";
    private static final String GET_USER_PROFILE_URL = Constants.BASE_URL + "get_user_profile/";
    private Map<String, Float> categorySpending = new HashMap<>();
    private float monthlyBudget = 0f;
    private float totalSpending = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        requestQueue = Volley.newRequestQueue(this);

        // Fetch user profile and transactions
        fetchUserProfile();
    }

    private void fetchUserProfile() {
        int userId = getUserIdFromSharedPreferences(); // Implement this method to get the user ID
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GET_USER_PROFILE_URL + userId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            monthlyBudget = (float) response.getDouble("monthly_budget");
                            fetchTransactions();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DashboardActivity.this, "Error parsing user profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DashboardActivity.this, "Error fetching user profile", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void fetchTransactions() {
        int userId = getUserIdFromSharedPreferences(); // Implement this method to get the user ID
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GET_TRANSACTIONS_URL + "?user_id=" + userId, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processTransactions(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DashboardActivity.this, "Error fetching transactions", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void processTransactions(JSONArray transactions) {
        try {
            for (int i = 0; i < transactions.length(); i++) {
                JSONObject transaction = transactions.getJSONObject(i);
                String category = transaction.getString("category");
                float amount = (float) transaction.getDouble("amount");

                // Accumulate spending by category
                categorySpending.put(category, categorySpending.getOrDefault(category, 0f) + amount);
                totalSpending += amount;
            }
            createPieChart();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing transactions", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPieChart() {
        PieChart pieChart = findViewById(R.id.pieChart);
        List<PieEntry> entries = new ArrayList<>();

        float remainingBudget = monthlyBudget - totalSpending;
        if (remainingBudget > 0) {
            entries.add(new PieEntry(remainingBudget, "Remaining Budget"));
        }

        for (Map.Entry<String, Float> entry : categorySpending.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Spending by Category");

        // Create a list of colors manually
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(64, 89, 128));  // Dark Blue
        colors.add(Color.rgb(149, 165, 124));  // Sage Green
        colors.add(Color.rgb(217, 184, 162));  // Peach
        colors.add(Color.rgb(191, 134, 134));  // Dusty Rose
        colors.add(Color.rgb(179, 48, 80));  // Dark Red
        colors.add(Color.rgb(193, 37, 82));  // Berry
        colors.add(Color.rgb(255, 102, 0));  // Orange
        colors.add(Color.rgb(245, 199, 0));  // Yellow
        colors.add(Color.rgb(106, 150, 31));  // Lime Green
        colors.add(Color.rgb(179, 100, 53));  // Brown

        // Add a distinct color for Remaining Budget
        colors.add(Color.rgb(0, 128, 0));  // Green for Remaining Budget

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setCenterText("Monthly Budget: $" + String.format("%.2f", monthlyBudget) +
                "\nSpent: $" + String.format("%.2f", totalSpending) +
                "\nLeft: $" + String.format("%.2f", remainingBudget));
        pieChart.setCenterTextSize(14f);
        pieChart.setDrawCenterText(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.animateY(1400);
        pieChart.getLegend().setEnabled(false);

        pieChart.invalidate(); // refresh
    }

    // Implement this method to get the user ID from SharedPreferences
    private int getUserIdFromSharedPreferences() {
        // Return the user ID stored in SharedPreferences
        return 2; // Placeholder return
    }
}
