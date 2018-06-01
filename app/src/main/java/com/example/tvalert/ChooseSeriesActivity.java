package com.example.tvalert;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tvalert.DTO.SeriesDatum;
import com.example.tvalert.Models.EpisodeModel;
import com.example.tvalert.Models.SeriesModel;
import com.example.tvalert.adapter.ChooseSeriesAdapter;
import com.example.tvalert.data.TVContract.SeriesEntry;

import java.util.List;

public class ChooseSeriesActivity extends AppCompatActivity {

    private static final String LOG_TAG = ChooseSeriesActivity.class.getSimpleName();
    private ChooseSeriesAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_choose_series);
        Intent intent = getIntent();
        List<SeriesDatum> seriesList = (List<SeriesDatum>) intent.getExtras().getSerializable("series");

        ListView listView = findViewById(R.id.list);
        mAdapter = new ChooseSeriesAdapter(this, seriesList);

        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SeriesDatum currentItem = mAdapter.getItem(position);
                AddSeriesAsyncTask task = new AddSeriesAsyncTask(ChooseSeriesActivity.this);
                task.execute(currentItem);
                Intent intent = new Intent(ChooseSeriesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * collect the chosen series details and insert them to db
     **/
    private class AddSeriesAsyncTask extends AsyncTask<SeriesDatum, Void, Uri> {

        private final Activity activity;

        public AddSeriesAsyncTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Uri doInBackground(SeriesDatum... seriesData) {
            SeriesModel series = new SeriesModel();
            EpisodeModel episode = null;
            Uri uri = null;
            long seriesId = seriesData[0].getId();
            String seriesName = seriesData[0].getSeriesName();

            if (seriesName != null && seriesId != 0) {
                series.setSeriesName(seriesName);
                series.setSeriesId(seriesId);
                try {
                    episode = ModelResolver.fetchEpisodesData(seriesId, activity);
                    if (episode != null) {
                        ContentValues values = new ContentValues();
                        values.put(SeriesEntry.COLUMN_SERIES_NAME, seriesName);
                        values.put(SeriesEntry.COLUMN_SERIES_ID, seriesId);
                        values.put(SeriesEntry.COLUMN_SEASON, episode.getSeason());
                        values.put(SeriesEntry.COLUMN_COLUMN_LAST_EPISODE_NUMBER, episode.getLastEpisodeNumber());
                        values.put(SeriesEntry.COLUMN_LAST_EPISODE_DATE, episode.getLastEpisodeDate());
                        values.put(SeriesEntry.COLUMN_LAST_EPISODE_SEEN, episode.getLastEpisodeSeen());

                        uri = getContentResolver().insert(SeriesEntry.CONTENT_URI, values);
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "An error in retrieving series data.", e);
                }
            } else {
                Toast.makeText(activity, R.string.error_get_series_data, Toast.LENGTH_SHORT).show();
            }
            return uri;
        }
    }
}
