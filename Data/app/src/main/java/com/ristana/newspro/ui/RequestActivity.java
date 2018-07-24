package com.ristana.newspro.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
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

public class RequestActivity extends AppCompatActivity {

    private EditText edit_text_key;
    private TextInputLayout input_layout_edit_text_key;
    private PrefManager prf;
    private Button button_edit_info;
    private ProgressDialog login_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prf= new PrefManager(getApplicationContext());
        initView();
        initAction();
    }

    private void initView() {
        setContentView(R.layout.activity_request);
        this.edit_text_key=(EditText) findViewById(R.id.edit_text_key);
        this.input_layout_edit_text_key=(TextInputLayout) findViewById(R.id.input_layout_edit_text_key);
        this.button_edit_info=(Button) findViewById(R.id.button_edit_info);

    }

    private void initAction() {
        this.edit_text_key.addTextChangedListener(new RequestActivity.EditTextWatcher(edit_text_key));
        this.button_edit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }
    private void submitForm() {
        if (!validateKey()) {
            return;
        }

        login_progress= ProgressDialog.show(this,null,getString(R.string.operation_progress));


        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.request(this.edit_text_key.getText().toString());
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.body()!=null){
                    int code = response.body().getCode();
                    String message=response.body().getMessage();
                    if (code==200) {
                        String id_user="0";
                        String token_user="0";
                        for (int i=0;i<response.body().getValues().size();i++){

                            if (response.body().getValues().get(i).getName().equals("token")){
                                token_user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("id")){
                                id_user=response.body().getValues().get(i).getValue();
                            }
                        }
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RequestActivity.this, ResetActivity.class);
                        intent.putExtra("PASSWORD_ID",id_user);
                        intent.putExtra("PASSWORD_KEY",token_user);
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
    private boolean validateKey() {
        if (edit_text_key.getText().toString().trim().isEmpty() || edit_text_key.getText().length()  < 6 ) {
            input_layout_edit_text_key.setError(getString(R.string.error_short_value));
            requestFocus(edit_text_key);
            return false;
        } else {
            input_layout_edit_text_key.setErrorEnabled(false);
        }
        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
                case R.id.edit_text_key:
                    validateKey();
                    break;

            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent= new Intent(RequestActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
