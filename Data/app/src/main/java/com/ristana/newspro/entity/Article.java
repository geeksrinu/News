package com.ristana.newspro.entity;

/**
 * Created by hsn on 06/01/2018.
 */


        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class Article {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("review")
    @Expose
    private Boolean review;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("userid")
    @Expose
    private Integer userid;
    @SerializedName("comment")
    @Expose
    private Boolean comment;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("views")
    @Expose
    private Integer views;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("extension")
    @Expose
    private String extension;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("userimage")
    @Expose
    private String userimage;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("comments")
    @Expose
    private Integer comments;
    @SerializedName("video")
    @Expose
    private String video;

    private Integer typeView = 0;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getReview() {
        return review;
    }

    public void setReview(Boolean review) {
        this.review = review;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Boolean getComment() {
        return comment;
    }

    public void setComment(Boolean comment) {
        this.comment = comment;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Article setTypeView(Integer typeView) {
        this.typeView = typeView;
        return this;
    }

    public Integer getTypeView() {
        return typeView;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideo() {
        return video;
    }
}