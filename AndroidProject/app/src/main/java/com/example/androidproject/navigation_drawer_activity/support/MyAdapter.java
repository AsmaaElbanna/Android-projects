package com.example.androidproject.navigation_drawer_activity.support;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.navigation_drawer_activity.model.TripData;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private static final String TAG = "TAG";
    private final Context context;
    private ArrayList<TripData> trips;
    private DataTransfer delegate ;

    public MyAdapter(Context _context, ArrayList<TripData> _trips, DataTransfer _delegate){
        context = _context;
        trips = _trips;
        delegate = _delegate;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.recycler_trip_row,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameLbl.setText(trips.get(position).tripName);
        holder.dateLbl.setText(trips.get(position).date);
        holder.timeLbl.setText(trips.get(position).time);
        holder.startPoint.setText(trips.get(position).tripStartPoint);
        holder.endPoint.setText(trips.get(position).tripEndPoint);

        holder.startBtn.setOnClickListener((event)->{
            Log.i(TAG, "onBindViewHolder: START PRESSED : "+position );
            delegate.startMap(trips.get(position).tripEndPoint);
        });
        holder.notesBtn.setOnClickListener((event)->{
            Log.i(TAG, "onBindViewHolder: NOTES PRESSED : "+position);
            //notes Code.
        });
        holder.menuBtn.setOnClickListener((event)->{
            showPopup(holder.menuBtn,position);
        });

    }

    public void showPopup(View v,int position) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.setOnMenuItemClickListener((item)->{
            switch (item.getItemId()){
                case R.id.action_edit:
                    Log.i(TAG, "showPopup: EDITING!!");
                    // editing action
                    return true;

                case R.id.action_remove:
                    Log.i(TAG, "showPopup: DELETING !!");
                    //dataSource.removeTrip(position);
                    trips.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,trips.size());
                    return true;

                default:
                    return false;
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.navigation, popup.getMenu());
        popup.show();
    }


//    private void removeItem(int position,ViewHolder holder) {
//        int newPosition = holder.getAdapterPosition();
//        trips.remove(trips.get(position));
//        notifyItemRemoved(newPosition);
//        notifyItemRangeChanged(newPosition, trips.size());
//    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nameLbl;
        public TextView dateLbl;
        public CardView cardView;
        public TextView timeLbl;
        public TextView startPoint;
        public TextView endPoint;
        public Button startBtn;
        public ImageButton notesBtn;
        public ImageButton menuBtn;
        public ConstraintLayout constraintLayout;
        public View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            cardView = itemView.findViewById(R.id.trip_row_cardView);
            nameLbl = itemView.findViewById(R.id.trip_row_nameLbl);
            dateLbl = itemView.findViewById(R.id.trip_row_dateLbl);
            timeLbl = itemView.findViewById(R.id.trip_row_timeLbl);
            startPoint = itemView.findViewById(R.id.trip_row_startLbl);
            endPoint = itemView.findViewById(R.id.trip_row_endLbl);
            startBtn = itemView.findViewById(R.id.btnStart);
            notesBtn = itemView.findViewById(R.id.trip_row_notesBtn);
            menuBtn = itemView.findViewById(R.id.trip_row_optionsBtn);
            constraintLayout = itemView.findViewById(R.id.trip_row_layout);
        }
    }
}
