package com.example.androidproject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.androidproject.dbroom.NoteViewModel;
import com.example.androidproject.dbroom.TripModel;
import com.example.androidproject.dbroom.TripViewModel;
import com.example.androidproject.navigation_drawer_activity.model.TripData;
import com.example.androidproject.navigation_drawer_activity.support.UploadWorker;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.compat.ui.PlaceAutocompleteFragment;
import com.google.android.libraries.places.compat.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.SQLData;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class AddTripActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    Button btnAdd, btnDate, btnTime;
    EditText etTripName;
    String sTripName, sStartPoint, sEndPoint, sDate, sTime, sData, sStatus;
    TextView tvDate, tvTime;
    private TripModel tripModel;
    private TripModel tripModelEdit;
    private TripViewModel tripViewModel;
    private Calendar calendar;

    PlaceAutocompleteFragment autocompleteFragmentSource;
    PlaceAutocompleteFragment autocompleteFragmentDest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        etTripName = findViewById(R.id.etTripName);
        btnAdd = findViewById(R.id.btnAddTrip);
        tripModel = new TripModel();
         calendar = Calendar.getInstance();

        tripModelEdit = (TripModel) getIntent().getSerializableExtra("editTrip");
        if(tripModelEdit !=null){
            btnAdd.setText("Edit");

        }

// part 1 room
        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);

        //autocomplete
        autocompleteFragmentSource = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.source);
        autocompleteFragmentDest = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.dest);

        //spinner
        Spinner spinnerRepeat = (Spinner) findViewById(R.id.spinnerRepeat);
        Spinner spinnerStatus = findViewById(R.id.spinnerStatus);

        tripModel = new TripModel();
        tripModel.setTripRepeatingType("No_Repeat");
        tripModel.setType("One Way");


        setupAutoCompleteFragmentSource();
        setupAutoCompleteFragmentDest();

        spinnerRepeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sData = parent.getItemAtPosition(position).toString();
                tripModel.setTripRepeatingType(sData);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sStatus = parent.getItemAtPosition(position).toString();
               tripModel.setType(sStatus);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sTripName = etTripName.getText().toString();
                sDate = tvDate.getText().toString();
                sTime = tvTime.getText().toString();

                if ( sTripName.equals("")) {
                    Toast.makeText(AddTripActivity.this, "Please, Enter Trip Name", Toast.LENGTH_SHORT).show();
                } else if (sStartPoint == null) {
                    Toast.makeText(AddTripActivity.this, "Please, Enter Start Point", Toast.LENGTH_SHORT).show();
                } else if (sEndPoint == null) {
                    Toast.makeText(AddTripActivity.this, "Please, Enter End Point", Toast.LENGTH_SHORT).show();
                } else if (sDate.equals("Set Date")) {
                    Toast.makeText(AddTripActivity.this, "Please, Choose a Date", Toast.LENGTH_SHORT).show();
                } else if (sTime.equals("Set Time")) {
                    Toast.makeText(AddTripActivity.this, "Please, Choose The Time", Toast.LENGTH_SHORT).show();
                }  else {
                    Intent returnIntent = new Intent();
                    TripData tripData = new TripData(sTripName, sStartPoint, sEndPoint, sDate, sTime, sStatus, sData);
                    returnIntent.putExtra("result", tripData);
                    setResult(Activity.RESULT_OK, returnIntent);

                    //code insert data in room
                    tripModel.setName(sTripName);
                    tripModel.setStartPoint(sStartPoint);
                    tripModel.setEndPoint(sEndPoint);
                    tripModel.setDate(sDate);
                    tripModel.setTime(sTime);
                    tripModel.setTimestamp(calendar.getTimeInMillis());
                   tripModel.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                   tripModel.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                  // String t= FirebaseAuth.getInstance().getCurrentUser().getEmail();
                   // tripViewModel.insert(tripModel);

                    // edit
                    if(tripModelEdit!=null){
                        tripViewModel.update(tripModelEdit);

                    }else{
                        tripViewModel.insert(tripModel,null);

                    }

                    startWorkManager();
                    finish();
                }
            }
        });
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DateFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimeFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
    }

    private void setupAutoCompleteFragmentSource() {

        autocompleteFragmentSource.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(com.google.android.libraries.places.compat.Place place) {
                autocompleteFragmentSource.setText(place.getName());
                sStartPoint = place.getName().toString();
                tripModel.setStartPointLatitude(place.getLatLng().latitude);
                tripModel.setStartPointLongitude(place.getLatLng().longitude);

            }

            @Override
            public void onError(Status status) {
                Log.e("Error", status.getStatusMessage());
            }
        });
    }
    private void setupAutoCompleteFragmentDest() {

        autocompleteFragmentDest.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(com.google.android.libraries.places.compat.Place place) {
                autocompleteFragmentDest.setText(place.getName());
                sEndPoint = place.getName().toString();
                tripModel.setEndPointLatitude(place.getLatLng().latitude);
                tripModel.setEndPointLongitude(place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                Log.e("Error", status.getStatusMessage());
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        tvDate.setText(currentDateString);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = hourOfDay + ":" + minute;
        tvTime.setText(time);
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        tripModel.setTime(time);


    }

    private void startWorkManager(){
        WorkRequest tripRequest = new OneTimeWorkRequest.Builder(UploadWorker.class)
                .setInitialDelay(60, TimeUnit.SECONDS)
                .addTag("mnem")
                .build();
        WorkManager.getInstance(this).enqueue(tripRequest);
    }

}