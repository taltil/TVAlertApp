package com.example.tvalert;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tvalert.adapter.TVCursorAdapter;
import com.example.tvalert.data.TVContract.SeriesEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TV_LOADER = 1;

    private TVCursorAdapter mCursorAdapter;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup floating button to open AddSeriesActivity.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddSeriesActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mCursorAdapter = new TVCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);

        //short click will change the series status (last episode seen or not).
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                try {
                    Uri currentUri = ContentUris.withAppendedId(SeriesEntry.CONTENT_URI, id);
                    MainActivityManger.updateSeenStatus(currentUri, MainActivity.this);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, getString(R.string.error_updating_seen), Toast.LENGTH_SHORT).show();
                    Log.e("Main", "Error in updating seen status", e);
                }
            }
        });

        //long click will open a dialog to verify the user wants to delete the series.
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Uri currentUri = ContentUris.withAppendedId(SeriesEntry.CONTENT_URI, id);
                MainActivityManger.showDeleteConfirmationDialog(currentUri, MainActivity.this);
                return true;
            }
        });

        UpdateSeriesAsyncTask task = new UpdateSeriesAsyncTask(MainActivity.this);
        task.execute();

        getLoaderManager().initLoader(TV_LOADER, null, this);
    }

    //loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                SeriesEntry._ID,
                SeriesEntry.COLUMN_SERIES_NAME,
                SeriesEntry.COLUMN_SEASON,
                SeriesEntry.COLUMN_COLUMN_LAST_EPISODE_NUMBER,
                SeriesEntry.COLUMN_LAST_EPISODE_DATE,
                SeriesEntry.COLUMN_LAST_EPISODE_SEEN};
        return new CursorLoader(this, SeriesEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_entries:
                deleteAllSeries();
                return true;
            case R.id.help:
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllSeries() {
        int rowsDeleted = getContentResolver().delete(SeriesEntry.CONTENT_URI, null, null);
        if (rowsDeleted <= 0) {
            Toast.makeText(this, getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * updating all series in DB
     **/
    private class UpdateSeriesAsyncTask extends AsyncTask<Void, Void, Void> {

        private final Activity activity;


        public UpdateSeriesAsyncTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... Voids) {
            MainActivityManger.updateSeriesData(activity);
            return null;
        }
    }
}
