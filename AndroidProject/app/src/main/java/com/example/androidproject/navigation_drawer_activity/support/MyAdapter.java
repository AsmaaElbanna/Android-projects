package com.example.androidproject.navigation_drawer_activity.support;

import android.content.Context;
import android.content.Intent;
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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.AddTripActivity;
import com.example.androidproject.R;
import com.example.androidproject.dbroom.TripModel;
import com.example.androidproject.dbroom.TripViewModel;
import com.example.androidproject.navigation_drawer_activity.model.TripData;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private static final String TAG = "TAG";
    private final Context context;
    private List<TripModel> trips;
    private DataTransfer delegate;
    private Status listStatus;
    TripViewModel tripViewModel;
    OnRecyclerViewListener onRecyclerViewListener;

    public enum Status {
        UPCOMING,
        HISTORY
    }

    public MyAdapter(Context _context, List<TripModel> _trips,
                     DataTransfer _delegate, Status _listStatus, OnRecyclerViewListener onRecyclerViewListener) {
        context = _context;
        trips = _trips;
        delegate = _delegate;
        listStatus = _listStatus;
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.recycler_trip_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameLbl.setText(trips.get(position).getName());
        holder.dateLbl.setText(trips.get(position).getDate());
        holder.timeLbl.setText(trips.get(position).getTime());
        holder.startPoint.setText(trips.get(position).getStartPoint());
        holder.endPoint.setText(trips.get(position).getEndPoint());

        holder.startBtn.setOnClickListener((event) -> {
            Log.i(TAG, "onBindViewHolder: START PRESSED : " + position);
            delegate.startMap(trips.get(position).getEndPoint(),trips.get(position).getId());
        });
        holder.notesBtn.setOnClickListener((event) -> {
            Log.i(TAG, "onBindViewHolder: NOTES PRESSED : " + position);
            delegate.saveNotes(position);
        });
        holder.menuBtn.setOnClickListener((event) -> {
            showPopup(holder.menuBtn, position);
        });
    }

    public void showPopup(View v, int position) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.setOnMenuItemClickListener((item) -> {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    Log.i(TAG, "showPopup: EDITING!!");
                    // editing action
                    Intent intent =new Intent(context, AddTripActivity.class);
                    intent.putExtra("editTrip",trips.get(position));
                    Log.i(TAG, "showPopup: trip Model.............. "+ trips.get(position).getName());
                    Log.i(TAG, "showPopup: trip Model.............. "+ trips.get(position).getStartPointLatitude());
                    Log.i(TAG, "showPopup: trip Model.............. "+ trips.get(position).getId());

                    context.startActivity(intent);

                    return true;

                case R.id.action_remove:
                    Log.i(TAG, "showPopup: DELETING !!");
                    //dataSource.removeTrip(position);
                    onRecyclerViewListener.onDeleteItem(position);
                    trips.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, trips.size());
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
    public void setTrips(List<TripModel> trips) {
        this.trips = trips;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (trips != null)
            return trips.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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

            switch (listStatus) {
                case HISTORY:
                    startBtn.setVisibility(View.GONE);
                    notesBtn.setVisibility(View.GONE);
                    break;

                default:
                    break;
            }
        }
    }
}
