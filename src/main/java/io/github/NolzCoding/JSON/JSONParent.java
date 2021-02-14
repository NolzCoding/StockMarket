package io.github.NolzCoding.JSON;

import java.util.List;

public class JSONParent {

    private String type;
    private List<JSONStock> data;


    public JSONParent(String type, List<JSONStock> data) {
        this.type = type;
        this.data = data;
    }

    public List<JSONStock> getData() {
        return data;
    }

    public void setData(List<JSONStock> data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
