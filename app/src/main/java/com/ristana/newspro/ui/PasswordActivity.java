package com.ristana.newspro.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ristana.newspro.R;
import com.ristana.newspro.api.apiClient;
import com.ristana.newspro.api.apiRest;
import com.ristana.newspro.entity.ApiResponse;
import com.ristana.newspro.manager.PrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PasswordActivity extends AppCompatActivity {

    private EditText cpassword_input_password;
    private EditText            cpassword_input_password_confirm;
    private EditText            cpassword_input_password_old;
    private TextInputLayout cpassword_input_layout_password;
    private TextInputLayout     cpassword_input_layout_password_old;
    private TextInputLayout     cpassword_input_layout_password_confirm;
    private Button change_password_change;
    private ProgressDialog login_progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        initView();
        initAction();
    }
    private void initView(){
        this.cpassword_input_password=(EditText) findViewById(R.id.cpassword_input_password);
        this.cpassword_input_password_confirm=(EditText) findViewById(R.id.cpassword_input_password_confirm);
        this.cpassword_input_password_old=(EditText) findViewById(R.id.cpassword_input_password_old);

        this.cpassword_input_layout_password= (TextInputLayout) findViewById(R.id.cpassword_input_layout_password);
        this.cpassword_input_layout_password_old= (TextInputLayout) findViewById(R.id.cpassword_input_layout_password_old);
        this.cpassword_input_layout_password_confirm= (TextInputLayout) findViewById(R.id.cpassword_input_layout_password_confirm);

        this.change_password_change=(Button) findViewById(R.id.change_password_change);


        this.cpassword_input_password.addTextChangedListener(new PasswordTextWatcher(cpassword_input_password));
        this.cpassword_input_password_old.addTextChangedListener(new PasswordTextWatcher(cpassword_input_password_old));
        this.cpassword_input_password_confirm.addTextChangedListener(new PasswordTextWatcher(cpassword_input_password_confirm));
    }
    private void initAction(){

        this.change_password_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }
    private void submitForm() {
        if (!validatePassword(cpassword_input_password_old,cpassword_input_layout_password_old)) {
            return;
        }
        if (!validatePassword(cpassword_input_password,cpassword_input_layout_password)) {
            return;
        }
        if (!validatePasswordConfrom()) {
            return;
        }
        login_progress= ProgressDialog.show(this,null,getString(R.string.operation_progress));

        PrefManager prf= new PrefManager(getApplicationContext());
        String id_ser=  prf.getString("ID_USER");

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.changePassword(id_ser,cpassword_input_password_old.getText().toString(),cpassword_input_password.getText().toString());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    int code = response.body().getCode();
                    String message=response.body().getMessage();
                    if (code==200){
                        String id_user="0";
                        String name_user="x";
                        String username_user="x";
                        String salt_user="0";
                        String token_user="0";
                        String type_user="x";
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


                        }
                        PrefManager prf= new PrefManager(getApplicationContext());


                        prf.setString("ID_USER",id_user);
                        prf.setString("SALT_USER",salt_user);
                        prf.setString("TOKEN_USER",token_user);
                        prf.setString("NAME_USER",name_user);
                        prf.setString("TYPE_USER",type_user);
                        prf.setString("USERNAME_USER",username_user);
                        prf.setString("LOGGED","TRUE");

                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PasswordActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        login_progress.dismiss();
                    } else if (code == 500) {
                        cpassword_input_password_old.setError(response.body().getMessage().toString());
                        requestFocus(cpassword_input_password_old);
                        login_progress.dismiss();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.no_connexion),Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                login_progress.dismiss();

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private boolean validatePassword(EditText et,TextInputLayout tIL) {
        if (et.getText().toString().trim().isEmpty() || et.getText().length()  < 6 ) {
            tIL.setError(getString(R.string.error_password_short));
            requestFocus(et);
            return false;
        } else {
            tIL.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatePasswordConfrom() {
        if (!cpassword_input_password.getText().toString().equals(cpassword_input_password_confirm.getText().toString())) {
            cpassword_input_layout_password_confirm.setError(getString(R.string.error_password_confirm));
            requestFocus(cpassword_input_password_confirm);
            return false;
        } else {
            cpassword_input_layout_password_confirm.setErrorEnabled(false);
        }
        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private class PasswordTextWatcher implements TextWatcher {
        private View view;
        private PasswordTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.cpassword_input_password:
                    validatePassword(cpassword_input_password,cpassword_input_layout_password);
                    break;
                case R.id.cpassword_input_password_confirm:
                    validatePassword(cpassword_input_password_confirm,cpassword_input_layout_password_confirm);
                    break;
                case R.id.cpassword_input_password_old:
                    validatePassword(cpassword_input_password_old,cpassword_input_layout_password_old);
                    break;
            }
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent= new Intent(PasswordActivity.this,AccountActivity.class);
        startActivity(intent);
        finish();
    }
}
