package io.github.NolzCoding.JSON;

public class JSONStock {
    private String s;
    private Float p;
    private int v;

    public JSONStock(String s, Float p, int v) {
        this.s = s;
        this.p = p;
        this.v = v;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public Float getP() {
        return p;
    }

    public void setP(Float p) {
        this.p = p;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }
}
