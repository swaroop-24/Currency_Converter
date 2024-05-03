package com.example.currencyconverter;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CurrencyApiService {
    @GET("latest.json")
    Call<JsonObject> getLatestRates(@Query("app_id") String appId);
}
