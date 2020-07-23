package com.example.gallery.services;


import com.example.gallery.data.Picture;
import com.example.gallery.data.User;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface GalleryService {


    @GET("getPicture")
    Call<ResponseBody> getAPicture(@Query("image_name") String imageName);



    @GET("getAllPictures")
    Call<List<Picture>> getAllPictures(@Query("userExternalId") String userExternalId);



    @Multipart
    @POST("addImage")
    Call<Picture> saveImage(@Part MultipartBody.Part image,
                            @Part("image_name") RequestBody imageName,
                            @Part("image_date") RequestBody imageDate,
                            @Part ("user_id") RequestBody userId);


    @GET("searchUser")
    Call<Boolean> searchUser(@Query("userName") String userName,
                             @Query("userPassword")String userPassword);

    @POST("addUser")
    Call<User> addUser(@Body User user);



    @GET("getUser")
    Call<User> getUser(@Query("userName") String userName,
                       @Query("userPassword")String userPassword);

    @DELETE("deleteAPicture")
    Call<Void> deleteAPicture(@Query("picture_id") int pictureId,
                              @Query("user_external_id") String userExternalId,
                              @Query("picture_name") String pictureName);


}
