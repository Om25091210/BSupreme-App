package com.example.bsupreme.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bsupreme.Adapters.DropDownAdapters.BedsAdapter;
import com.example.bsupreme.Adapters.DropDownAdapters.PriceAdapter;
import com.example.bsupreme.Adapters.DropDownAdapters.RoomTypeAdapter;
import com.example.bsupreme.Adapters.RoomListAdapter;
import com.example.bsupreme.Models.DropdownItems.BedsItem;
import com.example.bsupreme.Models.DropdownItems.PriceItem;
import com.example.bsupreme.Models.DropdownItems.RoomTypeItem;
import com.example.bsupreme.Models.RoomListModel;
import com.example.bsupreme.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class SelectBookRoomActivity extends AppCompatActivity {


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Intent Variables from Main Activity
    Intent intent;
    String documentId;
    SimpleDateFormat format = new SimpleDateFormat("dd / MM / yyyy");
    String cinDateStr, coutDateStr;

    Long cinDateLong, coutDateLong;

    List<String> queryRoomList = new ArrayList<String>();
    List<String> queryAllBookedRoomsList = new ArrayList<String>();
    List<String> queryInvalidRoomList = new ArrayList<String>();

    ArrayList<BedsItem> roomsBedsList = new ArrayList<BedsItem>();
    ArrayList<String> roomsBedsListString = new ArrayList<String>();
    ArrayList<RoomTypeItem> roomsTypeList = new ArrayList<RoomTypeItem>();
    ArrayList<String> roomsTypeListStr = new ArrayList<String>();
    ArrayList<PriceItem> priceList = new ArrayList<PriceItem>();
    ArrayList<String> priceListStr = new ArrayList<String>();
    Dialog ConfirmBookingDialog;
    int itemsInAdapter;
    //UI Variables
    TextView cinDate, coutDate, loadingtext, noRoomAvailableText;
    Button changeDates;
    ImageButton refreshButton;
    Spinner bedSpinner, roomTypeSpinner, priceSpinner;
    CollectionReference bookingsCollRefToPass;
    CollectionReference restaurantCollRef;
    PriceItem priceChoice;
    RoomTypeItem roomChoice;
    BedsItem bedsChoice;
    private RoomListAdapter adapter;
    //Spinner Adapter
    private RoomTypeAdapter roomTypeSpinnerAdapter;
    private BedsAdapter bedsSpinnerAdapter;
    private PriceAdapter priceSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        queryRoomList.clear();
        queryAllBookedRoomsList.clear();
        queryInvalidRoomList.clear();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_book_room);


        //Hooks
        cinDate = findViewById(R.id.checkInDateText);
        coutDate = findViewById(R.id.checkOutDateText);
        loadingtext = findViewById(R.id.LoadingText);
        changeDates = findViewById(R.id.changeDatesButton);
        noRoomAvailableText = findViewById(R.id.NoRoomText);
        bedSpinner = findViewById(R.id.spinner_beds);
        roomTypeSpinner = findViewById(R.id.spinner_types);
        priceSpinner = findViewById(R.id.spinner_price);
        refreshButton = findViewById(R.id.refresh_button);

        ConfirmBookingDialog = new Dialog(this);


        //Get data from previous activity
        intent = getIntent();
        documentId = intent.getStringExtra("documentId");
        cinDateStr = intent.getStringExtra("cin");
        coutDateStr = intent.getStringExtra("cout");

        cinDate.setText("From " + cinDateStr);
        coutDate.setText("To " + coutDateStr);

