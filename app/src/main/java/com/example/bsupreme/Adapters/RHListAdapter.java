package com.example.bsupreme.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bsupreme.Models.RHListModel;
import com.example.bsupreme.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class RHListAdapter extends FirestoreRecyclerAdapter<RHListModel, RHListAdapter.RHListHolder> {


    private OnItemClickListener listener;

    class RHListHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, desc, rating, location;

        public RHListHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.card_name);
            desc = itemView.findViewById(R.id.card_desc);
            rating = itemView.findViewById(R.id.card_rating);
            location = itemView.findViewById(R.id.card_location);
            image = itemView.findViewById(R.id.card_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.OnItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }

    }

    public interface OnItemClickListener {
        void OnItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public  void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public RHListAdapter(@NonNull FirestoreRecyclerOptions<RHListModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RHListHolder holder, int position, @NonNull RHListModel model) {
            String desc = getDesc(model);

            holder.name.setText(model.getName());
            holder.desc.setText(desc);
            holder.rating.setText(String.valueOf(model.getRating()));
            holder.location.setText(model.getDest());
            Picasso.get().load(model.getImage()).into(holder.image);

    }

    @NonNull
    @Override
    public RHListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.des_rest_hotel_card_view, parent, false);
        return new RHListHolder(view);
    }

    private String getDesc(RHListModel model) {
        String description = "";
        if (model.isIs_veg()) {
            description += "Veg ";
        } else {
            description += "Non-Veg ";
        }

        if (model.isIs_bar())
            description += "\u2022 Resto-bar ";
        if (model.isIs_room())
            description += "\u2022 Lodge ";
        if (model.isIs_table())
            description += "\u2022 Food ";

        return description;
    }


}
