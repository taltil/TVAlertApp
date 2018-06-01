package com.example.tvalert.Models;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EpisodeModel {

    private int mSeason;
    private String mLastEpisodeDate;
    private int mLastEpisodeNumber;
    private int mLastEpisodeSeen;

    public EpisodeModel(int season, String lastEpisodeDate, int lastEpisodeNumber, int lastEpisodeSeen) {
        mSeason = season;
        mLastEpisodeDate = lastEpisodeDate;
        mLastEpisodeNumber = lastEpisodeNumber;
        mLastEpisodeSeen = lastEpisodeSeen;
    }

    public EpisodeModel() {
        this(0, null, 0, 0);
    }

    public int getSeason() {
        return mSeason;
    }

    public void setSeason(int season) {
        this.mSeason = season;
    }

    public String getLastEpisodeDate() {
        return mLastEpisodeDate;
    }

    public void setLastEpisodeDate(String lastEpisodeDate) {
        this.mLastEpisodeDate = lastEpisodeDate;
    }

    public void setLastEpisodeDate(Date lastEpisodeDate) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.mLastEpisodeDate = dateFormat.format(lastEpisodeDate);
    }

    public int getLastEpisodeNumber() {
        return mLastEpisodeNumber;
    }

    public void setLastEpisodeNumber(int lastEpisodeNumber) {
        this.mLastEpisodeNumber = lastEpisodeNumber;
    }

    public boolean getLastEpisodeSeen() {
        return mLastEpisodeSeen == 1;
    }

    public void setLastEpisodeSeen(int lastEpisodeSeen) {
        this.mLastEpisodeSeen = lastEpisodeSeen;
    }

    public void setLastEpisodeSeen(boolean lastEpisodeSeen) {
        if (lastEpisodeSeen) {
            this.mLastEpisodeSeen = 1;
        } else {
            this.mLastEpisodeSeen = 0;
        }
    }

    @Override
    public String toString() {
        return "EpisodeModel{" +
                "mSeason=" + mSeason +
                ", mLastEpisodeDate='" + mLastEpisodeDate + '\'' +
                ", mLastEpisodeNumber=" + mLastEpisodeNumber +
                ", mLastEpisodeSeen=" + mLastEpisodeSeen +
                '}';
    }
}
