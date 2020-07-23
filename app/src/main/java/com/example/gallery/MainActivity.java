package com.example.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.gallery.data.Storage;
import com.example.gallery.data.StorageImpl;
import com.example.gallery.data.User;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Storage storage=new StorageImpl();
        if(storage.get(MainActivity.this, "currentUser", User.class)!=null){
            Intent intent=new Intent(MainActivity.this, GalleryActivity.class);
            startActivity(intent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void signIn(View view){
        Intent intent=new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void signUp(View view){
        Intent intent=new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}
