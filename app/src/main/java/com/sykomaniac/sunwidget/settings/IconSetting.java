package com.sykomaniac.sunwidget.settings;

import android.graphics.drawable.Drawable;
import android.view.View;

public class IconSetting extends BaseSetting {
    View.OnClickListener onClickListener;
    String title;
    String subtitle;
    Drawable icon;

    public IconSetting(Drawable icon, String title, String subtitle, View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
    }
}