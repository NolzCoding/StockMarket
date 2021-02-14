package io.github.NolzCoding.Utils;

public class Stock {

    private Float price;
    private String symbol;
    private String name;
    private Integer volume;


    public Stock(Float price, String symbol, String name, String currency, Integer volume) {
        this.price = price;
        this.symbol = symbol;
        this.name = name;
        this.volume = volume;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }
}
