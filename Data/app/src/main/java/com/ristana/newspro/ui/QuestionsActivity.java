package com.ristana.newspro.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ristana.newspro.R;
import com.ristana.newspro.adapter.QuestionAdapter;
import com.ristana.newspro.api.apiClient;
import com.ristana.newspro.api.apiRest;
import com.ristana.newspro.entity.Question;
import com.ristana.newspro.manager.PrefManager;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class QuestionsActivity extends AppCompatActivity {
    // final variables
    private List<Question> questionList =  new ArrayList<>();


    private GridLayoutManager gridLayoutManager;
    private RecyclerView recycle_view_questions_activity;
    private SwipeRefreshLayout swipe_refreshl_questions_activity;
    private RelativeLayout relative_layout_questions_activity_error;
    private RelativeLayout relative_layout_questions_activity;
    private Button button_try_again;
    private RelativeLayout relative_layout_questions_activity_load_more;


    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private Integer page = 0;
    private Boolean loaded=false;

    private PrefManager prefManager;
    private QuestionAdapter questionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);


        this.prefManager= new PrefManager(getApplicationContext());


        initView();
        initAction();
        loadArticlesByTag();
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
        }
        return super.onOptionsItemSelected(item);
    }



    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_surveys));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.button_try_again=(Button)  findViewById(R.id.button_try_again);
        this.relative_layout_questions_activity_error=(RelativeLayout)  findViewById(R.id.relative_layout_questions_activity_error);
        this.relative_layout_questions_activity=(RelativeLayout)  findViewById(R.id.relative_layout_questions_activity);
        this.recycle_view_questions_activity=(RecyclerView)  findViewById(R.id.recycle_view_questions_activity);
        this.swipe_refreshl_questions_activity= (SwipeRefreshLayout)  findViewById(R.id.swipe_refreshl_questions_activity);
        this.relative_layout_questions_activity_load_more=(RelativeLayout)  findViewById(R.id.relative_layout_questions_activity_load_more);
        swipe_refreshl_questions_activity.setProgressViewOffset(false,00,200);
        this.gridLayoutManager=  new GridLayoutManager(this.getApplicationContext(),1,GridLayoutManager.VERTICAL,false);
        this.questionAdapter =new QuestionAdapter(questionList,this);
        recycle_view_questions_activity.setHasFixedSize(true);
        recycle_view_questions_activity.setAdapter(questionAdapter);
        recycle_view_questions_activity.setLayoutManager(gridLayoutManager);



    }
    public void initAction(){
        this.swipe_refreshl_questions_activity.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loading = true;
                page = 0;
                loadArticlesByTag();

            }
        });
        this.button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading = true;
                page = 0;
                loadArticlesByTag();

            }
        });
        recycle_view_questions_activity.addOnScrollListener(new RecyclerView.OnScrollListener()
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
                            loadNextArticlesByTag();
                        }
                    }
                }else{

                }
            }
        });
    }
    private void loadNextArticlesByTag() {
        relative_layout_questions_activity_load_more.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Question>> call = service.questionsAll(page,Integer.parseInt(prefManager.getString("LANGUAGE_DEFAULT")));
        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful()){
                    if (response.body().size()>0){
                        for (int i = 0; i < response.body().size(); i++) {
                            questionList.add(response.body().get(i));
                        }
                        questionAdapter.notifyDataSetChanged();
                        page++;
                        loading=true;
                    }
                }else{
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                }
                relative_layout_questions_activity_load_more.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                Toasty.error(getApplicationContext(), getResources().getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                relative_layout_questions_activity_load_more.setVisibility(View.GONE);
            }
        });
    }

    public void loadArticlesByTag() {

        swipe_refreshl_questions_activity.setRefreshing(true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Question>> call = service.questionsAll(page,Integer.parseInt(prefManager.getString("LANGUAGE_DEFAULT")));

        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful()){

                    questionList.clear();
                    questionAdapter.notifyDataSetChanged();

                    if (response.body().size()>0){
                        for (int i = 0; i < response.body().size(); i++) {
                            questionList.add(response.body().get(i));
                        }
                    }
                    questionAdapter.notifyDataSetChanged();
                    page++;
                    loading=true;
                    relative_layout_questions_activity_error.setVisibility(View.GONE);
                    relative_layout_questions_activity.setVisibility(View.VISIBLE);
                }else{
                    relative_layout_questions_activity_error.setVisibility(View.VISIBLE);
                    relative_layout_questions_activity.setVisibility(View.GONE);
                }
                swipe_refreshl_questions_activity.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                swipe_refreshl_questions_activity.setRefreshing(false);
                relative_layout_questions_activity_error.setVisibility(View.VISIBLE);
                relative_layout_questions_activity.setVisibility(View.GONE);
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
