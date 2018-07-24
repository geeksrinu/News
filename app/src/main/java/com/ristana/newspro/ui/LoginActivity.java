package com.ristana.newspro.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ristana.newspro.R;
import com.ristana.newspro.api.apiClient;
import com.ristana.newspro.api.apiRest;
import com.ristana.newspro.entity.ApiResponse;
import com.ristana.newspro.manager.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {




    private TextInputLayout login_input_layout_password;
    private TextInputLayout login_input_layout_email;
    private EditText login_input_password;
    private EditText login_input_email;
    private Button login_btn;
    private ProgressDialog register_progress;
    private TextView login_text_view_to_register;
    private TextView login_text_view_to_password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initAction();
    }


    public void initView(){
        this.login_btn              =       (Button) findViewById(R.id.login_btn);


        this.login_input_email          =   (EditText) findViewById(R.id.login_input_email);
        this.login_input_password       =   (EditText) findViewById(R.id.login_input_password);
        this.login_input_layout_email   =   (TextInputLayout) findViewById(R.id.login_input_layout_email);
        this.login_input_layout_password=   (TextInputLayout) findViewById(R.id.login_input_layout_password);

        this.login_text_view_to_password=   (TextView)         findViewById(R.id.login_text_view_to_password);
        this.login_text_view_to_register=   (TextView)         findViewById(R.id.login_text_view_to_register);
    }
    public void initAction(){

        this.login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
        this.login_text_view_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        this.login_text_view_to_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(LoginActivity.this,ForgotActivity.class);
               startActivity(intent);
                finish();
            }
        });
    }

    private void submitForm() {
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        signIn(login_input_email.getText().toString(),login_input_password.getText().toString());
    }


    private boolean  validateEmail() {
        String email = login_input_email.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            login_input_layout_email.setError(getString(R.string.error_mail_valide));
            requestFocus(login_input_email);
            return false;
        } else {
            login_input_layout_email.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatePassword() {
        if (login_input_password.getText().toString().trim().isEmpty() || login_input_password.getText().length()  < 6 ) {
            login_input_layout_password.setError(getString(R.string.error_password_short));
            requestFocus(login_input_password);
            return false;
        } else {
            login_input_layout_password.setErrorEnabled(false);
        }
        return true;
    }
    private static  boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    public void signIn(String username,String password){

        register_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.login(username,password);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    if (response.body().getCode()==200){


                        String id_user="0";
                        String name_user="x";
                        String username_user="x";
                        String salt_user="0";
                        String token_user="0";
                        String type_user="x";
                        String image_user="x";
                        String enabled="x";
                        for (int i=0;i<response.body().getValues().size();i++){
                            if (response.body().getValues().get(i).getName().equals("salt")){
                                salt_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("token")){
                                token_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("id")){
                                id_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("name")){
                                name_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("type")){
                                type_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("username")){
                                username_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("url")){
                                image_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("enabled")){
                                enabled=response.body().getValues().get(i).getValue();
                            }
                        }if (enabled.equals("true")){
                            PrefManager prf= new PrefManager(getApplicationContext());
                            prf.setString("ID_USER",id_user);
                            prf.setString("SALT_USER",salt_user);
                            prf.setString("TOKEN_USER",token_user);
                            prf.setString("NAME_USER",name_user);
                            prf.setString("TYPE_USER",type_user);
                            prf.setString("USERN_USER",username_user);
                            prf.setString("IMAGE_USER",image_user);
                            prf.setString("LOGGED","TRUE");
//                            String  token = FirebaseInstanceId.getInstance().getToken();
//                            updateToken(Integer.parseInt(id_user),token_user,token);
                            finish();

                            Toasty.success(getApplicationContext(),response.body().getMessage(),Toast.LENGTH_LONG).show();
                        }else{
                            Toasty.error(getApplicationContext(),getResources().getString(R.string.account_disabled), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                    if (response.body().getCode()==500){
                        Toasty.error(getApplicationContext(),response.body().getMessage(),Toast.LENGTH_LONG).show();

                        login_input_layout_email.setError(response.body().getMessage().toString());
                        requestFocus(login_input_email);
                    }
                }else{
                    Toasty.error(getApplicationContext(),getResources().getString(R.string.no_connexion), Toast.LENGTH_SHORT, true).show();
                }
                register_progress.dismiss();
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(),getResources().getString(R.string.no_connexion), Toast.LENGTH_SHORT, true).show();
                register_progress.dismiss();

            }
        });
    }
    public void updateToken(Integer id,String key,String token){
        register_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.editToken(id,key,token);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()){

                    Toasty.success(getApplicationContext(),response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
                    register_progress.dismiss();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toasty.error(getApplicationContext(), "Operation has been cancelled ! ", Toast.LENGTH_SHORT, true).show();
                register_progress.dismiss();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent= new Intent(LoginActivity.this,AuthActivity.class);
        startActivity(intent);
        finish();
    }
}
