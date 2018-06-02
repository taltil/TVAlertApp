package com.example.tvalert;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tvalert.Models.SeriesModel;
import com.example.tvalert.data.TVContract.SeriesEntry;


public class AddSeriesActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddSeriesActivity.class.getSimpleName();
    private EditText mNameEditText;
    private EditText mIMDBEditText;
    private boolean mHasChanged = false;

    private final View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mNameEditText = findViewById(R.id.edit_series_name);
        mIMDBEditText = findViewById(R.id.edit_imdb_id);

        mNameEditText.setOnTouchListener(mTouchListener);
        mIMDBEditText.setOnTouchListener(mTouchListener);
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_series, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!mHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_save:
                saveSeries();
                return true;

            case android.R.id.home:
                if (!mHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddSeriesActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(AddSeriesActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.unsaved_changes_dialog_msg));
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.keep_editing), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //keep editing close the dialog and continue the editing
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void saveSeries() {
        String nameString = mNameEditText.getText().toString().trim();
        String imdbString = mIMDBEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString) && (TextUtils.isEmpty(imdbString))) {
            Toast.makeText(this, getString(R.string.error_data_missing), Toast.LENGTH_SHORT).show();
            return;
        }

        String[] data = {nameString, imdbString};

        AddSeriesAsyncTask task = new AddSeriesAsyncTask(AddSeriesActivity.this);
        task.execute(data);
    }

    private void addSeriesFeedback(Uri uri) {
        if (uri != null) {
            Toast.makeText(this, getString(R.string.insert_successful), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * collect new series details and insert them to db
     **/
    private class AddSeriesAsyncTask extends AsyncTask<String, Void, Uri> {

        private final Activity activity;

        public AddSeriesAsyncTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Uri doInBackground(String... data) {
            Uri newUri = null;
            try {
                SeriesModel newSeries = ModelResolver.fetchSeriesData(data[0], data[1], activity);
                if (newSeries == null) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(activity, getString(R.string.error_finding_series), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    ContentValues values = new ContentValues();
                    values.put(SeriesEntry.COLUMN_SERIES_NAME, newSeries.getSeriesName());
                    values.put(SeriesEntry.COLUMN_SERIES_ID, newSeries.getSeriesId());
                    values.put(SeriesEntry.COLUMN_SEASON, newSeries.getSeason());
                    values.put(SeriesEntry.COLUMN_COLUMN_LAST_EPISODE_NUMBER, newSeries.getLastEpisodeNumber());
                    values.put(SeriesEntry.COLUMN_LAST_EPISODE_DATE, newSeries.getLastEpisodeDate());
                    values.put(SeriesEntry.COLUMN_LAST_EPISODE_SEEN, newSeries.getLastEpisodeSeen());
                    newUri = getContentResolver().insert(SeriesEntry.CONTENT_URI, values);
                    activity.finish();
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Problem with finding the series.", e);
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(activity, getString(R.string.insert_failed), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return newUri;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            addSeriesFeedback(uri);
        }
    }
}

