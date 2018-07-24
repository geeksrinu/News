package com.ristana.newspro.ui.fragement;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ristana.newspro.R;
import com.ristana.newspro.adapter.ArticleAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class VideosFragment extends Fragment {



    // final variables
    private List<Tag> tagList =  new ArrayList<>();
    private List<Category> categoryList =  new ArrayList<>();
    private List<Article> articleList =  new ArrayList<>();
    private List<Question> questionList =  new ArrayList<>();
    private List<Slide> slideList =  new ArrayList<>();


    private View view;
    private GridLayoutManager gridLayoutManager;
    private ArticleAdapter articleAdapter;
    private RecyclerView recycle_view_videos_fragment;
    private SwipeRefreshLayout swipe_refreshl_videos_fragment;
    private RelativeLayout relative_layout_videos_fragment_error;
    private RelativeLayout relative_layout_videos_fragment;
    private Button button_try_again;
    private RelativeLayout relative_layout_videos_fragment_load_more;


    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private Integer page = 0;
    private Boolean loaded=false;
    private PrefManager prefManager;

    public VideosFragment() {
        // Required empty public constructor
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            if (!loaded){
                videoAll();
                loaded=true;
            }
        }
        else{

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view=  inflater.inflate(R.layout.fragment_videos, container, false);
        this.prefManager= new PrefManager(getActivity().getApplicationContext());
        initView();
        initAction();
        return view;
    }

    private void initView() {
        this.button_try_again=(Button) view.findViewById(R.id.button_try_again);
        this.relative_layout_videos_fragment_error=(RelativeLayout) view.findViewById(R.id.relative_layout_videos_fragment_error);
        this.relative_layout_videos_fragment=(RelativeLayout) view.findViewById(R.id.relative_layout_videos_fragment);
        this.recycle_view_videos_fragment=(RecyclerView) view.findViewById(R.id.recycle_view_videos_fragment);
        this.swipe_refreshl_videos_fragment= (SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshl_videos_fragment);
        this.relative_layout_videos_fragment_load_more=(RelativeLayout) view.findViewById(R.id.relative_layout_videos_fragment_load_more);
        swipe_refreshl_videos_fragment.setProgressViewOffset(false,00,200);
        this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),1,GridLayoutManager.VERTICAL,false);
        this.articleAdapter =new ArticleAdapter(slideList,tagList,categoryList,questionList,articleList,getActivity(),false);
        recycle_view_videos_fragment.setHasFixedSize(true);
        recycle_view_videos_fragment.setAdapter(articleAdapter);
        recycle_view_videos_fragment.setLayoutManager(gridLayoutManager);



    }
    public void initAction(){
        this.swipe_refreshl_videos_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loading = true;
                page = 0;
                videoAll();

            }
        });
        this.button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading = true;
                page = 0;
                videoAll();

            }
        });
        recycle_view_videos_fragment.addOnScrollListener(new RecyclerView.OnScrollListener()
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
                            loadNexArticles();
                        }
                    }
                }else{

                }
            }
        });
    }
    private void loadNexArticles() {
        relative_layout_videos_fragment_load_more.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Article>> call = service.videosAll(page,"created",Integer.parseInt(prefManager.getString("LANGUAGE_DEFAULT")));
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
                    Toasty.error(getActivity(), getResources().getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                }
                relative_layout_videos_fragment_load_more.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                Toasty.error(getActivity(), getResources().getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                relative_layout_videos_fragment_load_more.setVisibility(View.GONE);
            }
        });
    }

    public void videoAll() {

        swipe_refreshl_videos_fragment.setRefreshing(true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Article>> call = service.videosAll(page,"created",Integer.parseInt(prefManager.getString("LANGUAGE_DEFAULT")));

        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if (response.isSuccessful()){
                    apiClient.FormatData(getActivity(),response);

                    questionList.clear();
                    slideList.clear();
                    categoryList.clear();
                    articleList.clear();

                    articleAdapter.notifyDataSetChanged();
                    articleList.add(new Article().setTypeView(3));

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
                    relative_layout_videos_fragment_error.setVisibility(View.GONE);
                    relative_layout_videos_fragment.setVisibility(View.VISIBLE);
                }else{
                    relative_layout_videos_fragment_error.setVisibility(View.VISIBLE);
                    relative_layout_videos_fragment.setVisibility(View.GONE);
                }
                swipe_refreshl_videos_fragment.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                swipe_refreshl_videos_fragment.setRefreshing(false);
                relative_layout_videos_fragment_error.setVisibility(View.VISIBLE);
                relative_layout_videos_fragment.setVisibility(View.GONE);
            }
        });
    }
}
