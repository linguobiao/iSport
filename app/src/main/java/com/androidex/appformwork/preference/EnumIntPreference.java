package com.androidex.appformwork.preference;

import android.content.SharedPreferences;

/** 枚举型设置参数保存，适用于多选一。 */
public class EnumIntPreference<E extends Enum<E>> extends CommonPreference<E> {
    private final E[] values;
    private final SharedPreferences mGlobalPreferences;

    private EnumIntPreference(SharedPreferences preferences, String id, E defaultValue, E[] values)
    {
        super(id, defaultValue);
        this.values = values;
        this.mGlobalPreferences = preferences;
    }

    @Override
    public E getValue() {
        try {
            int i = mGlobalPreferences.getInt(getId(), -1);
            if (i >= 0 && i < values.length) {
                return values[i];
            }
        } catch (ClassCastException ex) {
            setValue(getDefaultValue());
        }
        return getDefaultValue();
    }

    @Override
    public boolean setValue(E val) {
        return mGlobalPreferences.edit().putInt(getId(), val.ordinal()).commit();
    }
}