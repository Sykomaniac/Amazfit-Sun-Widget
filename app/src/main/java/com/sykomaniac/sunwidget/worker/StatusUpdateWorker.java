package com.sykomaniac.sunwidget.worker;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static ca.rmen.sunrisesunset.SunriseSunset.getAstronomicalTwilight;
import static ca.rmen.sunrisesunset.SunriseSunset.getCivilTwilight;
import static ca.rmen.sunrisesunset.SunriseSunset.getNauticalTwilight;
import static ca.rmen.sunrisesunset.SunriseSunset.getSunriseSunset;
import static com.sykomaniac.sunwidget.Constants.CONFIGURATION_PREFERENCE_FILE_NAME;

public class StatusUpdateWorker extends Worker {
    private static final String TAG = "StatusUpdateWorker";
    private Context mContext;

    // Constants
    private static final String TIME_PATTERN_24H = "HH:mm";
    private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm";

    public StatusUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Calculating sunrise and sunset times");
        Log.d(TAG, mContext.getPackageName());
        SharedPreferences sharedPreferences =
                mContext.getSharedPreferences(CONFIGURATION_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        final String type = sharedPreferences.getString("widget_type_pref", "Dawn");
        final float latitude = sharedPreferences.getFloat("latitude", -1);
        final float longitude = sharedPreferences.getFloat("longitude", -1);

        if (latitude == -1 || longitude == -1) {
            return Result.failure();
        }

        Calendar[] sunriseSunset;
        switch (type.toUpperCase()) {
            case "ASTRONOMICAL":
                sunriseSunset = getAstronomicalTwilight(Calendar.getInstance(), latitude, longitude);
                break;
            case "CIVIL":
                sunriseSunset = getCivilTwilight(Calendar.getInstance(), latitude, longitude);
                break;
            case "NAUTICAL":
                sunriseSunset = getNauticalTwilight(Calendar.getInstance(), latitude, longitude);
                break;
            default:
                sunriseSunset = getSunriseSunset(Calendar.getInstance(), latitude, longitude);
        }

        String sunrise = dateToTimeString(sunriseSunset[0]);
        String sunset = dateToTimeString(sunriseSunset[1]);
        String updated = dateToString(Calendar.getInstance());

        // Store for sharing to widget
        sharedPreferences.edit()
                .putString("sunUp", sunrise)
                .putString("sunDown", sunset)
                .putString("widgetType", type)
                .putString("updated", updated)
                .apply();

        return Result.success();
    }

    // Convert a date to format
    private String dateToString (Calendar date) {
        return (new SimpleDateFormat(DATE_PATTERN, Locale.UK)).format(date.getTime());
    }

    private String dateToTimeString (Calendar date) {
        return (new SimpleDateFormat(TIME_PATTERN_24H, Locale.UK)).format(date.getTime());
    }
}