//        Add options in price adapter
        priceList.add(new PriceItem("Min to Max"));
        priceList.add(new PriceItem("Max to Min"));


        cinDateLong = getLongValforDate(cinDateStr);
        Log.d("Cin Date Long", String.valueOf(cinDateLong));
        coutDateLong = getLongValforDate(coutDateStr);
        Log.d("Cout Date Long", String.valueOf(coutDateLong));


        Log.d("DocumentId", documentId);
        Log.d("CheckIn Date String ", cinDateStr);
        Log.d("CheckIn Date String ", coutDateStr);

        bookingsCollRefToPass = db.collection("bookings");
        restaurantCollRef = db.collection("restaurants").document(documentId).collection("rooms");
        Log.d("Room ref ", bookingsCollRefToPass.getPath());

        roomsTypeList.add(new RoomTypeItem("All"));
        roomsBedsList.add(new BedsItem("All"));

        changeDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.stopListening();
                setUpRecyclerView(bookingsCollRefToPass, restaurantCollRef);
            }
        });

        setUpRecyclerView(bookingsCollRefToPass, restaurantCollRef);
    }


    private Long getLongValforDate(String cinDateStr) {
        Long returnDateLong;

        String[] splitDate = cinDateStr.split(" / ");
        String returnDateStr = splitDate[2].substring(2, 4) + splitDate[1] + splitDate[0];
        returnDateLong = Long.valueOf(returnDateStr);

        return returnDateLong;
    }


    private void setUpRecyclerView(CollectionReference bookingsCollRef, CollectionReference restaurantCollRef) {
        loadingtext.setVisibility(View.VISIBLE);
        noRoomAvailableText.setVisibility(View.GONE);

        itemsInAdapter = 0;

        Task<QuerySnapshot> getRoomsBooked = bookingsCollRef.whereEqualTo("restaurantId", documentId).get();

        getRoomsBooked.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                    Log.d("InValid Document", String.valueOf(documentSnapshot.getData()));
                    queryAllBookedRoomsList.add((String) documentSnapshot.get("roomId"));
                }
            }
        }).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                Log.d("Condition", String.valueOf(queryAllBookedRoomsList.isEmpty()));
                if (!queryAllBookedRoomsList.isEmpty()) {
                    Task taskCheckCinDate = bookingsCollRef.whereEqualTo("restaurantId", documentId).whereGreaterThanOrEqualTo("cin", coutDateLong).get();
                    Task taskCheckCoutDate = bookingsCollRef.whereEqualTo("restaurantId", documentId).whereLessThanOrEqualTo("cout", cinDateLong).get();

                    Task<List<QuerySnapshot>> checkCheckInDates = Tasks.whenAllSuccess(taskCheckCinDate, taskCheckCoutDate);
                    checkCheckInDates.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                        @Override
                        public void onSuccess(List<QuerySnapshot> querySnapshots) {
                            for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    Log.d("Valid Document", String.valueOf(documentSnapshot.getData()));
                                    queryRoomList.add((String) documentSnapshot.get("roomId"));
                                }
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<List<QuerySnapshot>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<QuerySnapshot>> task) {
                            Log.d("QueryAllValidRoomList Before Removing", String.valueOf(queryAllBookedRoomsList));
                            Log.d("QueryValidRoomList", String.valueOf(queryRoomList));

                            for(String query : queryRoomList){
                                int index = queryAllBookedRoomsList.indexOf(query);
                                if(index != -1)
                                queryAllBookedRoomsList.remove(index);
                            }

                            Log.d("QueryAllInvalidValidRoomList After", String.valueOf(queryAllBookedRoomsList));

                            if (!queryAllBookedRoomsList.isEmpty()) {
                                Log.d("QueryValidRoomList", String.valueOf(queryRoomList));

                                restaurantCollRef.orderBy("roomId").whereNotIn("roomId", queryAllBookedRoomsList).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                        for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                            Log.d("Document in Recycler View", String.valueOf(documentSnapshot.getData()));
                                            initDropDownItems(documentSnapshot);

                                        }
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        if (itemsInAdapter != 0) {
                                            noRoomAvailableText.setVisibility(View.GONE);
                                            Query queryToPass = restaurantCollRef.orderBy("roomId").orderBy("price").whereNotIn("roomId", queryAllBookedRoomsList);
                                            initRecyclerView(restaurantCollRef, queryToPass);
                                        } else {
                                            loadingtext.setVisibility(View.GONE);
                                            noRoomAvailableText.setVisibility(View.VISIBLE);
                                        }

                                    }
                                });

                            } else {

                                restaurantCollRef.orderBy("price").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot querySnapshot) {
                                        for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                            Log.d("Document in Recycler View", String.valueOf(documentSnapshot.getData()));
                                            initDropDownItems(documentSnapshot);

                                        }
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (itemsInAdapter != 0) {
                                            noRoomAvailableText.setVisibility(View.GONE);
                                            Query queryToPass = restaurantCollRef.orderBy("price");
                                            initRecyclerView(restaurantCollRef, queryToPass);
                                        } else {
                                            loadingtext.setVisibility(View.GONE);
                                            noRoomAvailableText.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });

                            }
                        }
                    });

                } else {
                    restaurantCollRef.orderBy("price").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                Log.d("Document in Recycler View", String.valueOf(documentSnapshot.getData()));
                                initDropDownItems(documentSnapshot);
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (itemsInAdapter != 0) {
                                noRoomAvailableText.setVisibility(View.GONE);
                                Query queryToPass = restaurantCollRef.orderBy("price");
                                initRecyclerView(restaurantCollRef, queryToPass);
                            } else {
                                loadingtext.setVisibility(View.GONE);
                                noRoomAvailableText.setVisibility(View.VISIBLE);
                            }

                        }
                    });

                }
            }


        });

    }

    public void initRecyclerView(CollectionReference restaurantCollRef, Query query) {
        FirestoreRecyclerOptions<RoomListModel> options = new FirestoreRecyclerOptions.Builder<RoomListModel>()
                .setQuery(query, RoomListModel.class)
                .build();

        adapter = new RoomListAdapter(options);
        loadingtext.setVisibility(View.GONE);
        adapter.startListening();


        RecyclerView recyclerView = findViewById(R.id.roomRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SelectBookRoomActivity.this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RoomListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                Map<String, Object> data = documentSnapshot.getData();
                ShowPopUp(String.valueOf(data.get("roomId")), String.valueOf(data.get("price")), String.valueOf(data.get("beds")), String.valueOf(data.get("type")), cinDateStr, coutDateStr);
            }
        });
    }

    public void reInitRecyclerView(CollectionReference restaurantCollRef, Query query) {
        noRoomAvailableText.setVisibility(View.GONE);
        loadingtext.setVisibility(View.VISIBLE);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                itemsInAdapter = 0;
                for (DocumentSnapshot documentSnapshot : querySnapshot) {
                    Log.d("Document in Recycler View", String.valueOf(documentSnapshot.getData()));
                    itemsInAdapter++;
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (itemsInAdapter != 0) {
                    noRoomAvailableText.setVisibility(View.GONE);
                    FirestoreRecyclerOptions<RoomListModel> options = new FirestoreRecyclerOptions.Builder<RoomListModel>()
                            .setQuery(query, RoomListModel.class)
                            .build();

                    loadingtext.setVisibility(View.GONE);

                    adapter = new RoomListAdapter(options);
                    adapter.startListening();

                    RecyclerView recyclerView = findViewById(R.id.roomRecyclerView);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(SelectBookRoomActivity.this));
                    recyclerView.setAdapter(adapter);

                    adapter.setOnItemClickListener(new RoomListAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                            Map<String, Object> data = documentSnapshot.getData();
                            ShowPopUp(String.valueOf(data.get("roomId")), String.valueOf(data.get("price")), String.valueOf(data.get("beds")), String.valueOf(data.get("type")), cinDateStr, coutDateStr);
                        }
                    });
                } else {
                    loadingtext.setVisibility(View.GONE);
                    noRoomAvailableText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void initDropDownItems(DocumentSnapshot documentSnapshot) {
        itemsInAdapter++;

        Log.d("Rooms beds List", String.valueOf(roomsBedsList));
        if (!roomsBedsListString.contains(String.valueOf(documentSnapshot.get("beds")))) {
            roomsBedsList.add(new BedsItem(String.valueOf(documentSnapshot.get("beds"))));
            roomsBedsListString.add(String.valueOf(documentSnapshot.get("beds")));
        }

        Log.d("Rooms types List", String.valueOf(roomsTypeList));
        if (documentSnapshot.get("type").equals("cottage")) {
            if (!roomsTypeListStr.contains("Cottage")) {
                roomsTypeList.add(new RoomTypeItem("Cottage"));
                roomsTypeListStr.add("Cottage");
            }
        } else if (documentSnapshot.get("type").equals("super deluxe")) {
            if (!roomsTypeListStr.contains("Super Deluxe")) {
                roomsTypeList.add(new RoomTypeItem("Super Deluxe"));
                roomsTypeListStr.add("Super Deluxe");
            }

        } else if (documentSnapshot.get("type").equals("deluxe")) {
            if (!roomsTypeListStr.contains("Deluxe")) {
                roomsTypeList.add(new RoomTypeItem("Deluxe"));
                roomsTypeListStr.add("Deluxe");
            }

        }
        initAllSpinners(priceList, roomsBedsList, roomsTypeList);
    }

    private void filterRecyclerView(PriceItem priceChoice, BedsItem bedsChoice, RoomTypeItem roomChoice) {
        adapter.stopListening();
        if (bedsChoice != null && roomChoice != null) {
            if (bedsChoice.getNumberOfBeds().equals("All") && roomChoice.getRoomType().equals("All")) {
                if(!queryAllBookedRoomsList.isEmpty()){
                    Query queryToPass = restaurantCollRef.orderBy("roomId").whereNotIn("roomId", queryAllBookedRoomsList);
                    reInitRecyclerView(restaurantCollRef, queryToPass);
                }else{
                    Query queryToPass = restaurantCollRef.orderBy("roomId");
                    reInitRecyclerView(restaurantCollRef, queryToPass);
                }

            } else if (!bedsChoice.getNumberOfBeds().equals("All") && roomChoice.getRoomType().equals("All")) {
                if(!queryAllBookedRoomsList.isEmpty()){
                    Query queryToPass = restaurantCollRef.orderBy("roomId").whereNotIn("roomId", queryAllBookedRoomsList).whereEqualTo("beds", Long.valueOf(bedsChoice.getNumberOfBeds()));
                    reInitRecyclerView(restaurantCollRef, queryToPass);
                }else{
                    Query queryToPass = restaurantCollRef.orderBy("roomId").whereEqualTo("beds", Long.valueOf(bedsChoice.getNumberOfBeds()));
                    reInitRecyclerView(restaurantCollRef, queryToPass);
                }
            } else if (bedsChoice.getNumberOfBeds().equals("All") && !roomChoice.getRoomType().equals("All")) {
                if(!queryAllBookedRoomsList.isEmpty()){
                    if (roomChoice.getRoomType().equals("Cottage")) {
                        Query queryToPass = restaurantCollRef.orderBy("roomId").whereNotIn("roomId", queryAllBookedRoomsList).whereEqualTo("type", "cottage");
                        reInitRecyclerView(restaurantCollRef, queryToPass);

                    } else if (roomChoice.getRoomType().equals("Super Deluxe")) {
                        Query queryToPass = restaurantCollRef.orderBy("roomId").whereNotIn("roomId", queryAllBookedRoomsList).whereEqualTo("type", "super deluxe");
                        reInitRecyclerView(restaurantCollRef, queryToPass);

                    } else if (roomChoice.getRoomType().equals("Deluxe")) {
                        Query queryToPass = restaurantCollRef.orderBy("roomId").whereNotIn("roomId", queryAllBookedRoomsList).whereEqualTo("type", "deluxe");
                        reInitRecyclerView(restaurantCollRef, queryToPass);
                    }
                }else{
                    if (roomChoice.getRoomType().equals("Cottage")) {
                        Query queryToPass = restaurantCollRef.orderBy("roomId").whereEqualTo("type", "cottage");
                        reInitRecyclerView(restaurantCollRef, queryToPass);

                    } else if (roomChoice.getRoomType().equals("Super Deluxe")) {
                        Query queryToPass = restaurantCollRef.orderBy("roomId").whereEqualTo("type", "super deluxe");
                        reInitRecyclerView(restaurantCollRef, queryToPass);

                    } else if (roomChoice.getRoomType().equals("Deluxe")) {
                        Query queryToPass = restaurantCollRef.orderBy("roomId").whereEqualTo("type", "deluxe");
                        reInitRecyclerView(restaurantCollRef, queryToPass);
                    }
                }





            } else if (!bedsChoice.getNumberOfBeds().equals("All") && !roomChoice.getRoomType().equals("All")) {
                if(!queryAllBookedRoomsList.isEmpty()){
                    if (roomChoice.getRoomType().equals("Cottage")) {
                        Query queryToPass = restaurantCollRef.orderBy("roomId").whereNotIn("roomId", queryAllBookedRoomsList).whereEqualTo("type", "cottage").whereEqualTo("beds", Long.valueOf(bedsChoice.getNumberOfBeds()));
                        reInitRecyclerView(restaurantCollRef, queryToPass);
                    } else if (roomChoice.getRoomType().equals("Super Deluxe")) {
                        Query queryToPass = restaurantCollRef.orderBy("roomId").whereNotIn("roomId", queryAllBookedRoomsList).whereEqualTo("type", "super deluxe").whereEqualTo("beds", Long.valueOf(bedsChoice.getNumberOfBeds()));
                        reInitRecyclerView(restaurantCollRef, queryToPass);

                    } else if (roomChoice.getRoomType().equals("Deluxe")) {
                        Query queryToPass = restaurantCollRef.orderBy("roomId").whereNotIn("roomId", queryAllBookedRoomsList).whereEqualTo("type", "deluxe").whereEqualTo("beds", Long.valueOf(bedsChoice.getNumberOfBeds()));
                        reInitRecyclerView(restaurantCollRef, queryToPass);
                    }
                }else{
                    if (roomChoice.getRoomType().equals("Cottage")) {
                        Query queryToPass = restaurantCollRef.orderBy("roomId").whereEqualTo("type", "cottage").whereEqualTo("beds", Long.valueOf(bedsChoice.getNumberOfBeds()));
                        reInitRecyclerView(restaurantCollRef, queryToPass);
                    } else if (roomChoice.getRoomType().equals("Super Deluxe")) {
                        Query queryToPass = restaurantCollRef.orderBy("roomId").whereEqualTo("type", "super deluxe").whereEqualTo("beds", Long.valueOf(bedsChoice.getNumberOfBeds()));
                        reInitRecyclerView(restaurantCollRef, queryToPass);

                    } else if (roomChoice.getRoomType().equals("Deluxe")) {
                        Query queryToPass = restaurantCollRef.orderBy("roomId").whereEqualTo("type", "deluxe").whereEqualTo("beds", Long.valueOf(bedsChoice.getNumberOfBeds()));
                        reInitRecyclerView(restaurantCollRef, queryToPass);
                    }
                }






            }
        }
    }

    private void initAllSpinners(ArrayList<PriceItem> priceList, ArrayList<BedsItem> roomsBedsList, ArrayList<RoomTypeItem> roomTypeItems) {
        priceSpinnerAdapter = new PriceAdapter(this, priceList);
        bedsSpinnerAdapter = new BedsAdapter(this, roomsBedsList);
        roomTypeSpinnerAdapter = new RoomTypeAdapter(this, roomTypeItems);

        priceSpinner.setAdapter(priceSpinnerAdapter);
        bedSpinner.setAdapter(bedsSpinnerAdapter);
        roomTypeSpinner.setAdapter(roomTypeSpinnerAdapter);

        priceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                priceChoice = (PriceItem) parent.getItemAtPosition(position);
                filterRecyclerView(priceChoice, bedsChoice, roomChoice);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bedsChoice = (BedsItem) parent.getItemAtPosition(position);
                filterRecyclerView(priceChoice, bedsChoice, roomChoice);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        roomTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                roomChoice = (RoomTypeItem) parent.getItemAtPosition(position);
                filterRecyclerView(priceChoice, bedsChoice, roomChoice);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void ShowPopUp(String roomId, String roomPriceStr, String roomBedsStr, String roomTypeStr, String checkInStr, String checkOutStr) {
        TextView roomPrice, roomBeds, roomType, checkInTime, checkOutTime;
        Button confirmBooking, cancelBooking;

        ConfirmBookingDialog.setContentView(R.layout.des_booking_confirmation_room);

        roomPrice = ConfirmBookingDialog.findViewById(R.id.roomPriceTextCard);
        roomBeds = ConfirmBookingDialog.findViewById(R.id.roomBedsTextCard);
        roomType = ConfirmBookingDialog.findViewById(R.id.roomTypeTextCard);

        checkInTime = ConfirmBookingDialog.findViewById(R.id.checkInBookingDate);
        checkOutTime = ConfirmBookingDialog.findViewById(R.id.checkOutBookingTime);

        cancelBooking = ConfirmBookingDialog.findViewById(R.id.cancelBookingButton);
        confirmBooking = ConfirmBookingDialog.findViewById(R.id.confirmBookingButton);

        confirmBooking.setClickable(true);
        cancelBooking.setClickable(true);

        roomPrice.setText(roomPriceStr);
        roomBeds.setText(roomBedsStr);

        if (roomTypeStr == "cottage") roomType.setText("Cottage");
        else if (roomTypeStr == "deluxe") roomType.setText("Deluxe");
        else if (roomTypeStr == "super deluxe") roomType.setText("Super Deluxe");

        checkInTime.setText("CheckIn : " + checkInStr);
        checkOutTime.setText("CheckOut : " + checkOutStr

        );

        ConfirmBookingDialog.show();

        cancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmBookingDialog.dismiss();
            }
        });


        confirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("booking", "Booking Confirmed");
                confirmBooking.setClickable(false);
                cancelBooking.setClickable(false);
                Map<String, Object> booking = new HashMap<>();
                booking.put("roomId", roomId);
                booking.put("userId", "anuragGorkar");
                booking.put("restaurantId", documentId);
                booking.put("cin", getLongValforDate(cinDateStr));
                booking.put("cout", getLongValforDate(coutDateStr));

                Log.d("booking", String.valueOf(booking));

                db.collection("bookings").add(booking).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Table Booked Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Map<String, Object> bookingInfo = new HashMap<>();

                        bookingInfo.put("cin", cinDateStr);
                        bookingInfo.put("cout", coutDateStr);
                        bookingInfo.put("roomId", roomId);
                        bookingInfo.put("roomDocRef", restaurantCollRef.getPath());


                        db.collection("users").document("anuragGorkar").collection("roomBookings").
                                document(UUID.randomUUID().toString()).set(bookingInfo, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Booking Success", "Booking added to user Details!");
                                Log.d("Room Booking Success", "DocumentSnapshot successfully written!");
                                Intent intent = new Intent(SelectBookRoomActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                confirmBooking.setClickable(true);
                                cancelBooking.setClickable(true);
                                //WRITE CODE TO DELETE DOCUMENT FROM THE ROOM BOOKINGS******************************************
                            }
                        });
                    }
                });
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}