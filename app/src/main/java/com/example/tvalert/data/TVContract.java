package com.example.tvalert.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class TVContract {

    public static final String CONTENT_AUTHORITY = "com.example.tvalert";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SERIES = "series";

    private TVContract() {
    }

    public static abstract class SeriesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SERIES);

        public final static String TABLE_NAME = "series";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_SERIES_NAME = "name";
        public final static String COLUMN_SERIES_ID = "seriesNumber";   //this is TVDB id
        public final static String COLUMN_SEASON = "season";
        public final static String COLUMN_LAST_EPISODE_DATE = "lastEpisodeDate";
        public final static String COLUMN_COLUMN_LAST_EPISODE_NUMBER = "lastEpisodeNumber";
        public final static String COLUMN_LAST_EPISODE_SEEN = "laseEpisodeSeen";

        /**
         * The MIME type of the {@link #CONTENT_URI} for the all table.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SERIES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single entry.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SERIES;
    }
}