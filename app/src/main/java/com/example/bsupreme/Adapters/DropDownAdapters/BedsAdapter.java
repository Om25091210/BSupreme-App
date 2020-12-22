package com.example.bsupreme.Adapters.DropDownAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bsupreme.R;


import com.example.bsupreme.Models.DropdownItems.BedsItem;

import java.util.ArrayList;


public class BedsAdapter extends ArrayAdapter<BedsItem> {


    public BedsAdapter(Context context, ArrayList<BedsItem> bedsList) {
        super(context, 0, bedsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View converView, ViewGroup parent) {
        if (converView == null) {
            converView = LayoutInflater.from(getContext()).inflate(
                    R.layout.des_drop_down_no_of_beds_row, parent, false
            );
        }
        TextView bedsText = converView.findViewById(R.id.bedsDropDown);

        BedsItem currentItem = getItem(position);

        if(currentItem != null) bedsText.setText(currentItem.getNumberOfBeds());

        return converView;


    }


}
