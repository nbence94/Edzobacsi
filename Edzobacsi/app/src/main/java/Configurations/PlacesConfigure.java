package Configurations;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import Adapterek.PlaceAdapter;
import InsertActivities.InsertPlace;

import HelperClasses.DatabaseHelper;
import HelperClasses.ConfigureHelper;
import com.example.Main.R;

import java.util.ArrayList;

public class PlacesConfigure extends AppCompatActivity {

    Toolbar toolbar;
    PlaceAdapter PA;

    DatabaseHelper dh;
    ConfigureHelper ch;
    ArrayList<String> place_id_list, place_name_list, place_deletable_list, place_in_plans;
    RecyclerView recView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_configure);

        toolbar = findViewById(R.id.tBarX_places);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dh = new DatabaseHelper(PlacesConfigure.this);
        ch = new ConfigureHelper(PlacesConfigure.this);

        place_id_list = new ArrayList<>();
        place_name_list = new ArrayList<>();
        place_deletable_list = new ArrayList<>();
        place_in_plans = new ArrayList<>();

        recView = findViewById(R.id.rViewX_place);

        ch.getIDsandNames(dh, dh.PLACES, "Nincsenek helyszíneid", place_id_list, place_name_list);
        checkPlacesForDeletableState();
        ch.getConnectsToList(dh.PLANS,"placeID", place_id_list, place_in_plans);

        PA = new PlaceAdapter(PlacesConfigure.this, this, place_id_list, place_name_list, place_deletable_list, place_in_plans);
        recView.setAdapter(PA);
        recView.setLayoutManager(new LinearLayoutManager(PlacesConfigure.this));
        PA.notifyDataSetChanged();
    }

    void checkPlacesForDeletableState(){
        for(int i = 0; i < place_id_list.size(); i++) {
            if(ch.checkDeletable(dh,dh.PLANS,"placeID", place_id_list.get(i))) place_deletable_list.add("1");
            else place_deletable_list.add("0");
        }
    }

    public void openInsertPlace(android.view.View v) {
        Intent open = new Intent(PlacesConfigure.this, InsertPlace.class);
        startActivityForResult(open, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) item.getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                PA.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}