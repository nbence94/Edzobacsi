package InsertActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
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

public class InsertExercise extends AppCompatActivity {
    DatabaseHelper dh;
    AlertDialogHelper adh;

    Toolbar toolbar;
    EditText exercise_name;//INSERT
    Switch static_switch, hand_switch, weight_switch;
    int static_state = 0;//INSERT
    int onehand_state = 0;//INSERT
    int weight_state = 0;//INSERT

    //Nehézségi szint betöltés
    TextView level_spinner;
    String[] show_these_levels;
    ArrayList<String> level_id_list, level_name_list;
    int chosen_level = -1;
    String the_chosen_level_text;
    String chosen_level_id = "-1";//INSERT

    //Típusok
    TextView type_spinner;
    String[] show_these_types;
    ArrayList<String> type_id_list, type_name_list;
    int type_id = -1;
    String the_chosen_type_text;
    String chosen_type_id = "-1";//INSERT

    //Kategóriák
    TextView category_spinner;
    ArrayList<String> category_list_id, category_list_name;
    boolean[] chosen_categories;
    ArrayList<String> chosen_categories_id_list = new ArrayList<>();
    String[] show_these_categories;

    //Izomcsoportok
    TextView musclegroup_spinner;
    ArrayList<String> musclegroup_list_id, musclegroup_list_name;
    boolean[] chosen_muscles;
    ArrayList<String> chosen_muscles_id_list;
    String[] show_these_muscles;

