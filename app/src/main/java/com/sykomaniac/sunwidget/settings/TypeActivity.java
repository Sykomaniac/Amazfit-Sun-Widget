package com.sykomaniac.sunwidget.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.sykomaniac.sunwidget.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sykomaniac.sunwidget.Constants.CONFIGURATION_PREFERENCE_FILE_NAME;

public class TypeActivity extends AppCompatActivity {

    private static final String WIDGET_TYPE_PREF = "widget_type_pref";
    private static final String DAWN = "Dawn";
    private static final String ASTRONOMICAL = "Astronomical";
    private static final String CIVIL = "Civil";
    private static final String NAUTICAL = "Nautical";
    private SharedPreferences sharedPreferences;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final RecyclerView root = new RecyclerView(this);
        mContext = getApplicationContext();
        sharedPreferences = getSharedPreferences(CONFIGURATION_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        // Add header to a list of settings
        List<BaseSetting> settings = new ArrayList<>();
        settings.add(new HeaderSetting(getString(R.string.type_settings_header)));

        //Setup items for each sunrise/set types
        final RadioSetting dawnDuskSetting = createRadioSetting(R.id.dawn_dusk_switch_id, R.string.type_dawn_dusk, DAWN);
        final RadioSetting astronomicalSetting = createRadioSetting(R.id.astronomical_switch_id, R.string.type_astronomical, ASTRONOMICAL);
        final RadioSetting civilSetting = createRadioSetting(R.id.civil_switch_id, R.string.type_civil, CIVIL);
        final RadioSetting nauticalSetting = createRadioSetting(R.id.nautical_switch_id, R.string.type_nautical, NAUTICAL);

        // Register change listeners
        dawnDuskSetting.setChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // HACK: This is not nice!
                String pref = sharedPreferences.getString(WIDGET_TYPE_PREF, "NA");

                if (b && !pref.equalsIgnoreCase(DAWN)) {
                    sharedPreferences.edit().putString(WIDGET_TYPE_PREF, DAWN).commit();
                    updateSwitchValues(true, false, false, false);
                }

                if (!b && pref.equalsIgnoreCase(DAWN)) {
                    resetSwitchValues(true, false, false, false);
                }
            }
        });

        astronomicalSetting.setChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // HACK: This is not nice!
                String pref = sharedPreferences.getString(WIDGET_TYPE_PREF, "NA");

                if (b && !pref.equalsIgnoreCase(ASTRONOMICAL)) {
                    sharedPreferences.edit().putString(WIDGET_TYPE_PREF, ASTRONOMICAL).commit();
                    updateSwitchValues(false, true, false, false);
                }

                if (!b && pref.equalsIgnoreCase(ASTRONOMICAL)) {
                    resetSwitchValues(false, true, false, false);
                }
            }
        });

        civilSetting.setChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // HACK: This is not nice!
                String pref = sharedPreferences.getString(WIDGET_TYPE_PREF, "NA");

                if (b&& !pref.equalsIgnoreCase(CIVIL)) {
                    sharedPreferences.edit().putString(WIDGET_TYPE_PREF, CIVIL).commit();
                    updateSwitchValues(false, false, true, false);
                }

                if (!b && pref.equalsIgnoreCase(CIVIL)) {
                    resetSwitchValues(false, false, true, false);
                }
            }
        });

        nauticalSetting.setChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // HACK: This is not nice!
                String pref = sharedPreferences.getString(WIDGET_TYPE_PREF, "NA");

                if (b && !pref.equalsIgnoreCase(NAUTICAL)) {
                    sharedPreferences.edit().putString(WIDGET_TYPE_PREF, NAUTICAL).commit();
                    updateSwitchValues(false, false, false, true);
                }

                if (!b && pref.equalsIgnoreCase(NAUTICAL)) {
                    resetSwitchValues(false, false, false, true);
                }
            }
        });

        // Add radio switches to settings
        settings.addAll(
                Arrays.asList(
                        dawnDuskSetting,
                        astronomicalSetting,
                        civilSetting,
                        nauticalSetting
                )
        );

        // Setup layout
        root.setLayoutManager(new LinearLayoutManager(this));
        root.setAdapter(new Adapter(this, settings));
        root.setPadding((int) getResources().getDimension(R.dimen.padding_round_small), 0, (int) getResources().getDimension(R.dimen.padding_round_small), (int) getResources().getDimension(R.dimen.padding_round_large));
        root.setClipToPadding(false);
        setContentView(root);
    }

    private void updateSwitchValues(final boolean dawn, final boolean astronomical,
                                    final boolean civil, final boolean nautical) {
        final Switch dawnDuskSwitch = findViewById(R.id.dawn_dusk_switch_id);
        final Switch astronomicalSwitch = findViewById(R.id.astronomical_switch_id);
        final Switch civilSwitch = findViewById(R.id.civil_switch_id);
        final Switch nauticalSwitch = findViewById(R.id.nautical_switch_id);

        if (!dawn && dawnDuskSwitch != null) {
            dawnDuskSwitch.setChecked(false);
        }
        if (!astronomical && astronomicalSwitch != null) {
            astronomicalSwitch.setChecked(false);
        }
        if (!civil && civilSwitch != null) {
            civilSwitch.setChecked(false);
        }
        if (!nautical && nauticalSwitch != null) {
            nauticalSwitch.setChecked(false);
        }

        displayMessage(mContext.getString(R.string.type_change_success));
    }

    private void resetSwitchValues(final boolean dawn, final boolean astronomical,
                                    final boolean civil, final boolean nautical) {
        final Switch dawnDuskSwitch = findViewById(R.id.dawn_dusk_switch_id);
        final Switch astronomicalSwitch = findViewById(R.id.astronomical_switch_id);
        final Switch civilSwitch = findViewById(R.id.civil_switch_id);
        final Switch nauticalSwitch = findViewById(R.id.nautical_switch_id);

        if (dawn && dawnDuskSwitch != null) {
            dawnDuskSwitch.setChecked(true);
        }
        if (astronomical && astronomicalSwitch != null) {
            astronomicalSwitch.setChecked(true);
        }
        if (civil && civilSwitch != null) {
            civilSwitch.setChecked(true);
        }
        if (nautical && nauticalSwitch != null) {
            nauticalSwitch.setChecked(true);
        }
    }

    private RadioSetting createRadioSetting(final int id, final int title, final String type) {
        return new RadioSetting(id,
                null,
                getString(title),
                null,
                null,
                sharedPreferences.getString(WIDGET_TYPE_PREF, "Dawn").equalsIgnoreCase(type));
    }

    private void displayMessage(String message) {
        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
