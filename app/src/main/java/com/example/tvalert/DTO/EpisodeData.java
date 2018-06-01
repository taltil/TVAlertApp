package com.example.tvalert.DTO;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EpisodeData {
    @SerializedName("links")
    @Expose
    private EpisodeLinks links;
    @SerializedName("data")
    @Expose
    private List<EpisodeDatum> data = null;

    public EpisodeLinks getLinks() {
        return links;
    }

    public void setLinks(EpisodeLinks links) {
        this.links = links;
    }

    public List<EpisodeDatum> getData() {
        return data;
    }

    public void setData(List<EpisodeDatum> data) {
        this.data = data;
    }
}
