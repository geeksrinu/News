package com.ristana.newspro.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ristana.newspro.R;
import com.ristana.newspro.entity.City;
import com.ristana.newspro.entity.Language;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsn on 26/01/2018.
 */

public class CityAdapter  extends RecyclerView.Adapter implements SelectableCItyViewHolder.OnItemSelectedListener {


    private final List<City> mValues;
    private boolean isMultiSelectionEnabled = false;
    SelectableCItyViewHolder.OnItemSelectedListener listener;
    Activity activity;
    public CityAdapter( SelectableCItyViewHolder.OnItemSelectedListener listener,  List<City> items,boolean isMultiSelectionEnabled,Activity activity) {
        this.listener = listener;
        this.isMultiSelectionEnabled = isMultiSelectionEnabled;
        this.activity=activity;
        mValues = new ArrayList<>();
        for (City item : items) {
            mValues.add(item);
        }
    }

    @Override
    public SelectableCItyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);

        return new SelectableCItyViewHolder(itemView, this,activity);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        SelectableCItyViewHolder holder = (SelectableCItyViewHolder) viewHolder;
        City selectableItem = mValues.get(position);
        String name = selectableItem.getName();
        holder.text_view_item_city_item_select.setText(name);


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

    public List<City> getSelectedItems() {

        List<City> selectedItems = new ArrayList<>();
        for (City item : mValues) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    @Override
    public int getItemViewType(int position) {
        if(isMultiSelectionEnabled){
            return SelectableCItyViewHolder.MULTI_SELECTION;
        }
        else{
            return SelectableCItyViewHolder.SINGLE_SELECTION;
        }
    }

    @Override
    public void onItemSelected(City item) {
        if (item.getId() == 0){
            for (City selectableItem : mValues) {
                selectableItem.setSelected(false);
            }

            mValues.get(0).setSelected(true);
            notifyDataSetChanged();

        }else{
            mValues.get(0).setSelected(false);
            if (!isMultiSelectionEnabled) {

                for (City selectableItem : mValues) {
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
        for (City selectableItem : mValues) {
            if (selectableItem.isSelected()) {
                nullable= false;
            }
        }
        if (nullable){
            for (City selectableItem : mValues) {
                selectableItem.setSelected(false);
            }

            mValues.get(0).setSelected(true);
            notifyDataSetChanged();
        }
        listener.onItemSelected(item);

    }
}
