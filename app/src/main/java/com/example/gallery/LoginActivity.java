package com.example.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.gallery.data.Storage;
import com.example.gallery.data.StorageImpl;
import com.example.gallery.data.User;
import com.example.gallery.services.GalleryService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends Activity {
    private EditText mUserName;
    private EditText mUserPassword;
    private Retrofit retrofit;
    private GalleryService galleryService;
    private String url = "http://192.168.2.243:8080/fotos_war_exploded/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mUserName = findViewById(R.id.login_username);
        mUserPassword = findViewById(R.id.login_password);
        retrofit= new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        galleryService =retrofit.create(GalleryService.class);
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        String userName = String.valueOf(mUserName.getText());
        String userPassword = String.valueOf(mUserPassword.getText());
        final User user = new User();
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        Call<Boolean> call=galleryService.searchUser(userName, userPassword);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Boolean bool=response.body();
                if(bool!=null){
                    Storage storage = new StorageImpl();
                    storage.save(LoginActivity.this, "currentUser", user);
                    Intent intent = new Intent(LoginActivity.this, GalleryActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(LoginActivity.this,
                            "User doesn't exist go to register page",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });


    }
}
