package com.example.bsupreme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bsupreme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.Objects;

public class DetailsBookingActivity extends AppCompatActivity {

    //Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Intent Variables from Main Activity
    Intent intent;
    String documentId;

    //UI variables
    ImageView detailsImage, vegNonVeg, barNoBar, stayNoStay, foodNoFood;
    CollapsingToolbarLayout detailsName;
    TextView detailsInfo, detailsDest, detailsRating;
    Button bookTable, bookRoom;



    //Collapseable Tool bar
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_booking);

        // Hooks
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // UI Hooks
        // Image Views
        detailsImage = findViewById(R.id.detailsImage);
        vegNonVeg = findViewById(R.id.detailsVegNonVeg);
        barNoBar = findViewById(R.id.detailsBarNoBar);
        stayNoStay = findViewById(R.id.detailsStayNoStay);
        foodNoFood = findViewById(R.id.detailsFoodNoFood);
        // ToolBar
        detailsName = findViewById(R.id.detailsNamecollapsingToolbarLayout);
        final Typeface tF = ResourcesCompat.getFont(this,R.font.nunito_semi_bold);

        // Text Views
        detailsInfo = findViewById(R.id.detailsInfo);
        detailsDest = findViewById(R.id.detailsLocation);
        detailsRating = findViewById(R.id.detailsRating);
        // Buttons
        bookRoom = findViewById(R.id.bookRoom);
        bookTable = findViewById(R.id.bookTable);



        //Get data from previous activity
        intent = getIntent();
        documentId = intent.getStringExtra("documentId");


        DocumentReference docRef = db.collection("restaurants").document(documentId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
//                        Log.d("Data", "DocumentSnapshot data: " + document.getData());
                        constructDetailsPage(document, tF);
                    } else {
                        Log.d("No Data", "No such document");
                    }
                } else {
                    Log.d("No Data", "get failed with ", task.getException());
                }
            }


        });

        bookTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsBookingActivity.this, BookTableActivity.class);
                Log.d("Button Table Pressed", "Pressed Going to booking screen ............");
                intent.putExtra("documentId", documentId);

                startActivity(intent);

            }
        });


        bookRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsBookingActivity.this, BookRoomActivity.class);
                Log.d("Button Room Pressed", "Pressed Going to booking screen ............");
                intent.putExtra("documentId", documentId);

                startActivity(intent);

            }
        });

    }

    private void constructDetailsPage(DocumentSnapshot document, Typeface tF) {
        Map<String, Object> data = document.getData();
        Picasso.get().load((String) data.get("image")).into(detailsImage);

        detailsName.setTitle((CharSequence) data.get("name"));
        detailsName.setCollapsedTitleTypeface(tF);
        detailsName.setExpandedTitleTypeface(tF);

        detailsRating.setText(String.valueOf(data.get("rating")));
        detailsInfo.setText((CharSequence) data.get("desc"));
        detailsDest.setText((CharSequence) data.get("dest"));

        if((boolean) data.get("is_veg"))
            vegNonVeg.setImageResource(R.drawable.ic_twotone_veg);
        else
            vegNonVeg.setImageResource(R.drawable.ic_twotone_non_veg);

        if ((boolean) data.get("is_bar"))
            barNoBar.setImageResource(R.drawable.ic_twotone_drinks);
        else
            barNoBar.setImageResource(R.drawable.ic_twotone_no_drinks);

        if ((boolean) data.get("is_room")){
            stayNoStay.setVisibility(View.VISIBLE);
            bookRoom.setVisibility(View.VISIBLE);}
        else {
            stayNoStay.setVisibility(View.GONE);
            bookRoom.setVisibility(View.GONE);
        }

        if ((boolean) data.get("is_tables")){
            foodNoFood.setVisibility(View.VISIBLE);
            bookTable.setVisibility(View.VISIBLE);
        }
        else {
            foodNoFood.setVisibility(View.GONE);
            bookTable.setVisibility(View.GONE);
        }

    }


}