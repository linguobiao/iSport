package com.androidex.appformwork.preference;

import android.content.SharedPreferences;

/** 布尔型参数保存 */
public class BooleanPreference extends CommonPreference<Boolean> {
    private final SharedPreferences mGlobalPreferences;

    private BooleanPreference(SharedPreferences preferences, String id, boolean defaultValue)
    {
        super(id, defaultValue);
        this.mGlobalPreferences = preferences;
    }

    @Override
    public Boolean getValue() {
        return mGlobalPreferences.getBoolean(getId(), getDefaultValue());
    }

    @Override
    public boolean setValue(Boolean val) {
        return mGlobalPreferences.edit().putBoolean(getId(), val).commit();
    }
}