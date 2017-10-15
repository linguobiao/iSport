package com.androidex.appformwork.preference;

import android.content.SharedPreferences;

/** String参数保存 */
public class StringPreference extends CommonPreference<String> {
    private final SharedPreferences mGlobalPreferences;

    public StringPreference(SharedPreferences preferences, String id, String defaultValue)
    {
        super(id, defaultValue);
        this.mGlobalPreferences = preferences;
    }

    @Override
    public String getValue() {
        return mGlobalPreferences.getString(getId(), getDefaultValue());
    }

    @Override
    public boolean setValue(String val) {
        return mGlobalPreferences.edit().putString(getId(), val).commit();
    }
}