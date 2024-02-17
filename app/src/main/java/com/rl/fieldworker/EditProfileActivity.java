package com.rl.fieldworker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.util.FileUriUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.rl.network.NetworkChangeListener;
import com.rl.pojo.EditProfile;
import com.rl.pojo.ProfileService;
import com.rl.util.SharedPreference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileActivity extends AppCompatActivity {
    CircleImageView imgProfile;
    String name="",userId="",address="",pincode="",profile_path="";
    TextInputEditText edtAddress,edtPincode;
    AppCompatButton btSubmit;
    ImageView imgBack;
    String[]  required_permission = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
    boolean is_storage_image_permitted = false;
    PermissionListener permissionlistener;
    String path1="",val="";
    RelativeLayout imgUpload,rlLoader;
    TextView TvName;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        edtAddress=findViewById(R.id.edtAddress);
        edtPincode=findViewById(R.id.edtPincode);
        btSubmit=findViewById(R.id.btSubmit);
        imgBack=findViewById(R.id.imgBack);
        imgUpload=findViewById(R.id.imgUpload);
        rlLoader=findViewById(R.id.rlLoader);
        TvName=findViewById(R.id.TvName);
        imgProfile = findViewById(R.id.imgProfile);

        Intent i =getIntent();
        if (i.hasExtra("userId")){
            name=i.getStringExtra("name");
            address=i.getStringExtra("address");
            pincode=i.getStringExtra("pincode");
            edtAddress.setText(address);
            edtPincode.setText(pincode);
            TvName.setText(name);
            profile_path=i.getStringExtra("profile_path");
        }
        if (profile_path == null || profile_path.isEmpty()) {
            imgProfile.setImageResource(R.drawable.img_profile_user);
        } else {
            Picasso.get().load(profile_path).noFade()
                    .placeholder(R.drawable.progress_animation).into(imgProfile);
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(EditProfileActivity.this, MainActivity.class);
                i.putExtra("redirect","1");
                startActivity(i);
                finish();
            }
        });

        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

                if (val.equals("1")){
                    ImagePicker.Companion.with(EditProfileActivity.this)
                            .crop()
                            .compress(500)
                            .maxResultSize(1080, 1080)
                            .galleryMimeTypes(new String[]{
                                    "image/png",
                                    "image/jpg",
                                    "image/jpeg"
                            })
                            .start(178);
                }
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(EditProfileActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val="1";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // This is Android 13 or higher
                    if (!checkAllPermission()) {
                        requestPermissionStorageImage();
                    } else {
                        ImagePicker.Companion.with(EditProfileActivity.this)
                                .crop()
                                .compress(500)
                                .maxResultSize(1080, 1080)
                                .galleryMimeTypes(new String[]{
                                        "image/png",
                                        "image/jpg",
                                        "image/jpeg"
                                })
                                .start(178);
                        // Toast.makeText(EditProfileActivity.this, "Granted...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // This is Android version lower than 13
                    TedPermission.create()
                            .setPermissionListener(permissionlistener)
                            .setDeniedMessage("If you reject permission, you cannot use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                            .setPermissions(
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA
                            )
                            .check();
                }
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address= edtAddress.getText().toString();
                String pincode= edtPincode.getText().toString();
                if (address.isEmpty()||pincode.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
                } else if (path1.equals("")) {
                    Toast.makeText(EditProfileActivity.this, "Please upload Image", Toast.LENGTH_SHORT).show();
                }else if (!isValidPincode(pincode)) {
                    Toast.makeText(EditProfileActivity.this, "Invalid Pincode. Please enter a valid pincode.", Toast.LENGTH_SHORT).show();
                } else {
                    EditConsumerProfile(SharedPreference.get("uuid"),address,path1,pincode);
                }

            }
        });
    }


    // Method to validate pincode
    private boolean isValidPincode(String pincode) {
        // Add your pincode validation logic here
        // For example, checking if it's a numeric value and has a specific length
        return pincode.matches("\\d{6}"); // Assuming pincode should be a 6-digit number
    }


    private void EditConsumerProfile(String consumerUuid, String address, String path1, String pincode) {
        rlLoader.setVisibility(View.VISIBLE);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://rlwork.in/api_gfhjfyRETfkmghTYudgnm/field_work_sfrfregRGdfghtRTr/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        try {
            File fileAdharFront = new File(path1);
            RequestBody requestFileAdharFront = RequestBody.create(MediaType.parse("image/*"), fileAdharFront);
            MultipartBody.Part bodyAdharFront = MultipartBody.Part.createFormData("profile_img", fileAdharFront.getName(), requestFileAdharFront);

            RequestBody u_uuid = RequestBody.create(MediaType.parse("text/plain"), consumerUuid);
            RequestBody u_address = RequestBody.create(MediaType.parse("text/plain"), address);
            RequestBody u_pincode = RequestBody.create(MediaType.parse("text/plain"), pincode);

            ProfileService profileService = retrofit.create(ProfileService.class);
            Call<EditProfile> call = profileService.register(u_uuid, u_address, u_pincode, bodyAdharFront);

            Log.i("pri", "request parameter=> " + call);
            call.enqueue(new Callback<EditProfile>() {
                @Override
                public void onResponse(Call<EditProfile> call, Response<EditProfile> response) {
                    rlLoader.setVisibility(View.GONE);
                    Log.i("pri", "profile response code => " + response.code());
                    if (response.isSuccessful()) {
                        EditProfile editProfile = response.body();
                        if (editProfile != null) {
                            Log.i("pri", "profile response body => " + new Gson().toJson(editProfile));

                            if (editProfile.getStatus()) {
                                openCustomSuccessDialog(editProfile.getMessage());
                            } else {
                                String errorMessage = editProfile.getMessage();
                                Toast.makeText(EditProfileActivity.this, "" + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.i("pri", "response not successful=>");
                    }
                }

                @Override
                public void onFailure(Call<EditProfile> call, Throwable t) {
                    t.printStackTrace();
                    rlLoader.setVisibility(View.GONE);
                }
            });
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
    }

    public boolean checkAllPermission(){
        return is_storage_image_permitted;
    }

    public void requestPermissionStorageImage(){
        if (ContextCompat.checkSelfPermission(EditProfileActivity.this,required_permission[0])== PackageManager.PERMISSION_GRANTED){
            Log.i("pri","granted.."+required_permission[0]);
            is_storage_image_permitted = true;

        }else {
            request_permission_launcher_storage_image.launch(required_permission[0]);
        }
    }

    private ActivityResultLauncher<String> request_permission_launcher_storage_image=
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted->{
                        if (isGranted){
                            Log.i("pri","granted.."+required_permission[0]);
                            is_storage_image_permitted = true;
                        }else {
                            Log.i("pri","denied.."+required_permission[0]);
                            is_storage_image_permitted = false;

                            sendToSettingDialog();

                        }
                    });

    private void sendToSettingDialog() {
        new AlertDialog.Builder(EditProfileActivity.this)
                .setTitle("Alert for permission")
                .setMessage("Go to setting for permission")
                .setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int in) {
                        Intent i = new Intent();
                        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",getPackageName(),null);
                        i.setData(uri);
                        startActivity(i);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 178) {
                Uri uri1 = data.getData();
                if (uri1 != null) {
                    path1 = FileUriUtils.INSTANCE.getRealPath(this, uri1);
//                    tvAdharFrontPath.setVisibility(View.VISIBLE);
//                    tvAdharFrontPath.setText("Selected");
                    imgProfile.setImageURI(uri1);
                    Log.i("pri", "path 1 =>" + path1);
                }
            }
        }
    }

    private void openCustomSuccessDialog(String text) {
        if (!isFinishing()) { // Check if the activity is still active
            final Dialog dialog = new Dialog(EditProfileActivity.this);
            dialog.setContentView(R.layout.popup_success_design);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

            TextView textViewYes = (TextView) dialog.findViewById(R.id.textViewYes);
            TextView txtMsg = (TextView) dialog.findViewById(R.id.txtMsg);

            txtMsg.setText(text);

            textViewYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(EditProfileActivity.this, MainActivity.class);
                    i.putExtra("redirect", "1");
                    startActivity(i);
                    finish();
                    dialog.dismiss();
                }
            });

            // show the exit dialog
            dialog.show();
        }
    }

    @Override
    protected void onStart() {
        IntentFilter filter= new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

}