package com.ristana.newspro.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hsn on 11/01/2018.
 */

public class Global {
    @SerializedName("tags")
    @Expose
    private List<Tag> tags = null;
    @SerializedName("slides")
    @Expose
    private List<Slide> slides = null;
    @SerializedName("categories")
    @Expose
    private List<Category> categories = null;
    @SerializedName("questions")
    @Expose
    private List<Question> questions = null;
    @SerializedName("articles")
    @Expose
    private List<Article> articles = null;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Slide> getSlides() {
        return slides;
    }

    public void setSlides(List<Slide> slides) {
        this.slides = slides;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

}