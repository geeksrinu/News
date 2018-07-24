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

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ristana.newspro.R;
import com.ristana.newspro.api.apiClient;
import com.ristana.newspro.api.apiRest;
import com.ristana.newspro.entity.ApiResponse;
import com.ristana.newspro.manager.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {


    private TextInputLayout register_input_layout_password;
    private TextInputLayout register_input_layout_email;
    private EditText register_input_password;
    private EditText register_input_email;
    private Button register_btn;
    private EditText register_input_name;
    private EditText register_input_password_confirm;
    private TextInputLayout register_input_layout_name;
    private TextInputLayout register_input_layout_password_confirm;
    private ProgressDialog register_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initAction();


    }


    public void initView(){
        this.register_btn            =      (Button)        findViewById(R.id.register_btn);

        this.register_input_name                =   (EditText) findViewById(R.id.register_input_name);
        this.register_input_email               =   (EditText) findViewById(R.id.register_input_email);
        this.register_input_password            =   (EditText) findViewById(R.id.register_input_password);
        this.register_input_password_confirm    =   (EditText) findViewById(R.id.register_input_password_confirm);

        this.register_input_layout_name     =   (TextInputLayout) findViewById(R.id.register_input_layout_name);
        this.register_input_layout_email        =   (TextInputLayout) findViewById(R.id.register_input_layout_email);
        this.register_input_layout_password     =   (TextInputLayout) findViewById(R.id.register_input_layout_password);
        this.register_input_layout_password_confirm     =   (TextInputLayout) findViewById(R.id.register_input_layout_password_confirm);

    }
    public void initAction(){

        this.register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        if (!validateName()) {
            return;
        }
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
        if (!validatePasswordConfrom()) {
            return;
        }
        signUp(this.register_input_email.getText().toString(),this.register_input_password.getText().toString() ,this.register_input_name.getText().toString(),"email");
    }
    private boolean  validateEmail() {
        String email = register_input_email.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            register_input_layout_email.setError(getString(R.string.error_mail_valide));
            requestFocus(register_input_email);
            return false;
        } else {
            register_input_layout_email.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatePasswordConfrom() {
        if (!register_input_password.getText().toString().equals(register_input_password_confirm.getText().toString())) {
            register_input_layout_password_confirm.setError(getString(R.string.error_password_confirm));
            requestFocus(register_input_password_confirm);
            return false;
        } else {
            register_input_layout_password_confirm.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validatePassword() {
        if (register_input_password.getText().toString().trim().isEmpty() || register_input_password.getText().length()  < 6 ) {
            register_input_layout_password.setError(getString(R.string.error_password_short));
            requestFocus(register_input_password);
            return false;
        } else {
            register_input_layout_password.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateName() {
        if (register_input_name.getText().toString().trim().isEmpty() || register_input_name.getText().length()  < 3 ) {
            register_input_layout_name.setError(getString(R.string.error_short_value));
            requestFocus(register_input_name);
            return false;
        } else {
            register_input_layout_name.setErrorEnabled(false);
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
    public void signUp(String username,String password,String name,String type){
        register_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.register(name,username,password,type,null);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    Toast.makeText(getApplicationContext(),response.body().getMessage(),Toast.LENGTH_LONG).show();
                    if (response.body().getCode()==200){
                        String id_user="0";
                        String name_user="x";
                        String username_user="x";
                        String salt_user="0";
                        String token_user="0";
                        String type_user="x";
                        String image_user="x";
                        String enabled="x";
                        for (int i=0;i<response.body().getValues().size();i++) {
                            if (response.body().getValues().get(i).getName().equals("salt")) {
                                salt_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("token")) {
                                token_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("id")) {
                                id_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("name")) {
                                name_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("type")) {
                                type_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("username")) {
                                username_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("url")) {
                                image_user = response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("enabled")) {
                                enabled = response.body().getValues().get(i).getValue();
                            }
                        }
                        PrefManager prf= new PrefManager(getApplicationContext());
                        prf.setString("ID_USER",id_user);
                        prf.setString("SALT_USER",salt_user);
                        prf.setString("TOKEN_USER",token_user);
                        prf.setString("NAME_USER",name_user);
                        prf.setString("TYPE_USER",type_user);
                        prf.setString("USERN_USER",username_user);
                        prf.setString("IMAGE_USER",image_user);
                        prf.setString("LOGGED","TRUE");
                        finish();
                    }
                    if (response.body().getCode()==500){
                        register_input_layout_email.setError(response.body().getMessage().toString());
                        requestFocus(register_input_email);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
                }
                register_progress.dismiss();
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_LONG).show();
                register_progress.dismiss();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent= new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
