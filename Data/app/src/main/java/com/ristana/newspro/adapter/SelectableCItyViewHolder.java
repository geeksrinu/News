package com.ristana.newspro.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ristana.newspro.R;
import com.ristana.newspro.entity.City;
import com.ristana.newspro.entity.Language;

/**
 * Created by hsn on 26/01/2018.
 */

public class SelectableCItyViewHolder extends RecyclerView.ViewHolder {


    public static final int MULTI_SELECTION = 2;
    public static final int SINGLE_SELECTION = 1;
    private final CardView card_view_city_item_select;
    private final Activity activity;
    CheckedTextView textView;
    TextView text_view_item_city_item_select;
    City mItem;
    SelectableCItyViewHolder.OnItemSelectedListener itemSelectedListener;

    public SelectableCItyViewHolder(View view, SelectableCItyViewHolder.OnItemSelectedListener listener, Activity activity) {
        super(view);
        this.activity= activity;
        card_view_city_item_select = (CardView) view.findViewById(R.id.card_view_city_item_select);

        itemSelectedListener = listener;
        textView = (CheckedTextView) view.findViewById(R.id.checked_text_item);
        text_view_item_city_item_select = (TextView) view.findViewById(R.id.text_view_item_city_item_select);
        card_view_city_item_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mItem.isSelected() && getItemViewType() == MULTI_SELECTION) {
                    setChecked(false);
                } else {
                    setChecked(true);
                }
                itemSelectedListener.onItemSelected(mItem);

            }
        });
    }

    public void setChecked(boolean value) {
        if (value) {
            card_view_city_item_select.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDarkSelect));
        } else {
            card_view_city_item_select.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        }
        mItem.setSelected(value);
        textView.setChecked(value);
    }

    public interface OnItemSelectedListener {

        void onItemSelected(City item);
    }
}
