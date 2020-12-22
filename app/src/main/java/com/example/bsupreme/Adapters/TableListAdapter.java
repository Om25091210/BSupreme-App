package com.example.bsupreme.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bsupreme.Models.TableModel;
import com.example.bsupreme.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class TableListAdapter extends FirestoreRecyclerAdapter<TableModel, TableListAdapter.TableListHolder> {
    private TableListAdapter.OnItemClickListener listener;

    class TableListHolder extends RecyclerView.ViewHolder {
        TextView seatNumber;
        LinearLayout ac, barCounter, grill;

        public TableListHolder(View itemView) {
            super(itemView);
            seatNumber = itemView.findViewById(R.id.tableSeats);
            ac = itemView.findViewById(R.id.acIndicator);
            grill = itemView.findViewById(R.id.grillIndicator);
            barCounter = itemView.findViewById(R.id.barIndicator);


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

    @NonNull
    @Override
    public TableListAdapter.TableListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.desc_table_booking_card_view, parent, false);
        return new TableListHolder(view);
    }

    public interface OnItemClickListener {
        void OnItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(TableListAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public TableListAdapter(@NonNull FirestoreRecyclerOptions<TableModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TableListAdapter.TableListHolder holder, int position, @NonNull TableModel model) {

        holder.seatNumber.setText(String.valueOf(model.getSeating()));

        if(!model.isIs_ac())
            holder.ac.setVisibility(LinearLayout.GONE);
        else
            holder.ac.setVisibility(LinearLayout.VISIBLE);

        if(!model.isIs_counter())
            holder.barCounter.setVisibility(LinearLayout.GONE);
        else
            holder.barCounter.setVisibility(LinearLayout.VISIBLE);

        if(!model.isIs_grill())
            holder.grill.setVisibility(LinearLayout.GONE);
        else
            holder.grill.setVisibility(LinearLayout.VISIBLE);

    }


}
