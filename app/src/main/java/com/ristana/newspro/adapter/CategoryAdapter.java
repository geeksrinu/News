package com.ristana.newspro.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopeer.shadow.ShadowView;
import com.ristana.newspro.R;
import com.ristana.newspro.entity.Category;
import com.ristana.newspro.ui.CategoryActivity;
import com.ristana.newspro.ui.TagActivity;
import com.squareup.picasso.Picasso;

import net.colindodd.gradientlayout.GradientLinearLayout;
import net.colindodd.gradientlayout.GradientRelativeLayout;

import java.util.List;

/**
 * Created by hsn on 22/12/2017.
 */


public class CategoryAdapter extends  RecyclerView.Adapter<CategoryAdapter.CategoryHolder>{
    private List<Category> categoryList;
    private Activity activity;
    public CategoryAdapter(List<Category> categoryList, Activity activity) {
        this.categoryList = categoryList;
        this.activity = activity;
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, null);
        CategoryHolder mh = new CategoryHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder,final int position) {

        holder.text_view_item_category.setText(categoryList.get(position).getTitle());
        Picasso.with(activity).load(categoryList.get(position).getImage()).into(holder.image_view_item_category);
        holder.shadow_view.setShadowColor(Color.parseColor(categoryList.get(position).getColor()));
        holder.shadow_view.setBackgroundClr(Color.parseColor(categoryList.get(position).getColor()));
        holder.shadow_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent  =  new Intent(activity.getApplicationContext(), CategoryActivity.class);
                    intent.putExtra("id",categoryList.get(position).getId());
                    intent.putExtra("title",categoryList.get(position).getTitle());
                    intent.putExtra("color",categoryList.get(position).getColor());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                }catch (IndexOutOfBoundsException e){

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {
        private ShadowView shadow_view;
        private TextView text_view_item_category;
        private ImageView image_view_item_category;
        private RelativeLayout relative_layout_item_category;

        public CategoryHolder(View itemView) {
            super(itemView);
            shadow_view =(ShadowView) itemView.findViewById(R.id.shadow_view);
            text_view_item_category=(TextView) itemView.findViewById(R.id.text_view_item_category);
            relative_layout_item_category =(RelativeLayout) itemView.findViewById(R.id.relative_layout_item_category);
            image_view_item_category=(ImageView) itemView.findViewById(R.id.image_view_item_category);
            Typeface face = Typeface.createFromAsset(activity.getAssets(), "Bitter-Bold.ttf");
            text_view_item_category.setTypeface(face);

        }
    }


    public int dip2Px(float dpValue) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 1.9f);
    }


}
