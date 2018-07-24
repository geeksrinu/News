package com.ristana.newspro.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.ristana.newspro.R;
import com.ristana.newspro.entity.Language;

/**
 * Created by hsn on 25/01/2018.
 */

public class SelectableLanguageViewHolder extends RecyclerView.ViewHolder {

    public static final int MULTI_SELECTION = 2;
    public static final int SINGLE_SELECTION = 1;
    CheckedTextView textView;
    Language mItem;
    OnItemSelectedListener itemSelectedListener;
    ImageView image_view_iten_language;
    Activity activity;
    public SelectableLanguageViewHolder(View view, OnItemSelectedListener listener,Activity activity) {
        super(view);
        this.activity=activity;
        itemSelectedListener = listener;
        textView = (CheckedTextView) view.findViewById(R.id.checked_text_item);
        image_view_iten_language = (ImageView) view.findViewById(R.id.image_view_iten_language);
        textView.setOnClickListener(new View.OnClickListener() {
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
            textView.setBackgroundColor(Color.parseColor("#6F36B2E2"));
        } else {
            textView.setBackgroundColor(Color.parseColor("#00000000"));

        }
        mItem.setSelected(value);
        textView.setChecked(value);
    }

    public interface OnItemSelectedListener {

        void onItemSelected(Language item);
    }

}