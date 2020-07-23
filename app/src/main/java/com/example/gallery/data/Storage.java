package com.example.gallery.data;


import android.content.Context;

public interface Storage {
    void save(Context context, String key, Object value);
    Object get(Context context, String key, Class klass);
}
