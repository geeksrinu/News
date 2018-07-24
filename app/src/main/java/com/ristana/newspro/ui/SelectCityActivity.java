package com.ristana.newspro.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ristana.newspro.R;
import com.ristana.newspro.adapter.CityAdapter;
import com.ristana.newspro.adapter.SelectableCItyViewHolder;
import com.ristana.newspro.entity.City;
import com.ristana.newspro.manager.PrefManager;

import java.util.ArrayList;
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

public class SelectCityActivity extends AppCompatActivity  implements SelectableCItyViewHolder.OnItemSelectedListener {

    private CityAdapter cityAdapter;
    private final List<City> cityList = new ArrayList<>();
    private final List<City> cityListsearch = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    private LinearLayout linear_layout_page_error;
    private Toolbar toolbar;
    private PrefManager prefManager;
    private RelativeLayout relative_layout_city;
    private LinearLayout linear_layout_progress;
    private EditText edit_text_search_select_city_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager= new PrefManager(getApplicationContext());
        setContentView(R.layout.activity_select_city);
        initView();
        loadCity();
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
                Intent intent_save2 = new Intent(SelectCityActivity.this, MainActivity.class);
                startActivity(intent_save2);
                finish();
                break;
            case R.id.language_save:
                if (cityAdapter.getSelectedItems().size()==0){
                    Toasty.error(this, getResources().getString(R.string.please_select_city), Toast.LENGTH_SHORT).show();
                }else {
                    String name = "None";
                    String id = "0";
                    for (int i = 0; i < cityAdapter.getSelectedItems().size(); i++) {
                        id = cityAdapter.getSelectedItems().get(i).getId() + "";
                        name = cityAdapter.getSelectedItems().get(i).getName() + "";
                    }
                    cityAdapter.notifyDataSetChanged();
                    prefManager.setString("CITY_DEFAULT_ID", id);
                    prefManager.setString("CITY_DEFAULT_NAME", name);

                    Intent intent_save = new Intent(SelectCityActivity.this, MainActivity.class);
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
        getSupportActionBar().setTitle(getResources().getString(R.string.select_city));
        this.linear_layout_progress=(LinearLayout) findViewById(R.id.linear_layout_progress);
        this.relative_layout_city=(RelativeLayout) findViewById(R.id.relative_layout_city);
        this.linear_layout_page_error=(LinearLayout) findViewById(R.id.linear_layout_page_error);
        this.edit_text_search_select_city_activity=(EditText) findViewById(R.id.edit_text_search_select_city_activity);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView = (RecyclerView) findViewById(R.id.selection_list);
        edit_text_search_select_city_activity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i0, int i1, int i2) {
                cityListsearch.clear();
                for (int j = 0; j < cityList.size(); j++) {
                    if (cityList.get(j).getName().toLowerCase().contains(charSequence.toString().toLowerCase().trim())){
                        cityListsearch.add(cityList.get(j));
                    }
                }
                for (int j = 0; j < cityListsearch.size(); j++) {

                        cityListsearch.get(j).setSelected(false);

                }
                for (int j = 0; j < cityListsearch.size(); j++) {
                    if (cityListsearch.get(j).getId() == Integer.parseInt( prefManager.getString("CITY_DEFAULT_ID")))
                        cityListsearch.get(j).setSelected(true);

                }

                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(gridLayoutManager);
                cityAdapter = new CityAdapter(SelectCityActivity.this, cityListsearch, false, SelectCityActivity.this);
                recyclerView.setAdapter(cityAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onItemSelected(City item) {

    }

    public void loadCity(){
        recyclerView.setVisibility(View.GONE);
        linear_layout_page_error.setVisibility(View.GONE);
        linear_layout_progress.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<City>> call = service.CityAll(Integer.parseInt(prefManager.getString("LANGUAGE_DEFAULT")));
        call.enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, final Response<List<City>> response) {
                if (response.isSuccessful()){
                    for (int i = 0; i <response.body().size() ; i++) {
                        cityList.add(response.body().get(i));
                        cityListsearch.add(response.body().get(i));
                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    linear_layout_page_error.setVisibility(View.GONE);


                    if (response.isSuccessful()) {
                        for (int j = 0; j < cityListsearch.size(); j++) {
                            if (cityListsearch.get(j).getId() == Integer.parseInt( prefManager.getString("CITY_DEFAULT_ID")))
                                cityListsearch.get(j).setSelected(true);
                        }
                    }
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    cityAdapter = new CityAdapter(SelectCityActivity.this, cityListsearch, false, SelectCityActivity.this);
                    recyclerView.setAdapter(cityAdapter);
                }else{
                    recyclerView.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }
                linear_layout_progress.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
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
        Intent intent_save = new Intent(SelectCityActivity.this, MainActivity.class);
        startActivity(intent_save);
        finish();
        return;
    }

}
