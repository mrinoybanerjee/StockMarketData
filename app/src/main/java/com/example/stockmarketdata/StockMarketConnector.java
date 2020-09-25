package com.example.stockmarketdata;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class StockMarketConnector extends AsyncTask<Void, Void, JSONObject>
{
    public StockMarketListenerDelegate delegate = null;

    @Override
    protected JSONObject doInBackground(Void... params)
    {
        String str="https://interviews.yum.dev/api/stocks/";
        HttpURLConnection urlConn = null;
        BufferedReader bufferedReader = null;
        try
        {
            URL url = new URL(str);
            urlConn = (HttpURLConnection) url.openConnection();

            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }

            JSONObject jo = new JSONObject();
            JSONArray ja = new JSONArray(stringBuffer.toString());

            jo.put("ja", ja);

            return jo;
        }
        catch(Exception ex)
        {
            Log.e("App", "yourDataTask", ex);
            return null;
        }
        finally
        {
            if(bufferedReader != null)
            {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(JSONObject response)
    {
        if(response != null)
        {

            System.out.println(response);
            try {
                delegate.initializeData((JSONArray) response.get("ja"));
                Log.e("App", "Success: " + response.get("ja") );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
