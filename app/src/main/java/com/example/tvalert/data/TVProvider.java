package com.example.tvalert.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.tvalert.data.TVContract.SeriesEntry;

/**
 * {@link ContentProvider} for tv alert app.
 */

public class TVProvider extends ContentProvider {

    private static final String LOG_TAG = TVProvider.class.getSimpleName();

    // URI matcher codes for the content URI for the series table
    private static final int SERIES_TABLE = 100;
    private static final int SERIES_ID = 101;
    //UriMatcher object to match a content URI to a corresponding code.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(TVContract.CONTENT_AUTHORITY, TVContract.PATH_SERIES, SERIES_TABLE);
        sUriMatcher.addURI(TVContract.CONTENT_AUTHORITY, TVContract.PATH_SERIES + "/#", SERIES_ID);
    }

    private DBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case SERIES_TABLE:
                cursor = database.query(SeriesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SERIES_ID:
                selection = SeriesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(SeriesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SERIES_TABLE:
                return SeriesEntry.CONTENT_LIST_TYPE;
            case SERIES_ID:
                return SeriesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SERIES_TABLE:
                return insertNewSeries(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertNewSeries(Uri uri, ContentValues values) {

        inputValidation(values);

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        long id = database.insert(SeriesEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SERIES_TABLE:
                rowsDeleted = database.delete(SeriesEntry.TABLE_NAME, selection, selectionArgs);
                // Delete all rows that match the selection and selection args
                break;
            case SERIES_ID:
                // Delete a single row given by the ID in the URI
                selection = SeriesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(SeriesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SERIES_TABLE:
                return updateEntry(uri, contentValues, selection, selectionArgs);
            case SERIES_ID:
                selection = SeriesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateEntry(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateEntry(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // update database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(SeriesEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private void inputValidation(ContentValues values) {
        String name = values.getAsString(SeriesEntry.COLUMN_SERIES_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Name is required.");
        }
        Integer id = values.getAsInteger(SeriesEntry.COLUMN_SERIES_ID);
        if (id == null) {
            throw new IllegalArgumentException("Valid series id is required.");
        }
    }

}

