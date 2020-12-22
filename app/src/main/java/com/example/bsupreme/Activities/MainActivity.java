package com.example.bsupreme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.bsupreme.Adapters.RHListAdapter;
import com.example.bsupreme.Models.RHListModel;
import com.example.bsupreme.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Constants
    static final float END_SCALE = 0.8f;

    //Variables
    String filter_option;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference RHListref = db.collection("restaurants");
    private RHListAdapter adapter;

    CoordinatorLayout contentView;

    // Bottom Navigation Bar
    ChipNavigationBar chipNavigationBar;

    // Side Navigation Drawer
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView sideDrawerIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Hooks
        chipNavigationBar = findViewById(R.id.bottom_nav_bar);
        contentView = findViewById(R.id.main_activity_content);
        //  Side Navigation Drawer Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        sideDrawerIcon = findViewById(R.id.side_navigation_icon);


        navigationDrawer();
        bottomNavMenu();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    private void navigationDrawer() {
        // Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.home_side_nav);

        sideDrawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else
                    drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        animateNavigationDrawer();


    }

    private void animateNavigationDrawer() {
        drawerLayout.setScrimColor(getResources().getColor(R.color.white_blue_accent));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    private void bottomNavMenu() {
        chipNavigationBar.setItemSelected(R.id.bottom_nav_hotel, true);
        filter_option = "hotels";
        setUpRecyclerView(filter_option, true);

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.bottom_nav_hotel:
                        filter_option = "hotels";
                        setUpRecyclerView(filter_option, false);

                        break;
                    case R.id.bottom_nav_restaurants:
                        filter_option = "restaurants";
                        setUpRecyclerView(filter_option, false);
                        break;
                }
            }
        });

    }

    private void setUpRecyclerView(String filter_option, boolean first_create) {
        Query query = RHListref.whereEqualTo("is_room", true);

        if (filter_option.equals("hotels")) {
            if (!first_create) adapter.stopListening();
            query = RHListref.whereEqualTo("is_room", true);

        } else if (filter_option.equals("restaurants")) {
            if (!first_create) adapter.stopListening();
            query = RHListref.whereEqualTo("is_tables", true);
        }
        FirestoreRecyclerOptions<RHListModel> options = new FirestoreRecyclerOptions.Builder<RHListModel>()
                .setQuery(query, RHListModel.class)
                .build();

        adapter = new RHListAdapter(options);
        adapter.startListening();
        RecyclerView recyclerView = findViewById(R.id.RH_Recycler_View);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RHListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                String docId = documentSnapshot.getId();
                Intent intent = new Intent(MainActivity.this, DetailsBookingActivity.class);
                intent.putExtra("documentId", docId);

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