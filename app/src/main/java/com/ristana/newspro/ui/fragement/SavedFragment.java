package com.ristana.newspro.ui.fragement;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ristana.newspro.R;
import com.ristana.newspro.adapter.ArticleAdapter;
import com.ristana.newspro.api.apiClient;
import com.ristana.newspro.api.apiRest;
import com.ristana.newspro.entity.Article;
import com.ristana.newspro.entity.Category;
import com.ristana.newspro.entity.Question;
import com.ristana.newspro.entity.Slide;
import com.ristana.newspro.entity.Tag;
import com.ristana.newspro.manager.FavoritesStorage;
import com.ristana.newspro.manager.PrefManager;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SavedFragment extends Fragment {


    // final variables
    private List<Tag> tagList =  new ArrayList<>();
    private List<Category> categoryList =  new ArrayList<>();
    private List<Article> articleList =  new ArrayList<>();
    private List<Question> questionList =  new ArrayList<>();
    private List<Slide> slideList =  new ArrayList<>();


    private View view;
    private GridLayoutManager gridLayoutManager;
    private ArticleAdapter articleAdapter;
    private RecyclerView recycle_view_saved_fragment;
    private SwipeRefreshLayout swipe_refreshl_saved_fragment;
    private RelativeLayout relative_layout_saved_fragment_error;
    private RelativeLayout relative_layout_saved_fragment;
    private Button button_try_again;
    private RelativeLayout relative_layout_saved_fragment_load_more;


    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private Integer page = 0;
    private Boolean loaded=false;
    private PrefManager prefManager;
    private ImageView image_view_empty;

    public SavedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view=  inflater.inflate(R.layout.fragment_saved, container, false);
        this.prefManager= new PrefManager(getActivity().getApplicationContext());
        initView();
        initAction();
        savedAll();
        return view;
    }

    private void initView() {
        this.button_try_again=(Button) view.findViewById(R.id.button_try_again);
        this.relative_layout_saved_fragment_error=(RelativeLayout) view.findViewById(R.id.relative_layout_saved_fragment_error);
        this.relative_layout_saved_fragment=(RelativeLayout) view.findViewById(R.id.relative_layout_saved_fragment);
        this.recycle_view_saved_fragment=(RecyclerView) view.findViewById(R.id.recycle_view_saved_fragment);
        this.swipe_refreshl_saved_fragment= (SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshl_saved_fragment);
        this.relative_layout_saved_fragment_load_more=(RelativeLayout) view.findViewById(R.id.relative_layout_saved_fragment_load_more);
        swipe_refreshl_saved_fragment.setProgressViewOffset(false,00,200);
        this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),1,GridLayoutManager.VERTICAL,false);
        this.articleAdapter =new ArticleAdapter(slideList,tagList,categoryList,questionList,articleList,getActivity(),true);
        recycle_view_saved_fragment.setHasFixedSize(true);
        recycle_view_saved_fragment.setAdapter(articleAdapter);
        recycle_view_saved_fragment.setLayoutManager(gridLayoutManager);
        this.image_view_empty= (ImageView) view.findViewById(R.id.image_view_empty);

    }
    public void initAction(){
        this.swipe_refreshl_saved_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loading = true;
                page = 0;
                savedAll();

            }
        });
        this.button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading = true;
                page = 0;
                savedAll();

            }
        });

    }

    public void savedAll() {
        questionList.clear();
        slideList.clear();
        categoryList.clear();
        articleList.clear();

        swipe_refreshl_saved_fragment.setRefreshing(true);
        swipe_refreshl_saved_fragment.setRefreshing(true);
        final FavoritesStorage storageFavorites= new FavoritesStorage(getActivity().getApplicationContext());
        List<Article> articles = storageFavorites.loadFavorites();

        if (articles==null){
            articles= new ArrayList<>();
        }
        if (articles.size()!=0){

            articleList.clear();
            articleList.add(new Article().setTypeView(3));
            for (int i=0;i<articles.size();i++){
                Article a= new Article();
                a = articles.get(i) ;
                articleList.add(a);
            }
            articleAdapter.notifyDataSetChanged();
            image_view_empty.setVisibility(View.GONE);
            recycle_view_saved_fragment.setVisibility(View.VISIBLE);
        }else{
            image_view_empty.setVisibility(View.VISIBLE);
            recycle_view_saved_fragment.setVisibility(View.GONE);
        }

        swipe_refreshl_saved_fragment.setRefreshing(false);
    }
}
