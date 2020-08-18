package com.flipoku.Util;

import android.content.Context;

import com.flipoku.Domain.Board;
import com.flipoku.Domain.Statistics;
import com.flipoku.Domain.UserPreferences;
import com.flipoku.R;
import com.flipoku.Repository.UserPreferencesRepository;

public class UserPreferencesHelper {
    private Context context;
    private UserPreferencesRepository repository;

    public UserPreferencesHelper(Context context) {
        this.context = context;
        repository = UserPreferences.getRepository(context);
    }

    public void saveChronometerRunning(boolean value) {
        repository.save(getUserPreferencesAndSetValue("ChronometerRunning", value));
    }

    public boolean getChronometerRunning() {
        return (boolean) getFunctionImplementation("ChronometerRunning", false);
    }

    public void savePlayingBoard(Board value) {
        repository.save(getUserPreferencesAndSetValue("PlayingBoard", value));
    }

    public Board getPlayingBoard() {
        return (Board) getFunctionImplementation("PlayingBoard", null);
    }

    public void saveTimerVisibility(boolean value) {
        repository.save(getUserPreferencesAndSetValue("TimerVisibility", value));
    }

    public boolean getTimerVisibility() {
        return (boolean) getFunctionImplementation("TimerVisibility", true);
    }

    public void saveLinesBlack(boolean value) {
        repository.save(getUserPreferencesAndSetValue("LinesBlack", value));
    }

    public boolean getLinesBlack() {
        return (boolean) getFunctionImplementation("LinesBlack", true);
    }

    public void saveColorModeIndex(int value) {
        repository.save(getUserPreferencesAndSetValue("ColorModeIndex", value));//0 dia, 1 noche, 2 custom
    }

    public int getColorModeIndex() {
        return (int) getFunctionImplementation("ColorModeIndex", 0);
    }

    public void saveBackgroundColor(int value) {
        repository.save(getUserPreferencesAndSetValue("BackgroundColor", value));
    }

    public int getBackgroundColor() {
        return (int) getFunctionImplementation("BackgroundColor", 0);
    }

    public void saveTextColor(int value) {
        repository.save(getUserPreferencesAndSetValue("TextColor", value));
    }

    public int getTextColor() {
        return (int) getFunctionImplementation("TextColor", -16777216);
    }

    private Object getFunctionImplementation(String text, Object defaultValue) {
        UserPreferences userPreferences = getUserPreferences(text);
        if (userPreferences != null) {
            return userPreferences.getValue();
        }// si no esta creado se crea uno, se guarda y se devuelve el creado
        userPreferences = repository.save(new UserPreferences(text, defaultValue));
        return userPreferences.getValue();
    }

    private UserPreferences getUserPreferencesAndSetValue(String name, Object value) {
        UserPreferences userPreferences = getUserPreferences(name);
        if (userPreferences != null) {
            userPreferences.setValue(value);
        } else {
            userPreferences = new UserPreferences(name, value);
        }
        return userPreferences;
    }

    private UserPreferences getUserPreferences(String name) {
        return (UserPreferences) repository.findFirstByKey(name);
    }
}
