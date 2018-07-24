package com.ristana.newspro.ui;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ristana.newspro.R;
import com.ristana.newspro.api.ProgressRequestBody;
import com.ristana.newspro.api.apiClient;
import com.ristana.newspro.api.apiRest;
import com.ristana.newspro.entity.ApiResponse;
import com.ristana.newspro.entity.Category;
import com.ristana.newspro.manager.PickerBuilder;
import com.ristana.newspro.manager.PrefManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AccountActivity extends AppCompatActivity implements ProgressRequestBody.UploadCallbacks{

    private Uri imagePath;
    private String          imageurl;
    private ContentValues values;
    private static final int CAMERA_REQUEST_IMAGE_1 = 3001;



    private Toolbar myToolbar;
    private PrefManager prf;
    private TextView header_text_view_name;
    private RelativeLayout relativeLayout_logout;
    private RelativeLayout relativeLayout_password;
    private RelativeLayout relativeLayout_edit;
    private RelativeLayout relativeLayout_photo;
    private String new_image_url_1="";
    private String new_image_id_1="";
    private ProgressBar profile_progress_image;
    private ImageView image_view_profile_img;
    private ImageButton Image_button_profile_pic;
    private RelativeLayout relativeLayout_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prf= new PrefManager(getApplicationContext());
        getCategories();
        initView();
        initAction();
        if (prf.getString("LOGGED").toString().equals("TRUE")){
            header_text_view_name.setText(prf.getString("NAME_USER").toString());

             Picasso.with(getApplicationContext())
                    .load(prf.getString("IMAGE_USER").toString())
                    .into(image_view_profile_img);

        }

    }
    public void initView(){
        setContentView(R.layout.activity_account);
        this.image_view_profile_img=(ImageView) findViewById(R.id.image_view_profile_img);
        this.profile_progress_image=(ProgressBar) findViewById(R.id.profile_progress_image);
        this.header_text_view_name=(TextView) findViewById(R.id.header_text_view_name);
        this.relativeLayout_logout=(RelativeLayout) findViewById(R.id.relativeLayout_logout);
        this.relativeLayout_edit=(RelativeLayout) findViewById(R.id.relativeLayout_edit);
        this.relativeLayout_password=(RelativeLayout) findViewById(R.id.relativeLayout_password );
        this.relativeLayout_photo=(RelativeLayout) findViewById(R.id.relativeLayout_photo);
        this.relativeLayout_profile=(RelativeLayout) findViewById(R.id.relativeLayout_profile);
        this.Image_button_profile_pic=(ImageButton) findViewById(R.id.Image_button_profile_pic);
        if ( !prf.getString("TYPE_USER").equals("email")){
            this.relativeLayout_password.setVisibility(View.GONE);

        }else{
            this.relativeLayout_password.setVisibility(View.VISIBLE);
        }

    }
    public void initAction(){
        this.relativeLayout_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        this.relativeLayout_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountActivity.this,PasswordActivity.class));
                finish();
            }
        });
        this.relativeLayout_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountActivity.this,EditActivity.class));
                finish();
            }
        });
        this.Image_button_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePic();
            }


        });
        this.relativeLayout_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this,UserActivity.class);
                intent.putExtra("id",Integer.parseInt(prf.getString("ID_USER")));
                intent.putExtra("name",prf.getString("NAME_USER"));
                intent.putExtra("image",prf.getString("IMAGE_USER"));
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        });
        this.relativeLayout_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePic();
            }
        });
    }

    private void ChangePic() {
        if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AccountActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }else{
            final CharSequence[] items = {getResources().getString(R.string.choose_picture),getResources().getString(R.string.Cancel) };

            AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
            builder.setTitle("Add Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals(getResources().getString(R.string.take_picture))) {
                        new PickerBuilder(AccountActivity.this, PickerBuilder.SELECT_FROM_CAMERA)
                                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                                    @Override
                                    public void onImageReceived(Uri imageUri) {
                                        imageurl = imageUri.getPath();
                                        upload(CAMERA_REQUEST_IMAGE_1);
                                    }
                                })

                                .start();
                    } else if (items[item].equals(getResources().getString(R.string.choose_picture))) {
                        new PickerBuilder(AccountActivity.this, PickerBuilder.SELECT_FROM_GALLERY)

                                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                                    @Override
                                    public void onImageReceived(Uri imageUri) {
                                        imageurl = imageUri.getPath();
                                        upload(CAMERA_REQUEST_IMAGE_1);
                                    }
                                })
                                .start();
                    } else if (items[item].equals(getResources().getString(R.string.Cancel))) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);

        }
        return super.onOptionsItemSelected(item);
    }
    public      void logout(){
        PrefManager prf= new PrefManager(getApplicationContext());
        prf.remove("ID_USER");
        prf.remove("SALT_USER");
        prf.remove("TOKEN_USER");
        prf.remove("NAME_USER");
        prf.remove("TYPE_USER");
        prf.remove("USERN_USER");
        prf.remove("IMAGE_USER");
        prf.setString("LOGGED","FALSE");
        if (prf.getString("LOGGED").toString().equals("TRUE")){
        }else{
            finish();
        }
        Toasty.warning(getApplicationContext(),getString(R.string.message_logout),Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* if (requestCode == CAMERA_REQUEST_IMAGE_1 && resultCode == RESULT_OK) {
            try {
                Bitmap t = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imagePath);
                Bitmap thumbnail = Bitmap.createScaledBitmap(t, 2048, 1052, true);
                imageurl = getRealPathFromURI(imagePath);
                OutputStream stream = new FileOutputStream(imageurl);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, stream);

                Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
                upload(CAMERA_REQUEST_IMAGE_1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    public void upload(final int CODE){

        profile_progress_image.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);

        //File creating from selected URL
        final File file = new File(imageurl);




        ProgressRequestBody requestFile = new ProgressRequestBody(file, AccountActivity.this);

        // create RequestBody instance from file
        // RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);
        String id_ser=  prf.getString("ID_USER");
        String key_ser=  prf.getString("TOKEN_USER");


        Call<ApiResponse> request = service.uploadImage(body,id_ser,key_ser);
        request.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                profile_progress_image.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    if (CODE==CAMERA_REQUEST_IMAGE_1){
                        for (int i=0;i<response.body().getValues().size();i++){
                            if (response.body().getValues().get(i).getName().equals("url")){
                                new_image_url_1=response.body().getValues().get(i).getValue();
                            }
                        }
                    }
                    prf.setString("IMAGE_USER",new_image_url_1);
                    Picasso.with(getApplicationContext())
                            .load(prf.getString("IMAGE_USER").toString())
                            .into(image_view_profile_img);
                    Toasty.success(AccountActivity.this, getResources().getString(R.string.profile_pic_edited), Toast.LENGTH_SHORT).show();
                }else{
                    Toasty.error(AccountActivity.this, getResources().getString(R.string.error_server), Toast.LENGTH_SHORT).show();
                }
                file.delete();
                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toasty.error(AccountActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                profile_progress_image.setVisibility(View.GONE);

            }
        });


    }

    @Override
    public void onProgressUpdate(int percentage) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }
    public  void getCategories(){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Category>> request = service.categoriesAll(0);
        request.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {

            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });

    }
}
