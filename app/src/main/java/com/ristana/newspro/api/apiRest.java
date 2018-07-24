package com.ristana.newspro.api;

import com.ristana.newspro.config.Config;
import com.ristana.newspro.entity.ApiResponse;
import com.ristana.newspro.entity.Article;
import com.ristana.newspro.entity.Category;
import com.ristana.newspro.entity.City;
import com.ristana.newspro.entity.Comment;
import com.ristana.newspro.entity.Global;
import com.ristana.newspro.entity.Language;
import com.ristana.newspro.entity.Question;
import com.ristana.newspro.entity.Slide;
import com.ristana.newspro.entity.Tag;
import com.ristana.newspro.entity.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by hsn on 29/12/2017.
 */

public interface apiRest {

    @GET("device/{tkn}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> addDevice(@Path("tkn")  String tkn);

    @GET("version/check/{code}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> check(@Path("code") Integer code);

    @GET("install/add/{id}/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> addInstall(@Path("id") String id);

    @GET("category/all/{category}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Category>> categoriesAll(@Path("category") Integer category);

    @GET("home/latest/{language}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<Global> homeLatest(@Path("language") Integer language);

    @GET("home/popular/{language}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<Global> homePopular(@Path("language") Integer language);

    @GET("article/all/{page}/{created}/{language}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Article>> articlesAll(@Path("page") Integer page,@Path("created") String created,@Path("language") Integer language);


    @GET("article/by/id/{id}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<Article> articleById(@Path("id") Integer id);

    @GET("article/user/{page}/{user}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Article>> articlesByUser(@Path("page") Integer page,@Path("user") Integer user);


    @GET("article/by/tag/{page}/{created}/{language}/{tag}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Article>> articlesByTag(@Path("page") Integer page,@Path("created") String created,@Path("language") Integer language,@Path("tag") Integer tag);

    @GET("article/by/city/{page}/{created}/{language}/{city}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Article>> articlesByCity(@Path("page") Integer page,@Path("created") String created,@Path("language") Integer language,@Path("city") Integer city);


    @GET("article/by/category/{page}/{created}/{language}/{category}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Article>> articlesByCategory(@Path("page") Integer page,@Path("created") String created,@Path("language") Integer language,@Path("category") Integer category);


    @GET("video/all/{page}/{created}/{language}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Article>> videosAll(@Path("page") Integer page,@Path("created") String created,@Path("language") Integer language);


    @GET("question/featured/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Question>> questionFeatured();

    @GET("question/vote/{question}/{choices}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> addVote(@Path("question") Integer question,@Path("choices") String choices);


    @GET("slide/all/{language}/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Slide>> slideAll(@Path("language") Integer language);

    @GET("tags/all/{language}/{page}/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Tag>> tagsAll(@Path("language") Integer language,@Path("page") Integer page);

    @GET("city/all/{language}/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<City>> CityAll(@Path("language") Integer language);


    @GET("language/all/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Language>> languageAll();

    @FormUrlEncoded
    @POST("user/register/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> register(@Field("name") String name, @Field("username") String username, @Field("password") String password, @Field("type") String type, @Field("image") String image);

    @FormUrlEncoded
    @POST("user/token/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> editToken(@Field("user") Integer user,@Field("key") String key,@Field("token_f") String token_f);

    @FormUrlEncoded
    @POST("user/login/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> login(@Field("email") String email, @Field("password") String password);


    @FormUrlEncoded
    @POST("comment/add/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> addComment(@Field("user")  String user, @Field("id") Integer id, @Field("comment") String comment);

    @GET("comment/list/{id}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Comment>> getComments(@Path("id") Integer id);

    @FormUrlEncoded
    @POST("article/add/likes/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<Integer> addLike(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("article/add/views/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<Integer> addView(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("article/remove/likes/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<Integer> removeLike(@Field("id") Integer id);

    @GET("article/tags/{id}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Tag>> getTags(@Path("id") Integer id);

    @GET("user/request/{key}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> request(@Path("key") String key);

    @GET("user/reset/{id}/{key}/{new_}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> reset(@Path("id") String id,@Path("key") String key,@Path("new_") String new_);


    @GET("user/password/{id}/{old}/{new_}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> changePassword(@Path("id") String id,@Path("old") String old,@Path("new_") String new_);


    @GET("user/email/{email}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> sendEmail(@Path("email") String email);


    @Multipart
    @POST("user/upload/{id}/{key}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> uploadImage(@Part MultipartBody.Part file, @Path("id") String id, @Path("key") String key);


    @GET("article/query/{page}/{order}/{language}/{query}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Article>> articlesByQuery(@Path("page") Integer page, @Path("order") String order, @Path("language") Integer language,@Path("query") String query);

    @FormUrlEncoded
    @POST("support/add/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> addSupport(@Field("email") String email, @Field("name") String name , @Field("message") String message);

    @GET("question/all/{page}/{language}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Question>> questionsAll(@Path("page") Integer page,@Path("language") Integer language);

    @GET("user/follow/{user}/{follower}/{key}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> follow(@Path("user") Integer user,@Path("follower") Integer follower,@Path("key") String key);


    @GET("user/followers/{user}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<User>> getFollowers(@Path("user") Integer user);

    @GET("user/followings/{user}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<User>> getFollowing(@Path("user") Integer user);

    @GET("user/get/{user}/{me}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> getUser(@Path("user") Integer user,@Path("me") Integer me);

    @FormUrlEncoded
    @POST("user/edit/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> editUser(@Field("user") Integer user,@Field("key") String key,@Field("name") String name,@Field("email") String email,@Field("facebook") String facebook,@Field("twitter") String twitter,@Field("instagram") String instagram);


}