package com.example.pawz_2;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.PlatformVpnProfile;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>
        implements Filterable {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allEventRef = database.getReference("events");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    ChildEventListener eventRefListener;
    private List<Event> eventList;
    private List<Event> eventList_filtered;
    private List<String> keyList;
    private List<String> keyList_filtered;

    private OnListItemClickListener onListItemClickListener = null;   //Call back to the Activity

    public MyRecyclerAdapter(RecyclerView recyclerView)     //Constructor
    {
        eventList = new ArrayList<>();
        keyList = new ArrayList<>();
        eventRefListener = allEventRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Long interestedLong = (Long) snapshot.child("interested").getValue();
                Long attendingLong = (Long) snapshot.child("attending").getValue();
                Double latDouble = (Double) snapshot.child("lat").getValue();
                Log.d("SNAPSHOT", "lat: " + latDouble + "lon: " + snapshot.child("lon").getValue());
                Event eventItem = new Event(
                        snapshot.child("title").getValue().toString(),
                        snapshot.child("uid").getValue().toString(),
                        snapshot.child("detail").getValue().toString(),
                        snapshot.child("date").getValue().toString(),
                        snapshot.child("time").getValue().toString(),
                        snapshot.child("location").getValue().toString(),
                        snapshot.child("locationID").getValue().toString(),
                        (Double) snapshot.child("lat").getValue(),
                        (Double) snapshot.child("lon").getValue(),
                        interestedLong.intValue(),
                        attendingLong.intValue());

                eventList.add(0,eventItem);
                keyList.add(0,snapshot.getKey());

                MyRecyclerAdapter.this.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                Long interestedLong = (Long) snapshot.child("interested").getValue();
                Long attendingLong = (Long) snapshot.child("attending").getValue();
                for(int i=0; i < keyList.size(); i++){
                    if(keyList.get(i).equals(key)){
                        Event event = eventList.get(i);
                        event.setTitle(snapshot.child("title").getValue().toString());
                        event.setDetail(snapshot.child("detail").getValue().toString());
                        event.setDate(snapshot.child("date").getValue().toString());
                        event.setTime(snapshot.child("time").getValue().toString());
                        event.setLocation(snapshot.child("location").getValue().toString());
                        event.setInterested(interestedLong.intValue());
                        event.setAttending(attendingLong.intValue());
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                int index = keyList_filtered.indexOf(snapshot.getKey());
                eventList_filtered.remove(index);
                keyList_filtered.remove(index);
                MyRecyclerAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        eventList_filtered = eventList;
        keyList_filtered = keyList;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    eventList_filtered = eventList;
                    keyList_filtered = keyList;
                } else {
                    List<Event> filteredList = new ArrayList<>();
                    List<String> filteredKeyList = new ArrayList<>();
                    for (int i = 0; i < eventList.size(); i++) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (eventList.get(i).getTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(eventList.get(i));
                            filteredKeyList.add(keyList.get(i));
                        }
                    }
                    eventList_filtered = filteredList;
                    keyList_filtered = filteredKeyList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = eventList_filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                eventList_filtered = (ArrayList<Event>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleText;
        public TextView detailText;
        public TextView dateText;
        public TextView timeText;
        public TextView locationText;
        public TextView interestedText;
        public TextView attendingText;
        public ImageButton optionsBtn;
        public ImageView profileImg;

        public ViewHolder(View view) {
            super(view);
            titleText = (TextView) view.findViewById(R.id.title_text);
            dateText = (TextView) view.findViewById(R.id.date_text);
            timeText = (TextView) view.findViewById(R.id.time_text);
            locationText = (TextView) view.findViewById(R.id.location_text);
            detailText = (TextView) view.findViewById(R.id.detail_text);
            interestedText = (TextView) view.findViewById(R.id.interested_text);
            attendingText = (TextView) view.findViewById(R.id.attending_text);
            optionsBtn = (ImageButton) view.findViewById(R.id.option_button);
            profileImg = (ImageView) view.findViewById(R.id.profileImage);
        }
    }

    public Event getItem(int i) {
        return eventList_filtered.get(i);
    }

    public void setOnListItemClickListener(OnListItemClickListener listener) {
        onListItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout, parent, false);
        final ViewHolder view_holder = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onListItemClickListener != null) {
                    onListItemClickListener.onItemClick(v, view_holder.getAdapterPosition());
                }
            }
        });
        return view_holder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Event currentEvent = eventList_filtered.get(position);
        holder.titleText.setText(currentEvent.getTitle());
        holder.dateText.setText(currentEvent.getDate());
        holder.timeText.setText(currentEvent.getTime());
        holder.locationText.setText(currentEvent.getLocation());
        holder.detailText.setText(currentEvent.getDetail());
        holder.interestedText.setText(currentEvent.getInterested() + " Interested");
        holder.attendingText.setText(currentEvent.getAttending() + " Attending");
        DatabaseReference userRef = database.getReference("Users/"+currentEvent.getUid());
        userRef.child("profile_picture").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    if(task.getResult().exists()){
                        Picasso.get().load(task.getResult().getValue().toString()).transform(new CircleTransform()).into(holder.profileImg);
                    }
                }
            }
        });

        if (currentUser.getUid().equals(eventList_filtered.get(position).getUid())) {
            holder.optionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(v.getContext(), v);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.event_options, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.edit_event:
                                    Intent intent = new Intent(v.getContext(), EditEvent.class);
                                    intent.putExtra("Title",eventList_filtered.get(position).getTitle());
                                    intent.putExtra("Date", eventList_filtered.get(position).getDate());
                                    intent.putExtra("Time", eventList_filtered.get(position).getTime());
                                    intent.putExtra("Location", eventList_filtered.get(position).getLocation());
                                    intent.putExtra("Latitude", eventList_filtered.get(position).getLat());
                                    intent.putExtra("Longitude", eventList_filtered.get(position).getLon());
                                    intent.putExtra("Detail", eventList_filtered.get(position).getDetail());
                                    intent.putExtra("Key", keyList_filtered.get(position));
                                    v.getContext().startActivity(intent);

                                    return true;
                                case R.id.delete_event:
                                    AlertDialog.Builder alertbox_d;
                                    alertbox_d = new AlertDialog.Builder(v.getRootView().getContext());
                                    alertbox_d.setMessage("Are you sure to delete this Event?");
                                    alertbox_d.setTitle("Delete Event");
                                    alertbox_d.setCancelable(true);
                                    alertbox_d.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(v.getContext(), "Delete Movie: Yes", Toast.LENGTH_SHORT).show();
                                            allEventRef.child(keyList_filtered.get(position)).removeValue();
                                            return;
                                        }
                                    });
                                    alertbox_d.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(v.getContext(), "Delete Movie: No", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                    });
                                    alertbox_d.show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.show();
                }
            });
        } else {
            holder.optionsBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return eventList_filtered.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
