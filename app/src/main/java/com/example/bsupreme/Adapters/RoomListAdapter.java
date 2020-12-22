package com.example.bsupreme.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.bsupreme.Models.RoomListModel;
import com.example.bsupreme.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class RoomListAdapter extends FirestoreRecyclerAdapter<RoomListModel, RoomListAdapter.RoomListHolder> {


    private RoomListAdapter.OnItemClickListener listener;


    class RoomListHolder extends RecyclerView.ViewHolder {

        TextView roomPriceText, roomBedsText, roomTypeText;


        public RoomListHolder(View itemView) {
            super(itemView);
            roomPriceText = itemView.findViewById(R.id.roomPriceTextCard);
            roomBedsText = itemView.findViewById(R.id.roomBedsTextCard);
            roomTypeText = itemView.findViewById(R.id.roomTypeTextCard);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.OnItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }

    }

    public interface OnItemClickListener {
        void OnItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public RoomListAdapter(@NonNull FirestoreRecyclerOptions<RoomListModel> options) {
        super(options);
    }

    @NonNull
    @Override
    public RoomListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.desc_room_booking_card, parent, false);
        return new RoomListAdapter.RoomListHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull RoomListHolder holder, int position, @NonNull RoomListModel model) {
        String Type = "";

        if(model.getType().equals("cottage")) Type = "Cottage";
        else if(model.getType().equals("super deluxe")) Type = "Super Deluxe";
        else Type = "Deluxe";

        holder.roomTypeText.setText(Type);
        holder.roomPriceText.setText(String.valueOf(model.getPrice()));
        holder.roomBedsText.setText(String.valueOf(model.getBeds()));
    }



}
