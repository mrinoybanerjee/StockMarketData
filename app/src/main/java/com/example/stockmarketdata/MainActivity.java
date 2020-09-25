package com.example.stockmarketdata;
/**
 * Created by: Mrinoy Banerjee
 * Date: 09/18/2020
 */

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.health.SystemHealthManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements StockMarketListenerDelegate, StockMarketAdapter.OnItemClickListener {
    public static final String EXTRA_URL = "imageUrl";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_PRICE = "price";
    public static final String EXTRA_ATHPRICE = "allTimeHigh";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_TYPE = "companyType";

    private RecyclerView stockRecyclerView;
    private StockMarketAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private JSONArray data = new JSONArray();
    private StockMarketConnector stockMarketConnector;
    private StockMarketListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stockRecyclerView = (RecyclerView) findViewById(R.id.stock_recycler_view);
        stockRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        stockRecyclerView.setLayoutManager(layoutManager);

        try {
            mAdapter = new StockMarketAdapter(this, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        stockRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(MainActivity.this);
        stockMarketConnector = new StockMarketConnector();
        stockMarketConnector.delegate = this;
        stockMarketConnector.execute();

        listener = new StockMarketListener();
        listener.delegate = this;
        listener.start();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void processFinish(final JSONArray output) throws JSONException {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (output != null) {

                    try {
                        updateData(output);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mAdapter.notifyDataSetChanged();
                }

            }
        });
    }

    @Override
    public void initializeData(final JSONArray output) throws JSONException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (output != null) {
                    try {
                        for (int i = 0; i < output.length(); i++) {
                            data.put(output.getJSONObject(i));
                            output.getJSONObject(i).put("previousValue", output.getJSONObject(i).get("price"));
                            mAdapter.notifyItemChanged(data.length()-1);
                        }
                        mAdapter.resetFilteredData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    // Updates the data with the data from the websocket
    private void updateData(JSONArray output) throws JSONException {
        for (int i = 0; i < output.length(); i++) {
            int objectIndex = indexOfObject(output.getJSONObject(i));

            //If object is not in the array (objectIndex == -1)
            if (objectIndex == -1) {
                // get the JSONObject
                output.getJSONObject(i).put("previousValue", output.getJSONObject(i).get("price"));
                data.put(output.getJSONObject(i));

                mAdapter.notifyItemChanged(data.length()-1);
            } else {
                updateDataAtPositionWithObject(objectIndex, output.getJSONObject(i));
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    //Update the values within the JSONObject at a certain position
    private void updateDataAtPositionWithObject(int objectIndex, JSONObject jsonObject) throws JSONException {
        Iterator<String> keys = jsonObject.keys();
        data.getJSONObject(objectIndex).put("previousValue",
                data.getJSONObject(objectIndex).get("price"));
        while (keys.hasNext()) {
            String next = keys.next();
            data.getJSONObject(objectIndex).put(next,jsonObject.get(next));
        }
        mAdapter.notifyItemChanged(objectIndex);

    }

    //Get the index of the object with the same name (Could add more fields to make certain it is
    //The correct JSONObject
    private int indexOfObject(JSONObject object) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            if (object.get("name").equals(data.getJSONObject(i).get("name"))) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.stock_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mAdapter != null) {
                    mAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public void onItemClick(int position) throws JSONException {
        JSONObject obj = data.getJSONObject(position);
        Intent detailIntent = new Intent(this, StockDetailActivity.class);
        detailIntent.putExtra(EXTRA_URL, obj.get("imageUrl").toString());
        detailIntent.putExtra(EXTRA_NAME, obj.get("name").toString());
        detailIntent.putExtra(EXTRA_ADDRESS, obj.get("address").toString());

        DecimalFormat df = new DecimalFormat();
        df.setRoundingMode(RoundingMode.DOWN);

        double price = (double) obj.get("price");
        String price_string = df.format(price);
        detailIntent.putExtra(EXTRA_PRICE, price_string);

        double athPrice = (double) obj.get("allTimeHigh");
        String ath_price_string = df.format(athPrice);
        detailIntent.putExtra(EXTRA_ATHPRICE, ath_price_string);

        detailIntent.putExtra(EXTRA_ID, obj.get("id").toString());

        JSONArray company_types = (JSONArray) obj.get("companyType");
        String companyType = "";
        for (int i = 0; i < company_types.length(); i++) {
            companyType += (String) company_types.getString(i) + " ";
        }
        detailIntent.putExtra(EXTRA_TYPE, companyType);

        startActivity(detailIntent);
    }
}