    //Eszközök
    TextView tool_spinner;
    ArrayList<String> tool_id_list, tool_name_list,chosen_tool_id_list;
    boolean[] chosen_tools;
    String[] show_these_tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_excercise);

        setResult(1);
        dh = new DatabaseHelper(InsertExercise.this);
        adh = new AlertDialogHelper();

        toolbar = findViewById(R.id.toolbar_insert_exercise);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        exercise_name = findViewById(R.id.givename_insert_exercise);
        static_switch = findViewById(R.id.static_switch_insert_exercise);
        hand_switch = findViewById(R.id.hand_switch_insert_exercise);
        weight_switch = findViewById(R.id.weight_switch_insert_exercise);

        //Nehézségi szint
        level_spinner = findViewById(R.id.level_spinner_insert_exercise);
        level_id_list = new ArrayList<>();
        level_name_list = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.LEVELS, level_id_list, level_name_list);
        show_these_levels = new String[level_id_list.size()];
        show_these_levels = level_name_list.toArray(show_these_levels);

        level_spinner.setOnClickListener(v -> {
            AlertDialog.Builder ablak = new AlertDialog.Builder(InsertExercise.this);
            ablak.setTitle("Nehézségi szint");

            ablak.setSingleChoiceItems(show_these_levels, chosen_level, (dialog, which) -> {
                the_chosen_level_text = show_these_levels[which];
                chosen_level_id = level_id_list.get(which);
                chosen_level = which;
            });

            ablak.setPositiveButton("Rendben", (dialog, which) -> {
                level_spinner.setText(the_chosen_level_text);
                dialog.dismiss();
            });
            ablak.show();
        });

        //Típus betöltése
        type_spinner = findViewById(R.id.type_spinner_insert_exercise);
        type_id_list = new ArrayList<>();
        type_name_list = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.TYPES, type_id_list, type_name_list);
        show_these_types = new String[type_name_list.size()];
        show_these_types = type_name_list.toArray(show_these_types);

        type_spinner.setOnClickListener(v -> {
            AlertDialog.Builder ablak = new AlertDialog.Builder(InsertExercise.this);
            ablak.setTitle("Erősség");
            ablak.setCancelable(false);

            ablak.setSingleChoiceItems(show_these_types, type_id, (dialog, which) -> {
                the_chosen_type_text = type_name_list.get(which);
                type_id = which;
                chosen_type_id = type_id_list.get(which);
            });

            ablak.setPositiveButton("Rendben", (dialog, which) -> {
                type_spinner.setText(the_chosen_type_text);
                dialog.dismiss();
            });

            ablak.show();
        });

        //Kategóriák betöltése
        category_spinner = findViewById(R.id.category_spinner_insert_exercise);//Ez a "textview"
        category_list_id = new ArrayList<>();
        category_list_name = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.CATEGORY, category_list_id, category_list_name);
        chosen_categories = new boolean[category_list_id.size()];
        show_these_categories = new String[category_list_id.size()];
        show_these_categories = category_list_name.toArray(show_these_categories);

        category_spinner.setOnClickListener(v -> {
            AlertDialog.Builder cDialog = new AlertDialog.Builder(InsertExercise.this);
            cDialog.setTitle("Kategóriák");
            cDialog.setCancelable(false);

            cDialog.setMultiChoiceItems(show_these_categories, chosen_categories, (dialog, which, isChecked) -> {
                if(isChecked) chosen_categories_id_list.add(category_list_id.get(which));
                else chosen_categories_id_list.remove(category_list_id.get(which));
                Collections.sort(chosen_categories_id_list);
            });

            cDialog.setPositiveButton("Kiválaszt", (dialog, which) -> {
                String result = adh.choseItems(chosen_categories_id_list, show_these_categories, chosen_categories);
                category_spinner.setText(result);
            });

            cDialog.setNeutralButton("Ürít", (dialog, which) -> {
                Arrays.fill(chosen_categories, false);
                chosen_categories_id_list.clear();
                category_spinner.setText("");
            });
            cDialog.show();
        });

        //Izomcsoportok
        musclegroup_spinner = findViewById(R.id.muscles_spinner_insert_exercise);
        musclegroup_list_id = new ArrayList<>();
        musclegroup_list_name = new ArrayList<>();
        chosen_muscles_id_list = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.MUSCLE_GROUPS, musclegroup_list_id, musclegroup_list_name);
        chosen_muscles = new boolean[musclegroup_list_id.size()];
        show_these_muscles = new String[musclegroup_list_id.size()];
        show_these_muscles = musclegroup_list_name.toArray(show_these_muscles);

        musclegroup_spinner.setOnClickListener(v -> {
            AlertDialog.Builder cDialog = new AlertDialog.Builder(InsertExercise.this);
            cDialog.setTitle("Izomcsoportok");
            cDialog.setCancelable(false);

            cDialog.setMultiChoiceItems(show_these_muscles, chosen_muscles, (dialog, which, isChecked) -> {
                if(isChecked) chosen_muscles_id_list.add(musclegroup_list_id.get(which));
                else chosen_muscles_id_list.remove(musclegroup_list_id.get(which));
                Collections.sort(chosen_muscles_id_list);
            });

            cDialog.setPositiveButton("Kiválaszt", (dialog, which) -> {
                String result = adh.choseItems(chosen_muscles_id_list, show_these_muscles, chosen_muscles);
                musclegroup_spinner.setText(result);
            });

            cDialog.setNeutralButton("Ürít", (dialog, which) -> {
                Arrays.fill(chosen_muscles, false);
                chosen_muscles_id_list.clear();
                musclegroup_spinner.setText("");
            });
            cDialog.show();
        });

        tool_spinner = findViewById(R.id.tool_spinner_insert_exercise);
        tool_id_list = new ArrayList<>();
        tool_name_list = new ArrayList<>();
        chosen_tool_id_list = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.TOOLS, tool_id_list, tool_name_list);
        chosen_tools = new boolean[tool_id_list.size()];
        show_these_tools = new String[tool_id_list.size()];
        show_these_tools = tool_name_list.toArray(show_these_tools);

        tool_spinner.setOnClickListener(v -> {
            AlertDialog.Builder cDialog = new AlertDialog.Builder(InsertExercise.this);
            cDialog.setTitle("Eszközök");
            cDialog.setCancelable(false);

            cDialog.setMultiChoiceItems(show_these_tools, chosen_tools, (dialog, which, isChecked) -> {
                if(isChecked) chosen_tool_id_list.add(tool_id_list.get(which));
                else chosen_tool_id_list.remove(tool_id_list.get(which));
                Collections.sort(chosen_tool_id_list);
            });

            cDialog.setPositiveButton("Kiválaszt", (dialog, which) -> {
                String result = adh.choseItems(chosen_tool_id_list, show_these_tools, chosen_tools);
                tool_spinner.setText(result);
            });

            cDialog.setNeutralButton("Ürít", (dialog, which) -> {
                Arrays.fill(chosen_tools, false);
                chosen_tool_id_list.clear();
                tool_spinner.setText("");
            });
            cDialog.show();
        });
    }

    public void doExInsert(android.view.View v) {
        boolean insertable = true;
        if (exercise_name.getText().toString().length() < 2) {
            Toast.makeText(this, "Adj nevet a gyakorlatnak!", Toast.LENGTH_SHORT).show();
            insertable = false;
        }
        if(Integer.parseInt(chosen_level_id) == -1 || chosen_level_id == null) {
            Toast.makeText(this, "Válaszd ki a nehézségét", Toast.LENGTH_SHORT).show();
            insertable = false;
        }
        if(Integer.parseInt(chosen_type_id) == -1) {
            Toast.makeText(this, "Válaszd ki a típusát", Toast.LENGTH_SHORT).show();
            insertable = false;
        }

        if(insertable) {
            if(static_switch.isChecked())  static_state = 1;
            else static_state = 0;

            if(hand_switch.isChecked())  onehand_state = 1;
            else onehand_state = 0;

            if(weight_switch.isChecked())  weight_state = 1;
            else weight_state = 0;

            //Gyakorlat feltöltés
            try {
                boolean succesInsert = dh.insertExercises(exercise_name.getText().toString(), onehand_state, static_state, weight_state,Integer.parseInt(chosen_level_id),Integer.parseInt(chosen_type_id));

                if(succesInsert) {
                    int gyakorlatID = dh.getTheNewID(dh.EXERCISES);

                    if (chosen_categories_id_list.size() > 0) {
                        for (int i = 0; i < chosen_categories_id_list.size(); i++) {
                            dh.insertConnectTable(dh.EXE_AND_CATE, "categoryID", Integer.parseInt(chosen_categories_id_list.get(i)), "exerciseID", gyakorlatID);
                        }
                    }

                    if (chosen_muscles_id_list.size() > 0) {
                        for (int i = 0; i < chosen_muscles_id_list.size(); i++) {
                            dh.insertConnectTable(dh.EXE_AND_MG, "exerciseID", gyakorlatID, "musclegroupID", Integer.parseInt(chosen_muscles_id_list.get(i)));
                        }
                    }

                    if (chosen_tool_id_list.size() > 0) {
                        for (int i = 0; i < chosen_tool_id_list.size(); i++) {
                            dh.insertConnectTable(dh.TOOL_AND_EXE, "toolID", Integer.parseInt(chosen_tool_id_list.get(i)), "exerciseID", gyakorlatID); }
                    }

                    Toast.makeText(this, "Sikeres mentés", Toast.LENGTH_SHORT).show();
                    setResult(1);
                    finish();
                }
            }
            catch (Exception exc) {
                System.err.println("Error: " + exc);
                Toast.makeText(this, "Sikertelen mentés", Toast.LENGTH_SHORT).show();
            }
        }
    }
}