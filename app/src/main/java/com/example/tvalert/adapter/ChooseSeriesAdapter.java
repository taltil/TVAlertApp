package com.example.tvalert.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tvalert.DTO.SeriesDatum;
import com.example.tvalert.R;

import java.util.List;

public class ChooseSeriesAdapter extends ArrayAdapter<SeriesDatum> {

    public ChooseSeriesAdapter(@NonNull Context context, @NonNull List<SeriesDatum> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.choose_series_list, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.nameView = convertView.findViewById(R.id.name);
            holder.dateView = convertView.findViewById(R.id.date);

            SeriesDatum currentSeries = getItem(position);
            holder.nameView.setText(currentSeries.getSeriesName());
            if (currentSeries.getFirstAired().length() >= 4) {
                holder.dateView.setText(currentSeries.getFirstAired().substring(0, 4));
            }
        }
        return convertView;
    }

    private class ViewHolder {
        TextView nameView;
        TextView dateView;
    }
}
