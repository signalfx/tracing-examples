package com.splunk.rum.demoApp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
@SuppressWarnings("ALL")
public class PreferenceHelper {

    private PreferenceHelper() {
    }

    private static void edit(Context context, Performer<SharedPreferences.Editor> performer) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        performer.performOperation(editor);
        editor.apply();
    }

    public static <T> void setValue(Context context, String key, T value) {
        if (value instanceof String) {
            edit(context, (editor) -> editor.putString(key, (String) value));
        } else if (value instanceof Boolean) {
            edit(context, (editor) -> editor.putBoolean(key, (Boolean) value));
        } else if (value instanceof Integer) {
            edit(context, (editor) -> editor.putInt(key, (Integer) value));
        } else if (value instanceof Float) {
            edit(context, (editor) -> editor.putFloat(key, (Float) value));
        } else {
            throw new UnsupportedOperationException("Not yet implemented.");
        }
    }

    public static <T> T getValue(Context context, String key, Class<?> aClass, T defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Object value;
        if (aClass.equals(String.class)) {
            value = sharedPreferences.getString(key, (String) defaultValue);
        } else if (aClass.equals(Boolean.class)) {
            value = sharedPreferences.getBoolean(key, (Boolean) defaultValue);
        } else if (aClass.equals(Integer.class)) {
            value = sharedPreferences.getInt(key, (Integer) defaultValue);
        } else if (aClass.equals(Float.class)) {
            value = sharedPreferences.getFloat(key, (Float) defaultValue);
        } else {
            throw new UnsupportedOperationException("Not yet implemented.");
        }
        //noinspection unchecked
        return (T) value;
    }

    public static void removeKey(Context context,String key){
        edit(context,
                (editor) -> editor.remove(key).apply());
    }

    public static void clearPreference(Context context) {
        edit(context,
                (editor) -> editor.clear().apply());
    }


    public interface Performer<T> {
        void performOperation(T victim);
    }
}