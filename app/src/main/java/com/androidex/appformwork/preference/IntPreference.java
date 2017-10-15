package com.androidex.appformwork.preference;

import android.content.SharedPreferences;

public class IntPreference extends CommonPreference<Integer> {
    private final SharedPreferences mGlobalPreferences;

    public IntPreference(SharedPreferences preferences, String id, int defaultValue)
    {
        super(id, defaultValue);
        this.mGlobalPreferences = preferences;
    }

    @Override
    public Integer getValue() {
        return mGlobalPreferences.getInt(getId(), getDefaultValue());
    }

    @Override
    public boolean setValue(Integer val) {
        return mGlobalPreferences.edit().putInt(getId(), val).commit();
    }
}