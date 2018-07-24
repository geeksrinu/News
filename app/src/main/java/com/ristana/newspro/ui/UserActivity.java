package com.ristana.newspro.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ristana.newspro.R;
import com.ristana.newspro.adapter.ArticleAdapter;
import com.ristana.newspro.adapter.UserAdapter;
import com.ristana.newspro.api.apiClient;
import com.ristana.newspro.api.apiRest;
import com.ristana.newspro.entity.ApiResponse;
import com.ristana.newspro.entity.Article;
import com.ristana.newspro.entity.Category;
import com.ristana.newspro.entity.Question;
import com.ristana.newspro.entity.Slide;
import com.ristana.newspro.entity.Tag;
import com.ristana.newspro.entity.User;
import com.ristana.newspro.manager.PrefManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class UserActivity extends AppCompatActivity {

    private int id;
    private String name;
    private String image;
    private String facebook;
    private String email;
    private String instagram;
    private String twitter;

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private Integer page = 0;
    private Boolean loaded=false;
    private PrefManager prefManager;


    private List<Tag> tagList =  new ArrayList<>();
    private List<Category> categoryList =  new ArrayList<>();
    private List<Article> articleList =  new ArrayList<>();
    private List<Question> questionList =  new ArrayList<>();
    private List<Slide> slideList =  new ArrayList<>();
    private ArticleAdapter articleAdapter;


    private Toolbar toolbar;
    private RelativeLayout coordinatorLayout;
    private Button button_try_again;
    private CircleImageView image_view_profile_user_activity;
    private TextView text_view_profile_user_activity
            ;
    private TextView text_view_followers_count_user_activity;
    private TextView text_view_following_count_activity_user;
    private LinearLayout linear_layout_page_error;
    private Button button_follow_user_activity;
    private RelativeLayout relative_layout_load_more;
    private LinearLayout linear_layout_followers;
    private LinearLayout linear_layout_following;
    private ImageView image_view_activity_user_email;
    private ImageView image_view_activity_user_instagram;
    private ImageView image_view_activity_user_twitter;
    private ImageView image_view_activity_user_facebook;
    private Button button_edit_user_activity;


    private AlertDialog.Builder builderFollowing;
    private List<User> followings=new ArrayList<>();

    private AlertDialog.Builder builderFollowers;
    private List<User> followers=new ArrayList<>();


    private ProgressDialog loading_progress;
    private RelativeLayout relative_layout_user_activity_error;
    private SwipeRefreshLayout swipe_refreshl_user_activity;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recycle_view_user_activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras() ;


        PrefManager prf= new PrefManager(getApplicationContext());
        this.prefManager= new PrefManager(getApplicationContext());

        this.id =  bundle.getInt("id");
        this.name =  bundle.getString("name");
        this.image =  bundle.getString("image");
        setContentView(R.layout.activity_user);
        initView();
        initAction();
        initUser();
        loadArticlesByUser();
    }
    private void initUser() {
        text_view_profile_user_activity.setText(name);
        if (!image.isEmpty()){
            Picasso.with(getApplicationContext()).load(image).error(R.drawable.profile).placeholder(R.drawable.profile).into(this.image_view_profile_user_activity);
        }else{
            Picasso.with(getApplicationContext()).load(R.drawable.profile).error(R.drawable.profile).placeholder(R.drawable.profile).into(this.image_view_profile_user_activity);
        }
        PrefManager prf= new PrefManager(getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            Integer me = Integer.parseInt(prf.getString("ID_USER"));
            if (id==me){
                button_follow_user_activity.setVisibility(View.GONE);
               // button_edit_user_activity.setVisibility(View.VISIBLE);
            }else{
                button_follow_user_activity.setVisibility(View.VISIBLE);
               // button_edit_user_activity.setVisibility(View.GONE);
            }
        }else{
            button_follow_user_activity.setVisibility(View.VISIBLE);
            //button_edit_user_activity.setVisibility(View.GONE);
        }

        getUser();
    }

    private void initView() {
        setContentView(R.layout.activity_user);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        this.coordinatorLayout =  (RelativeLayout) findViewById(R.id.coordinatorLayout);
        this.button_try_again=(Button) findViewById(R.id.button_try_again);
        this.image_view_profile_user_activity=(CircleImageView) findViewById(R.id.image_view_profile_user_activity);
        this.text_view_profile_user_activity=(TextView) findViewById(R.id.text_view_profile_user_activity);
        this.text_view_followers_count_user_activity =(TextView) findViewById(R.id.text_view_followers_count_user_activity);
        this.text_view_following_count_activity_user=(TextView) findViewById(R.id.text_view_following_count_activity_user);
        this.linear_layout_page_error=(LinearLayout) findViewById(R.id.linear_layout_page_error);
        this.relative_layout_load_more=(RelativeLayout) findViewById(R.id.relative_layout_load_more);
        this.button_follow_user_activity=(Button) findViewById(R.id.button_follow_user_activity);
        this.linear_layout_followers=(LinearLayout) findViewById(R.id.linear_layout_followers);
        this.linear_layout_following=(LinearLayout) findViewById(R.id.linear_layout_following);
        this.image_view_activity_user_email=(ImageView) findViewById(R.id.image_view_activity_user_email);
        this.image_view_activity_user_instagram=(ImageView) findViewById(R.id.image_view_activity_user_instagram);
        this.image_view_activity_user_twitter=(ImageView) findViewById(R.id.image_view_activity_user_twitter);
        this.image_view_activity_user_facebook=(ImageView) findViewById(R.id.image_view_activity_user_facebook);
        this.button_edit_user_activity=(Button) findViewById(R.id.button_edit_user_activity);
        this.relative_layout_user_activity_error=(RelativeLayout) findViewById(R.id.relative_layout_user_activity_error);
        this.swipe_refreshl_user_activity=(SwipeRefreshLayout) findViewById(R.id.swipe_refreshl_user_activity);
        this.recycle_view_user_activity=(RecyclerView) findViewById(R.id.recycle_view_user_activity);


        this.gridLayoutManager=  new GridLayoutManager(this.getApplicationContext(),1,GridLayoutManager.VERTICAL,false);
        this.articleAdapter =new ArticleAdapter(slideList,tagList,categoryList,questionList,articleList,this,false);
        recycle_view_user_activity.setHasFixedSize(true);
        recycle_view_user_activity.setAdapter(articleAdapter);
        recycle_view_user_activity.setLayoutManager(gridLayoutManager);





    }

    private void initAction() {
        this.image_view_activity_user_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",email, null));
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
        this.image_view_activity_user_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(facebook));
                startActivity(i);
            }
        });
        this.image_view_activity_user_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(instagram));
                startActivity(i);
            }
        });
        this.image_view_activity_user_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(twitter));
                startActivity(i);
            }
        });
        this.linear_layout_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                builderFollowing = new AlertDialog.Builder(UserActivity.this);
                builderFollowing.setIcon(R.drawable.ic_follow);
                builderFollowing.setTitle("Following");

                View view = (View)  getLayoutInflater().inflate(R.layout.user_row,null);

                ListView listView= (ListView) view.findViewById(R.id.user_rows);
                listView.setAdapter(new UserAdapter(followings,UserActivity.this));
                builderFollowing.setView(view);
                builderFollowing.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderFollowing.show();
            }
        });
        this.linear_layout_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFollowers();

            }
        });
        this.linear_layout_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFollowings();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        this.button_follow_user_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow();
            }
        });
        this.button_edit_user_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this,EditActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("name",name);
                intent.putExtra("image",image);
                startActivity(intent);
            }
        });
        this.swipe_refreshl_user_activity.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loading = true;
                page = 0;
                loadArticlesByUser();

            }
        });
        this.button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading = true;
                page = 0;
                loadArticlesByUser();

            }
        });
        recycle_view_user_activity.addOnScrollListener(new RecyclerView.OnScrollListener()
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
    public void loadFollowings(){
        loading_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<User>> call = service.getFollowing(id);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()){
                    builderFollowing = new AlertDialog.Builder(UserActivity.this);
                    builderFollowing.setIcon(R.drawable.ic_follow);
                    builderFollowing.setTitle("Followings");
                    View view = (View)  getLayoutInflater().inflate(R.layout.user_row,null);
                    ListView listView= (ListView) view.findViewById(R.id.user_rows);
                    listView.setAdapter(new UserAdapter(response.body(),UserActivity.this));
                    builderFollowing.setView(view);
                    builderFollowing.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderFollowing.show();
                }
                loading_progress.dismiss();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loading_progress.dismiss();
            }
        });

    }
    public void loadFollowers(){
        loading_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<User>> call = service.getFollowers(id);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()){
                    for (int i = 0; i < followers.size(); i++) {
                        followers.add(response.body().get(i));
                    }
                    builderFollowers = new AlertDialog.Builder(UserActivity.this);
                    builderFollowers.setIcon(R.drawable.ic_follow);
                    builderFollowers.setTitle("Followers");
                    View view = (View)  getLayoutInflater().inflate(R.layout.user_row,null);
                    ListView listView= (ListView) view.findViewById(R.id.user_rows);
                    listView.setAdapter(new UserAdapter(response.body(),UserActivity.this));
                    builderFollowers.setView(view);
                    builderFollowers.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderFollowers.show();
                }
                loading_progress.dismiss();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loading_progress.dismiss();
            }
        });

    }
    public void follow(){

        PrefManager prf= new PrefManager(getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            button_follow_user_activity.setText(getResources().getString(R.string.loading));
            button_follow_user_activity.setEnabled(false);
            String follower = prf.getString("ID_USER");
            String key = prf.getString("TOKEN_USER");
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<ApiResponse> call = service.follow(id, Integer.parseInt(follower), key);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getCode().equals(200)){
                            button_follow_user_activity.setText("UnFollow");
                            text_view_followers_count_user_activity.setText((Integer.parseInt(text_view_followers_count_user_activity.getText().toString())+1)+"");
                        }else if (response.body().getCode().equals(202)) {
                            button_follow_user_activity.setText("Follow");
                            text_view_followers_count_user_activity.setText((Integer.parseInt(text_view_followers_count_user_activity.getText().toString())-1)+"");

                        }
                    }
                    button_follow_user_activity.setEnabled(true);

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    button_follow_user_activity.setEnabled(true);
                }
            });
        }else{
            Intent intent = new Intent(UserActivity.this,AuthActivity.class);
            startActivity(intent);
        }
    }

    private void getUser() {
        PrefManager prf= new PrefManager(getApplicationContext());
        Integer follower= -1;
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            button_follow_user_activity.setEnabled(false);
            follower = Integer.parseInt(prf.getString("ID_USER"));
        }
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.getUser(id,follower);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()){

                    for (int i=0;i<response.body().getValues().size();i++){
                        if (response.body().getValues().get(i).getName().equals("followers")){
                            text_view_followers_count_user_activity.setText(format(Integer.parseInt(response.body().getValues().get(i).getValue())));
                        }
                        if (response.body().getValues().get(i).getName().equals("followings")){
                            text_view_following_count_activity_user.setText(format(Integer.parseInt(response.body().getValues().get(i).getValue())));

                        }
                        if (response.body().getValues().get(i).getName().equals("facebook")){
                            facebook = response.body().getValues().get(i).getValue();
                            if (facebook!=null){
                                if (!facebook.isEmpty()){
                                    if (facebook.startsWith("http://") || facebook.startsWith("https://")) {
                                        image_view_activity_user_facebook.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                        if (response.body().getValues().get(i).getName().equals("twitter")){
                            twitter = response.body().getValues().get(i).getValue();
                            if (twitter!=null) {

                                if (!twitter.isEmpty()) {
                                    if (twitter.startsWith("http://") || twitter.startsWith("https://")) {
                                        image_view_activity_user_twitter.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                        if (response.body().getValues().get(i).getName().equals("instagram")){

                            instagram = response.body().getValues().get(i).getValue();
                            if (instagram!=null) {

                                if (!instagram.isEmpty()){
                                    if (instagram.startsWith("http://") || instagram.startsWith("https://")) {

                                        image_view_activity_user_instagram.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                        if (response.body().getValues().get(i).getName().equals("email")){
                            email = response.body().getValues().get(i).getValue();
                            if (email!=null) {
                                if (!email.isEmpty()){
                                    image_view_activity_user_email.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        if (response.body().getValues().get(i).getName().equals("follow")){
                            if (response.body().getValues().get(i).getValue().equals("true"))
                                button_follow_user_activity.setText("UnFollow");
                            else
                                button_follow_user_activity.setText("Follow");
                        }
                    }

                }else{
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getUser();
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();

                }
                button_follow_user_activity.setEnabled(true);
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                button_follow_user_activity.setEnabled(true);

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getUser();
                            }
                        });
                snackbar.setActionTextColor(Color.RED);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();

            }
        });
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
    @Override
    public void onBackPressed(){
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
            return;
    }


    private void loadNextArticlesByTag() {
        relative_layout_load_more.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Article>> call = service.articlesByUser(page,id);
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
                    Toasty.error(UserActivity.this, getResources().getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                }
                relative_layout_load_more.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                Toasty.error(UserActivity.this, getResources().getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                relative_layout_load_more.setVisibility(View.GONE);
            }
        });
    }

    public void loadArticlesByUser() {


        swipe_refreshl_user_activity.setRefreshing(true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Article>> call = service.articlesByUser(page,id);

        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if (response.isSuccessful()){

                    questionList.clear();
                    slideList.clear();
                    categoryList.clear();
                    articleList.clear();

                    if (response.body().size()>0){
                        for (int i = 0; i < response.body().size(); i++) {
                            if (response.body().get(i).getType().equals("article")){
                                articleList.add(response.body().get(i).setTypeView(0));
                            }else{
                                articleList.add(response.body().get(i).setTypeView(4));
                            }
                        }
                    }else{

                    }
                    articleAdapter.notifyDataSetChanged();
                    page++;
                    loading=true;
                    relative_layout_user_activity_error.setVisibility(View.GONE);
                    recycle_view_user_activity.setVisibility(View.VISIBLE);
                }else{
                    relative_layout_user_activity_error.setVisibility(View.VISIBLE);
                    recycle_view_user_activity.setVisibility(View.GONE);
                }
                swipe_refreshl_user_activity.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                swipe_refreshl_user_activity.setRefreshing(false);
                relative_layout_user_activity_error.setVisibility(View.VISIBLE);
                recycle_view_user_activity.setVisibility(View.GONE);
            }
        });
    }
}
