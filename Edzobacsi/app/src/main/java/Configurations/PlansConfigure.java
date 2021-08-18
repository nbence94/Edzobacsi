package Configurations;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import Adapterek.PlanConfigureAdapter;
import HelperClasses.ConfigureHelper;
import InsertActivities.InsertPlanPhaseOne;

import HelperClasses.DatabaseHelper;

import com.example.Main.R;

import java.util.ArrayList;

public class PlansConfigure extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseHelper dh;
    PlanConfigureAdapter PCA;
    ConfigureHelper ch;

    //Adatok megjelenítése
    RecyclerView recView;
    ArrayList<String> plan_id_list, plan_name_list, plan_place_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans_configure);

        dh = new DatabaseHelper(PlansConfigure.this);
        ch = new ConfigureHelper(this);

        //Vissza gomb
        toolbar = findViewById(R.id.tBar_plans);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //--
        recView = findViewById(R.id.recViewX_plan);
        plan_id_list = new ArrayList<>();
        plan_name_list = new ArrayList<>();
        plan_place_list = new ArrayList<>();

        getDatasIntoList();

        PCA = new PlanConfigureAdapter(PlansConfigure.this, this, plan_id_list, plan_name_list, plan_place_list);
        recView.setAdapter(PCA);
        recView.setLayoutManager(new LinearLayoutManager(PlansConfigure.this));
    }

    public void openInsertPlan(android.view.View v) {
        Intent open = new Intent(PlansConfigure.this, InsertPlanPhaseOne.class);
        startActivity(open);
    }

    public void getDatasIntoList() {
        Cursor c = dh.select("SELECT * FROM " + dh.PLANS);
        if(c.getCount() == 0) {
            Toast.makeText(this,"Nincsenek terveid", Toast.LENGTH_SHORT).show();
        }
        else
        {
            while(c.moveToNext()){
                plan_id_list.add(c.getString(0));
                plan_name_list.add(c.getString(1));
                ch.writeNameOrNothing(dh.PLACES, c.getString(5), plan_place_list);
            }
        }
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
                PCA.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}