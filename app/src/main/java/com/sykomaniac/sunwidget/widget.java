package com.sykomaniac.sunwidget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sykomaniac.sunwidget.prefs.SharedPreferenceAPIClient;
import com.sykomaniac.sunwidget.util.AlwaysMarqueeTextView;

import clc.sliteplugin.flowboard.AbstractPlugin;
import clc.sliteplugin.flowboard.ISpringBoardHostStub;

public class widget extends AbstractPlugin {

    // Tag for logging purposes.
    private final static String TAG = "SunWidget";

    // Activity variables
    private boolean isActive = false;
    private Context mContext;
    private View mView;
    private TextView sunriseView;
    private TextView sunsetView;
    private AlwaysMarqueeTextView updatedView;

    // Set up the widget's layout
    @Override
    public View getView(Context paramContext) {
        this.mContext = paramContext;
        this.mView = LayoutInflater.from(paramContext).inflate(R.layout.widget, null);

        Log.d(widget.TAG, "Starting widget...");
        this.init();

        Log.d(widget.TAG, "Done...");
        return this.mView;
    }

    // Initialize widget
    private void init() {
        sunriseView = this.mView.findViewById(R.id.sunrise);
        sunsetView = this.mView.findViewById(R.id.sunset);
        updatedView = this.mView.findViewById(R.id.updated);
    }

    /*
     * Widget active/deactivate state management
     */
    private void onShow() {
        // If view loaded (and was inactive)
        if (this.mView != null && !this.isActive) {
            // Get widget values from settings
            SharedPreferenceAPIClient apiClient = new SharedPreferenceAPIClient(this.mContext, this.mContext.getString(R.string.api_authority));
            sunriseView.setText(apiClient.getString("sunUp", R.string.sunrise_time));
            sunsetView.setText(apiClient.getString("sunDown", R.string.sunset_time));
            updatedView.setText(apiClient.getString("updated", R.string.update_time));
        }

        this.isActive = true;
    }

    private void onHide() {
        this.isActive = false;
    }

    // Events for widget hide
    @Override
    public void onInactive(Bundle paramBundle) {
        super.onInactive(paramBundle);
        this.onHide();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.onHide();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.onHide();
    }

    // Events for widget show
    @Override
    public void onActive(Bundle paramBundle) {
        super.onActive(paramBundle);
        this.onShow();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.onShow();
    }

    /*
     * Below where are unchanged functions that the widget should have
     */
    // Return the icon for this page, used when the page is disabled in the app list. In this case, the launcher icon is used
    @Override
    public Bitmap getWidgetIcon(Context paramContext) {
        return ((BitmapDrawable) this.mContext.getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap();
    }

    // Return the launcher intent for this page. This might be used for the launcher as well when the page is disabled?
    @Override
    public Intent getWidgetIntent() {
        return new Intent();
    }

    // Return the title for this page, used when the page is disabled in the app list. In this case, the app name is used
    @Override
    public String getWidgetTitle(Context paramContext) {
        return this.mContext.getResources().getString(R.string.app_name);
    }

    // Save springboard host
    private ISpringBoardHostStub host = null;

    // Returns the springboard host
    public ISpringBoardHostStub getHost() {
        return this.host;
    }

    // Called when the page is loading and being bound to the host
    @Override
    public void onBindHost(ISpringBoardHostStub paramISpringBoardHostStub) {
        //Store host
        this.host = paramISpringBoardHostStub;
    }

    // Not sure what this does, can't find it being used anywhere. Best leave it alone
    @Override
    public void onReceiveDataFromProvider(int paramInt, Bundle paramBundle) {
        super.onReceiveDataFromProvider(paramInt, paramBundle);
    }

    // Called when the page is destroyed completely (in app mode). Same as the onDestroy method of an activity
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}