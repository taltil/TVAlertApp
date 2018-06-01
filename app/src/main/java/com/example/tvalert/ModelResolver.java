package com.example.tvalert;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.tvalert.DTO.EpisodeDatum;
import com.example.tvalert.DTO.SeriesDatum;
import com.example.tvalert.Models.EpisodeModel;
import com.example.tvalert.Models.SeriesModel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * convert datum for Json response to application models
 */
public class ModelResolver {

    private static final String LOG_TAG = ModelResolver.class.getSimpleName();

    private ModelResolver() {
    }

    public static SeriesModel fetchSeriesData(String name, String imdbId, final Activity activity) {
        SeriesModel series = new SeriesModel();
        List<SeriesDatum> seriesDatum = APIManger.fetchSeriesDatum(name, imdbId, activity);
        String seriesName = null;
        long seriesId = 0;
        if (seriesDatum == null) {
            series = null;
        } else if (seriesDatum.size() == 1) {
            seriesName = seriesDatum.get(0).getSeriesName();
            seriesId = seriesDatum.get(0).getId();
            if (seriesName != null && seriesId != 0) {
                series.setSeriesName(seriesName);
                series.setSeriesId(seriesId);
                series.setEpisodeData(fetchEpisodesData(seriesId, activity));
            } else {
                series = null;
            }
        } else if (seriesDatum.size() > 1) {
            Intent intent = new Intent(activity, ChooseSeriesActivity.class);
            intent.putExtra("series", (Serializable) seriesDatum);
            activity.startActivity(intent);
        } else {
            series = null;
        }
        return series;
    }

    public static EpisodeModel fetchEpisodesData(long id, Activity activity) {
        EpisodeModel episode = null;
        List<EpisodeDatum> episodeDatum = APIManger.fetchEpisodesDatum(id, activity);
        episode = findLastEpisode(episodeDatum);
        return episode;
    }

    /**
     * return last episode if there is one or null otherwise
     */
    private static EpisodeModel findLastEpisode(List<EpisodeDatum> episodes) {
        EpisodeDatum lastEpisode = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        EpisodeDatum currentEpisode = null;
        Date currentDate = new Date();
        Date lastEpisodeDate = new Date();
        for (int i = 0; i < episodes.size(); i++) {
            try {
                currentEpisode = episodes.get(i);
                if (currentEpisode.getFirstAired().length() > 0) {
                    currentDate = dateFormat.parse(currentEpisode.getFirstAired());
                    if (currentDate.compareTo(today) < 0) {
                        if (lastEpisode == null) {
                            lastEpisode = currentEpisode;
                        } else {
                            lastEpisodeDate = dateFormat.parse(lastEpisode.getFirstAired());
                            if (currentDate.compareTo(lastEpisodeDate) > 0) {
                                lastEpisode = currentEpisode;
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Problem with converting string to date.", e);
            }
        }
        EpisodeModel episode = null;
        if (lastEpisode != null) {
            episode = new EpisodeModel(lastEpisode.getAiredSeason(), lastEpisode.getFirstAired(), lastEpisode.getAiredEpisodeNumber(), 0);
        }
        return episode;
    }
}
