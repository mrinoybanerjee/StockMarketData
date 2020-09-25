package com.example.stockmarketdata;
import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class StockMarketListener extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    public static StockMarketListenerDelegate delegate = null;

    private OkHttpClient client;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        JSONArray ja;

        try {
            ja = new JSONArray(text.toString());

            if (delegate != null) {
                delegate.processFinish(ja);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {

    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {

        System.out.println(response);
        System.out.println(t.getLocalizedMessage());
    }

    public void start() {
        client = new OkHttpClient();
        Request request = new Request.Builder().url("wss://interviews.yum.dev/ws/stocks").build();

        StockMarketListener listener = new StockMarketListener();
        WebSocket ws = client.newWebSocket(request, listener);

        client.dispatcher().executorService().shutdown();
    }

}