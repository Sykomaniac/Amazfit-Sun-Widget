package com.sykomaniac.sunwidget.broadcasts;

import android.arch.lifecycle.LifecycleObserver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.sykomaniac.sunwidget.worker.StatusUpdateWorker;

import java.util.concurrent.TimeUnit;

public class BootDeviceReceiver extends BroadcastReceiver implements LifecycleObserver {

    private static final String TAG = "Boot";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
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

            // Immediate sync
            OneTimeWorkRequest sunTimesRequest = new OneTimeWorkRequest.Builder(
                    StatusUpdateWorker.class)
                    .build();

            WorkManager.getInstance()
                    .enqueue(sunTimesRequest);

            Log.d(TAG, "Device booted");
        }
    }
}
