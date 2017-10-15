package com.androidex.appformwork.preference;

public abstract class CommonPreference<T> {
    private final String id;
    private T defaultValue;

    /**
     * @param id
     *            数据保存的Key
     * @param defaultValue
     *            初始值
     */
    public CommonPreference(String id, T defaultValue)
    {
        this.id = id;
        this.defaultValue = defaultValue;
    }

    protected T getDefaultValue() {
        return defaultValue;
    }

    public abstract T getValue();

    public abstract boolean setValue(T val);

    public String getId() {
        return id;
    }

    /** 重置为初始值 */
    public void resetToDefault() {
        setValue(getDefaultValue());
    }
}