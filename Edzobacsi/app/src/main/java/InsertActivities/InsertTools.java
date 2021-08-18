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

import HelperClasses.DatabaseHelper;
import com.example.Main.R;

import java.util.ArrayList;

import HelperClasses.AlertDialogHelper;
import SelectionActivities.ExercisesForTools;

public class
InsertTools extends AppCompatActivity {

    DatabaseHelper dh;
    AlertDialogHelper adh;

    Toolbar toolbar;
    EditText tool_name;
    ListView exercises_listview;
    TextView title;

    ArrayList<String> exercises_name_list, exercises_id_list, chosen_exercises_id_list;

    boolean[] chosen_exercises;
    String[] show_these_exercises;
    ArrayAdapter<String> listView_adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_tools);

        dh = new DatabaseHelper(InsertTools.this);
        adh = new AlertDialogHelper();

        //Vissza gomb
        toolbar = findViewById(R.id.toolbar_insert_tool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tool_name = findViewById(R.id.name_insert_tool);
        title = findViewById(R.id.chosenexes_title);
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
        exercises_listview = findViewById(R.id.exercise_lview_insert_tool);
        listView_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.elements_to_show);
        exercises_listview.setAdapter(listView_adapter);
        getIntentData();
        adh.loadListView(listView_adapter, chosen_exercises_id_list, exercises_name_list, title);
        setResult(1);
    }

    public void doInsert(android.view.View v) {
        boolean insertable = true;

        if (tool_name.getText().toString().equals("")) {
            insertable = false;
        }

        if (insertable) {
            //Eszköz feltöltés
            boolean insertSuccess = dh.insert("Name", tool_name.getText().toString(),dh.TOOLS);
            if(insertSuccess) {
                //Összekötés - Eszközön végezhető gyakorlatok
                if (chosen_exercises_id_list.size() > 0) {
                    int new_toolID = dh.getTheNewID(dh.TOOLS);
                    for (int i = 0; i < chosen_exercises_id_list.size(); i++) {
                        dh.insertConnectTable(dh.TOOL_AND_EXE, "toolID", new_toolID, "exerciseID", Integer.parseInt(chosen_exercises_id_list.get(i)));
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
        else
        {
            Toast.makeText(this, "Adj nevet az eszköznek", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchExercisesInToolInsert(android.view.View V) {
        Intent open = new Intent(InsertTools.this, ExercisesForTools.class);
        open.putStringArrayListExtra("chosen_exercises", chosen_exercises_id_list);
        open.putExtra("name", tool_name.getText().toString());
        startActivityForResult(open, 1);
        finish();
    }

    void getIntentData() {
        if(getIntent().hasExtra("chosen_exercises")) {
            chosen_exercises_id_list = getIntent().getStringArrayListExtra("chosen_exercises");
            tool_name.setText(getIntent().getStringExtra("name"));
        }
    }
}