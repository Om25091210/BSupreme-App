package com.example.bsupreme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import com.example.bsupreme.Adapters.RHListAdapter;
import com.example.bsupreme.Adapters.TableListAdapter;
import com.example.bsupreme.Models.RHListModel;
import com.example.bsupreme.Models.TableModel;
import com.example.bsupreme.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class BookTableActivity extends AppCompatActivity {
    //Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Intent Variables from Main Activity
    Intent intent;
    String documentId;

    //Text View
    TextView hotelName;

    //Variables
    private TableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_table);


        hotelName = findViewById(R.id.bookingHotelName);



        //Get data from previous activity
        intent = getIntent();
        documentId = intent.getStringExtra("documentId");


        CollectionReference tableListRef = db.collection("restaurants").document(documentId).collection("tables");
        DocumentReference docRef = db.collection("restaurants").document(documentId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
//                        Log.d("Data", "DocumentSnapshot data: " + document.getData());
                        hotelName.setText((String) document.getData().get("name"));




                    } else {
                        Log.d("No Data", "No such document");
                    }
                } else {
                    Log.d("No Data", "get failed with ", task.getException());
                }
            }


        });

        setUpRecyclerView(tableListRef);
    }

    private void setUpRecyclerView(CollectionReference tableListRef) {
        Query query = tableListRef;



        FirestoreRecyclerOptions<TableModel> options = new FirestoreRecyclerOptions.Builder<TableModel>()
                .setQuery(query, TableModel.class)
                .build();

        adapter = new TableListAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.bookingTableRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new TableListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                String  tableDocumentId= documentSnapshot.getId();
                Intent intent = new Intent(BookTableActivity.this,SelectDateTableActivity.class );
                intent.putExtra("tableDocumentId", tableDocumentId);
                intent.putExtra("restaurantDocumentId", documentId);


                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}