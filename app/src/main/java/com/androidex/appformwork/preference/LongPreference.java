package com.androidex.appformwork.preference;

import android.content.SharedPreferences;

/** 长整型数值参数保存 */
public class LongPreference extends CommonPreference<Long> {
    private final SharedPreferences mGlobalPreferences;

    private LongPreference(SharedPreferences preferences, String id, long defaultValue)
    {
        super(id, defaultValue);
        this.mGlobalPreferences = preferences;
    }

    @Override
    public Long getValue() {
        return mGlobalPreferences.getLong(getId(), getDefaultValue());
    }

    @Override
    public boolean setValue(Long val) {
        return mGlobalPreferences.edit().putLong(getId(), val).commit();
    }
}