package com.example.bsupreme.Adapters.DropDownAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bsupreme.Models.DropdownItems.PriceItem;
import com.example.bsupreme.Models.DropdownItems.RoomTypeItem;
import com.example.bsupreme.R;

import java.util.ArrayList;

public class RoomTypeAdapter extends ArrayAdapter<RoomTypeItem> {

    public RoomTypeAdapter(Context context, ArrayList<RoomTypeItem> roomList) {
        super(context, 0, roomList);
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
                    R.layout.des_drop_down_room_type_row, parent, false
            );
        }
        TextView roomText = converView.findViewById(R.id.roomTypeDropDown);

        RoomTypeItem currentItem = getItem(position);

        if(currentItem != null) roomText.setText(currentItem.getRoomType());

        return converView;

    }


}
