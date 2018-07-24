package com.ristana.newspro.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ristana.newspro.R;
import com.ristana.newspro.entity.Language;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsn on 25/01/2018.
 */


public class LanguageAdapter extends RecyclerView.Adapter implements SelectableLanguageViewHolder.OnItemSelectedListener {

    private final List<Language> mValues;
    private boolean isMultiSelectionEnabled = false;
    SelectableLanguageViewHolder.OnItemSelectedListener listener;
    Activity activity;
    public LanguageAdapter( SelectableLanguageViewHolder.OnItemSelectedListener listener,
                            List<Language> items,boolean isMultiSelectionEnabled,Activity activity) {
        this.listener = listener;
        this.isMultiSelectionEnabled = isMultiSelectionEnabled;
        this.activity=activity;
        mValues = new ArrayList<>();
        for (Language item : items) {
            mValues.add(item);
        }
    }

    @Override
    public SelectableLanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_language, parent, false);

        return new SelectableLanguageViewHolder(itemView, this,activity);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        SelectableLanguageViewHolder holder = (SelectableLanguageViewHolder) viewHolder;
        Language selectableItem = mValues.get(position);
        String name = selectableItem.getLanguage();
        holder.textView.setText(name);

        Picasso.with(activity.getApplicationContext()).load(selectableItem.getImage()).error(R.drawable.ic_language).placeholder(R.drawable.ic_language).into(holder.image_view_iten_language);

        if (isMultiSelectionEnabled) {
            TypedValue value = new TypedValue();
            holder.textView.getContext().getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorMultiple, value, true);
            int checkMarkDrawableResId = value.resourceId;
            holder.textView.setCheckMarkDrawable(checkMarkDrawableResId);
        } else {
            TypedValue value = new TypedValue();
            holder.textView.getContext().getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorSingle, value, true);
            int checkMarkDrawableResId = value.resourceId;
            holder.textView.setCheckMarkDrawable(checkMarkDrawableResId);
        }

        holder.mItem = selectableItem;
        holder.setChecked(holder.mItem.isSelected());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public List<Language> getSelectedItems() {

        List<Language> selectedItems = new ArrayList<>();
        for (Language item : mValues) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    @Override
    public int getItemViewType(int position) {
        if(isMultiSelectionEnabled){
            return SelectableLanguageViewHolder.MULTI_SELECTION;
        }
        else{
            return SelectableLanguageViewHolder.SINGLE_SELECTION;
        }
    }

    @Override
    public void onItemSelected(Language item) {
        if (item.getId() == 0){
            for (Language selectableItem : mValues) {
                selectableItem.setSelected(false);
            }

            mValues.get(0).setSelected(true);
            notifyDataSetChanged();

        }else{
            mValues.get(0).setSelected(false);
            if (!isMultiSelectionEnabled) {

                for (Language selectableItem : mValues) {
                    if (!selectableItem.equals(item) && selectableItem.isSelected()) {
                        selectableItem.setSelected(false);
                    } else if (selectableItem.equals(item)  && item.isSelected()) {
                        selectableItem.setSelected(true);
                    }
                }
                notifyDataSetChanged();
            }
            notifyDataSetChanged();

        }
        Boolean nullable = true;
        for (Language selectableItem : mValues) {
            if (selectableItem.isSelected()) {
                nullable= false;
            }
        }
        if (nullable){
            for (Language selectableItem : mValues) {
                selectableItem.setSelected(false);
            }

            mValues.get(0).setSelected(true);
            notifyDataSetChanged();
        }
        listener.onItemSelected(item);

    }
}