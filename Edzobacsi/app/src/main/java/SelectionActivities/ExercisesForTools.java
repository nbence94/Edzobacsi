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

import Adapterek.EFTAdapter;
import HelperClasses.AlertDialogHelper;
import HelperClasses.ConfigureHelper;
import HelperClasses.DatabaseHelper;
import HelperClasses.PlanHelper;
import InsertActivities.InsertTools;
import UpdateActivities.UpdateTool;

public class ExercisesForTools extends AppCompatActivity {

    DatabaseHelper dh;
    ConfigureHelper ch;
    PlanHelper ph;
    AlertDialogHelper adh;
    EFTAdapter EFTA;

    ArrayList<String> exercise_id_list, exercise_name_list;
    public ArrayList<String> checked_id_list;
    RecyclerView recView;
    Toolbar toolbar;
    String tool_id = "";
    String tool_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_for_tools);

        //Toolbar, visszagomb
        toolbar = findViewById(R.id.select_tools_toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dh = new DatabaseHelper(this);
        ch = new ConfigureHelper(this);
        adh = new AlertDialogHelper();
        ph = new PlanHelper(this);

        exercise_id_list = new ArrayList<>();
        exercise_name_list = new ArrayList<>();
        checked_id_list = new ArrayList<>();

        getIntentData();//Ha esetleg módosításból jönne
        adh.loadChoseableListsForSpinners(dh,dh.EXERCISES, exercise_id_list, exercise_name_list);

        recView = findViewById(R.id.select_tools_recycler);
        EFTA = new EFTAdapter(this, exercise_id_list, exercise_name_list, checked_id_list);
        recView.setAdapter(EFTA);
        recView.setLayoutManager(new LinearLayoutManager(ExercisesForTools.this));
    }

    public void getCheckedItems(View v, int index) {
        if(((CheckBox)v).isChecked()) {
            checked_id_list.add(String.valueOf(exercise_id_list.get(index)));
            Collections.sort(checked_id_list, (s1, s2) -> {
                int s1int = Integer.parseInt(s1);
                int s2int = Integer.parseInt(s2);
                return s1int - s2int;
            });
        }
        else
        {
            checked_id_list.remove(String.valueOf(exercise_id_list.get(index)));
        }
    }

    void getIntentData() {
        if(getIntent().hasExtra("chosen_exercises") || getIntent().hasExtra("name")) {
            checked_id_list = getIntent().getStringArrayListExtra("chosen_exercises");
            tool_name = getIntent().getStringExtra("name");
        }
        if(getIntent().hasExtra("id")) {
            tool_id = getIntent().getStringExtra("id");
        }
    }

    public void saveExercises(android.view.View v) {
        Intent open;
        if(tool_id.equals("")) {
            open = new Intent(ExercisesForTools.this, InsertTools.class);
        } else {
            open = new Intent(ExercisesForTools.this, UpdateTool.class);
        }
        open.putStringArrayListExtra("chosen_exercises", checked_id_list);
        open.putExtra("name", tool_name);
        open.putExtra("id", tool_id);
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
                EFTA.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}