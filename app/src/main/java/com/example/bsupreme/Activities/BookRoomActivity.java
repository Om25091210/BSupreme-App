package com.example.bsupreme.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.parser.IntegerParser;
import com.example.bsupreme.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookRoomActivity extends AppCompatActivity {

    DatePickerDialog datePickerDialog;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("dd / MM / yyyy");

    Date checkInnDate ,checkOuttDate;


    // Intent Variables from Main Activity
    Intent intent;
    String documentId;

    //Variables
    String checkInDate, checkOutDate;

    //UI Variables
    TextView hotelName;
    EditText checkInDateEditText, checkOutDateEditText;
    Button nextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_room);

        //hoooks
        hotelName = findViewById(R.id.bookingHotelName);
        checkInDateEditText = findViewById(R.id.checkInText);
        checkOutDateEditText = findViewById(R.id.checkOutText);
        nextButton = findViewById(R.id.nextButton);

        //Get data from previous activity
        intent = getIntent();
        documentId = intent.getStringExtra("documentId");


        int currentHourIn24Format = calendar.get(Calendar.HOUR); // return the hour in 12 hrs format (ranging from 0-11))
        if (currentHourIn24Format >= 12) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);

            if(calendar.get(Calendar.DAY_OF_MONTH) < 10 && (calendar.get(Calendar.MONTH) + 1) < 10)
                checkInDateEditText.setText("0"+calendar.get(Calendar.DAY_OF_MONTH) + " / 0" + (calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.YEAR));
            else if(calendar.get(Calendar.DAY_OF_MONTH) > 10 && (calendar.get(Calendar.MONTH) + 1) < 10)
                checkInDateEditText.setText(calendar.get(Calendar.DAY_OF_MONTH) + " / 0" + (calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.YEAR));
            else
                checkInDateEditText.setText(calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.YEAR));

            calendar.add(Calendar.DAY_OF_YEAR, 1);

            if(calendar.get(Calendar.DAY_OF_MONTH) < 10 && (calendar.get(Calendar.MONTH) + 1) < 10)
                checkOutDateEditText.setText("0"+calendar.get(Calendar.DAY_OF_MONTH) + " / 0" + (calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.YEAR));
            else if(calendar.get(Calendar.DAY_OF_MONTH) > 10 && (calendar.get(Calendar.MONTH) + 1) < 10)
                checkOutDateEditText.setText(calendar.get(Calendar.DAY_OF_MONTH) + " / 0" + (calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.YEAR));
            else
                checkOutDateEditText.setText(calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.YEAR));


        } else {
            if(calendar.get(Calendar.DAY_OF_MONTH) < 10 && (calendar.get(Calendar.MONTH) + 1) < 10)
                checkInDateEditText.setText("0"+calendar.get(Calendar.DAY_OF_MONTH) + " / 0" + (calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.YEAR));
            else if(calendar.get(Calendar.DAY_OF_MONTH) > 10 && (calendar.get(Calendar.MONTH) + 1) < 10)
                checkInDateEditText.setText(calendar.get(Calendar.DAY_OF_MONTH) + " / 0" + (calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.YEAR));
            else
                checkInDateEditText.setText(calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.YEAR));

            calendar.add(Calendar.DAY_OF_YEAR, 1);

            if(calendar.get(Calendar.DAY_OF_MONTH) < 10 && (calendar.get(Calendar.MONTH) + 1) < 10)
                checkOutDateEditText.setText("0"+calendar.get(Calendar.DAY_OF_MONTH) + " / 0" + (calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.YEAR));
            else if(calendar.get(Calendar.DAY_OF_MONTH) > 10 && (calendar.get(Calendar.MONTH) + 1) < 10)
                checkOutDateEditText.setText(calendar.get(Calendar.DAY_OF_MONTH) + " / 0" + (calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.YEAR));
            else
                checkOutDateEditText.setText(calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " / " + calendar.get(Calendar.YEAR));
        }


        // CheckIn Date Selection
        checkInDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                try {
                    Date date = format.parse(String.valueOf(checkInDateEditText.getText()));
                    calendar.setTime(date);
                    int mYear = calendar.get(Calendar.YEAR); // current year
                    int mMonth = calendar.get(Calendar.MONTH); // current month
                    int mDay = calendar.get(Calendar.DAY_OF_MONTH);// current year
                    Log.d("Dialog Date Picker ", mYear + mMonth + mDay + "");

                    datePickerDialog = new DatePickerDialog(BookRoomActivity.this, R.style.date_picker_view_custom_, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                            if ((monthOfYear + 1) < 10 && (dayOfMonth)<10 )
                                checkInDateEditText.setText("0"+ dayOfMonth + " / 0" + (monthOfYear + 1) + " / " + year);
                            else if ((monthOfYear + 1) < 10 && (dayOfMonth)>=10 )
                                checkInDateEditText.setText(dayOfMonth + " / 0" + (monthOfYear + 1) + " / " + year);
                            else
                                checkInDateEditText.setText(dayOfMonth + " / " + (monthOfYear + 1) + " / " + year);
                        }
                    }, mYear, mMonth, mDay);
                    Calendar currentCalender = Calendar.getInstance();

                    int currentHourIn24Format = currentCalender.get(Calendar.HOUR); // return the hour in 12 hrs format (ranging from 0-11))

                    if (currentHourIn24Format >= 12) {
                        currentCalender.add(Calendar.DAY_OF_YEAR, 1);
                        datePickerDialog.getDatePicker().setMinDate(currentCalender.getTimeInMillis());
                        currentCalender.add(Calendar.MONTH, 4);
                        datePickerDialog.getDatePicker().setMaxDate(currentCalender.getTimeInMillis());

                    } else {
                        datePickerDialog.getDatePicker().setMinDate(currentCalender.getTimeInMillis());
                        currentCalender.add(Calendar.MONTH, 4);
                        datePickerDialog.getDatePicker().setMaxDate(currentCalender.getTimeInMillis());
                    }


                    datePickerDialog.show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


        // Checkout Date Selection
        checkOutDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                try {
                    Date date = format.parse(String.valueOf(checkOutDateEditText.getText()));
                    calendar.setTime(date);
                    int mYear = calendar.get(Calendar.YEAR); // current year
                    int mMonth = calendar.get(Calendar.MONTH); // current month
                    int mDay = calendar.get(Calendar.DAY_OF_MONTH);// current year
                    Log.d("Dialog Date Picker ", mYear + mMonth + mDay + "");

                    datePickerDialog = new DatePickerDialog(BookRoomActivity.this, R.style.date_picker_view_custom_, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                            if ((monthOfYear + 1) < 10 && (dayOfMonth)<10 )
                                checkOutDateEditText.setText("0"+ dayOfMonth + " / 0" + (monthOfYear + 1) + " / " + year);
                            else if ((monthOfYear + 1) < 10 && (dayOfMonth)>=10 )
                                checkOutDateEditText.setText(dayOfMonth + " / 0" + (monthOfYear + 1) + " / " + year);
                            else
                                checkOutDateEditText.setText(dayOfMonth + " / " + (monthOfYear + 1) + " / " + year);
                        }
                    }, mYear, mMonth, mDay);
                    Calendar currentCalender = Calendar.getInstance();

                    int currentHourIn24Format = currentCalender.get(Calendar.HOUR); // return the hour in 12 hrs format (ranging from 0-11))

                    if (currentHourIn24Format >= 12) {
                        currentCalender.add(Calendar.DAY_OF_YEAR, 1);
                        datePickerDialog.getDatePicker().setMinDate(currentCalender.getTimeInMillis());
                        currentCalender.add(Calendar.MONTH, 4);
                        datePickerDialog.getDatePicker().setMaxDate(currentCalender.getTimeInMillis());

                    } else {
                        datePickerDialog.getDatePicker().setMinDate(currentCalender.getTimeInMillis());
                        currentCalender.add(Calendar.MONTH, 4);
                        datePickerDialog.getDatePicker().setMaxDate(currentCalender.getTimeInMillis());
                    }


                    datePickerDialog.show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get dates
                try {
                    checkInnDate = format.parse(String.valueOf(checkInDateEditText.getText()));
                    checkOuttDate = format.parse(String.valueOf(checkOutDateEditText.getText()));
                    Log.d("CheckIn", String.valueOf(checkInnDate));
                    Log.d("CheckOut", String.valueOf(checkOuttDate));


                    if (checkInnDate.compareTo(checkOuttDate) == 0) {
                        Toast.makeText(getApplicationContext(), "Please Select CheckOut Date after CheckIn Date! CheckIn and CheckOut Date cannot be the same!", Toast.LENGTH_LONG).show();
                    } else if (checkInnDate.compareTo(checkOuttDate) >= 1) {
                        Toast.makeText(getApplicationContext(), "Please Select CheckOut Date after CheckIn Date!", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(BookRoomActivity.this, SelectBookRoomActivity.class);
                        intent.putExtra("documentId", documentId);
                        intent.putExtra("cin",  String.valueOf(checkInDateEditText.getText()));
                        intent.putExtra("cout", String.valueOf(checkOutDateEditText.getText()));
                        startActivity(intent);
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });


    }


}