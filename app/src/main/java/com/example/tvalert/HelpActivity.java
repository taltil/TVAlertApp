package com.example.tvalert;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        TextView addSeriesLabel = findViewById(R.id.help_add_series_label);
        TextView addSeriesText = findViewById(R.id.help_add_series_text);
        TextView seenLabel = findViewById(R.id.help_seen_label);
        TextView seenText = findViewById(R.id.help_seen_text);
        addSeriesLabel.setText(getString(R.string.help_add_series_label));
        addSeriesText.setText(getString(R.string.help_add_series));
        seenLabel.setText(getString(R.string.help_seen_label));
        seenText.setText(getString(R.string.help_seen));
    }
}
