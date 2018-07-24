package com.ristana.newspro.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ristana.newspro.R;
import com.ristana.newspro.entity.Slide;
import com.ristana.newspro.ui.ArticleActivity;
import com.ristana.newspro.ui.CategoryActivity;
import com.ristana.newspro.ui.VideoActivity;
import com.ristana.newspro.ui.YoutubeActivity;
import com.squareup.picasso.Picasso;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by hsn on 22/12/2017.
 */

public class SlideAdapter extends PagerAdapter {
    private List<Slide> slideList =new ArrayList<Slide>();
    private Activity activity;

    public SlideAdapter( Activity activity,List<Slide> stringList) {
        this.slideList = stringList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return slideList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater)this.activity.getSystemService(this.activity.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.item_slide_one, container, false);

        TextView text_view_item_slide_one_title =  (TextView)  view.findViewById(R.id.text_view_item_slide_one_title);
        ImageView image_view_item_slide_one =  (ImageView)  view.findViewById(R.id.image_view_item_slide_one);

        Typeface face = Typeface.createFromAsset(activity.getAssets(), "Bitter-Bold.ttf");
        text_view_item_slide_one_title.setTypeface(face);

        byte[] data = android.util.Base64.decode(slideList.get(position).getTitle(), android.util.Base64.DEFAULT);
        final String final_text = new String(data, Charset.forName("UTF-8"));

        text_view_item_slide_one_title.setText(final_text);

        CardView card_view_item_slide_one = (CardView) view.findViewById(R.id.card_view_item_slide_one);
        card_view_item_slide_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideList.get(position).getType().equals("3") && slideList.get(position).getArticle()!=null) {
                    if (!slideList.get(position).getArticle().getType().equals("article")) {
                        Intent intent = new Intent(activity.getApplicationContext(), VideoActivity.class);
                        if (slideList.get(position).getArticle().getType().equals("video"))
                            intent = new Intent(activity.getApplicationContext(), VideoActivity.class);
                        else
                            intent = new Intent(activity.getApplicationContext(), YoutubeActivity.class);

                        intent.putExtra("id", slideList.get(position).getArticle().getId());
                        intent.putExtra("title", slideList.get(position).getArticle().getTitle());
                        intent.putExtra("video", slideList.get(position).getArticle().getVideo());
                        intent.putExtra("image", slideList.get(position).getArticle().getImage());
                        intent.putExtra("created", slideList.get(position).getArticle().getCreated());
                        intent.putExtra("color", slideList.get(position).getArticle().getColor());
                        intent.putExtra("comment", slideList.get(position).getArticle().getComment());
                        intent.putExtra("content", slideList.get(position).getArticle().getContent());
                        intent.putExtra("user_id", slideList.get(position).getArticle().getUserid());
                        intent.putExtra("user_name", slideList.get(position).getArticle().getUser());
                        intent.putExtra("user_image", slideList.get(position).getArticle().getUserimage());
                        intent.putExtra("likes", slideList.get(position).getArticle().getLikes());
                        intent.putExtra("views", slideList.get(position).getArticle().getViews());
                        intent.putExtra("type", slideList.get(position).getArticle().getType());
                        intent.putExtra("extension", slideList.get(position).getArticle().getExtension());

                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else {
                        Intent intent = new Intent(activity.getApplicationContext(), ArticleActivity.class);
                        intent.putExtra("id", slideList.get(position).getArticle().getId());
                        intent.putExtra("title", slideList.get(position).getArticle().getTitle());
                        intent.putExtra("image", slideList.get(position).getArticle().getImage());
                        intent.putExtra("created", slideList.get(position).getArticle().getCreated());
                        intent.putExtra("color", slideList.get(position).getArticle().getColor());
                        intent.putExtra("comment", slideList.get(position).getArticle().getComment());
                        intent.putExtra("content", slideList.get(position).getArticle().getContent());
                        intent.putExtra("user_id", slideList.get(position).getArticle().getUserid());
                        intent.putExtra("user_name", slideList.get(position).getArticle().getUser());
                        intent.putExtra("user_image", slideList.get(position).getArticle().getUserimage());
                        intent.putExtra("likes", slideList.get(position).getArticle().getLikes());
                        intent.putExtra("views", slideList.get(position).getArticle().getViews());
                        intent.putExtra("type", slideList.get(position).getArticle().getType());
                        intent.putExtra("extension", slideList.get(position).getArticle().getExtension());

                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                }else if(slideList.get(position).getType().equals("1") && slideList.get(position).getCategory()!=null){
                    Intent intent  =  new Intent(activity.getApplicationContext(), CategoryActivity.class);
                    intent.putExtra("id",slideList.get(position).getCategory().getId());
                    intent.putExtra("title",slideList.get(position).getCategory().getTitle());
                    intent.putExtra("color",slideList.get(position).getCategory().getColor());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                }else if(slideList.get(position).getType().equals("2") &&  slideList.get(position).getUrl()!=null){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(slideList.get(position).getUrl()));
                    activity.startActivity(browserIntent);
                }

            }
        });

        Picasso.with(activity).load(slideList.get(position).getImage()).into(image_view_item_slide_one);

        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }



    @Override
    public float getPageWidth (int position) {
        return 1f;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);

    }
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);

    }
    @Override
    public Parcelable saveState() {
        return null;
    }
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
