package com.ristana.newspro.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.*;
import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.loopeer.shadow.ShadowView;
import com.ristana.newspro.R;
import com.ristana.newspro.api.apiClient;
import com.ristana.newspro.api.apiRest;
import com.ristana.newspro.entity.Article;
import com.ristana.newspro.entity.Category;
import com.ristana.newspro.entity.Question;
import com.ristana.newspro.entity.Slide;
import com.ristana.newspro.entity.Tag;
import com.ristana.newspro.entity.Vote;
import com.ristana.newspro.manager.FavoritesStorage;
import com.ristana.newspro.manager.PrefManager;
import com.ristana.newspro.ui.ArticleActivity;
import com.ristana.newspro.ui.CategoryActivity;
import com.ristana.newspro.ui.VideoActivity;
import com.ristana.newspro.ui.YoutubeActivity;
import com.ristana.newspro.ui.views.ClickableViewPager;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by hsn on 21/12/2017.
 */


public class ArticleAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private  Boolean favorites = false;
    private  FavoritesStorage storageFavorites;
    private ArrayList<Article> articles_fav;
    private List<Article>  articleList;


    //final variable
    private Activity activity;


    private List<Tag> tagList = new ArrayList<>();
    public TagAdapter tagAdapter;

    private List<Category> categoryList = new ArrayList<>();
    public CategoryAdapter categoryAdapter;

    public SlideAdapter slideAdapter;
    private List<Slide> slideList  = new ArrayList<>();

    public QuestionAdapter questionAdapter;
    private List<Question> questionList  = new ArrayList<>();

    private InterstitialAd mInterstitialAd;

    public ArticleAdapter(List<Slide> slideList, List<Tag> tagList, List<Category> categoryList, List<Question> questionList, List<Article> articleList, Activity activity,Boolean favorites) {
        this.articleList = articleList;
        this.activity = activity;

        this.slideList=slideList;
        this.slideAdapter = new SlideAdapter(activity,slideList);


        this.categoryList=categoryList;
        this.categoryAdapter =new CategoryAdapter(categoryList,activity);



        this.tagList=tagList;
        this.tagAdapter =new TagAdapter(tagList,activity);

        this.questionList=questionList;
        this.questionAdapter=new QuestionAdapter(questionList,activity);


        storageFavorites = new FavoritesStorage(activity.getApplicationContext());


        this.favorites = favorites;

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0: {
                View v1 = inflater.inflate(R.layout.item_article, parent, false);
                viewHolder = new ArticleHolder(v1);
                break;
            }
            case 1: {
                View v2 = inflater.inflate(R.layout.item_slide, parent, false);
                viewHolder = new SlideHolder(v2);
                break;
            }
            case 2: {
                View v3 = inflater.inflate(R.layout.item_categories, parent, false);
                viewHolder = new CategoryHolder(v3);
                break;
            }
            case 3: {
                View v4 = inflater.inflate(R.layout.item_empty, parent, false);
                viewHolder = new EmptyHolder(v4);
                break;
            }
            case 4: {
                View v5 = inflater.inflate(R.layout.item_video, parent, false);
                viewHolder = new VideoHolder(v5);
                break;
            }case 5: {
                View v6 = inflater.inflate(R.layout.item_questions, parent, false);
                viewHolder = new QUestionsHolder(v6);
                break;
            }case 6: {
                View v7 = inflater.inflate(R.layout.item_tags, parent, false);
                viewHolder = new TagHolder(v7);
                break;
            }
        }
        return viewHolder;

    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder_parent,final int position) {
        switch (getItemViewType(position)) {
            case 1:{
                SlideHolder holder=(SlideHolder) holder_parent;

                holder.view_pager_slide.setAdapter(slideAdapter);
                holder.view_pager_slide.setOffscreenPageLimit(1);
                holder.view_pager_slide.setClipToPadding(false);
                holder.view_pager_slide.setPageMargin(0);
                holder.view_pager_indicator.setupWithViewPager(holder.view_pager_slide);
                holder.view_pager_slide.setCurrentItem(slideList.size()/2);
                break;
            }
            case 0: {

                requestNewInterstitial();

                final ArticleHolder holder=(ArticleHolder) holder_parent;
                articles_fav = storageFavorites.loadFavorites();

                Boolean exist = false;
                for (int i = 0; i <articles_fav.size() ; i++) {
                    if (articles_fav.get(i).getId().equals(articleList.get(position).getId())){
                        exist = true;
                    }
                }
                if (exist == false) {
                    holder.image_view_item_article_save.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                } else {
                    holder.image_view_item_article_save.setImageResource(R.drawable.ic_bookmark_black_24dp);
                }
                holder.image_view_item_article_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            List<Article> favorites_list = storageFavorites.loadFavorites();
                            Boolean exist = false;
                            if (favorites_list==null){
                                favorites_list= new ArrayList<>();
                            }

                            for (int i = 0; i <favorites_list.size() ; i++) {
                                if (favorites_list.get(i).getId().equals(articleList.get(position).getId())){
                                    exist = true;
                                }
                            }

                            if (exist  == false) {
                                ArrayList<Article> audios= new ArrayList<Article>();

                                for (int i = 0; i < favorites_list.size(); i++) {
                                    audios.add(favorites_list.get(i));
                                }
                                audios.add(articleList.get(position));
                                storageFavorites.storeFavorite(audios);
                                holder.image_view_item_article_save.setImageResource(R.drawable.ic_bookmark_black_24dp);

                            }else{
                                ArrayList<Article> new_favorites= new ArrayList<Article>();
                                for (int i = 0; i < favorites_list.size(); i++) {
                                    if (!favorites_list.get(i).getId().equals(articleList.get(position).getId())){
                                        new_favorites.add(favorites_list.get(i));

                                    }
                                }
                                if (favorites){

                                    articleList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();

                                }
                                storageFavorites.storeFavorite(new_favorites);
                                holder.image_view_item_article_save.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                            }
                        }catch (IndexOutOfBoundsException e){
                            try {
                                articleList.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                            }catch (IndexOutOfBoundsException ex){

                            }
                        }
                    }
                });
                holder.text_view_item_article_time.setText(articleList.get(position).getCreated());
               try{
                   holder.text_view_item_article_comments.setText(format(articleList.get(position).getComments())+" "+activity.getResources().getString(R.string.comment_number));

               }catch (Exception e){
                   Log.v("OK",position+"");
               }
                holder.text_view_item_article_views.setText(format(articleList.get(position).getViews()) +" "+ activity.getResources().getString(R.string.views_number));
                holder.text_view_item_article_likes.setText(format(articleList.get(position).getLikes()) +" "+activity.getResources().getString(R.string.likes_number));

                holder.text_view_item_article_title.setText(articleList.get(position).getTitle());
                Picasso.with(activity).load(articleList.get(position).getImage()).into(holder.rounded_image_view_item_article);


                holder.card_view_item_article_shadow.setCardBackgroundColor(Color.parseColor(articleList.get(position).getColor()));

               // holder.shadow_view.setBackgroundClr(Color.parseColor(articleList.get(position).getColor()));
               // holder.shadow_view.setShadowColor(Color.parseColor(articleList.get(position).getColor()));
                holder.relative_layout_item_article_shadow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent  =  new Intent(activity.getApplicationContext(), ArticleActivity.class);
                        intent.putExtra("id",articleList.get(position).getId());
                        intent.putExtra("title",articleList.get(position).getTitle());
                        intent.putExtra("image",articleList.get(position).getImage());
                        intent.putExtra("created",articleList.get(position).getCreated());
                        intent.putExtra("color",articleList.get(position).getColor());
                        intent.putExtra("comment",articleList.get(position).getComment());
                        intent.putExtra("content",articleList.get(position).getContent());
                        intent.putExtra("user_id",articleList.get(position).getUserid());
                        intent.putExtra("user_name",articleList.get(position).getUser());
                        intent.putExtra("user_image",articleList.get(position).getUserimage());
                        intent.putExtra("likes",articleList.get(position).getLikes());
                        intent.putExtra("views",articleList.get(position).getViews());
                        intent.putExtra("type",articleList.get(position).getType());
                        intent.putExtra("extension",articleList.get(position).getExtension());

                        if (mInterstitialAd.isLoaded()) {
                            Log.v("ads","is loaded");
                            if (check()){
                                Log.v("ads","is checked");
                                mInterstitialAd.show();
                            }else{
                                Log.v("ads","is not checked");
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        } else {
                            Log.v("ads","is not loaded");
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                requestNewInterstitial();
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        });

                    }
                });
                break;
            }
            case 2:{

                break;
            }
            case 4: {

                requestNewInterstitial();

                final VideoHolder holder=(VideoHolder) holder_parent;

                holder.text_view_item_video_time.setText(articleList.get(position).getCreated());
                holder.text_view_item_video_comments.setText(format(articleList.get(position).getComments())+" "+activity.getResources().getString(R.string.comment_number));
                holder.text_view_item_video_views.setText(format(articleList.get(position).getViews()) +" "+ activity.getResources().getString(R.string.views_number));
                holder.text_view_item_video_likes.setText(format(articleList.get(position).getLikes()) +" "+activity.getResources().getString(R.string.likes_number));

                holder.text_view_item_video_title.setText(articleList.get(position).getTitle());
               Picasso.with(activity).load(articleList.get(position).getImage()).into(holder.rounded_image_view_item_video);
                articles_fav = storageFavorites.loadFavorites();

                Boolean exist = false;
                for (int i = 0; i <articles_fav.size() ; i++) {
                    if (articles_fav.get(i).getId().equals(articleList.get(position).getId())){
                        exist = true;
                    }
                }
                if (exist == false) {
                    holder.image_view_item_video_save.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                } else {
                    holder.image_view_item_video_save.setImageResource(R.drawable.ic_bookmark_black_24dp);
                }
                holder.image_view_item_video_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            List<Article> favorites_list = storageFavorites.loadFavorites();
                            Boolean exist = false;
                            if (favorites_list==null){
                                favorites_list= new ArrayList<>();
                            }

                            for (int i = 0; i <favorites_list.size() ; i++) {
                                if (favorites_list.get(i).getId().equals(articleList.get(position).getId())){
                                    exist = true;
                                }
                            }

                            if (exist  == false) {
                                ArrayList<Article> audios= new ArrayList<Article>();

                                for (int i = 0; i < favorites_list.size(); i++) {
                                    audios.add(favorites_list.get(i));
                                }
                                audios.add(articleList.get(position));
                                storageFavorites.storeFavorite(audios);
                                holder.image_view_item_video_save.setImageResource(R.drawable.ic_bookmark_black_24dp);

                            }else{
                                ArrayList<Article> new_favorites= new ArrayList<Article>();
                                for (int i = 0; i < favorites_list.size(); i++) {
                                    if (!favorites_list.get(i).getId().equals(articleList.get(position).getId())){
                                        new_favorites.add(favorites_list.get(i));

                                    }
                                }
                                if (favorites){

                                    articleList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();

                                }
                                storageFavorites.storeFavorite(new_favorites);
                                holder.image_view_item_video_save.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                            }
                        }catch (IndexOutOfBoundsException e){
                            try {
                                articleList.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                            }catch (IndexOutOfBoundsException ex){

                            }
                        }
                    }
                });

              //  holder.shadow_view.setBackgroundClr(Color.parseColor(articleList.get(position).getColor()));
                holder.card_view_item_article_shadow.setCardBackgroundColor(Color.parseColor(articleList.get(position).getColor()));
                holder.relative_layout_item_video_shadow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent  =  new Intent(activity.getApplicationContext(), VideoActivity.class);
                        if (articleList.get(position).getType().equals("video"))
                             intent  =  new Intent(activity.getApplicationContext(), VideoActivity.class);
                        else
                             intent  =  new Intent(activity.getApplicationContext(), YoutubeActivity.class);

                        intent.putExtra("id",articleList.get(position).getId());
                        intent.putExtra("title",articleList.get(position).getTitle());
                        intent.putExtra("video",articleList.get(position).getVideo());
                        intent.putExtra("image",articleList.get(position).getImage());
                        intent.putExtra("created",articleList.get(position).getCreated());
                        intent.putExtra("color",articleList.get(position).getColor());
                        intent.putExtra("comment",articleList.get(position).getComment());
                        intent.putExtra("content",articleList.get(position).getContent());
                        intent.putExtra("user_id",articleList.get(position).getUserid());
                        intent.putExtra("user_name",articleList.get(position).getUser());
                        intent.putExtra("user_image",articleList.get(position).getUserimage());
                        intent.putExtra("likes",articleList.get(position).getLikes());
                        intent.putExtra("views",articleList.get(position).getViews());
                        intent.putExtra("type",articleList.get(position).getType());
                        intent.putExtra("extension",articleList.get(position).getExtension());

                        if (mInterstitialAd.isLoaded()) {
                            Log.v("ads","is loaded");
                            if (check()){
                                Log.v("ads","is checked");
                                mInterstitialAd.show();
                            }else{
                                Log.v("ads","is not checked");
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        } else {
                            Log.v("ads","is not loaded");
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                        final Intent finalIntent = intent;
                        mInterstitialAd.setAdListener(new AdListener() {
                            @Override
                            public void onAdClosed() {
                                requestNewInterstitial();
                                activity.startActivity(finalIntent);
                                activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        });


                    }
                });
                break;
            }
            case 5:{

                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public class ArticleHolder extends RecyclerView.ViewHolder {

        private  ImageView image_view_item_article_save;
        private  CardView card_view_item_article_shadow;
       private RoundedImageView rounded_image_view_item_article;

        private  TextView text_view_item_article_title;

        private  TextView text_view_item_article_comments;
        private  TextView text_view_item_article_time;
        private  TextView text_view_item_article_likes;
        private  TextView text_view_item_article_views;

        private  RelativeLayout relative_layout_item_article_shadow;

        public ArticleHolder(View itemView) {
            super(itemView);
            card_view_item_article_shadow = (CardView) itemView.findViewById(R.id.card_view_item_article_shadow);
            image_view_item_article_save =(ImageView) itemView.findViewById(R.id.image_view_item_article_save);
            rounded_image_view_item_article =(RoundedImageView) itemView.findViewById(R.id.rounded_image_view_item_article);
            text_view_item_article_title=(TextView) itemView.findViewById(R.id.text_view_item_article_title);

            text_view_item_article_comments=(TextView) itemView.findViewById(R.id.text_view_item_article_comments);
            text_view_item_article_time=(TextView) itemView.findViewById(R.id.text_view_item_article_time);
            text_view_item_article_likes=(TextView) itemView.findViewById(R.id.text_view_item_article_likes);
            text_view_item_article_views=(TextView) itemView.findViewById(R.id.text_view_item_article_views);

            relative_layout_item_article_shadow =(RelativeLayout) itemView.findViewById(R.id.relative_layout_item_article_shadow);

            Typeface face = Typeface.createFromAsset(activity.getAssets(), "Bitter-Regular.ttf");

            text_view_item_article_title.setTypeface(face);

            text_view_item_article_comments.setTypeface(face);
            text_view_item_article_time.setTypeface(face);
            text_view_item_article_likes.setTypeface(face);
            text_view_item_article_views.setTypeface(face);


        }


    }

    private class SlideHolder extends RecyclerView.ViewHolder {
        private final ViewPagerIndicator view_pager_indicator;
        private final ClickableViewPager view_pager_slide;
        public SlideHolder(View itemView) {
            super(itemView);
            this.view_pager_indicator=(ViewPagerIndicator) itemView.findViewById(R.id.view_pager_indicator);
            this.view_pager_slide=(ClickableViewPager) itemView.findViewById(R.id.view_pager_slide);



        }

    }
    @Override
    public int getItemViewType(int position) {
            return articleList.get(position).getTypeView();
    }

    private class CategoryHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_category_items;
        private final LinearLayoutManager linearLayoutManager;

        public CategoryHolder(View view) {
            super(view);
            this.recycle_view_category_items=(RecyclerView) view.findViewById(R.id.recycle_view_category_items);


            this.linearLayoutManager=  new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false);
            this.recycle_view_category_items.setHasFixedSize(true);
            this.recycle_view_category_items.setAdapter(categoryAdapter);
            this.recycle_view_category_items.setLayoutManager(linearLayoutManager);

        }
    }


    private class TagHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_tags_items;
        private final LinearLayoutManager linearLayoutManager;

        public TagHolder(View view) {
            super(view);
            this.recycle_view_tags_items=(RecyclerView) view.findViewById(R.id.recycle_view_tags_items);


            this.linearLayoutManager=  new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false);
            this.recycle_view_tags_items.setHasFixedSize(true);
            this.recycle_view_tags_items.setAdapter(tagAdapter);
            this.recycle_view_tags_items.setLayoutManager(linearLayoutManager);

        }
    }

    private class EmptyHolder extends RecyclerView.ViewHolder {
        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }

    private class VideoHolder extends RecyclerView.ViewHolder {

        private  ImageView image_view_item_video_save;
        private  CardView card_view_item_article_shadow;
        private  RoundedImageView rounded_image_view_item_video;

        private  TextView text_view_item_video_title;

        private  TextView text_view_item_video_comments;
        private  TextView text_view_item_video_time;
        private  TextView text_view_item_video_likes;
        private  TextView text_view_item_video_views;

        private   RelativeLayout relative_layout_item_video_shadow;

        public VideoHolder(View itemView) {
            super(itemView);
            card_view_item_article_shadow = (CardView) itemView.findViewById(R.id.card_view_item_article_shadow);
            image_view_item_video_save =(ImageView) itemView.findViewById(R.id.image_view_item_video_save);

            rounded_image_view_item_video =(RoundedImageView) itemView.findViewById(R.id.rounded_image_view_item_video);
            text_view_item_video_title=(TextView) itemView.findViewById(R.id.text_view_item_video_title);

            text_view_item_video_comments=(TextView) itemView.findViewById(R.id.text_view_item_video_comments);
            text_view_item_video_time=(TextView) itemView.findViewById(R.id.text_view_item_video_time);
            text_view_item_video_likes=(TextView) itemView.findViewById(R.id.text_view_item_video_likes);
            text_view_item_video_views=(TextView) itemView.findViewById(R.id.text_view_item_video_views);

            relative_layout_item_video_shadow =(RelativeLayout) itemView.findViewById(R.id.relative_layout_item_video_shadow);

            Typeface face = Typeface.createFromAsset(activity.getAssets(), "Bitter-Regular.ttf");

            text_view_item_video_title.setTypeface(face);

            text_view_item_video_comments.setTypeface(face);
            text_view_item_video_time.setTypeface(face);
            text_view_item_video_likes.setTypeface(face);
            text_view_item_video_views.setTypeface(face);


        }
    }

    private class QUestionsHolder extends RecyclerView.ViewHolder {

        private final LinearLayoutManager linearLayoutManager;
        private  RecyclerView recycler_view_item_questions;

        public QUestionsHolder(View itemView) {
            super(itemView);
            this.recycler_view_item_questions =(RecyclerView) itemView.findViewById(R.id.recycler_view_item_questions);
            this.linearLayoutManager=  new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false);
            this.recycler_view_item_questions.setHasFixedSize(true);
            this.recycler_view_item_questions.setAdapter(questionAdapter);
            this.recycler_view_item_questions.setLayoutManager(linearLayoutManager);

        }
    }
    public int dip2Px(float dpValue) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.9f);
    }
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
    private void requestNewInterstitial() {

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        if (mInterstitialAd==null){
            mInterstitialAd = new InterstitialAd(activity.getApplicationContext());
            mInterstitialAd.setAdUnitId(activity.getString(R.string.ad_unit_id_interstitial));
        }
        if (!mInterstitialAd.isLoaded()){
            mInterstitialAd.loadAd(adRequest);
        }
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();

            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
    }
    public boolean check(){
        PrefManager prf = new PrefManager(activity.getApplicationContext());
        if (activity.getString(R.string.AD_MOB_ENABLED_INTERSTITAL).equals("false")){
            return false;
        }
        if (!prf.getString("SUBSCRIBED").equals("FALSE")) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());

        if (prf.getString("LAST_DATE_ADS").equals("")) {
            prf.setString("LAST_DATE_ADS", strDate);
        } else {
            String toyBornTime = prf.getString("LAST_DATE_ADS");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                Date oldDate = dateFormat.parse(toyBornTime);
                System.out.println(oldDate);

                Date currentDate = new Date();

                long diff = currentDate.getTime() - oldDate.getTime();
                long seconds = diff / 1000;

                if (seconds > Integer.parseInt(activity.getResources().getString(R.string.AD_MOB_INTERSTITAL_TIME))) {
                    prf.setString("LAST_DATE_ADS", strDate);
                    return  true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }
}
