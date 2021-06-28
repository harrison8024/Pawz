package com.example.pawz_2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recycler_view;
    private MyRecyclerAdapter myRecyclerAdapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpToolbar();
        recycler_view = (RecyclerView) findViewById(R.id.mainRecyclerView);
        myRecyclerAdapter = new MyRecyclerAdapter(recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        recycler_view.setLayoutManager(layoutManager);
        myRecyclerAdapter.setOnListItemClickListener(new OnListItemClickListener(){
            @Override
            public void onItemClick(View v, int position) {
                Log.d("ON ITEM CLICK", "movie: " + myRecyclerAdapter.getItem(position) + "Name: " + myRecyclerAdapter.getItem(position).getTitle());
                Event eventMap = myRecyclerAdapter.getItem(position);
                Intent intent = new Intent(getBaseContext(), EventActivity.class);
                intent.putExtra("Title", eventMap.title);
                intent.putExtra("Date", eventMap.date);
                intent.putExtra("Time", eventMap.time);
                intent.putExtra("Location", eventMap.location);
                intent.putExtra("Latitude", eventMap.lat);
                intent.putExtra("Longitude", eventMap.lon);
                intent.putExtra("Detail", eventMap.detail);
                intent.putExtra("Interested", eventMap.interested);
                intent.putExtra("Attending", eventMap.attending);
                intent.putExtra("Uid", eventMap.uid);
                startActivity(intent);
            }
        });
        recycler_view.setAdapter(myRecyclerAdapter);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.profile:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            case R.id.message:
                // go to message activity
                return true;
            case R.id.sign_out:
                mAuth.signOut();
                Intent intent = new Intent(this, SignupLogin.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast toast = Toast.makeText(getApplication(), "Query Text=" + query, Toast.LENGTH_SHORT);
                toast.show();
                myRecyclerAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myRecyclerAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void addNewEvent(View view) {
        Intent intent = new Intent(this, AddEvent.class);
        startActivity(intent);
    }
}