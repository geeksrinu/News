package com.ristana.newspro.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ristana.newspro.R;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ristana.newspro.adapter.ArticleAdapter;
import com.ristana.newspro.api.apiClient;
import com.ristana.newspro.api.apiRest;
import com.ristana.newspro.entity.Article;
import com.ristana.newspro.entity.Category;
import com.ristana.newspro.entity.Question;
import com.ristana.newspro.entity.Slide;
import com.ristana.newspro.entity.Tag;
import com.ristana.newspro.manager.PrefManager;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
public class CategoryActivity extends AppCompatActivity {

    // final variables
    private List<Tag> tagList =  new ArrayList<>();
    private List<Category> categoryList =  new ArrayList<>();
    private List<Article> articleList =  new ArrayList<>();
    private List<Question> questionList =  new ArrayList<>();
    private List<Slide> slideList =  new ArrayList<>();


    private GridLayoutManager gridLayoutManager;
    private ArticleAdapter articleAdapter;
    private RecyclerView recycle_view_category_activity;
    private SwipeRefreshLayout swipe_refreshl_category_activity;
    private RelativeLayout relative_layout_category_activity_error;
    private RelativeLayout relative_layout_category_activity;
    private Button button_try_again;
    private RelativeLayout relative_layout_category_activity_load_more;


    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private Integer page = 0;
    private Boolean loaded=false;
    private int id;
    private String title;
    private PrefManager prefManager;
    private RelativeLayout acb;
    private String color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        this.prefManager= new PrefManager(getApplicationContext());

        Bundle bundle = getIntent().getExtras() ;
        this.id =  bundle.getInt("id");
        this.title =  bundle.getString("title");
        this.color =  bundle.getString("color");
        this.prefManager= new PrefManager(getApplicationContext());

