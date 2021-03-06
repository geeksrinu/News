package com.ristana.newspro.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.halilibo.bettervideoplayer.BetterVideoCallback;
import com.halilibo.bettervideoplayer.BetterVideoPlayer;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.ristana.newspro.R;
import com.ristana.newspro.adapter.CommentAdapter;
import com.ristana.newspro.adapter.TagAdapter;
import com.ristana.newspro.api.apiClient;
import com.ristana.newspro.api.apiRest;
import com.ristana.newspro.config.Config;
import com.ristana.newspro.entity.ApiResponse;
import com.ristana.newspro.entity.Article;
import com.ristana.newspro.entity.Comment;
import com.ristana.newspro.entity.Tag;
import com.ristana.newspro.manager.FavoritesStorage;
import com.ristana.newspro.manager.PrefManager;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class VideoActivity extends AppCompatActivity {

    private static final String TAG = "VIDEO : ";


    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;

    private Timeline.Window window;
    private DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private BandwidthMeter bandwidthMeter;

    private ImageView ivHideControllerButton;
    private String URL;


    private Toolbar toolbar;
    private ImageView image_view_activity_video;
    private int id;
    private String title;
    private String image;
    private String user_image;
    private int user_id;
    private String video;

    private String user_name;
    private Boolean from;
    private List<Tag> tagList =  new ArrayList<>();

    private TextView text_view_activity_video_title;
    private CircularImageView rounded_image_view_activity_video_user;
    private TextView text_view_activity_video_user;
    private String content;
    private WebView web_view_activity_video_content;
    private LikeButton android_like_button_fav_video_activity;
    private LikeButton android_like_button_share_video_activity;
    private LikeButton android_like_button_like_video_activity;
    private LikeButton android_like_button_comment_video_activity;
    private RelativeLayout relative_layout_comment_section;
    private EditText edit_text_comment_add;
    private ProgressBar progress_bar_comment_add;
    private ProgressBar progress_bar_comment_list;
    private CardView card_view_comments;
    private ImageView image_button_comment_add;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recycle_view_comment;
    private CommentAdapter commentAdapter;
    private ImageView imageView_empty_comment;
    private List<Comment> commentList = new ArrayList<>();
    private boolean comment;
    private String color;
    private int likes;
    private int views;
    private String type;
    private String extension;
    private String created;
    private PrefManager prefManager;
    private RecyclerView recycler_view_activity_video_tags;
    private ChipsLayoutManager chipsLayoutManager;
    private TagAdapter tagAdapter;
    private LinearLayout linear_layout_user;
    private TextView text_view_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager =  new PrefManager(getApplicationContext());

        setContentView(R.layout.activity_video);
        Bundle bundle = getIntent().getExtras() ;
        this.id =  bundle.getInt("id");
        this.title =  bundle.getString("title");
        this.image =  bundle.getString("image");
        this.created =  bundle.getString("created");
        this.content =  bundle.getString("content");
        this.user_name =  bundle.getString("user_name");
        this.user_image =  bundle.getString("user_image");
        this.user_id =  bundle.getInt("user_id");
        this.comment =  bundle.getBoolean("comment");
        this.color =  bundle.getString("color");
        this.likes =  bundle.getInt("likes");
        this.views =  bundle.getInt("views");
        this.type =  bundle.getString("type");
        this.extension =  bundle.getString("extension");
        this.video = bundle.getString("video");
        initView();
        initArticle();
        initAction();
        showAdsBanner();
    }
    @Override
    public void onBackPressed(){
        if (card_view_comments.getVisibility() == View.VISIBLE) {
            Animation c = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_down);
            c.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    card_view_comments.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            card_view_comments.startAnimation(c);
            return;
        }
        if (this.from==null) {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
            return;
        }else{
            Intent intent= new Intent(VideoActivity.this,MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
            finish();
        }

    }
    public void initAction(){
        this.linear_layout_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VideoActivity.this,UserActivity.class);
                intent.putExtra("id",user_id);
                intent.putExtra("name",user_name);
                intent.putExtra("image",user_image);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        this.rounded_image_view_activity_video_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VideoActivity.this,UserActivity.class);
                intent.putExtra("id",user_id);
                intent.putExtra("name",user_name);
                intent.putExtra("image",user_image);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        this.image_button_comment_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });
        this.edit_text_comment_add.addTextChangedListener(new VideoActivity.CommentTextWatcher(this.edit_text_comment_add));


        Animation c= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.initial);
        c.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                card_view_comments.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.card_view_comments.startAnimation(c);



        this.android_like_button_comment_video_activity.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                android_like_button_comment_video_activity.setLiked(false);
                showCommentBox();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                android_like_button_comment_video_activity.setLiked(false);
                showCommentBox();
            }
        });
        this.android_like_button_like_video_activity.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                toggleLike();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                toggleLike();
            }
        });
        this.android_like_button_fav_video_activity.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addFavorite();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                addFavorite();
            }
        });
        this.android_like_button_share_video_activity.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                share();
                android_like_button_share_video_activity.setLiked(false);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                share();
                android_like_button_share_video_activity.setLiked(false);
            }
        });
    }
    public void share(){
        String shareBody =title+"\n\n"+getResources().getString(R.string.read_article_from)+"\n"+ Config.BASE_URL.replace("api","share")+id+".html";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.app_name));
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));
    }

    private void showCommentBox() {
        getComments();
        if (card_view_comments.getVisibility() == View.VISIBLE)
        {
            Animation c= AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_down);
            c.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    card_view_comments.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            card_view_comments.startAnimation(c);


        }else{
            Animation c= AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);
            c.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    card_view_comments.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            card_view_comments.startAnimation(c);

        }
    }
    public void addFavorite(){
        final FavoritesStorage storageFavorites= new FavoritesStorage(getApplicationContext());
        List<Article> wallpapers = storageFavorites.loadFavorites();
        List<Article> favorites_list = storageFavorites.loadFavorites();
        Boolean exist = false;
        if (favorites_list==null){
            favorites_list= new ArrayList<>();
        }
        for (int i = 0; i <favorites_list.size() ; i++) {
            if (favorites_list.get(i).getId().equals(id)){
                exist = true;
            }
        }
        if (exist  == false) {
            ArrayList<Article> audios= new ArrayList<Article>();
            for (int i = 0; i < favorites_list.size(); i++) {
                audios.add(favorites_list.get(i));
            }
            Article w = new Article();
            w.setId(id);
            w.setTitle(title);
            w.setCreated(created);
            w.setColor(color);
            w.setComment(comment);
            w.setContent(content);
            w.setExtension(extension);
            w.setUserid(user_id);
            w.setUser(user_name);
            w.setUserimage(user_image);
            w.setExtension(extension);
            w.setLikes(likes);
            w.setType(type);
            w.setViews(views);




            audios.add(w);
            storageFavorites.storeFavorite(audios);

            android_like_button_fav_video_activity.setLiked(true);
        }else{
            ArrayList<Article> new_favorites= new ArrayList<Article>();
            for (int i = 0; i < favorites_list.size(); i++) {
                if (!favorites_list.get(i).getId().equals(id)){
                    new_favorites.add(favorites_list.get(i));

                }
            }
            storageFavorites.storeFavorite(new_favorites);
            android_like_button_fav_video_activity.setLiked(false);
        }
    }


    public void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        // Grabs a reference to the player view
        this.linear_layout_user=(LinearLayout) findViewById(R.id.linear_layout_user);
        this.android_like_button_fav_video_activity=(LikeButton) findViewById(R.id.android_like_button_fav_video_activity);
        this.android_like_button_share_video_activity=(LikeButton) findViewById(R.id.android_like_button_share_video_activity);
        this.android_like_button_comment_video_activity=(LikeButton) findViewById(R.id.android_like_button_comment_video_activity);
        this.android_like_button_like_video_activity=(LikeButton) findViewById(R.id.android_like_button_like_video_activity);
        this.text_view_time=(TextView) findViewById(R.id.text_view_time);

        this.web_view_activity_video_content=(WebView) findViewById(R.id.web_view_activity_video_content);
        web_view_activity_video_content.setWebChromeClient(new WebChromeClient());
        web_view_activity_video_content.setWebViewClient(new WebViewClient());
        web_view_activity_video_content.getSettings().setJavaScriptEnabled(true);
        web_view_activity_video_content.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && (url.startsWith("http://")  || url.startsWith("https://"))) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                    return true;
                } else {
                    return false;
                }
            }
        });
        web_view_activity_video_content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        web_view_activity_video_content.setLongClickable(false);
        web_view_activity_video_content.setHapticFeedbackEnabled(false);
        this.text_view_activity_video_user=(TextView) findViewById(R.id.text_view_activity_video_user);
        this.text_view_activity_video_title=(TextView) findViewById(R.id.text_view_activity_video_title);
        this.rounded_image_view_activity_video_user=(CircularImageView) findViewById(R.id.rounded_image_view_activity_video_user);
        this.image_view_activity_video=(ImageView) findViewById(R.id.image_view_activity_video);
        // comment
        this.relative_layout_comment_section=(RelativeLayout) findViewById(R.id.relative_layout_comment_section);
        this.edit_text_comment_add=(EditText) findViewById(R.id.edit_text_comment_add);
        this.progress_bar_comment_add=(ProgressBar) findViewById(R.id.progress_bar_comment_add);
        this.progress_bar_comment_list=(ProgressBar) findViewById(R.id.progress_bar_comment_list);
        this.card_view_comments=(CardView) findViewById(R.id.card_view_comments);
        this.image_button_comment_add=(ImageView) findViewById(R.id.image_button_comment_add);
        this.recycle_view_comment=(RecyclerView) findViewById(R.id.recycle_view_comment);


        this.linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        this.linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.commentAdapter = new CommentAdapter(commentList, getApplication());
        this.recycle_view_comment.setHasFixedSize(true);
        this.recycle_view_comment.setAdapter(commentAdapter);
        this.recycle_view_comment.setLayoutManager(linearLayoutManager);
        this.imageView_empty_comment=(ImageView) findViewById(R.id.imageView_empty_comment);


        this.recycler_view_activity_video_tags=(RecyclerView) findViewById(R.id.recycler_view_activity_video_tags);
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
        recycler_view_activity_video_tags.setHasFixedSize(true);
        recycler_view_activity_video_tags.setAdapter(tagAdapter);
        recycler_view_activity_video_tags.setLayoutManager(chipsLayoutManager);


        shouldAutoPlay = true;
        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
        window = new Timeline.Window();
        ivHideControllerButton = (ImageView) findViewById(R.id.exo_controller);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_screen, menu);
        return true;
    }
    public void initArticle(){
        final FavoritesStorage storageFavorites= new FavoritesStorage(getApplicationContext());
        List<Article> articles = storageFavorites.loadFavorites();
        Boolean exist = false;
        if (articles==null){
            articles= new ArrayList<>();
        }
        for (int i = 0; i <articles.size() ; i++) {
            if (articles.get(i).getId().equals(id)){
                exist = true;
            }
        }
        if (exist == false) {
            android_like_button_fav_video_activity.setLiked(false);
        } else {
            android_like_button_fav_video_activity.setLiked(true);
        }

        text_view_activity_video_user.setText(user_name);
        text_view_activity_video_title.setText(title);
        text_view_time.setText(created);
        Picasso.with(this).load(image).into(this.image_view_activity_video);
        Picasso.with(this).load(user_image).into(this.rounded_image_view_activity_video_user);
        String content_video ="<style type=\"text/css\">img{background-color:#ccc;min-height:100px;max-width:100%  !important;border-radius: 5px;  box-shadow: 0px 5px 5px 1px  #ccc;margin-bottom:10px;margin-top:10px}</style>"+ content+" <script type=\"text/javascript\"> function showAndroidToast(toast) {Android.showToast(toast);}</script>";
        web_view_activity_video_content.loadData(content_video, "text/html; charset=utf-8", "utf-8");
        web_view_activity_video_content.addJavascriptInterface(new WebAppInterface(this), "Android");


        if (prefManager.getBoolean("like_"+id+"_done")){
            android_like_button_like_video_activity.setLiked(true);
        }else{
            android_like_button_like_video_activity.setLiked(false);
        }
        if (!prefManager.getBoolean("view_"+id+"_done")){
            addView();
        }
        getTags();




    }
    public int dip2Px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 1.9f);
    }
    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String url) {
            Intent intent = new Intent(VideoActivity.this,ImageActivity.class);
            intent.putExtra("url",url);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);

        }
    }
    public void addComment(){


         PrefManager prf= new PrefManager(getApplicationContext());
         if (prf.getString("LOGGED").toString().equals("TRUE")){

        byte[] data = new byte[0];
        String comment_final ="";
        try {
            data = edit_text_comment_add.getText().toString().getBytes("UTF-8");
            comment_final = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            comment_final = edit_text_comment_add.getText().toString();
            e.printStackTrace();
        }
        progress_bar_comment_add.setVisibility(View.VISIBLE);
        image_button_comment_add.setVisibility(View.GONE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.addComment(prf.getString("ID_USER"),id,comment_final);
        call.enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getCode()==200){
                        recycle_view_comment.setVisibility(View.VISIBLE);
                        imageView_empty_comment.setVisibility(View.GONE);
                        Toasty.success(VideoActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        edit_text_comment_add.setText("");
                        String id="";
                        String content="";
                        String user="";
                        String image="";

                        for (int i=0;i<response.body().getValues().size();i++){
                            if (response.body().getValues().get(i).getName().equals("id")){
                                id=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("content")){
                                content=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("user")){
                                user=response.body().getValues().get(i).getValue();
                            }
                            if (response.body().getValues().get(i).getName().equals("image")){
                                image=response.body().getValues().get(i).getValue();
                            }
                        }
                        Comment comment= new Comment();
                        comment.setId(Integer.parseInt(id));
                        comment.setUser(user);
                        comment.setContent(content);
                        comment.setImage(image);
                        comment.setEnabled(true);
                        comment.setCreated(getResources().getString(R.string.now_time));
                        commentList.add(comment);
                        commentAdapter.notifyDataSetChanged();

                    }else{
                        Toasty.error(VideoActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                recycle_view_comment.scrollToPosition(recycle_view_comment.getAdapter().getItemCount()-1);
                recycle_view_comment.scrollToPosition(recycle_view_comment.getAdapter().getItemCount()-1);
                commentAdapter.notifyDataSetChanged();
                progress_bar_comment_add.setVisibility(View.GONE);
                image_button_comment_add.setVisibility(View.VISIBLE);
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                progress_bar_comment_add.setVisibility(View.VISIBLE);
                image_button_comment_add.setVisibility(View.GONE);
            }
        });
        }else{
            Intent intent = new Intent(VideoActivity.this,AuthActivity.class);
            startActivity(intent);
        }

    }
    public void getTags(){

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Tag>> call = service.getTags(id);
        call.enqueue(new retrofit2.Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                if(response.isSuccessful()) {
                    commentList.clear();
                    if (response.body().size() != 0) {
                        for (int i = 0; i < response.body().size(); i++)
                            tagList.add(response.body().get(i));

                        tagAdapter.notifyDataSetChanged();


                    }
                }
                recycle_view_comment.setNestedScrollingEnabled(false);

            }

            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {


            }
        });
    }
    public void getComments(){
        progress_bar_comment_list.setVisibility(View.VISIBLE);
        recycle_view_comment.setVisibility(View.GONE);
        imageView_empty_comment.setVisibility(View.GONE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Comment>> call = service.getComments(id);
        call.enqueue(new retrofit2.Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if(response.isSuccessful()) {
                    commentList.clear();
                    if (response.body().size() != 0) {
                        for (int i = 0; i < response.body().size(); i++)
                            commentList.add(response.body().get(i));

                        commentAdapter.notifyDataSetChanged();

                        progress_bar_comment_list.setVisibility(View.GONE);
                        recycle_view_comment.setVisibility(View.VISIBLE);
                        imageView_empty_comment.setVisibility(View.GONE);


                    } else {
                        progress_bar_comment_list.setVisibility(View.GONE);
                        recycle_view_comment.setVisibility(View.GONE);
                        imageView_empty_comment.setVisibility(View.VISIBLE);

                    }
                }else{

                }
                recycle_view_comment.setNestedScrollingEnabled(false);

            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {


            }
        });
    }
    private class CommentTextWatcher implements TextWatcher {
        private View view;
        private CommentTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edit_text_comment_add:
                    ValidateComment();
                    break;
            }
        }
    }
    private boolean ValidateComment() {
        String email = edit_text_comment_add.getText().toString().trim();
        if (email.isEmpty()) {
            image_button_comment_add.setEnabled(false);
            return false;
        }else{
            image_button_comment_add.setEnabled(true);
        }
        return true;
    }

    private void toggleLike() {
        if (prefManager.getBoolean("like_"+id+"_done")){
            android_like_button_like_video_activity.setLiked(false);
            removeLike();
        }else{
            android_like_button_like_video_activity.setLiked(true);
            addLike();
        }
    }
    public void addView(){

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.addView(id);
        call.enqueue(new retrofit2.Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()) {
                    prefManager.setBoolean("view_"+id+"_done",true);

                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }
    public void removeLike(){

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.removeLike(id);
        call.enqueue(new retrofit2.Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()) {
                    prefManager.setBoolean("like_"+id+"_done",false);
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }
    public void addLike(){

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.addLike(id);
        call.enqueue(new retrofit2.Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()) {
                    prefManager.setBoolean("like_"+id+"_done",true);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                finish();
                overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
                break;
        }
        return super.onOptionsItemSelected(item);
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



    private void initializePlayer() {

        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.video_view);
        simpleExoPlayerView.requestFocus();

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        simpleExoPlayerView.setPlayer(player);

        player.setPlayWhenReady(shouldAutoPlay);
/*        MediaSource mediaSource = new HlsMediaSource(Uri.parse("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"),
                mediaDataSourceFactory, mainHandler, null);*/

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(video),
                mediaDataSourceFactory, extractorsFactory, null, null);

        player.prepare(mediaSource);

        ivHideControllerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(VideoActivity.this,FullscreenActivity.class);
                intent.putExtra("video",video);
                startActivity(intent);

            }
        });
    }

    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

}
