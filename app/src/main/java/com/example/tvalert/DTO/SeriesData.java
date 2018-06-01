package com.example.tvalert.DTO;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SeriesData {
    @SerializedName("data")
    @Expose
    private List<SeriesDatum> data = null;

    public List<SeriesDatum> getData() {
        return data;
    }

    public void setData(List<SeriesDatum> data) {
        this.data = data;
    }
}