        initView();
        initAction();
        loadCategoryByCategrory();
        showAdsBanner();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
                break;
            case R.id.action_order :
                if ( prefManager.getString("ORDER_DEFAULT").equals("created")){
                    page = 0;
                    loading = true;
                    prefManager.setString("ORDER_DEFAULT","views");
                    loadCategoryByCategrory();
                    item.setIcon(getResources().getDrawable(R.drawable.ic_remove_red_eye_white_24dp));

                }else{
                    page = 0;
                    loading = true;
                    prefManager.setString("ORDER_DEFAULT","created");
                    item.setIcon(getResources().getDrawable(R.drawable.ic_access_time_white_24dp));
                    loadCategoryByCategrory();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order, menu);
        MenuItem item = menu.findItem(R.id.action_order);
        if ( prefManager.getString("ORDER_DEFAULT").equals("created")){

            item.setIcon(getResources().getDrawable(R.drawable.ic_access_time_white_24dp));

        }else{
            item.setIcon(getResources().getDrawable(R.drawable.ic_remove_red_eye_white_24dp));
        }
        return true;
    }


    private void initView() {
        float factor=0.8f;
        int a = Color.alpha(Color.parseColor(color));
        int r = Math.round(Color.red(Color.parseColor(color)) * factor);
        int g = Math.round(Color.green(Color.parseColor(color)) * factor);
        int b = Math.round(Color.blue(Color.parseColor(color)) * factor);
        int statusColor= Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = CategoryActivity.this.getWindow();
            window.setStatusBarColor(statusColor);
        }

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.acb=(RelativeLayout) findViewById(R.id.acb);

        this.button_try_again=(Button)  findViewById(R.id.button_try_again);
        this.relative_layout_category_activity_error=(RelativeLayout)  findViewById(R.id.relative_layout_category_activity_error);
        this.relative_layout_category_activity=(RelativeLayout)  findViewById(R.id.relative_layout_category_activity);
        this.recycle_view_category_activity=(RecyclerView)  findViewById(R.id.recycle_view_category_activity);
        this.swipe_refreshl_category_activity= (SwipeRefreshLayout)  findViewById(R.id.swipe_refreshl_category_activity);
        this.relative_layout_category_activity_load_more=(RelativeLayout)  findViewById(R.id.relative_layout_category_activity_load_more);
        swipe_refreshl_category_activity.setProgressViewOffset(false,00,200);
        this.gridLayoutManager=  new GridLayoutManager(this.getApplicationContext(),1,GridLayoutManager.VERTICAL,false);
        this.articleAdapter =new ArticleAdapter(slideList,tagList,categoryList,questionList,articleList,this,false);
        recycle_view_category_activity.setHasFixedSize(true);
        recycle_view_category_activity.setAdapter(articleAdapter);
        recycle_view_category_activity.setLayoutManager(gridLayoutManager);

        acb.setBackgroundColor(Color.parseColor(color));

    }
    public void initAction(){
        this.swipe_refreshl_category_activity.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loading = true;
                page = 0;
                loadCategoryByCategrory();

            }
        });
        this.button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading = true;
                page = 0;
                loadCategoryByCategrory();

            }
        });
        recycle_view_category_activity.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {

                    visibleItemCount    = gridLayoutManager.getChildCount();
                    totalItemCount      = gridLayoutManager.getItemCount();
                    pastVisiblesItems   = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            loadNextArticlesByCategory();
                        }
                    }
                }else{

                }
            }
        });
    }
    private void loadNextArticlesByCategory() {
        relative_layout_category_activity_load_more.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Article>> call = service.articlesByCategory(page,prefManager.getString("ORDER_DEFAULT"),Integer.parseInt( prefManager.getString("LANGUAGE_DEFAULT")),id);
        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if (response.isSuccessful()){
                    if (response.body().size()>0){
                        for (int i = 0; i < response.body().size(); i++) {
                            if (response.body().get(i).getType().equals("article")){
                                articleList.add(response.body().get(i).setTypeView(0));
                            }else{
                                articleList.add(response.body().get(i).setTypeView(4));
                            }
                        }
                        articleAdapter.notifyDataSetChanged();
                        page++;
                        loading=true;
                    }
                }else{
                    Toasty.error(CategoryActivity.this, getResources().getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                }
                relative_layout_category_activity_load_more.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                Toasty.error(CategoryActivity.this, getResources().getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                relative_layout_category_activity_load_more.setVisibility(View.GONE);
            }
        });
    }
    public void loadCategoryByCategrory() {


        swipe_refreshl_category_activity.setRefreshing(true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Category>> call = service.categoriesAll(id);

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()){
                    apiClient.FormatData(CategoryActivity.this,response);

                    questionList.clear();
                    slideList.clear();
                    categoryList.clear();
                    articleList.clear();

                    if (response.body().size()>0){
                        articleList.add(new Article().setTypeView(2));
                        for (int i = 0; i < response.body().size(); i++) {
                            categoryList.add(response.body().get(i));
                        }
                    }
                    articleAdapter.notifyDataSetChanged();

                }
                loadArticlesByCategrory();
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                loadArticlesByCategrory();
            }
        });
    }
    public void loadArticlesByCategrory() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Article>> call = service.articlesByCategory(page,prefManager.getString("ORDER_DEFAULT"),Integer.parseInt( prefManager.getString("LANGUAGE_DEFAULT")),id);

        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if (response.isSuccessful()){


                    if (response.body().size()>0){
                        for (int i = 0; i < response.body().size(); i++) {
                            if (response.body().get(i).getType().equals("article")){
                                articleList.add(response.body().get(i).setTypeView(0));
                            }else{
                                articleList.add(response.body().get(i).setTypeView(4));
                            }
                        }
                    }
                    articleAdapter.notifyDataSetChanged();
                    page++;
                    loading=true;
                    relative_layout_category_activity_error.setVisibility(View.GONE);
                    relative_layout_category_activity.setVisibility(View.VISIBLE);
                }else{
                    relative_layout_category_activity_error.setVisibility(View.VISIBLE);
                    relative_layout_category_activity.setVisibility(View.GONE);
                }
                swipe_refreshl_category_activity.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                swipe_refreshl_category_activity.setRefreshing(false);
                relative_layout_category_activity_error.setVisibility(View.VISIBLE);
                relative_layout_category_activity.setVisibility(View.GONE);
            }
        });
    }
    private void showAdsBanner() {
        PrefManager prefManager= new PrefManager(getApplicationContext());
        if (getString(R.string.AD_MOB_ENABLED_BANNER).equals("true")) {

            if (prefManager.getString("SUBSCRIBED").equals("FALSE")) {
                final AdView mAdView = (AdView) findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder()
                        .build();

                // Start loading the ad in the background.
                mAdView.loadAd(adRequest);

                mAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        mAdView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }
}
