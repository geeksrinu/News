package com.ristana.newspro.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

public class ForgotActivity extends AppCompatActivity {

    private EditText edit_text_email;
    private TextInputLayout input_layout_edit_text_email;
    private PrefManager prf;
    private Button button_edit_info;
    private ProgressDialog login_progress;
    private Button button_edit_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prf= new PrefManager(getApplicationContext());
        initView();
        initAction();
    }

    private void initView() {
        setContentView(R.layout.activity_forgot);
        this.edit_text_email=(EditText) findViewById(R.id.edit_text_email);
        this.input_layout_edit_text_email=(TextInputLayout) findViewById(R.id.input_layout_edit_text_email);
        this.button_edit_info=(Button) findViewById(R.id.button_edit_info);
        this.button_edit_request=(Button) findViewById(R.id.button_edit_request);
    }

    private void initAction() {
        this.edit_text_email.addTextChangedListener(new ForgotActivity.EditTextWatcher(edit_text_email));
        this.button_edit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
        this.button_edit_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotActivity.this,RequestActivity.class));
                finish();
            }
        });
    }
    private void submitForm() {
        if (!validateEmail()) {
            return;
        }

        login_progress= ProgressDialog.show(this,null,getString(R.string.operation_progress));



        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.sendEmail(edit_text_email.getText().toString());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    int code = response.body().getCode();
                    String message=response.body().getMessage();
                    if (code==200) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ForgotActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.no_connexion),Toast.LENGTH_SHORT).show();
                }
                login_progress.dismiss();

            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                login_progress.dismiss();

            }
        });
    }

    private boolean  validateEmail() {
        String email = edit_text_email.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            input_layout_edit_text_email.setError(getString(R.string.error_mail_valide));
            requestFocus(edit_text_email);
            return false;
        } else {
            input_layout_edit_text_email.setErrorEnabled(false);
        }
        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private static  boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private class EditTextWatcher implements TextWatcher {
        private View view;
        private EditTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edit_text_email:
                    validateEmail();
                    break;
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent= new Intent(ForgotActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
