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

import Adapterek.ToolsAdapter;
import HelperClasses.ConfigureHelper;
import InsertActivities.InsertTools;

import HelperClasses.DatabaseHelper;
import com.example.Main.R;

import java.util.ArrayList;

public class ToolsConfigure extends AppCompatActivity {

    DatabaseHelper dh;
    ConfigureHelper ch;
    ToolsAdapter TA;

    Toolbar toolbar;
    RecyclerView recView;
    ArrayList<String> tool_id_list, tool_name_list, tool_has_place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools_configure);

        dh = new DatabaseHelper(ToolsConfigure.this);
        ch = new ConfigureHelper(this);

        //Vissza gomb
        toolbar = findViewById(R.id.tBarX_tools);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Eszközök megjelenítése
        recView = findViewById(R.id.recViewX_tools);
        tool_id_list = new ArrayList<>();
        tool_name_list = new ArrayList<>();
        tool_has_place = new ArrayList<>();

        ch.getIDsandNames(dh, dh.TOOLS, "Nincsenek eszközeid", tool_id_list, tool_name_list);
        ch.getConnectsToList(dh.PLACE_AND_TOOLS,"toolID", tool_id_list, tool_has_place);

        TA = new ToolsAdapter(ToolsConfigure.this, this, tool_id_list, tool_name_list, tool_has_place);
        recView.setAdapter(TA);
        recView.setLayoutManager(new LinearLayoutManager(ToolsConfigure.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            recreate();
        }
    }

    public void openInsertTools(android.view.View v) {
        Intent open = new Intent(ToolsConfigure.this, InsertTools.class);
        startActivityForResult(open, 1);
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
                TA.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}