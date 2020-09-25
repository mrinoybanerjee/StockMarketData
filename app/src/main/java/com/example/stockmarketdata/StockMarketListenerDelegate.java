package com.example.stockmarketdata;

import org.json.JSONArray;
import org.json.JSONException;

public interface StockMarketListenerDelegate {
    void processFinish(JSONArray output) throws JSONException;
    void initializeData(JSONArray output) throws JSONException;
}
