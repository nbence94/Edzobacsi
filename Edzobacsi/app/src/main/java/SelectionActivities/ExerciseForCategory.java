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

import Adapterek.EFCAdapter;
import HelperClasses.AlertDialogHelper;
import HelperClasses.DatabaseHelper;
import InsertActivities.InsertCategory;
import UpdateActivities.UpdateCategory;

public class ExerciseForCategory extends AppCompatActivity {

    DatabaseHelper dh;
    AlertDialogHelper adh;
    EFCAdapter efc;

    ArrayList<String> exercise_id_list, exercise_name_list, checked_id_list;

    RecyclerView recView;
    Toolbar toolbar;
    String category_id = "";
    String category_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_for_category);

        //Toolbar, visszagomb
        toolbar = findViewById(R.id.select_tools_toolbar);
        setSupportActionBar(toolbar);

        dh = new DatabaseHelper(this);
        adh = new AlertDialogHelper();

        exercise_id_list = new ArrayList<>();
        exercise_name_list = new ArrayList<>();
        checked_id_list = new ArrayList<>();

        getIntentData();//Ha esetleg módosításból jönne
        adh.loadChoseableListsForSpinners(dh,dh.EXERCISES, exercise_id_list, exercise_name_list);

        recView = findViewById(R.id.select_tools_recycler);
        efc = new EFCAdapter(this, exercise_id_list, exercise_name_list, checked_id_list);
        recView.setAdapter(efc);
        recView.setLayoutManager(new LinearLayoutManager(ExerciseForCategory.this));
    }

    public void getCheckedItems(View v, int index) {
        if(((CheckBox)v).isChecked()) {
            checked_id_list.add(exercise_id_list.get(index));
            Collections.sort(checked_id_list, (s1, s2) -> {
                int s1int = Integer.parseInt(s1);
                int s2int = Integer.parseInt(s2);
                return s1int - s2int;
            });
        }
        else
        {
            checked_id_list.remove(exercise_id_list.get(index));
        }
    }

    void getIntentData() {
        if(getIntent().hasExtra("chosen_exercises") || getIntent().hasExtra("name")) {
            checked_id_list = getIntent().getStringArrayListExtra("chosen_exercises");
            category_name = getIntent().getStringExtra("name");
        }
        if(getIntent().hasExtra("id")) {
            category_id = getIntent().getStringExtra("id");
        }
    }

    public void saveExercises_c(android.view.View v) {
        Intent open;
        if(category_id.equals("")) {
            open = new Intent(ExerciseForCategory.this, InsertCategory.class);
        } else {
            open = new Intent(ExerciseForCategory.this, UpdateCategory.class);
        }
        open.putStringArrayListExtra("chosen_exercises", checked_id_list);
        open.putExtra("name", category_name);
        open.putExtra("id", category_id);
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
                efc.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

}