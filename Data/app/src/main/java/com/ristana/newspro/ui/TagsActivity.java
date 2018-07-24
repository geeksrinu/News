package com.ristana.newspro.ui;

import android.support.annotation.IntRange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.IRowBreaker;
import com.ristana.newspro.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ristana.newspro.R;
import com.ristana.newspro.adapter.ArticleAdapter;
import com.ristana.newspro.adapter.TagAdapter;
import com.ristana.newspro.api.apiClient;
import com.ristana.newspro.api.apiRest;
import com.ristana.newspro.entity.Article;
import com.ristana.newspro.entity.Category;
import com.ristana.newspro.entity.Global;
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

public class TagsActivity extends AppCompatActivity {

    private Button button_try_again;
    private RelativeLayout relative_layout_tags_activity_error;
    private RelativeLayout relative_layout_tags_activity;
    private RecyclerView recycle_view_tags_activity;
    private SwipeRefreshLayout swipe_refreshl_tags_activity;
    private RelativeLayout relative_layout_tags_activity_load_more;
    private ChipsLayoutManager chipsLayoutManager;
    private TagAdapter tagAdapter ;


    private List<Tag> tagList =  new ArrayList<>();

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private Integer page = 0;
    private Boolean loaded=false;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.prefManager= new PrefManager(getApplicationContext());

        setContentView(R.layout.activity_tags);
        initView();
        initAction();
        loadTags();
    }

    private void initAction() {
        this.swipe_refreshl_tags_activity.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tagList.clear();
                loading = true;
                recycle_view_tags_activity.setVisibility(View.GONE);
                page = 0;
                loadTags();

            }
        });
        this.button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagList.clear();
                tagAdapter.notifyDataSetChanged();
                loading = true;
                page = 0;
                loadTags();

            }
        });
        recycle_view_tags_activity.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {

                    visibleItemCount    = chipsLayoutManager.getChildCount();
                    totalItemCount      = chipsLayoutManager.getItemCount();
                    pastVisiblesItems   = chipsLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            loadTags();
                        }
                    }
                }else{

                }
            }
        });
    }

    private void loadTags() {

        if (page!=0){
            relative_layout_tags_activity_load_more.setVisibility(View.VISIBLE);
        }
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Tag>> call = service.tagsAll(page,Integer.parseInt(prefManager.getString("LANGUAGE_DEFAULT")));
        call.enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if (response.isSuccessful()){
                    if (response.body().size()>0){
                        for (int i = 0; i < response.body().size(); i++) {
                                tagList.add(response.body().get(i));
                        }
                        tagAdapter.notifyDataSetChanged();
                        page++;
                        loading=true;

                    }
                    recycle_view_tags_activity.setVisibility(View.VISIBLE);
                    relative_layout_tags_activity_error.setVisibility(View.GONE);
                    relative_layout_tags_activity.setVisibility(View.VISIBLE);
                }else{
                    Toasty.error(TagsActivity.this, getResources().getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                    if (page == 0){
                        relative_layout_tags_activity_error.setVisibility(View.VISIBLE);
                        relative_layout_tags_activity.setVisibility(View.GONE);
                    }
                }
                relative_layout_tags_activity_load_more.setVisibility(View.GONE);

                swipe_refreshl_tags_activity.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                if (page == 0){
                    relative_layout_tags_activity_error.setVisibility(View.VISIBLE);
                    relative_layout_tags_activity.setVisibility(View.GONE);
                }
                relative_layout_tags_activity_load_more.setVisibility(View.GONE);
                swipe_refreshl_tags_activity.setRefreshing(false);
                Toasty.error(TagsActivity.this, getResources().getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initView(){

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.all_tags));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.button_try_again=(Button) findViewById(R.id.button_try_again);
        this.relative_layout_tags_activity_error=(RelativeLayout) findViewById(R.id.relative_layout_tags_activity_error);
        this.relative_layout_tags_activity=(RelativeLayout) findViewById(R.id.relative_layout_tags_activity);
        this.recycle_view_tags_activity=(RecyclerView) findViewById(R.id.recycle_view_tags_activity);
        this.swipe_refreshl_tags_activity= (SwipeRefreshLayout) findViewById(R.id.swipe_refreshl_tags_activity);
        this.relative_layout_tags_activity_load_more=(RelativeLayout) findViewById(R.id.relative_layout_tags_activity_load_more);
        swipe_refreshl_tags_activity.setProgressViewOffset(false,00,200);
        this.chipsLayoutManager=   ChipsLayoutManager.newBuilder(getApplicationContext())
                //set vertical gravity for all items in a row. Default = Gravity.CENTER_VERTICAL
                .setChildGravity(Gravity.TOP)
                //whether RecyclerView can scroll. TRUE by default
                .setScrollingEnabled(true)
                //set maximum views count in a particular row
                //set gravity resolver where you can determine gravity for item in position.
                //This method have priority over previous one
                .setGravityResolver(new IChildGravityResolver() {
                    @Override
                    public int getItemGravity(int position) {
                        return Gravity.CENTER;
                    }
                })
                //a layoutOrientation of layout manager, could be VERTICAL OR HORIZONTAL. HORIZONTAL by default
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                // row strategy for views in completed row, could be STRATEGY_DEFAULT, STRATEGY_FILL_VIEW,
                //STRATEGY_FILL_SPACE or STRATEGY_CENTER
                .setRowStrategy(ChipsLayoutManager.STRATEGY_CENTER_DENSE)

                .build();;
        this.tagAdapter =new TagAdapter(tagList,this);
        recycle_view_tags_activity.setHasFixedSize(true);
        recycle_view_tags_activity.setAdapter(tagAdapter);
        recycle_view_tags_activity.setLayoutManager(chipsLayoutManager);
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
}
