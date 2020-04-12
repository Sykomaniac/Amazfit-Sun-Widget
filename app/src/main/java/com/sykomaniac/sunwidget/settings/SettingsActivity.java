package com.sykomaniac.sunwidget.settings;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.sykomaniac.sunwidget.R;
import com.sykomaniac.sunwidget.worker.StatusUpdateWorker;

import java.util.ArrayList;
import java.util.List;

import static com.sykomaniac.sunwidget.Constants.CONFIGURATION_PREFERENCE_FILE_NAME;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final RecyclerView root = new RecyclerView(this);
        mContext = getApplicationContext();
        sharedPreferences = getSharedPreferences(CONFIGURATION_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        List<BaseSetting> settings = new ArrayList<>();
        settings.add(new HeaderSetting(getString(R.string.settings)));

        settings.add(new IconSetting(getDrawable(android.R.drawable.ic_dialog_map),
                getString(R.string.set_loc), getString(R.string.set_loc_c),
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMessage(mContext.getString(R.string.location_refresh));
                Location loc = setLocationFromWatch();
                if (loc == null) {
                    displayMessage(mContext.getString(R.string.location_refresh_failure));
                } else {
                    sharedPreferences.edit()
                            .putFloat("latitude", (float)loc.getLatitude())
                            .putFloat("longitude", (float)loc.getLongitude())
                            .apply();
                    displayMessage(mContext.getString(R.string.location_refresh_success));
                }
            }
        }));

        settings.add(new IconSetting(getDrawable(R.drawable.method_type), getString(R.string.set_widget_type), getString(R.string.set_widget_type_c), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, TypeActivity.class));
            }
        }));

        settings.add(new IconSetting(getDrawable(android.R.drawable.ic_popup_sync),
                getString(R.string.set_refresh), getString(R.string.set_refresh_c),
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMessage(mContext.getString(R.string.refresh_time));

                boolean updateLocation = sharedPreferences.getBoolean("forced", false);
                if (updateLocation) {
                    Location loc = setLocationFromWatch();
                    if (loc == null) {
                        displayMessage(mContext.getString(R.string.location_refresh_failure));
                    } else {
                        sharedPreferences.edit()
                                .putFloat("latitude", (float)loc.getLatitude())
                                .putFloat("longitude", (float)loc.getLongitude())
                                .commit();
                        calculateTimes();
                    }
                } else {
                    calculateTimes();
                }
            }
        }));

        settings.add(new SwitchSetting(null,
                getString(R.string.set_force_refresh),
                getString(R.string.set_force_refresh_c),
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sharedPreferences.edit().putBoolean("forced", b).apply();
            }
        }, sharedPreferences.getBoolean("forced", false)));

        //Setup layout
        root.setLayoutManager(new LinearLayoutManager(this));
        root.setAdapter(new Adapter(this, settings));
        root.setPadding((int) getResources().getDimension(R.dimen.padding_round_small), 0, (int) getResources().getDimension(R.dimen.padding_round_small), (int) getResources().getDimension(R.dimen.padding_round_large));
        root.setClipToPadding(false);
        setContentView(root);
    }

    private Location setLocationFromWatch() {
        try {
            LocationManager mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            assert mLocationManager != null;
            boolean gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Location networkLocation = null, gpsLocation = null, finalLocation = null;

            if (gps_enabled)
                gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (network_enabled)
                networkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (gpsLocation != null && networkLocation != null) {
                if (gpsLocation.getAccuracy() > networkLocation.getAccuracy())
                    finalLocation = networkLocation;
                else
                    finalLocation = gpsLocation;
            } else {
                if (gpsLocation != null) {
                    finalLocation = gpsLocation;
                } else if (networkLocation != null) {
                    finalLocation = networkLocation;
                }
            }
            return finalLocation;
        } catch (SecurityException e) {
            return null;
        }
    }

    private void calculateTimes() {
        OneTimeWorkRequest sunTimesRequest = new OneTimeWorkRequest.Builder(
                StatusUpdateWorker.class)
                .build();

        WorkManager.getInstance()
                .enqueue(sunTimesRequest);

        WorkManager.getInstance()
                .getWorkInfoByIdLiveData(sunTimesRequest.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            displayMessage(mContext.getString(R.string.refresh_time_success));
                        } else if (workInfo != null && workInfo.getState() == WorkInfo.State.FAILED) {
                            displayMessage(mContext.getString(R.string.refresh_time_failure));
                        }
                    }
                });
    }

    private void displayMessage(String message) {
        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}