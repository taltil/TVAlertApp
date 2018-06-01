package com.example.tvalert;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.tvalert.Models.EpisodeModel;
import com.example.tvalert.data.TVContract.SeriesEntry;

/**
 * This class contain methods that support the main activity
 */

public class MainActivityManger {

    private static final String LOG_TAG = MainActivityManger.class.getSimpleName();

    private MainActivityManger() {
    }

    /**
     * update all series in db
     **/
    public static void updateSeriesData(final Activity activity) {
        String[] projection = {
                SeriesEntry._ID,
                SeriesEntry.COLUMN_SERIES_ID,
                SeriesEntry.COLUMN_LAST_EPISODE_DATE};

        try (Cursor cursor = activity.getContentResolver().query(SeriesEntry.CONTENT_URI, projection,
                null, null, null)) {
            int idColumnIndex = cursor.getColumnIndex(SeriesEntry._ID);
            int seriesIdColumnIndex = cursor.getColumnIndex(SeriesEntry.COLUMN_SERIES_ID);
            int episodeDateColumnIndex = cursor.getColumnIndex(SeriesEntry.COLUMN_LAST_EPISODE_DATE);

            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                long currentSeriesID = cursor.getLong(seriesIdColumnIndex);
                String currentEpisodeDate = cursor.getString(episodeDateColumnIndex);
                EpisodeModel episode = ModelResolver.fetchEpisodesData(currentSeriesID, activity);
                if (episode.getLastEpisodeDate().compareTo(currentEpisodeDate) != 0) {
                    ContentValues values = new ContentValues();
                    values.put(SeriesEntry.COLUMN_SEASON, episode.getSeason());
                    values.put(SeriesEntry.COLUMN_COLUMN_LAST_EPISODE_NUMBER, episode.getLastEpisodeNumber());
                    values.put(SeriesEntry.COLUMN_LAST_EPISODE_DATE, episode.getLastEpisodeDate());
                    values.put(SeriesEntry.COLUMN_LAST_EPISODE_SEEN, episode.getLastEpisodeSeen());
                    Uri currentUri = ContentUris.withAppendedId(SeriesEntry.CONTENT_URI, currentID);
                    int rowsAffected = activity.getContentResolver().update(currentUri, values, null, null);
                    Log.v(LOG_TAG, "rows updates: " + rowsAffected);
                }

            }
        } catch (Exception e) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, activity.getString(R.string.error_get_series_data), Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(LOG_TAG, "An error in retrieving series data", e);
        }
    }

    public static void showDeleteConfirmationDialog(final Uri currentUri, final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteSeries(currentUri, activity);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private static void deleteSeries(Uri currentUri, final Activity activity) {
        int rowsDeleted = activity.getContentResolver().delete(currentUri, null, null);
        if (rowsDeleted == 0) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, activity.getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(activity, activity.getString(R.string.delete_successful), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void updateSeenStatus(Uri currentUri, Activity activity) {
        String[] projection = {
                SeriesEntry.COLUMN_LAST_EPISODE_SEEN};
        try (Cursor cursor = activity.getContentResolver().query(currentUri, projection, null, null, null)) {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                int lastSeenColumnIndex = cursor.getColumnIndex(SeriesEntry.COLUMN_LAST_EPISODE_SEEN);
                int lastSeen = cursor.getInt(lastSeenColumnIndex);
                if (lastSeen == 1) {
                    values.put(SeriesEntry.COLUMN_LAST_EPISODE_SEEN, 0);
                } else {
                    values.put(SeriesEntry.COLUMN_LAST_EPISODE_SEEN, 1);
                }
                int rowsAffected = activity.getContentResolver().update(currentUri, values, null, null);
                Log.v("row updates: ", Integer.toString(rowsAffected));
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Update seen status failed", e);
        }
    }
}
