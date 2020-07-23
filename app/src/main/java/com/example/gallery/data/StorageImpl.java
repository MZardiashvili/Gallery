package com.example.gallery.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class StorageImpl implements Storage {
    private String CURRENT_USERS="cureent_users";


    @Override
    public void save(Context context, String key, Object object) {
        SharedPreferences sharedPreferences=getInstance(context);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(key, new Gson().toJson(object));
        editor.commit();
    }
    @Override
    public Object get( Context context, String key, Class klass) {
        SharedPreferences sharedPreferences=getInstance(context);
        String data=sharedPreferences.getString(key, null);
        return data==null ? null : new Gson().fromJson(data, klass);
    }

    private SharedPreferences getInstance(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences
                (CURRENT_USERS, Context.MODE_PRIVATE);
        return sharedPref;
    }
}