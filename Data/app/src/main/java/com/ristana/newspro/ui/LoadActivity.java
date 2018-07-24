package com.ristana.newspro.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ristana.newspro.R;
import com.ristana.newspro.api.apiClient;
import com.ristana.newspro.api.apiRest;
import com.ristana.newspro.entity.Article;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoadActivity extends AppCompatActivity {
    private  Integer id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Uri data = this.getIntent().getData();
        if (data==null){
            Bundle bundle = getIntent().getExtras() ;
            this.id =  bundle.getInt("id");
        }else{
            this.id=Integer.parseInt(data.getPath().replace("/share/","").replace(".html",""));
        }
        getArticle();
    }
    public void getArticle(){

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Article> call = service.articleById(id);
        call.enqueue(new retrofit2.Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                if(response.isSuccessful()) {
                    if (response.body().getType().equals("article")) {
                        Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                        intent.putExtra("id", response.body().getId());
                        intent.putExtra("title", response.body().getTitle());
                        intent.putExtra("image", response.body().getImage());
                        intent.putExtra("created", response.body().getCreated());
                        intent.putExtra("color", response.body().getColor());
                        intent.putExtra("comment", response.body().getComment());
                        intent.putExtra("content", response.body().getContent());
                        intent.putExtra("user_id", response.body().getUserid());
                        intent.putExtra("user_name", response.body().getUser());
                        intent.putExtra("user_image", response.body().getUserimage());
                        intent.putExtra("likes", response.body().getLikes());
                        intent.putExtra("views", response.body().getViews());
                        intent.putExtra("type", response.body().getType());
                        intent.putExtra("extension", response.body().getExtension());

                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    } else {
                        Intent intent  =  new Intent(getApplicationContext(), VideoActivity.class);
                        if (response.body().getType().equals("video"))
                            intent  =  new Intent(getApplicationContext(), VideoActivity.class);
                        else
                            intent  =  new Intent(getApplicationContext(), YoutubeActivity.class);

                        intent.putExtra("id",response.body().getId());
                        intent.putExtra("title",response.body().getTitle());
                        intent.putExtra("video",response.body().getVideo());
                        intent.putExtra("image",response.body().getImage());
                        intent.putExtra("created",response.body().getCreated());
                        intent.putExtra("color",response.body().getColor());
                        intent.putExtra("comment",response.body().getComment());
                        intent.putExtra("content",response.body().getContent());
                        intent.putExtra("user_id",response.body().getUserid());
                        intent.putExtra("user_name",response.body().getUser());
                        intent.putExtra("user_image",response.body().getUserimage());
                        intent.putExtra("likes",response.body().getLikes());
                        intent.putExtra("views",response.body().getViews());
                        intent.putExtra("type",response.body().getType());
                        intent.putExtra("extension",response.body().getExtension());
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();

                    }
                }
            }
            @Override
            public void onFailure(Call<Article> call, Throwable t) {

            }
        });
    }
}
