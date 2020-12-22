package com.example.bsupreme.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bsupreme.Models.DateMonthModel;
import com.example.bsupreme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class SelectDateTableActivity extends AppCompatActivity {

    //Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    CollectionReference tableBookingRef;


    // Intent Variables from Main Activity
    Intent intent;
    String restaurantDocumentId, tableDocumentId;
    DocumentReference docRef;
    String selectedDateForBooking;

    //Variables
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
    String seatNumber;
    boolean isAc, isBar, isGrill;

    //UI elements
    CalendarView datecalendar;
    CardView cardOne, cardTwo, cardThree, cardFour, cardFive;
    TextView tableSeats;
    LinearLayout acIndicator, wineIndicator, grillIndicator;
    Dialog ConfirmBookingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date_table);


        //Get data from previous activity
        intent = getIntent();
        tableDocumentId = intent.getStringExtra("tableDocumentId");
        restaurantDocumentId = intent.getStringExtra("restaurantDocumentId");

        // Hooks
        tableSeats = findViewById(R.id.tableSeats);
        acIndicator = findViewById(R.id.acIndicator);
        grillIndicator = findViewById(R.id.grillIndicator);
        wineIndicator = findViewById(R.id.barIndicator);

        datecalendar = findViewById(R.id.selectDateCalanderView);
        cardOne = findViewById(R.id.table_booking_card_1);
        cardTwo = findViewById(R.id.table_booking_card_2);
        cardThree = findViewById(R.id.table_booking_card_3);
        cardFour = findViewById(R.id.table_booking_card_4);
        cardFive = findViewById(R.id.table_booking_card_5);

        ConfirmBookingDialog = new Dialog(this);


        //Set Date
        datecalendar.setDate(new Date().getTime());
        //Set minimum calendar date
        datecalendar.setMinDate(new Date().getTime());
        calendar.setTime(new Date());
        //Set maximum calendar Date
        calendar.add(Calendar.MONTH, 2);
        datecalendar.setMaxDate(calendar.getTime().getTime());

        tableBookingRef = db.collection("restaurants").document(restaurantDocumentId).collection("tables").document(tableDocumentId).collection("bookings");
        DocumentReference docRef = db.collection("restaurants").document(restaurantDocumentId).collection("tables").document(tableDocumentId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("Data", "DocumentSnapshot data: " + document.getData());
                        seatNumber = String.valueOf(document.get("seating"));
                        isAc = (boolean) document.get("is_ac");
                        isBar = (boolean) document.get("is_counter");
                        isGrill = (boolean) document.get("is_grill");

                        tableSeats.setText(seatNumber);

                        if (!isAc)
                            acIndicator.setVisibility(LinearLayout.GONE);
                        else
                            acIndicator.setVisibility(LinearLayout.VISIBLE);

                        if (!isBar)
                            wineIndicator.setVisibility(LinearLayout.GONE);
                        else
                            wineIndicator.setVisibility(LinearLayout.VISIBLE);

                        if (!isGrill)
                            grillIndicator.setVisibility(LinearLayout.GONE);
                        else
                            grillIndicator.setVisibility(LinearLayout.VISIBLE);

                        //Display table info
                    } else {
                        Log.d("No Data", "No such document");
                    }
                } else {
                    Log.d("No Data", "get failed with ", task.getException());
                }
            }
        });

        // Get Booked Date List
        tableBookingRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> bookingDateList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        bookingDateList.add(document.getId());
                    }
                    Log.d("List of Dates", bookingDateList.toString());
                    Log.d("calendar Date", convertDate(dateToString(datecalendar.getDate()), false));

                    selectedDateForBooking = convertDate(dateToString(datecalendar.getDate()), false);
                    displayAvailableTablesForDate(bookingDateList, convertDate(dateToString(datecalendar.getDate()), false));


                } else {
                    Log.d("Error", "Error getting documents: ", task.getException());
                }
            }


        });

        datecalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Log.d("Date Changed", "Date selected has been changed in calendar View..." + dayOfMonth + month + year);

                tableBookingRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> bookingDateList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                bookingDateList.add(document.getId());
                            }
                            Log.d("List of Dates", bookingDateList.toString());
                            Log.d("calendar Date", convertDate(dateToString(datecalendar.getDate()), true));

                            selectedDateForBooking = convertDate("" + dayOfMonth + "_" + month + "_" + year, true);
                            displayAvailableTablesForDate(bookingDateList, selectedDateForBooking);


                        } else {
                            Log.d("Error", "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });
    }


    private void displayAvailableTablesForDate(List<String> bookingDateList, String date) {
        Log.d("Date Passed to function", date);
        if (bookingDateList.contains(date)) {
            tableBookingRef.document(date).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot dateBookingsdoc = task.getResult();
                        if (dateBookingsdoc.exists()) {
                            Log.d("Date Data for bookings", "DocumentSnapshot data: " + dateBookingsdoc.getData());
                            Map<String, Object> bookedTables = dateBookingsdoc.getData();
                            int currentHourIn24Format = calendar.get(Calendar.HOUR_OF_DAY); // return the hour in 12 hrs format (ranging from 0-11))

                            Log.d("Current Hour", String.valueOf(currentHourIn24Format));
                            Log.d("Is Today", convertDate(dateToString(datecalendar.getDate()), false));


                            if (bookedTables.containsKey("one") || (currentHourIn24Format >= 19 && (date.equals(convertDate(dateToString(datecalendar.getDate()), false))) )) {
                                cardOne.setVisibility(View.GONE);
                            } else {
                                cardOne.setVisibility(View.VISIBLE);
                            }

                            if (bookedTables.containsKey("two") || ((currentHourIn24Format >= 20) && (date.equals(convertDate(dateToString(datecalendar.getDate()), false))))) {
                                cardTwo.setVisibility(View.GONE);
                            } else {
                                cardTwo.setVisibility(View.VISIBLE);
                            }

                            if (bookedTables.containsKey("three") || ((currentHourIn24Format >= 21) && (date.equals(convertDate(dateToString(datecalendar.getDate()), false))))) {
                                cardThree.setVisibility(View.GONE);
                            } else {
                                cardThree.setVisibility(View.VISIBLE);
                            }

                            if (bookedTables.containsKey("four") || ((currentHourIn24Format >= 22) && (date.equals(convertDate(dateToString(datecalendar.getDate()), false))))) {
                                cardFour.setVisibility(View.GONE);
                            } else {
                                cardFour.setVisibility(View.VISIBLE);
                            }

                            if (bookedTables.containsKey("one") || ((currentHourIn24Format >= 23) && (date.equals(convertDate(dateToString(datecalendar.getDate()), false))))) {
                                cardFive.setVisibility(View.GONE);
                            } else {
                                cardFive.setVisibility(View.VISIBLE);
                            }
                            //Display table info
                        } else {
                            Log.d("No Data", "No such document");
                        }
                    } else {
                        Log.d("No Data", "get failed with ", task.getException());
                    }

                }
            });


        } else {
            int currentHourIn24Format = calendar.get(Calendar.HOUR_OF_DAY); // return the hour in 12 hrs format (ranging from 0-11))

            Log.d("Current Hour", String.valueOf(currentHourIn24Format));
            Log.d("Is Today", convertDate(dateToString(datecalendar.getDate()), false));

            if (((currentHourIn24Format >= 19 && (date.equals(convertDate(dateToString(datecalendar.getDate()), false))))) ) {
                cardOne.setVisibility(View.GONE);
            } else {
                cardOne.setVisibility(View.VISIBLE);
            }

            if ( ((currentHourIn24Format >= 20) && (date.equals(convertDate(dateToString(datecalendar.getDate()), false))))) {
                cardTwo.setVisibility(View.GONE);
            } else {
                cardTwo.setVisibility(View.VISIBLE);
            }

            if (((currentHourIn24Format >= 21) && (date.equals(convertDate(dateToString(datecalendar.getDate()), false))))) {
                cardThree.setVisibility(View.GONE);
            } else {
                cardThree.setVisibility(View.VISIBLE);
            }

            if (((currentHourIn24Format >= 22) && (date.equals(convertDate(dateToString(datecalendar.getDate()), false))))) {
                cardFour.setVisibility(View.GONE);
            } else {
                cardFour.setVisibility(View.VISIBLE);
            }

            if (((currentHourIn24Format >= 23) && (date.equals(convertDate(dateToString(datecalendar.getDate()), false))))) {
                cardFive.setVisibility(View.GONE);
            } else {
                cardFive.setVisibility(View.VISIBLE);
            }
        }

    }

    String dateToString(long date) {
        String dateString;
        dateString = simpleDateFormat.format(date);

        return dateString;
    }

    String convertDate(String date, boolean notDeduct) {
        String convertedDate, monthStr, dayStr;
        String[] splitDate = date.split("_");
        Long month = Long.valueOf(splitDate[1]);
        Long day = Long.valueOf(splitDate[0]);

        if(! notDeduct)
            month = Long.valueOf(splitDate[1]) - 1;

        if (month < 10)
            monthStr = "0" + String.valueOf(month);
        else
            monthStr = String.valueOf(month);

        if (day < 10)
            dayStr = "0" + String.valueOf(day);
        else
            dayStr = String.valueOf(day);

        convertedDate = dayStr + "_" + monthStr + "_" + splitDate[2];
        return convertedDate;
    }

    public void ShowPopUp(View view, String dateDoc, String date, String time, String tableNo) {
        LinearLayout acIndicator, wineIndicator, grillIndicator;
        TextView bookingDate, bookingTime, hotelName, seats;
        Button confirmBooking, cancelBooking;

        ConfirmBookingDialog.setContentView(R.layout.des_booking_confirmation_layout);

        seats = ConfirmBookingDialog.findViewById(R.id.confirmtableSeats);
        acIndicator = ConfirmBookingDialog.findViewById(R.id.confirmAcIndicator);
        grillIndicator = ConfirmBookingDialog.findViewById(R.id.confirmGrillIndicator);
        wineIndicator = ConfirmBookingDialog.findViewById(R.id.confirmBarIndicator);

        bookingDate = ConfirmBookingDialog.findViewById(R.id.bookingDate);
        bookingTime = ConfirmBookingDialog.findViewById(R.id.bookingTime);

        cancelBooking = ConfirmBookingDialog.findViewById(R.id.cancelBookingButton);
        confirmBooking = ConfirmBookingDialog.findViewById(R.id.confirmBookingButton);

        confirmBooking.setClickable(true);
        cancelBooking.setClickable(true);

        seats.setText(seatNumber);

        if (!isAc)
            acIndicator.setVisibility(LinearLayout.GONE);
        else
            acIndicator.setVisibility(LinearLayout.VISIBLE);

        if (!isBar)
            wineIndicator.setVisibility(LinearLayout.GONE);
        else
            wineIndicator.setVisibility(LinearLayout.VISIBLE);

        if (!isGrill)
            grillIndicator.setVisibility(LinearLayout.GONE);
        else
            grillIndicator.setVisibility(LinearLayout.VISIBLE);

        bookingDate.setText("Date : "+dateDoc);
        bookingTime.setText("Time : " + time);

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
                confirmBooking.setClickable(false);
                cancelBooking.setClickable(false);
                Map<String, Object> booking = new HashMap<>();
                booking.put(tableNo, "anuragGorkar");

                tableBookingRef.document(date).set(booking, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getApplicationContext(),"Table Booked Successfully",Toast.LENGTH_SHORT).show();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Map<String, Object> bookingInfo = new HashMap<>();

                        bookingInfo.put("date", dateDoc);
                        bookingInfo.put("time", tableNo);
                        bookingInfo.put("tableDocRef",tableBookingRef.document(date) );


                        db.collection("users").document("anuragGorkar").collection("bookings").document(UUID.randomUUID().toString())
                                .set(bookingInfo,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Booking Success", "Booking added to user Details!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                confirmBooking.setClickable(true);
                                cancelBooking.setClickable(true);
                                //WRITE CODE TO DELETE DOCUMENT FROM THE RESTAURANT BOOKINGS******************************************
                            }
                        });
                        Log.d("Booking Success", "DocumentSnapshot successfully written!");
                        Intent intent = new Intent(SelectDateTableActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Booking Failed", "Error writing document", e);
                        Toast.makeText(getApplicationContext(),"Error Could not book table.",Toast.LENGTH_SHORT).show();
                        ConfirmBookingDialog.dismiss();
                        confirmBooking.setClickable(true);
                        cancelBooking.setClickable(true);
                    }
                });
            }
        });

    }

    public Long getMonthInt(String date) {
        String[] splitDate = date.split("_");
        return Long.valueOf(splitDate[1]);
    }


    public void cardOneClick(View view) {
        String[] splitDate = selectedDateForBooking.split("_");
        String month = DateMonthModel.getMonth(Long.valueOf(splitDate[1]));
        ShowPopUp(view, splitDate[0] + " " + month + " " + splitDate[2], selectedDateForBooking, "07:00 pm - 08:00 pm", "one");
    }

    public void cardTwoClick(View view) {
        String[] splitDate = selectedDateForBooking.split("_");
        String month = DateMonthModel.getMonth(Long.valueOf(splitDate[1]));
        ShowPopUp(view, splitDate[0] + " " + month + " " + splitDate[2], selectedDateForBooking, "08:00 pm - 09:00 pm", "two");
    }

    public void cardThreeClick(View view) {
        String[] splitDate = selectedDateForBooking.split("_");
        String month = DateMonthModel.getMonth(Long.valueOf(splitDate[1]));
        ShowPopUp(view, splitDate[0] + " " + month + " " + splitDate[2], selectedDateForBooking, "09:00 pm - 10:00 pm", "three");
    }

    public void cardFourClick(View view) {
        String[] splitDate = selectedDateForBooking.split("_");
        String month = DateMonthModel.getMonth(Long.valueOf(splitDate[1]));
        ShowPopUp(view, splitDate[0] + " " + month + " " + splitDate[2], selectedDateForBooking, "10:00 pm - 11:00 pm", "four");
    }

    public void carFiveClick(View view) {
        String[] splitDate = selectedDateForBooking.split("_");
        String month = DateMonthModel.getMonth(Long.valueOf(splitDate[1]));
        ShowPopUp(view, splitDate[0] + " " + month + " " + splitDate[2], selectedDateForBooking, "11:00 pm - 12:00 pm", "five");
    }

}