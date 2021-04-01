package com.example.androidproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.dbroom.NoteModel;
import com.example.androidproject.dbroom.NoteRepository;
import com.example.androidproject.dbroom.NoteViewModel;
import com.example.androidproject.navigation_drawer_activity.ui.upcoming.UpcomingFragment;

import java.util.ArrayList;
import java.util.List;

import static com.example.androidproject.navigation_drawer_activity.ui.upcoming.UpcomingFragment.POSITION;

public class Addnote extends AppCompatActivity {
    private static final String TAG = "tag";
    RecyclerView recyclerView;
    List<NoteModel> listOfNotes;
    EditText addNoteTxt;
    Button saveBtn,doneBtn;
    private int position;
    NoteAdapter myNoteAdapter;
    NoteViewModel noteViewModel;
    NoteRepository noteRepository;
    NoteModel noteModel;
    private int tripId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnote);
        Intent intent = getIntent();
        tripId = intent.getIntExtra("tripId",1);
        listOfNotes =new ArrayList<>();
        addNoteTxt =findViewById(R.id.note_edit_txt);
        recyclerView = findViewById(R.id.note_recyclerview);
        myNoteAdapter =new NoteAdapter(getBaseContext(),listOfNotes);
        recyclerView.setAdapter(myNoteAdapter);
        noteModel = new NoteModel();

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
//          noteRepository = new NoteRepository(this);

// room

            noteViewModel.getAllNotesById(tripId).observe(this,noteModels -> {
            listOfNotes = noteModels;
            myNoteAdapter =new NoteAdapter(getBaseContext(),listOfNotes);
            myNoteAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(myNoteAdapter);
        });

        saveBtn = findViewById(R.id.save_note_btn);
        doneBtn = findViewById(R.id.done_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteString = addNoteTxt.getText().toString().trim();
                noteModel.setNote(noteString);
                Log.i(TAG, "onClick: "+noteModel);
               // listOfNotes.add(noteModel);
              // listOfNotes.add(noteString);
             // myNoteAdapter.notifyDataSetChanged();
              NoteModel noteModel=new NoteModel();
              noteModel.setNote(noteString);
              noteModel.setTripId(tripId);
              noteViewModel.insert(noteModel);

            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent returnIntent = new Intent();
//                Bundle bundle =new Bundle();
//                bundle.putStringArrayList("NOTES",listOfNotes);
//                bundle.putInt(UpcomingFragment.POSITION,position);
//                returnIntent.putExtra(UpcomingFragment.BUNDLE_NAME,bundle);
//                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
        Intent caller = getIntent();
        position = caller.getIntExtra(POSITION,-1);

//        listOfNotes.addAll(caller.getStringArrayListExtra("NOTES"));
        Log.i(TAG, "onCreate: " + caller.getStringArrayListExtra("NOTES"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }
}