package InsertActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import HelperClasses.DatabaseHelper;
import com.example.Main.R;

import java.util.ArrayList;

import Adapterek.PhaseTwoAdapter;
import HelperClasses.ConfigureHelper;
import HelperClasses.PlanHelper;

public class InsertPlanPhaseTwo extends AppCompatActivity {

    DatabaseHelper dh;
    PhaseTwoAdapter PTA;
    PlanHelper ph;
    ConfigureHelper ch;
    Toolbar toolbar;
    RecyclerView recView;

    //Lekérdezéshez
    ArrayList<String> muscles;// választott izomcsoportok
    String category_id, place_id, exercise_type_id;
    ArrayList<String> type_id_list, type_name_list;
    ArrayList<String> exercise_name_list, exercise_id_list, exercise_level_list;
    String plan_name;//Ezt csak tovább kell adni
    int plan_type_state = 0;
    public ArrayList<String> checked_exercises_list;//Ezt a listát adom tovább (választott gyakorlat ID-k)*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_plan_phase_two);

        //Vissza gomb
        toolbar = findViewById(R.id.second_phase_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Alap szükséglet
        dh = new DatabaseHelper(InsertPlanPhaseTwo.this);
        ph = new PlanHelper(this);
        ch = new ConfigureHelper(this);

        recView = findViewById(R.id.phase_two_recView);
        exercise_name_list = new ArrayList<>();
        exercise_id_list = new ArrayList<>();
        exercise_level_list = new ArrayList<>();
        type_id_list = new ArrayList<>();
        type_name_list = new ArrayList<>();

        //Adatok beszerzése
        getIntentData();
        ch.getIDsandNames(dh, dh.TYPES, "Hiba", type_id_list, type_name_list);
        String query = ph.createQueryForGoodExercises("DISTINCT ID, Name, Level",Integer.parseInt(place_id), Integer.parseInt(category_id), Integer.parseInt(exercise_type_id), muscles);
        ph.putInListTheResult(query, exercise_id_list, exercise_name_list, exercise_level_list);
        Log.d("Log", query);
        if(exercise_id_list.size() == 0) {
            Toast.makeText(this, "Nincs elég gyakorlat a választott szempontok szerint", Toast.LENGTH_SHORT).show();
            finish();
        }

        //Elemek elhelyezése
        PTA = new PhaseTwoAdapter(InsertPlanPhaseTwo.this, this, exercise_id_list, exercise_name_list, exercise_level_list);
        recView.setAdapter(PTA);
        recView.setLayoutManager(new LinearLayoutManager(InsertPlanPhaseTwo.this));

        //Összeszedni a gyakorlatokat a tervhez
        checked_exercises_list = new ArrayList<>();
    }

    public void openThirdPhase(android.view.View v) {
        if(checked_exercises_list.size() > 1) {
            Intent openPhase = new Intent(InsertPlanPhaseTwo.this, InsertPlanPhaseThree.class);
            openPhase.putStringArrayListExtra("getExercises", checked_exercises_list);
            openPhase.putExtra("name", plan_name);
            openPhase.putExtra("place_id", place_id);
            openPhase.putExtra("category_id", category_id);
            openPhase.putExtra("plan_type", plan_type_state);
            startActivity(openPhase);
            finish();
        }
        else
        {
            Toast.makeText(this, "Jelölj ki több gyakorlatot", Toast.LENGTH_SHORT).show();
        }
    }

    public void getCheckedItems(View v, int index) {
        if(((CheckBox)v).isChecked()) {
            checked_exercises_list.add(exercise_id_list.get(index));
        }
        else
        {
            checked_exercises_list.remove(exercise_id_list.get(index));
        }
    }

    void getIntentData() {
        if(getIntent().hasExtra("plan_name")) {
            plan_name = getIntent().getStringExtra("plan_name");
            place_id = getIntent().getStringExtra("place_id");
            category_id = getIntent().getStringExtra("category_id");
            exercise_type_id = getIntent().getStringExtra("exercise_type_id");
            muscles = getIntent().getStringArrayListExtra("muscles_list");
            plan_type_state = getIntent().getIntExtra("plan_type", 0);
        }
        else
        {
            Toast.makeText(this, "Nem sikerült átadni a kért adatokat", Toast.LENGTH_SHORT).show();
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
                PTA.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}