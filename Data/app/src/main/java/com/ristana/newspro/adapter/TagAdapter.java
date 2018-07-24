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
import com.ristana.newspro.entity.Tag;
import com.ristana.newspro.ui.TagActivity;
import com.ristana.newspro.ui.TagsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by hsn on 14/01/2018.
 */


public class TagAdapter extends  RecyclerView.Adapter<com.ristana.newspro.adapter.TagAdapter.TagHolder>{
    private List<Tag> TagList;
    private Activity activity;
    private  String[] allColors ;

    public TagAdapter(List<Tag> TagList, Activity activity) {
        this.TagList = TagList;
        this.activity = activity;
        allColors = activity.getResources().getStringArray(R.array.colors);
    }

    @Override
    public com.ristana.newspro.adapter.TagAdapter.TagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==1){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, null);
            com.ristana.newspro.adapter.TagAdapter.TagHolder mh = new com.ristana.newspro.adapter.TagAdapter.TagHolder(v);
            return mh;

        }else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_all, null);
            com.ristana.newspro.adapter.TagAdapter.TagHolder mh = new com.ristana.newspro.adapter.TagAdapter.TagHolder(v);
            return mh;

        }


    }

    @Override
    public void onBindViewHolder(com.ristana.newspro.adapter.TagAdapter.TagHolder holder, final int position) {


            int step=1;
            int final_step=1;
            for (int i = 1; i < position+1; i++) {
                if (i==position+1){
                    final_step=step;
                }
                step++;
                if (step>allColors.length){
                    step=1;
                }
            }
        holder.shadow_view.setShadowColor(Color.parseColor(allColors[step-1]));
        holder.shadow_view.setBackgroundClr(Color.parseColor(allColors[step-1]));

        if (getItemViewType(position)==1){

                holder.text_view_item_tag.setText(TagList.get(position).getName());
                holder.text_view_item_number.setText(format(TagList.get(position).getArticles())+"");
                holder.text_view_item_number.setTextColor(Color.parseColor(allColors[step-1]));
                holder.shadow_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent intent  =  new Intent(activity.getApplicationContext(), TagActivity.class);
                            intent.putExtra("id",TagList.get(position).getId());
                            intent.putExtra("name",TagList.get(position).getName());
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                        }catch (IndexOutOfBoundsException e){

                        }
                    }
                });
            }else{
                holder.shadow_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent  =  new Intent(activity.getApplicationContext(), TagsActivity.class);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });
            }

    }

    @Override
    public int getItemCount() {
        return TagList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (TagList.get(position)==null){
            return 0;
        }else{
            return 1;
        }
    }

    public class TagHolder extends RecyclerView.ViewHolder {
        private TextView text_view_item_number;
        private TextView text_view_item_tag;
        private ShadowView shadow_view;

        public TagHolder(View itemView) {
            super(itemView);
            text_view_item_tag=(TextView) itemView.findViewById(R.id.text_view_item_tag);
            shadow_view =(ShadowView) itemView.findViewById(R.id.shadow_view);
            text_view_item_number = (TextView) itemView.findViewById(R.id.text_view_item_number);
            Typeface face = Typeface.createFromAsset(activity.getAssets(), "Bitter-Bold.ttf");
            text_view_item_tag.setTypeface(face);
        }
    }


    public int dip2Px(float dpValue) {
        final float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 1.6f);
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
}


