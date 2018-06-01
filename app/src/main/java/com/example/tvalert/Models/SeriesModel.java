package com.example.tvalert.Models;


import java.util.Date;

public class SeriesModel {

    private String mSeriesName;
    private long mSeriesId;
    private EpisodeModel mLastEpisode;

    public SeriesModel(String name, long id, EpisodeModel episode) {
        mSeriesName = name;
        mSeriesId = id;
        mLastEpisode = episode;
    }

    public SeriesModel() {
        this(null, 0, null);
    }

    public SeriesModel(String name, long id) {
        this(name, id, null);
    }

    public String getSeriesName() {
        return mSeriesName;
    }

    public void setSeriesName(String SeriesName) {
        this.mSeriesName = SeriesName;
    }

    public long getSeriesId() {
        return mSeriesId;
    }

    public void setSeriesId(long SeriesId) {
        this.mSeriesId = SeriesId;
    }

    public int getSeason() {
        return mLastEpisode.getSeason();
    }

    public void setSeason(int season) {
        this.mLastEpisode.setSeason(season);
    }

    public String getLastEpisodeDate() {
        return mLastEpisode.getLastEpisodeDate();
    }

    public void setLastEpisodeDate(String lastEpisodeDate) {
        this.mLastEpisode.setLastEpisodeDate(lastEpisodeDate);
    }

    public void setLastEpisodeDate(Date nextEpisodeDate) {
        this.mLastEpisode.setLastEpisodeDate(nextEpisodeDate);
    }

    public int getLastEpisodeNumber() {
        return mLastEpisode.getLastEpisodeNumber();
    }

    public void setLastEpisodeNumber(int lastEpisodeNumber) {
        this.mLastEpisode.setLastEpisodeNumber(lastEpisodeNumber);
    }

    public boolean getLastEpisodeSeen() {
        return mLastEpisode.getLastEpisodeSeen();
    }

    public void setLastEpisodeSeen(int lastEpisodeSeen) {
        this.mLastEpisode.setLastEpisodeSeen(lastEpisodeSeen);
    }

    public void setLastEpisodeSeen(boolean lastEpisodeSeen) {
        this.mLastEpisode.setLastEpisodeSeen(lastEpisodeSeen);
    }

    public EpisodeModel getNextEpisodeData() {
        return mLastEpisode;
    }

    public void setEpisodeData(int season, String lastEpisodeDate, int lastEpisodeNumber, int lastEpisodeSeen) {
        this.mLastEpisode.setSeason(season);
        this.mLastEpisode.setLastEpisodeDate(lastEpisodeDate);
        this.mLastEpisode.setLastEpisodeNumber(lastEpisodeNumber);
        this.mLastEpisode.setLastEpisodeSeen(lastEpisodeSeen);
    }

    public void setEpisodeData(EpisodeModel episode) {
        this.mLastEpisode = episode;
    }

    @Override
    public String toString() {
        return "SeriesModel{" +
                "mSeriesName='" + mSeriesName + '\'' +
                ", mSeriesId=" + mSeriesId +
                ", mLastEpisode=" + mLastEpisode +
                '}';
    }
}

