package com.example.androidproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyHolder> {

    List<String> note;
    Context context;

    public NoteAdapter(Context context, List<String> note) {
        this.note = note;
        this.context = context;
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
        holder.noteTxt.setText(note.get(position));

    }

    @Override
    public int getItemCount() {
      return note.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView noteTxt;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            noteTxt = itemView.findViewById(R.id.note_txt);
        }
    }
}
