package io.github.NolzCoding.Utils;

import java.util.HashMap;

public class StockInfo {

    private HashMap<String, Stock> stocks;

    public StockInfo(HashMap<String, Stock> stocks) {
        this.stocks = stocks;
    }

    public HashMap<String, Stock> getStocks() {
        return stocks;
    }
}
