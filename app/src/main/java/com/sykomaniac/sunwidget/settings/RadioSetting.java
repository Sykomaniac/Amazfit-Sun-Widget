package com.sykomaniac.sunwidget.settings;

import android.graphics.drawable.Drawable;
import android.widget.CompoundButton;

public class RadioSetting extends BaseSetting {

    int id;
    boolean isChecked;

    public void setChangeListener(CompoundButton.OnCheckedChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    CompoundButton.OnCheckedChangeListener changeListener;
    String title;
    String subtitle;
    Drawable icon;

    public RadioSetting(int id, Drawable icon, String title, String subtitle, CompoundButton.OnCheckedChangeListener changeListener, boolean isChecked) {
        this.id = id;
        this.changeListener = changeListener;
        this.isChecked = isChecked;
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
    }
}