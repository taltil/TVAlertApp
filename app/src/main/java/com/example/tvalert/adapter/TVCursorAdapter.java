package com.example.tvalert.adapter;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.tvalert.R;
import com.example.tvalert.data.TVContract.SeriesEntry;

public class TVCursorAdapter extends CursorAdapter {

    public TVCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = new ViewHolder();
        holder.name = view.findViewById(R.id.name);
        holder.season = view.findViewById(R.id.season);
        holder.episodeNumber = view.findViewById(R.id.episode_number);
        holder.episodeDate = view.findViewById(R.id.episode_date);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_NAME));
        int seriesNumber = cursor.getInt(cursor.getColumnIndexOrThrow(SeriesEntry.COLUMN_SEASON));
        String episodeNumber = cursor.getString(cursor.getColumnIndexOrThrow(SeriesEntry.COLUMN_COLUMN_LAST_EPISODE_NUMBER));
        String episodeDate = cursor.getString(cursor.getColumnIndexOrThrow(SeriesEntry.COLUMN_LAST_EPISODE_DATE));
        int isLastEpisodeSeen = cursor.getInt(cursor.getColumnIndexOrThrow(SeriesEntry.COLUMN_LAST_EPISODE_SEEN));

        setBackgroundColor(view, context, isLastEpisodeSeen);

        holder.name.setText(name);
        if (episodeDate != null) {
            holder.season.setText("Season " + seriesNumber);
            holder.episodeNumber.setText("Episode No. " + episodeNumber);
            holder.episodeDate.setText("Date: " + episodeDate);
        }
    }

    private void setBackgroundColor(View view, Context context, int isSeen) {
        if (isSeen == 1) {
            view.setBackgroundColor(context.getResources().getColor(R.color.background_seen));
        } else {
            view.setBackgroundColor(context.getResources().getColor(R.color.background_not_seen));
        }
    }

    private class ViewHolder {
        TextView name;
        TextView season;
        TextView episodeNumber;
        TextView episodeDate;
    }
}
