package UpdateActivities;

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

import HelperClasses.DatabaseHelper;
import com.example.Main.R;

import java.util.ArrayList;

import HelperClasses.AlertDialogHelper;
import SelectionActivities.ExerciseForCategory;

public class UpdateCategory extends AppCompatActivity {

    DatabaseHelper dh;
    AlertDialogHelper adh;

    Toolbar toolbar;
    EditText category_name;
    String category_id;
    ListView exercises_listview;
    TextView title;

    boolean[] chosen_exercises;
    String[] show_these_exercises;
    ArrayAdapter<String> listView_adapter = null;
    ArrayList<String> exercises_name_list, exercises_id_list, chosen_exercises_id_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_category);

        //Vissza gomb
        toolbar = findViewById(R.id.tBarX_editCategory);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Alap inicializálás
        dh = new DatabaseHelper(UpdateCategory.this);
        adh = new AlertDialogHelper();
        category_name = findViewById(R.id.categoryName_edit);
        title = findViewById(R.id.chosen_exercises_category_upgade);
        title.setVisibility(View.GONE);
        exercises_name_list = new ArrayList<>();
        exercises_id_list = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.EXERCISES, exercises_id_list, exercises_name_list);

        //AlertDialoghoz
        show_these_exercises = new String[exercises_name_list.size()];
        chosen_exercises = new boolean[exercises_name_list.size()];
        show_these_exercises = exercises_name_list.toArray(show_these_exercises);

        //Megjeleníteni a kiválasztott elemeket list view-ban
        exercises_listview = findViewById(R.id.tool_lview_update_place);
        listView_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.elements_to_show);
        exercises_listview.setAdapter(listView_adapter);

        //Adatok betöltés
        getIntentData();

        if(chosen_exercises_id_list.size() > 0) title.setVisibility(View.VISIBLE);
        adh.loadListView(listView_adapter, chosen_exercises_id_list, exercises_name_list, title);
        setResult(1);
    }

    public void doEditCategory(android.view.View v) {
        boolean updateable = true;
        if(category_name.getText().toString().equals("")) {
            updateable = false;
        }
        if(updateable){
            boolean category_update = dh.update(dh.CATEGORY, "Name", category_name.getText().toString().trim(), "ID", category_id);
            boolean connect_delete = dh.deleteConnect(dh.EXE_AND_CATE,"categoryID", category_id);

            //Kategória módosítása
            if(category_update && connect_delete) {
                //Kapcsolatok újratöltése
                if(chosen_exercises_id_list.size() > 0) {
                       for(int i = 0; i < chosen_exercises_id_list.size(); i++) {
                            dh.insertConnectTable(dh.EXE_AND_CATE,"categoryID", Integer.parseInt(category_id),"exerciseID", Integer.parseInt(chosen_exercises_id_list.get(i)));
                       }
                }
                finish();
                Toast.makeText(this, "Sikeres módosítás", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this,"Sikertelen módosítás", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this,"Adj nevet az eszköznek", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchExercisesInCategoryUpdate(android.view.View V) {
        Intent open = new Intent(UpdateCategory.this, ExerciseForCategory.class);
        open.putStringArrayListExtra("chosen_exercises", chosen_exercises_id_list);
        open.putExtra("id", category_id);
        open.putExtra("name", category_name.getText().toString());
        startActivityForResult(open, 1);
        finish();
    }

    void getIntentData() {
        if(getIntent().hasExtra("id") && getIntent().hasExtra("name")) {
            category_id = getIntent().getStringExtra("id");
            category_name.setText(getIntent().getStringExtra("name"));
        }
        else
        {
            Toast.makeText(this, "Nem lehetett átölteni az adatokat", Toast.LENGTH_SHORT).show();
        }
        if(getIntent().hasExtra("chosen_exercises")) {
            chosen_exercises_id_list = getIntent().getStringArrayListExtra("chosen_exercises");
        } else {
            chosen_exercises_id_list = new ArrayList<>();
            adh.getDatasFromConnect(dh, dh.EXE_AND_CATE, "categoryID", category_id, chosen_exercises_id_list,1);
        }
    }
}