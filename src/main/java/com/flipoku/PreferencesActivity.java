package com.flipoku;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.flipoku.Util.StaticMethodsHelper;
import com.flipoku.Util.UserPreferencesHelper;

public class PreferencesActivity extends AppCompatActivity {
    private Toolbar preferencesToolbar;
    private UserPreferencesHelper preferences;
    private Switch timerSwitch;
    private TextView timerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new UserPreferencesHelper(this);
        setContentView(R.layout.activity_preferences);
        preferencesToolbar = (Toolbar) findViewById(R.id.preferencesToolbar);
        timerSwitch = (Switch) findViewById(R.id.timerSwitch);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        addListeners();
        setSupportActionBar(preferencesToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        refreshView();
    }

    private void addListeners() {
        timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.saveTimerVisibility(isChecked);
                refreshView();
            }
        });
    }

    private void refreshView() {
        boolean isTimerVisible = preferences.getTimerVisibility();
        StaticMethodsHelper.setSwitchValues(isTimerVisible, timerSwitch, timerTextView, getResources().getString(R.string.timer_show), getResources().getString(R.string.timer_hide));
    }
}