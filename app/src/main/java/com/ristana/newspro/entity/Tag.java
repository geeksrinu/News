package com.ristana.newspro.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hsn on 14/01/2018.
 */

public class Tag {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("articles")
    @Expose
    private Integer articles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArticles(Integer articles) {
        this.articles = articles;
    }

    public Integer getArticles() {
        return articles;
    }
}