package InsertActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import HelperClasses.AlertDialogHelper;
import HelperClasses.DatabaseHelper;
import SelectionActivities.ExerciseForCategory;

import com.example.Main.R;

import java.util.ArrayList;

public class InsertCategory extends AppCompatActivity {

    DatabaseHelper dh;
    AlertDialogHelper adh;

    Toolbar toolbar;
    EditText category_name;

    ListView exercises_listview;
    TextView title;

    ArrayList<String> exercises_name_list, exercises_id_list, chosen_exercises_id_list;

    boolean[] chosen_exercises;
    String[] show_these_exercises;
    ArrayAdapter<String> listView_adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_category);

        dh = new DatabaseHelper(InsertCategory.this);
        adh = new AlertDialogHelper();

        //Toolbar, visszagomb
        toolbar = findViewById(R.id.toolbar_insert_category);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Alapok
        category_name = findViewById(R.id.category_name_textbox_insert);
        title = findViewById(R.id.chosen_exercises_category_insert);
        title.setVisibility(View.GONE);
        exercises_name_list = new ArrayList<>();
        exercises_id_list = new ArrayList<>();
        chosen_exercises_id_list = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.EXERCISES, exercises_id_list, exercises_name_list);

        //AlertDialoghoz
        show_these_exercises = new String[exercises_name_list.size()];
        chosen_exercises = new boolean[exercises_name_list.size()];
        show_these_exercises = exercises_name_list.toArray(show_these_exercises);

        //Megjeleníteni a kiválasztott elemeket list view-ban
        exercises_listview = findViewById(R.id.tools_listview_cinsert);
        listView_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.elements_to_show);
        exercises_listview.setAdapter(listView_adapter);
        getIntentData();
        adh.loadListView(listView_adapter, chosen_exercises_id_list, exercises_name_list, title);
        setResult(1);
    }

    public void doInsertCategory(android.view.View v) {
        if(category_name.getText().toString().equals("")){
            Toast.makeText(this, "Adj nevet a kategóriának", Toast.LENGTH_SHORT).show();
        }
        else
        {
            boolean insertSuccess = dh.insert("Name", category_name.getText().toString().trim(), dh.CATEGORY);

            if(insertSuccess) {
                //Összekötés - Kategória végezhető gyakorlatok
                if (chosen_exercises_id_list.size() > 0) {
                    int new_categoryID = dh.getTheNewID(dh.CATEGORY);
                    for (int i = 0; i < chosen_exercises_id_list.size(); i++) {
                        dh.insertConnectTable(dh.EXE_AND_CATE, "categoryID", new_categoryID, "exerciseID", Integer.parseInt(chosen_exercises_id_list.get(i)));
                    }
                }
                finish();
                Toast.makeText(this, "Sikeres mentés", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Mentés sikertelen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void searchExercisesInCategoryInsert(android.view.View V) {
        Intent open = new Intent(InsertCategory.this, ExerciseForCategory.class);
        open.putStringArrayListExtra("chosen_exercises", chosen_exercises_id_list);
        open.putExtra("name", category_name.getText().toString());
        startActivityForResult(open, 1);
        finish();
    }

    void getIntentData() {
        if(getIntent().hasExtra("chosen_exercises")) {
            chosen_exercises_id_list = getIntent().getStringArrayListExtra("chosen_exercises");
            category_name.setText(getIntent().getStringExtra("name"));
        }
    }
}