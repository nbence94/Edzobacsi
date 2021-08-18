package SelectionActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.example.Main.R;

import java.util.ArrayList;
import java.util.Collections;

import Adapterek.TFPAdapter;
import HelperClasses.AlertDialogHelper;
import HelperClasses.DatabaseHelper;
import InsertActivities.InsertPlace;
import UpdateActivities.UpdatePlace;

public class ToolsForPlaces extends AppCompatActivity {

    DatabaseHelper dh;
    AlertDialogHelper adh;
    TFPAdapter tfp;

    ArrayList<String> tool_id_list, tool_name_list, checked_id_list;

    RecyclerView recView;
    Toolbar toolbar;
    String place_id = "";
    String place_name = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools_for_places);

        //Toolbar, visszagomb
        toolbar = findViewById(R.id.select_tools_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Eszközök");

        dh = new DatabaseHelper(this);
        adh = new AlertDialogHelper();

        tool_id_list = new ArrayList<>();
        tool_name_list = new ArrayList<>();
        checked_id_list = new ArrayList<>();

        getIntentData();//Ha esetleg módosításból jönne
        adh.loadChoseableListsForSpinners(dh, dh.TOOLS, tool_id_list, tool_name_list);

        recView = findViewById(R.id.select_tools_recycler);
        tfp = new TFPAdapter(this, tool_id_list, tool_name_list, checked_id_list);
        recView.setAdapter(tfp);
        recView.setLayoutManager(new LinearLayoutManager(ToolsForPlaces.this));
    }

    public void getCheckedItems(View v, int index) {
        if(((CheckBox)v).isChecked()) {
            checked_id_list.add(tool_id_list.get(index));
            Collections.sort(checked_id_list, (s1, s2) -> {
                int s1int = Integer.parseInt(s1);
                int s2int = Integer.parseInt(s2);
                return s1int - s2int;
            });
        }
        else
        {
            checked_id_list.remove(tool_id_list.get(index));
        }
    }

    void getIntentData() {
        if(getIntent().hasExtra("chosen_tools") || getIntent().hasExtra("name")) {
            checked_id_list = getIntent().getStringArrayListExtra("chosen_tools");
            place_name = getIntent().getStringExtra("name");
        }
        if(getIntent().hasExtra("id")) {
            place_id = getIntent().getStringExtra("id");
        }
    }

    public void saveTools_forPlace(android.view.View v) {
        Intent open;
        if(place_id.equals("")) {
            open = new Intent(ToolsForPlaces.this, InsertPlace.class);
        } else {
            open = new Intent(ToolsForPlaces.this, UpdatePlace.class);
        }
        open.putStringArrayListExtra("chosen_tools", checked_id_list);
        open.putExtra("name", place_name);
        open.putExtra("id", place_id);
        startActivity(open);
        finish();
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
                tfp.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}