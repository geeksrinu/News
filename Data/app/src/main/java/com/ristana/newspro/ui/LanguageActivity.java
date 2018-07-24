package com.ristana.newspro.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ristana.newspro.R;
import com.ristana.newspro.adapter.LanguageAdapter;
import com.ristana.newspro.adapter.SelectableLanguageViewHolder;
import com.ristana.newspro.api.apiClient;
import com.ristana.newspro.api.apiRest;
import com.ristana.newspro.entity.Language;
import com.ristana.newspro.manager.PrefManager;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LanguageActivity extends AppCompatActivity implements SelectableLanguageViewHolder.OnItemSelectedListener {

    private LanguageAdapter languageAdapter;
    private final List<Language> languageList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    private LinearLayout linear_layout_page_error;
    private Toolbar toolbar;
    private PrefManager prefManager;
    private RelativeLayout relative_layout_language;
    private LinearLayout linear_layout_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager= new PrefManager(getApplicationContext());

        setContentView(R.layout.activity_language);
        initView();
        loadLang();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.language_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent_save2 = new Intent(LanguageActivity.this, MainActivity.class);
                startActivity(intent_save2);
                finish();
                break;
            case R.id.language_save:
                if (languageAdapter.getSelectedItems().size()==0){
                    Toasty.error(this, getResources().getString(R.string.please_select_language), Toast.LENGTH_SHORT).show();
                }else {
                    String s = "0";
                    for (int i = 0; i < languageAdapter.getSelectedItems().size(); i++) {
                        s = languageAdapter.getSelectedItems().get(i).getId() + "";
                    }
                    languageAdapter.notifyDataSetChanged();
                    prefManager.setString("LANGUAGE_DEFAULT", s);

                    Intent intent_save = new Intent(LanguageActivity.this, SelectCityActivity.class);
                    startActivity(intent_save);
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.select_language));
        this.linear_layout_progress=(LinearLayout) findViewById(R.id.linear_layout_progress);
        this.relative_layout_language=(RelativeLayout) findViewById(R.id.relative_layout_language);
        this.linear_layout_page_error=(LinearLayout) findViewById(R.id.linear_layout_page_error);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView = (RecyclerView) findViewById(R.id.selection_list);


    }

    @Override
    public void onItemSelected(Language item) {

    }

    public void loadLang(){
        recyclerView.setVisibility(View.GONE);
        linear_layout_page_error.setVisibility(View.GONE);
        linear_layout_progress.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Language>> call = service.languageAll();
        call.enqueue(new Callback<List<Language>>() {
            @Override
            public void onResponse(Call<List<Language>> call, final Response<List<Language>> response) {
                if (response.isSuccessful()){
                    for (int i = 0; i <response.body().size() ; i++) {
                        languageList.add(response.body().get(i));
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    linear_layout_page_error.setVisibility(View.GONE);



                    if (response.isSuccessful()) {
                        languageList.clear();
                        String s = prefManager.getString("LANGUAGE_DEFAULT");
                        String[] array = s.split(",");
                        for (int i = 0; i < response.body().size(); i++) {
                            languageList.add(response.body().get(i));
                            if (s.length() != 0) {
                                for (int j = 0; j < array.length; j++) {
                                    if (languageList.get(i).getId() == Integer.parseInt(array[j])) {
                                        languageList.get(i).setSelected(true);
                                    }
                                }
                            }
                        }
                    }

                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    languageAdapter = new LanguageAdapter(LanguageActivity.this, languageList, false, LanguageActivity.this);
                    recyclerView.setAdapter(languageAdapter);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }
                linear_layout_progress.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<List<Language>> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                linear_layout_progress.setVisibility(View.GONE);

            }
        });
    }
    public int dip2Px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 1.9f);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        Intent intent_save = new Intent(LanguageActivity.this, MainActivity.class);
        startActivity(intent_save);
        finish();
        return;
    }
}
