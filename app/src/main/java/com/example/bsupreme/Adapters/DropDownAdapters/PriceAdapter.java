package com.example.bsupreme.Adapters.DropDownAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bsupreme.Models.DropdownItems.BedsItem;
import com.example.bsupreme.Models.DropdownItems.PriceItem;
import com.example.bsupreme.R;

import java.util.ArrayList;

public class PriceAdapter extends ArrayAdapter<PriceItem> {

    public PriceAdapter(Context context, ArrayList<PriceItem> priceList) {
        super(context, 0, priceList);
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
                    R.layout.des_drop_down_price_row, parent, false
            );
        }
        TextView priceText = converView.findViewById(R.id.priceDropDown);

        PriceItem currentItem = getItem(position);

        if(currentItem != null) priceText.setText(currentItem.getRoomPrice());

        return converView;

    }

}
