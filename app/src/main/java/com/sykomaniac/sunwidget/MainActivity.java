package com.sykomaniac.sunwidget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.sykomaniac.sunwidget.settings.SettingsActivity;
import com.sykomaniac.sunwidget.worker.StatusUpdateWorker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Background sync
        PeriodicWorkRequest backgroundStatusRequest =
                new PeriodicWorkRequest.Builder(StatusUpdateWorker.class,
                        6, TimeUnit.HOURS,
                        2, TimeUnit.HOURS)
                        .build();

        WorkManager.getInstance()
                .enqueueUniquePeriodicWork("background-status-updater",
                        ExistingPeriodicWorkPolicy.KEEP,
                        backgroundStatusRequest);

        setContentView(R.layout.activity_main);
    }

    public void close(View view) {
        finish();
    }

    public void settings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}