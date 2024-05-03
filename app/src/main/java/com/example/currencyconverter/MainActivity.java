package com.example.currencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Spinner currencyFromSpinner;
    private Spinner currencyToSpinner;
    private TextView exchangeRateTextView;
    private EditText fromText;
    private EditText toText;
    private CurrencyApiService currencyApiService;
    private JsonObject rates;
    private Button viewChartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currencyFromSpinner = findViewById(R.id.currencySpinner2);
        currencyToSpinner = findViewById(R.id.spinner4);
        exchangeRateTextView = findViewById(R.id.exchangeRate);
        fromText = findViewById(R.id.fromText);
        toText = findViewById(R.id.toText);
        viewChartButton = findViewById(R.id.chartButton);

        viewChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAndSendExchangeRates();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://openexchangerates.org/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        currencyApiService = retrofit.create(CurrencyApiService.class);

        fetchLatestRates();

        currencyFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                convertCurrency();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        currencyToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                convertCurrency();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        fromText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                convertCurrency();
            }
        });
    }

    private void fetchLatestRates() {
        currencyApiService.getLatestRates("ab2bf7d677d94c63a1065ad18ada0ab4").enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();
                    JsonObject ratesJson = responseJson.getAsJsonObject("rates");

                    Set<String> currencyCodes = ratesJson.keySet();
                    ArrayList<String> currencyList = new ArrayList<>(currencyCodes);
                    Collections.sort(currencyList);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_item, currencyList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    currencyFromSpinner.setAdapter(adapter);
                    currencyToSpinner.setAdapter(adapter);

                    rates = ratesJson;

                    convertCurrency();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    private void fetchAndSendExchangeRates() {
        currencyApiService.getLatestRates("ab2bf7d677d94c63a1065ad18ada0ab4").enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject responseJson = response.body();
                    JsonObject ratesJson = responseJson.getAsJsonObject("rates");

                    double usdRate = ratesJson.get("USD").getAsDouble();
                    double gbpRate = ratesJson.get("GBP").getAsDouble();
                    double eurRate = ratesJson.get("EUR").getAsDouble();
                    double audRate = ratesJson.get("AUD").getAsDouble();
                    double aedRate = ratesJson.get("AED").getAsDouble();

                    convertCurrency2(usdRate, gbpRate, eurRate, audRate, aedRate);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }

    private void convertCurrency() {
        if (rates != null) {
            String baseCurrency = currencyFromSpinner.getSelectedItem().toString();
            String targetCurrency = currencyToSpinner.getSelectedItem().toString();
            double amountToConvert = 0.0;
            try {
                amountToConvert = Double.parseDouble(fromText.getText().toString());
            } catch (NumberFormatException e) {
                return;
            }

            if (baseCurrency == null || targetCurrency == null) {
                return;
            }

            double exchangeRate = rates.get(targetCurrency).getAsDouble() / rates.get(baseCurrency).getAsDouble();
            double convertedAmount = amountToConvert * exchangeRate;

            toText.setText(String.format("%.2f", convertedAmount));
            exchangeRateTextView.setText("Exchange Rate: 1 " + baseCurrency + " = " + String.format("%.4f", exchangeRate) + " " + targetCurrency);
        }
    }

    private void convertCurrency2(double usdRate, double gbpRate, double eurRate, double audRate, double aedRate) {
        if (rates != null) {
            String baseCurrency = currencyFromSpinner.getSelectedItem().toString();
            double amountToConvert = 0.0;
            try {
                amountToConvert = Double.parseDouble(fromText.getText().toString());
            } catch (NumberFormatException e) {
                return;
            }

            if (baseCurrency == null) {
                return;
            }

            double usdToBaseRate = usdRate / rates.get(baseCurrency).getAsDouble();
            double gbpToBaseRate = gbpRate / rates.get(baseCurrency).getAsDouble();
            double eurToBaseRate = eurRate / rates.get(baseCurrency).getAsDouble();
            double audToBaseRate = audRate / rates.get(baseCurrency).getAsDouble();
            double aedToBaseRate = aedRate / rates.get(baseCurrency).getAsDouble();

            Intent intent = new Intent(MainActivity.this, ChartActivity.class);

            intent.putExtra("USD_TO_BASE_RATE", usdToBaseRate);
            intent.putExtra("GBP_TO_BASE_RATE", gbpToBaseRate);
            intent.putExtra("EUR_TO_BASE_RATE", eurToBaseRate);
            intent.putExtra("AUD_TO_BASE_RATE", audToBaseRate);
            intent.putExtra("AED_TO_BASE_RATE", aedToBaseRate);

            startActivity(intent);

        }
    }
}
