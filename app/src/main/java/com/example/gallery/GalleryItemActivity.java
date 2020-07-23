package com.example.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.gallery.data.Picture;
import com.example.gallery.data.Storage;
import com.example.gallery.data.StorageImpl;
import com.example.gallery.data.User;
import com.example.gallery.services.GalleryService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GalleryItemActivity extends Activity {
    private Retrofit retrofit;
    private GalleryService galleryService;
    private String url = "http://192.168.2.243:8080/fotos_war_exploded/";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_item);
        ImageView imageView=findViewById(R.id.gallery_item);
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        galleryService = retrofit.create(GalleryService.class);
        Intent intent=getIntent();
        Bitmap imageBitmap=null;
        if(intent.hasExtra("image")){
          int i=intent.getIntExtra("image", 0);
          imageBitmap = new GalleryActivity().imageAdapter.bitmaps.get(i);
          imageView.setImageBitmap(imageBitmap);

        }
        findViewById(R.id.delete_a_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Storage storage = new StorageImpl();
                User user = (User) storage.get(GalleryItemActivity.this, "currentUser", User.class);
                Call<User> call2 = galleryService.getUser(user.getUserName(), user.getUserPassword());
                call2.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user1 = response.body();
                        Call<List<Picture>> call1 = galleryService.getAllPictures(user1.getExternalId());
                        ArrayList<String> picturesNames = new ArrayList<>();
                        call1.enqueue(new Callback<List<Picture>>() {
                            @Override
                            public void onResponse(Call<List<Picture>> call, Response<List<Picture>> response) {
                                List<Picture> pictures = response.body();
                                int i=intent.getIntExtra("image", 0);
                                int id=pictures.get(i).getId();
                                String name=pictures.get(i).getPictureName();
                                Call<Void> call3=galleryService.deleteAPicture(id, user1.getExternalId(), name);
                                call3.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Toast.makeText(GalleryItemActivity.this,
                                                "Picture has been deleted",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent1=new Intent(GalleryItemActivity.this, GalleryActivity.class);
                                        startActivity(intent1);

                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {

                                    }
                                });


                            }

                            @Override
                            public void onFailure(Call<List<Picture>> call, Throwable t) {

                            }
                        });

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });


    }


});
    }
}
