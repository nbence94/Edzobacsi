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
import SelectionActivities.ExercisesForTools;

public class UpdateTool extends AppCompatActivity {


    DatabaseHelper dh;
    AlertDialogHelper adh;

    Toolbar toolbar;
    EditText tool_name;
    ListView exercises_listview;
    TextView title;

    String tool_id = "";// Ez alapján UPDATE
    ArrayList<String> exercises_name_list, exercises_id_list, chosen_exercises_id_list;

    boolean[] chosen_exercises;
    String[] show_these_exercises;
    ArrayAdapter<String> listView_adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_tool);

        //Vissza gomb
        toolbar = findViewById(R.id.toolbar_update_tools);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Alap inicializálás
        dh = new DatabaseHelper(UpdateTool.this);
        adh = new AlertDialogHelper();
        tool_name = findViewById(R.id.name_update_tools);
        title = findViewById(R.id.chosenexes_title_update);
        title.setVisibility(View.GONE);
        exercises_name_list = new ArrayList<>();
        exercises_id_list = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.EXERCISES, exercises_id_list, exercises_name_list);

        //AlertDialoghoz
        show_these_exercises = new String[exercises_name_list.size()];
        chosen_exercises = new boolean[exercises_name_list.size()];
        show_these_exercises = exercises_name_list.toArray(show_these_exercises);

        //Megjeleníteni a kiválasztott elemeket list view-ban
        exercises_listview = findViewById(R.id.exercise_lview_update_tools);
        listView_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.elements_to_show);
        exercises_listview.setAdapter(listView_adapter);

        //Adatok betöltése
        getIntentData();

        if(chosen_exercises_id_list.size() > 0) title.setVisibility(View.VISIBLE);
        adh.loadListView(listView_adapter, chosen_exercises_id_list, exercises_name_list, title);
        setResult(1);
    }

    public void doUpdate(android.view.View v) {
        boolean updateable = true;
        if(tool_name.getText().toString().equals("")) {
            updateable = false;
        }

        if(updateable){
            //Eszköz módosítása
            if(dh.update(dh.TOOLS, "Name", tool_name.getText().toString(), "ID", tool_id)) {
                //Kapcsolatok törlése
                if(dh.deleteConnect(dh.TOOL_AND_EXE,"toolID", tool_id))
                {
                    //Kapcsolatok újra töltése
                    if(chosen_exercises_id_list.size() > 0) {
                        for (int i = 0; i < chosen_exercises_id_list.size(); i++) {
                            dh.insertConnectTable(dh.TOOL_AND_EXE, "toolID", Integer.parseInt(tool_id), "exerciseID", Integer.parseInt(chosen_exercises_id_list.get(i)));
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
                Toast.makeText(this,"Sikertelen módosítás", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this,"Adj nevet az eszköznek", Toast.LENGTH_SHORT).show();
        }

    }

    public void searchExercisesInToolUpdate(android.view.View V) {
        Intent open = new Intent(UpdateTool.this, ExercisesForTools.class);
        open.putStringArrayListExtra("chosen_exercises", chosen_exercises_id_list);
        open.putExtra("id", tool_id);
        open.putExtra("name", tool_name.getText().toString());
        startActivityForResult(open, 1);
        finish();
    }

    void getIntentData() {
        if(getIntent().hasExtra("id") || getIntent().hasExtra("name")) {
            tool_id = getIntent().getStringExtra("id");
            tool_name.setText(getIntent().getStringExtra("name"));
        }
        else
        {
            Toast.makeText(this, "Nem sikerült betölteni a kért adatokat", Toast.LENGTH_SHORT).show();
        }
        if(getIntent().hasExtra("chosen_exercises")) {
            chosen_exercises_id_list = getIntent().getStringArrayListExtra("chosen_exercises");
        } else {
            chosen_exercises_id_list = new ArrayList<>();
            adh.getDatasFromConnect(dh, dh.TOOL_AND_EXE, "toolID", tool_id, chosen_exercises_id_list, 1);
        }
    }
}