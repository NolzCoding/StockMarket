package io.github.NolzCoding.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.NolzCoding.JSON.JSONParent;
import io.github.NolzCoding.JSON.JSONStock;
import io.github.NolzCoding.Main;
import org.apache.commons.lang.ObjectUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.http.WebSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

public class WebSocketClient implements java.net.http.WebSocket.Listener {

    private ArrayList<String> jsonArray = new ArrayList<>();

    private final List<String> symbols;
    private final StockInfo stockInfo;
    private final HashMap<String, Stock> stocks;
    private final CountDownLatch latch;
    private final Gson gson = new Gson();
    public WebSocketClient(CountDownLatch latch, List<String> symbols) {
        this.latch = latch;
        this.symbols = symbols;
        this.stockInfo = Main.getmain().getStockInfo();
        stocks = stockInfo.getStocks();
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        System.out.println("on Open using subprotocol " + webSocket.getSubprotocol());

        for (String key : symbols) {
            JSONObject obj = new JSONObject();
            obj.put("type", "subscribe");
            obj.put("symbol", key);
            webSocket.sendText(obj.toJSONString(), true);
        }

        WebSocket.Listener.super.onOpen(webSocket);

    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        JSONParent jsonParent;
        try {
            jsonParent = gson.fromJson(data.toString(), JSONParent.class);
        } catch (JsonSyntaxException e) {
            if (jsonArray.size() != 0) {
                jsonArray.add(data.toString());
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String string : jsonArray) {
                        stringBuilder.append(string);
                    }
                    jsonParent = gson.fromJson(stringBuilder.toString(), JSONParent.class);
                }
                catch (JsonSyntaxException e2) {
                    latch.countDown();
                    return WebSocket.Listener.super.onText(webSocket, data, last);
                }
                jsonArray.clear();
            }
            else {
                jsonArray.add(data.toString());
                latch.countDown();
                return WebSocket.Listener.super.onText(webSocket, data, last);
            }
        }
        jsonParent = gson.fromJson(data.toString(), JSONParent.class);

        try {
            for (JSONStock jsonStock : jsonParent.getData()) {
                Stock stock = stocks.get(jsonStock.getS());
                try {
                    stock.setVolume(jsonStock.getV());
                    stock.setPrice(jsonStock.getP());
                } catch (NullPointerException e) {
                    System.out.println("There was something wrong with the json string > JSON: " + data);
                    return WebSocket.Listener.super.onText(webSocket, data, last);
                }
            }
        }
        catch (NullPointerException ignored) {

        }

        latch.countDown();

        return WebSocket.Listener.super.onText(webSocket, data, last);

    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        System.out.println("Bad day! " + webSocket.toString());
        error.printStackTrace();

        WebSocket.Listener.super.onError(webSocket, error);
    }



}
