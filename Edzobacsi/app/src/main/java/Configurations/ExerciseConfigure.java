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

import HelperClasses.DatabaseHelper;
import HelperClasses.ConfigureHelper;
import com.example.Main.R;

import java.util.ArrayList;

import Adapterek.ExerciseAdapter;
import InsertActivities.InsertExercise;

public class ExerciseConfigure extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseHelper dh;
    ConfigureHelper ch;
    RecyclerView recView;
    ArrayList<String> exercise_id_list, exercise_name_list, exercise_level_name_list, exercise_deletable_list, exercise_has_tool;
    ExerciseAdapter EA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excercise_configure);

        //Vissza gomb
        toolbar = findViewById(R.id.toolbar_exercise_configuration);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Inicializálás
        dh = new DatabaseHelper(ExerciseConfigure.this);
        ch = new ConfigureHelper(this);
        recView = findViewById(R.id.recycler_exercise_configuration);
        exercise_id_list = new ArrayList<>();
        exercise_name_list = new ArrayList<>();
        exercise_level_name_list = new ArrayList<>();
        exercise_deletable_list = new ArrayList<>();
        exercise_has_tool = new ArrayList<>();

        //Adatok begyűjtése
        getExercisesIntoList();
        checkExercisesForDeletableState();
        ch.getConnectsToList(dh.TOOL_AND_EXE,"exerciseID", exercise_id_list, exercise_has_tool);

        EA = new ExerciseAdapter(ExerciseConfigure.this,this, exercise_id_list, exercise_name_list, exercise_level_name_list, exercise_deletable_list, exercise_has_tool);
        recView.setAdapter(EA);
        recView.setLayoutManager(new LinearLayoutManager(ExerciseConfigure.this));
    }

    public void openInsertExc(android.view.View v) {
        Intent openInsert = new Intent(ExerciseConfigure.this, InsertExercise.class);
        startActivityForResult(openInsert, 1);
    }

    public void getExercisesIntoList() {
        Cursor c = dh.select("SELECT * FROM " + dh.EXERCISES + " ORDER BY Name ASC");
        if(c.getCount() == 0) {
            Toast.makeText(this,"Nincsenek gyakorlataid", Toast.LENGTH_SHORT).show();
        } else {
            while(c.moveToNext()){
                exercise_id_list.add(c.getString(0));
                exercise_name_list.add(c.getString(1));
                ch.getNameForID(dh, dh.LEVELS, c.getString(5), "Hiba", exercise_level_name_list);
            }
            c.close();
        }
    }

    void checkExercisesForDeletableState(){
        for(int i = 0; i < exercise_id_list.size(); i++) {
            if(ch.checkDeletable(dh,dh.PLAN_AND_EXE,"exerciseID", exercise_id_list.get(i))) exercise_deletable_list.add("1");
            else exercise_deletable_list.add("0");
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
                EA.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}