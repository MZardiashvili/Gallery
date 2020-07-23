package com.example.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.gallery.data.User;
import com.example.gallery.services.GalleryService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends Activity {
    private EditText mUserName;
    private EditText mUserPassword;
    private Retrofit retrofit;
    private GalleryService galleryService;
    private String url = "http://192.168.2.243:8080/fotos_war_exploded/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mUserName=findViewById(R.id.register_username);
        mUserPassword=findViewById(R.id.register_password);
        retrofit= new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        galleryService =retrofit.create(GalleryService.class);
    }

    public void signUp(View view){
        String userName=mUserName.getText().toString();
        String userPassword=mUserPassword.getText().toString();
        final User user=new User();
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        Call<Boolean> call=galleryService.searchUser(userName, userPassword);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Boolean bool=response.body();
                if(bool==true){
                    Toast.makeText(RegisterActivity.this, "User Already Exists", Toast.LENGTH_SHORT).show();
                }
                else
                {
                  Call<User> call1=galleryService.addUser(user);
                  call1.enqueue(new Callback<User>() {
                      @Override
                      public void onResponse(Call<User> call, Response<User> response) {
                          Toast.makeText(RegisterActivity.this, "User has been successfully registered", Toast.LENGTH_SHORT).show();
                      }

                      @Override
                      public void onFailure(Call<User> call, Throwable t) {}
                  });

                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }


    public void alreadyRegistered(View view){
        Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
