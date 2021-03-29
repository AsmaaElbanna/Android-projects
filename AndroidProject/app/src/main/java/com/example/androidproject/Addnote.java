package com.example.androidproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.navigation_drawer_activity.ui.upcoming.UpcomingFragment;

import java.util.ArrayList;

public class Addnote extends AppCompatActivity {
    private static final String TAG = "tag";
    RecyclerView recyclerView;
    ArrayList<String> listOfNotes;
    EditText addNoteTxt;
    Button saveBtn,doneBtn;
    private int position;

    NoteAdapter myNoteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnote);
        listOfNotes =new ArrayList<>();
        addNoteTxt =findViewById(R.id.note_edit_txt);
        recyclerView = findViewById(R.id.note_recyclerview);
        myNoteAdapter =new NoteAdapter(getBaseContext(),listOfNotes);
        recyclerView.setAdapter(myNoteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        saveBtn = findViewById(R.id.save_note_btn);
        doneBtn = findViewById(R.id.done_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteString = addNoteTxt.getText().toString();
                listOfNotes.add(noteString);
                myNoteAdapter.notifyDataSetChanged();

            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                Bundle bundle =new Bundle();
                bundle.putStringArrayList("NOTES",listOfNotes);
                bundle.putInt(UpcomingFragment.POSITION,position);
                returnIntent.putExtra(UpcomingFragment.BUNDLE_NAME,bundle);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

        Intent caller = getIntent();
        position = caller.getIntExtra(UpcomingFragment.POSITION,-1);

//        listOfNotes.addAll(caller.getStringArrayListExtra("NOTES"));
        Log.i(TAG, "onCreate: " + caller.getStringArrayListExtra("NOTES"));
        myNoteAdapter.notifyDataSetChanged();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }
}