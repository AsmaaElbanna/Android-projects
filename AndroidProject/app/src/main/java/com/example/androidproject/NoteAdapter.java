package com.example.androidproject;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.dbroom.NoteModel;
import com.example.androidproject.dbroom.NoteViewModel;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyHolder> {

    List<NoteModel> note;
    Context context;
    NoteViewModel noteViewModel;


   public void changeData(List<NoteModel> data){
        note = data;
        notifyDataSetChanged();
    }

    public NoteAdapter(Context context) {
        this.context = context;
        if(!(context instanceof Service)){
            noteViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(NoteViewModel.class);
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_note,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.noteTxt.setText(note.get(position).getNote());
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteViewModel.delete(note.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
      return note.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView noteTxt;
        Button deleteBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            noteTxt = itemView.findViewById(R.id.note_txt);
            deleteBtn = itemView.findViewById(R.id.delete_note);

        }
    }
}
