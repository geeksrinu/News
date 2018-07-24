package com.ristana.newspro.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ristana.newspro.entity.Article;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by hsn on 01/02/2018.
 */

public class FavoritesStorage {
    private final String STORAGE = "MY_FAVORITE";
    private SharedPreferences preferences;
    private Context context;
    public FavoritesStorage(Context context) {
        this.context = context;
    }
    public void storeFavorite(ArrayList<Article> arrayList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("favoritesList", json);
        editor.apply();
    }

    public ArrayList<Article> loadFavorites() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("favoritesList", null);
        Type type = new TypeToken<ArrayList<Article>>() {
        }.getType();
        ArrayList<Article> articles_fav= gson.fromJson(json, type);

        if (articles_fav==null){
            articles_fav= new ArrayList<>();
        }
        return  articles_fav;
    }
    public void clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
