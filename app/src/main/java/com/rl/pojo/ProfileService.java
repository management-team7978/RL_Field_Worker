package com.rl.pojo;

import com.rl.pojo.EditProfile;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ProfileService {
    @Multipart
    @POST("profile_img.php")
    Call<EditProfile> register(
            @Part("uuid") RequestBody uuid,
            @Part("address") RequestBody address,
            @Part("pincode") RequestBody pincode,
            @Part MultipartBody.Part profile_img
    );
}
