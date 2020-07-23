package com.example.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.example.gallery.adapters.ImageAdapter;
import com.example.gallery.data.Picture;
import com.example.gallery.data.Storage;
import com.example.gallery.data.StorageImpl;
import com.example.gallery.data.User;
import com.example.gallery.services.GalleryService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GalleryActivity extends Activity {
    static final int SELECT_IMAGE = 1000;
    private static final int CAMERA_REQUEST = 1001;
    private String url = "http://192.168.2.243:8080/fotos_war_exploded/";
    private GridView gridView;
    public static ImageAdapter imageAdapter;
    private Retrofit retrofit;
    private GalleryService galleryService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        gridView = findViewById(R.id.grid_view);
        imageAdapter = new ImageAdapter(this);
        imageAdapter.bitmaps=new ArrayList<>();
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        galleryService = retrofit.create(GalleryService.class);
        Storage storage = new StorageImpl();
        User user = (User) storage.get(this, "currentUser", User.class);
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
                        for (Picture picture : pictures) {
                            picturesNames.add(picture.getPictureName());
                            }
                            showResponse(picturesNames);

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


        findViewById(R.id.take_picture_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(GalleryActivity.this, GalleryItemActivity.class);
                intent.putExtra("image", i);
                startActivity(intent);


            }
        });
    }

    public void logOut(View view){
        Storage storage=new StorageImpl();
        storage.save(this, "currentUser", null);
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void showResponse(ArrayList<String> names) {
        imageAdapter.bitmaps = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            Call<ResponseBody> call1 = galleryService.getAPicture(names.get(i));
            call1.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    InputStream inputStream = response.body().byteStream();
                    final Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);
                    imageAdapter.bitmaps.add(selectedImage);
                    gridView.setAdapter(imageAdapter);

                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }



    }



    public void choosePictures(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Storage storage = new StorageImpl();
        Picture picture = new Picture();
        User user = (User) storage.get(this, "currentUser", User.class);
        if (requestCode == SELECT_IMAGE) {
            Uri imageUri=data.getData();
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap imageBitmap=BitmapFactory.decodeStream(inputStream);
            imageAdapter.bitmaps.add(imageBitmap);
            gridView.setAdapter(imageAdapter);
            Call<User> call2 = galleryService.getUser(user.getUserName(), user.getUserPassword());
            call2.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user1 = response.body();
                    picture.setUserId(user1.getUserId());
                    picture.setUserExternalId(user1.getExternalId());
                    String filePath = data.getData().getPath();
                    File file = FileUtils.getFile(filePath);
                    String random = UUID.randomUUID().toString();
                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), random);
                    RequestBody date = RequestBody.create(MediaType.parse("text/plain"),
                            LocalDateTime.now().toString());
                    RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(picture.getUserId()));
                    RequestBody requestFile =
                            null;

                    try {
                        requestFile = RequestBody.create(
                                MediaType.parse(getContentResolver().getType(imageUri)), IOUtils.toByteArray(getContentResolver().openInputStream(imageUri)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
                    Call<Picture> call1 = galleryService.saveImage(body, name, date, userId);
                    call1.enqueue(new Callback<Picture>() {
                        @Override
                        public void onResponse(Call<Picture> call, Response<Picture> response) {
                            Toast.makeText(GalleryActivity.this, "Image has been succesfully added", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Picture> call, Throwable t) {
                        }
                    });

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }


            });
        }
        else if(requestCode==CAMERA_REQUEST){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageAdapter.bitmaps.add(imageBitmap);
            gridView.setAdapter(imageAdapter);
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), imageBitmap, "Title", null);
            Uri imageUri=Uri.parse(path);
            Call<User> call2 = galleryService.getUser(user.getUserName(), user.getUserPassword());
            call2.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user1 = response.body();
                    picture.setUserId(user1.getUserId());
                    picture.setUserExternalId(user1.getExternalId());
                    String filePath = imageUri.getPath();
                    File file = FileUtils.getFile(filePath);
                    String random = UUID.randomUUID().toString();
                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), random);
                    RequestBody date = RequestBody.create(MediaType.parse("text/plain"),
                            LocalDateTime.now().toString());
                    RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(picture.getUserId()));
                    RequestBody requestFile =
                            null;

                    try {
                        requestFile = RequestBody.create(
                                MediaType.parse(getContentResolver().getType(imageUri)), IOUtils.toByteArray(getContentResolver().openInputStream(imageUri)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
                    Call<Picture> call1 = galleryService.saveImage(body, name, date, userId);
                    call1.enqueue(new Callback<Picture>() {
                        @Override
                        public void onResponse(Call<Picture> call, Response<Picture> response) {
                            Toast.makeText(GalleryActivity.this, "Image has been succesfully added", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Picture> call, Throwable t) {
                        }
                    });

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }


            });
        }


    }

   public void deleteAllPictures(View view){
       Storage storage = new StorageImpl();
       User user = (User) storage.get(this, "currentUser", User.class);
       Call<User> call2 = galleryService.getUser(user.getUserName(), user.getUserPassword());
       call2.enqueue(new Callback<User>() {
           @Override
           public void onResponse(Call<User> call, Response<User> response) {
               User user1 = response.body();
               Call<List<Picture>> call1 = galleryService.getAllPictures(user1.getExternalId());
               call1.enqueue(new Callback<List<Picture>>() {
                   @Override
                   public void onResponse(Call<List<Picture>> call, Response<List<Picture>> response) {
                       List<Picture> pictures = response.body();
                       for (Picture picture : pictures) {
                           Call<Void> call3=galleryService.deleteAPicture(picture.getId(), user1.getExternalId(),
                                   picture.getPictureName());
                           call3.enqueue(new Callback<Void>() {
                               @Override
                               public void onResponse(Call<Void> call, Response<Void> response) {
                                   Toast.makeText(GalleryActivity.this, "Pictures have been deleted",
                                           Toast.LENGTH_SHORT).show();
                                   imageAdapter.bitmaps.clear();
                                   gridView.setAdapter(imageAdapter);
                               }

                               @Override
                               public void onFailure(Call<Void> call, Throwable t) {
                                        Toast.makeText(GalleryActivity.this, "Pictures can't be deleted",
                                                Toast.LENGTH_SHORT).show();
                               }
                           });
                       }

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
}


