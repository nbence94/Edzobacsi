package UpdateActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import HelperClasses.DatabaseHelper;
import com.example.Main.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import HelperClasses.AlertDialogHelper;

public class UpdateExercise extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseHelper dh;
    AlertDialogHelper adh;
    EditText exercise_name;
    TextView level_spinner, type_spinner, category_spinner, muscle_spinner, tool_spinner;
    Switch static_switch, onehand_switch, weight_switch;
    String exercise_id;//UPDATE

    //Nehézségi Spinner felöltés
    ArrayList<String> level_id_list, level_names_list;
    String[] levels_to_show;
    int chosen_level = 0;
    String level_text;
    String chosen_level_id;//UPDATE

    //Típusok
    ArrayList<String> type_id_list, type_names_list;
    String[] types_to_show;
    int chosen_type = 0;
    String type_text, chosen_type_id;//UPDATE

    //Kategóriák
    ArrayList<String> category_id_list, category_names_list;
    String[] categories_to_show;
    boolean[] chosen_categories;//UPDATE
    ArrayList<String> chosen_categories_list;

    //Izomcsoport
    ArrayList<String> muscle_names_list, muscle_id_list;
    String[] muscles_to_show;
    boolean[] chosen_muscles;
    ArrayList<String> chosen_muscles_id_list;//UPDATE

    //Tools
    ArrayList<String> tool_id_list, tool_name_list;
    String[] tools_to_show;
    boolean[] chosen_tools;
    ArrayList<String> chosen_tools_id_list;//UPDATE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_exercise);

        //Vissza gomb
        toolbar = findViewById(R.id.toolbar_update_exercise);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Alapok
        dh = new DatabaseHelper(UpdateExercise.this);
        adh = new AlertDialogHelper();
        exercise_name = findViewById(R.id.name_update_exercise);
        level_spinner = findViewById(R.id.level_spinner_update_exercise);
        type_spinner = findViewById(R.id.type_spinner_update_exercise);
        category_spinner = findViewById(R.id.category_spinner_update_exercise);
        muscle_spinner = findViewById(R.id.muscles_spinner_update_exercise);
        static_switch = findViewById(R.id.static_switch_update_exercise);
        onehand_switch = findViewById(R.id.hand_switch_update_exercise);
        weight_switch = findViewById(R.id.weight_switch_update_exercise);
        tool_spinner = findViewById(R.id.tool_spinner_update_exercise);

        //Adatok megjelenítése
        getIntentData();
        loadExerciseDatas();

        //Nehézségi Spinner - Single Choice alertDialog
        level_names_list = new ArrayList<>();
        level_id_list = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.LEVELS, level_id_list, level_names_list);
        level_spinner.setText(level_names_list.get(chosen_level));
        levels_to_show = new String[level_names_list.size()];
        levels_to_show = level_names_list.toArray(levels_to_show);

        level_spinner.setOnClickListener(v -> {
            AlertDialog.Builder ablak = new AlertDialog.Builder(UpdateExercise.this);
            ablak.setTitle("Nehézségi szint");
            ablak.setCancelable(false);

            ablak.setSingleChoiceItems(levels_to_show, chosen_level, (dialog, which) -> {
                level_text = levels_to_show[which];
                chosen_level_id = level_id_list.get(which);
                chosen_level = which;
            });

            ablak.setPositiveButton("Rendben", (dialog, which) -> {
                level_spinner.setText(level_text);
                dialog.dismiss();
            });
            ablak.show();
        });

        //Tipusok - Single Choice alertDialog
        type_names_list = new ArrayList<>();
        type_id_list = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.TYPES, type_id_list, type_names_list);
        type_spinner.setText(type_names_list.get(chosen_type));
        types_to_show = new String[type_names_list.size()];
        types_to_show = type_names_list.toArray(types_to_show);

        type_spinner.setOnClickListener(v -> {
            AlertDialog.Builder ablak = new AlertDialog.Builder(UpdateExercise.this);
            ablak.setTitle("Típusok");
            ablak.setCancelable(false);

            ablak.setSingleChoiceItems(types_to_show, chosen_type, (dialog, which) -> {
                type_text = types_to_show[which];
                chosen_type_id = type_id_list.get(which);
                chosen_type = which;
            });

            ablak.setPositiveButton("Rendben", (dialog, which) -> {
                type_spinner.setText(type_text);
                dialog.dismiss();
            });
            ablak.show();
        });

        //Kategóriák - Multi Choice
        category_names_list = new ArrayList<>();
        category_id_list = new ArrayList<>();
        chosen_categories_list = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.CATEGORY, category_id_list, category_names_list);
        chosen_categories = new boolean[category_id_list.size()];
        categories_to_show = new String[category_id_list.size()];
        categories_to_show = category_names_list.toArray(categories_to_show);
        adh.getDatasFromConnect(dh, dh.EXE_AND_CATE, "exerciseID", exercise_id, chosen_categories_list, 0);
        adh.checkAndWriteChosenItems(chosen_categories_list, category_id_list,category_names_list, chosen_categories, category_spinner);

        category_spinner.setOnClickListener(v -> {
            AlertDialog.Builder cDialog = new AlertDialog.Builder(UpdateExercise.this);
            cDialog.setTitle("Kategóriák");
            cDialog.setCancelable(false);

            cDialog.setMultiChoiceItems(categories_to_show, chosen_categories, (dialog, which, isChecked) -> {
                if(isChecked) chosen_categories_list.add(category_id_list.get(which));
                else chosen_categories_list.remove(category_id_list.get(which));
                Collections.sort(chosen_categories_list);
            });

            cDialog.setPositiveButton("Kiválaszt", (dialog, which) -> {
                String result = adh.choseItems(chosen_categories_list, categories_to_show, chosen_categories);
                category_spinner.setText(result);
            });

            cDialog.setNeutralButton("Ürít", (dialog, which) -> {
                Arrays.fill(chosen_categories, false);
                chosen_categories_list.clear();
                category_spinner.setText("");
            });
            cDialog.show();
        });

        //Izomcsoportok - Multi Choice
        muscle_names_list = new ArrayList<>();
        muscle_id_list = new ArrayList<>();
        chosen_muscles_id_list = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.MUSCLE_GROUPS, muscle_id_list, muscle_names_list);
        chosen_muscles = new boolean[muscle_names_list.size()];
        muscles_to_show = new String[category_names_list.size()];
        muscles_to_show = muscle_names_list.toArray(muscles_to_show);
        adh.getDatasFromConnect(dh, dh.EXE_AND_MG, "exerciseID", exercise_id, chosen_muscles_id_list, 1);
        adh.checkAndWriteChosenItems(chosen_muscles_id_list, muscle_id_list, muscle_names_list, chosen_muscles, muscle_spinner);

        muscle_spinner.setOnClickListener(v -> {
            AlertDialog.Builder cDialog = new AlertDialog.Builder(UpdateExercise.this);
            cDialog.setTitle("Izomcsoportok");
            cDialog.setCancelable(false);

            cDialog.setMultiChoiceItems(muscles_to_show, chosen_muscles, (dialog, which, isChecked) -> {
                if(isChecked) chosen_muscles_id_list.add(muscle_id_list.get(which));
                else chosen_muscles_id_list.remove(muscle_id_list.get(which));
                Collections.sort(chosen_muscles_id_list);
            });

            cDialog.setPositiveButton("Kiválaszt", (dialog, which) -> {
                String result = adh.choseItems(chosen_muscles_id_list, muscles_to_show, chosen_muscles);
                muscle_spinner.setText(result);
            });

            cDialog.setNeutralButton("Ürít", (dialog, which) -> {
                Arrays.fill(chosen_muscles, false);
                chosen_muscles_id_list.clear();
                muscle_spinner.setText("");
            });
            cDialog.show();
        });


        //Eszközök - Multi Choice
        tool_id_list = new ArrayList<>();
        tool_name_list = new ArrayList<>();
        chosen_tools_id_list = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.TOOLS, tool_id_list, tool_name_list);
        chosen_tools = new boolean[tool_id_list.size()];
        tools_to_show = new String[tool_id_list.size()];
        tools_to_show = tool_name_list.toArray(tools_to_show);
        adh.getDatasFromConnect(dh, dh.TOOL_AND_EXE, "exerciseID", exercise_id, chosen_tools_id_list, 0);
        adh.checkAndWriteChosenItems(chosen_tools_id_list, tool_id_list, tool_name_list, chosen_tools, tool_spinner);

        tool_spinner.setOnClickListener(v -> {
            AlertDialog.Builder cDialog = new AlertDialog.Builder(UpdateExercise.this);
            cDialog.setTitle("Eszközök");
            cDialog.setCancelable(false);

            cDialog.setMultiChoiceItems(tools_to_show, chosen_tools, (dialog, which, isChecked) -> {
                if(isChecked) chosen_tools_id_list.add(tool_id_list.get(which));
                else chosen_tools_id_list.remove(tool_id_list.get(which));
                Collections.sort(chosen_tools_id_list);
            });

            cDialog.setPositiveButton("Kiválaszt", (dialog, which) -> {
                String result = adh.choseItems(chosen_tools_id_list, tools_to_show, chosen_tools);
                tool_spinner.setText(result);
            });

            cDialog.setNeutralButton("Ürít", (dialog, which) -> {
                Arrays.fill(chosen_tools, false);
                chosen_tools_id_list.clear();
                tool_spinner.setText("");
            });
            cDialog.show();
        });
    }

    void loadExerciseDatas(){
        Cursor c = dh.getID(dh.EXERCISES, exercise_id);
        if(c.getCount() == 0) {
            Toast.makeText(this,"Nem sikerült betölteni a gyakorlat adatait", Toast.LENGTH_SHORT).show();
        }
        else
        {
            while(c.moveToNext()) {
                exercise_name.setText(c.getString(1));
                if(c.getString(2).equals("1")){
                    onehand_switch.setChecked(true);
                }
                if(c.getString(3).equals("1")){
                    static_switch.setChecked(true);
                }
                if(c.getString(4).equals("1")){
                    weight_switch.setChecked(true);
                }
                chosen_level_id = c.getString(5);
                chosen_type_id = c.getString(6);
                chosen_level = Integer.parseInt(chosen_level_id)-1;
                chosen_type = Integer.parseInt(chosen_type_id)-1;
            }
        }
    }

    void getIntentData() {
        if(getIntent().hasExtra("id")) {
            exercise_id = getIntent().getStringExtra("id");
        }
        else
        {
            Toast.makeText(this, "Nem sikerült betölteni a kért adatokat", Toast.LENGTH_SHORT).show();
        }
    }

    public void doUpdateInExerciseUpdate(android.view.View v) {
        boolean insertable = true;
        String name = exercise_name.getText().toString();
        if(name.equals("")) {
            insertable = false;
        }
        if(chosen_level_id == null || Integer.parseInt(chosen_level_id) < 1) {
            insertable = false;
        }
        if(chosen_type_id == null || Integer.parseInt(chosen_type_id) < 1)  {
            insertable = false;
        }

        if(insertable) {
            int static_state, onehand_state, weight_state;
            if(static_switch.isChecked())  static_state = 1;
            else static_state = 0;

            if(onehand_switch.isChecked())  onehand_state = 1;
            else onehand_state = 0;

            if(weight_switch.isChecked())  weight_state = 1;
            else weight_state = 0;

            //Kategória és Izomcsoport összeköttetések törlése először
            boolean execate_result = dh.deleteConnect(dh.EXE_AND_CATE,"exerciseID", exercise_id);
            boolean exemg_result = dh.deleteConnect(dh.EXE_AND_MG,"exerciseID", exercise_id);
            boolean delete_tools_result = dh.deleteConnect(dh.TOOL_AND_EXE, "exerciseID", exercise_id);

            if(!execate_result || !exemg_result || !delete_tools_result) {
                Toast.makeText(this,"Sikertelen törlés", Toast.LENGTH_SHORT).show();
            }
            else {

                if (chosen_categories_list.size() > 0) {
                    for (int i = 0; i < chosen_categories_list.size(); i++) {
                        dh.insertConnectTable(dh.EXE_AND_CATE, "categoryID", Integer.parseInt(chosen_categories_list.get(i)), "exerciseID", Integer.parseInt(exercise_id));
                    }
                }

                if (chosen_muscles_id_list.size() > 0) {
                    for (int i = 0; i < chosen_muscles_id_list.size(); i++) {
                        dh.insertConnectTable(dh.EXE_AND_MG, "exerciseID", Integer.parseInt(exercise_id), "musclegroupID", Integer.parseInt(chosen_muscles_id_list.get(i)));
                    }
                }

                if (chosen_tools_id_list.size() > 0) {
                    for (int i = 0; i < chosen_tools_id_list.size(); i++) {
                        dh.insertConnectTable(dh.TOOL_AND_EXE, "toolID", Integer.parseInt(chosen_tools_id_list.get(i)), "exerciseID", Integer.parseInt(exercise_id)); }
                }

                //Gyakorlat update
                int levelID = Integer.parseInt(chosen_level_id);
                int typeID = Integer.parseInt(chosen_type_id);
                boolean successUpdate = dh.updateExercises(name, onehand_state, static_state, weight_state, levelID, typeID, exercise_id);
                if(successUpdate) {
                    Toast.makeText(this, "Sikeres mentés", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Mentés sikertelen", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        }
        else
        {
            Toast.makeText(this, "Valami nincs kiválasztva!", Toast.LENGTH_SHORT).show();
        }
    }
}