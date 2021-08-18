package InsertActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import HelperClasses.DatabaseHelper;
import HelperClasses.PlanHelper;

import com.example.Main.R;

import java.util.ArrayList;
import java.util.Arrays;

public class InsertPlanPhaseOne extends AppCompatActivity {

    DatabaseHelper dh;
    PlanHelper ph;
    Toolbar toolbar;
    EditText plan_name;//this
    TextView plan_place_spinner, plan_category_spinner, plan_muscle_spinner, plan_type_spinner, type_title;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch plan_type_switch;//this


    ArrayList<String>   place_name_list,
                        category_name_list,
                        muscle_name_list,
                        type_name_list;

    ArrayList<String>   place_id_list,
                        category_id_list,
                        muscle_id_list,
                        type_id_list;

    //AlertDialogDolgok
    String[] show_these_places, show_these_categories, show_these_muscles, show_these_types;
    boolean[] chosen_muscles;//this
    ArrayList<String> chosen_muscles_list;
    int chosen_place = 0, chosen_category= 0, chosen_type=0;//these
    String category_title, place_title, exercise_type_title;
    int counter = 0;
    int plan_type_state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_plan_phase_one);

        dh = new DatabaseHelper(InsertPlanPhaseOne.this);
        ph = new PlanHelper(this);

        //Vissza gomb
        toolbar = findViewById(R.id.phase_one_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Alap elemek
        plan_name = findViewById(R.id.phase_one_name);
        plan_place_spinner = findViewById(R.id.phase_one_place_spinner);
        plan_category_spinner = findViewById(R.id.phase_one_category_spinner);
        plan_muscle_spinner = findViewById(R.id.phase_one_muscle_spinner);
        plan_type_spinner = findViewById(R.id.phase_one_type_spinner);
        type_title = findViewById(R.id.plan_type_title);
        plan_type_switch = findViewById(R.id.phase_one_type_switch);

        //Listák
        place_name_list = new ArrayList<>();
        category_name_list = new ArrayList<>();
        muscle_name_list = new ArrayList<>();
        type_name_list = new ArrayList<>();

        place_id_list = new ArrayList<>();
        category_id_list = new ArrayList<>();
        muscle_id_list = new ArrayList<>();
        type_id_list = new ArrayList<>();

        //Listák/Tömbök feltöltése
        putInList(dh.PLACES, place_id_list, place_name_list);
        putInList(dh.CATEGORY, category_id_list, category_name_list);
        putInList(dh.MUSCLE_GROUPS, muscle_id_list, muscle_name_list);
        putInList(dh.TYPES, type_id_list, type_name_list);

        show_these_places = new String[place_name_list.size()];
        show_these_categories = new String[category_name_list.size()];
        show_these_muscles = new String[muscle_name_list.size()];
        show_these_types = new String[type_name_list.size()];
        chosen_muscles = new boolean[muscle_name_list.size()];
        chosen_muscles_list = new ArrayList<>();

        show_these_places = place_name_list.toArray(show_these_places);
        show_these_categories = category_name_list.toArray(show_these_categories);
        show_these_muscles = muscle_name_list.toArray(show_these_muscles);
        show_these_types = type_name_list.toArray(show_these_types);

        //Place AlertDialog
        plan_place_spinner.setOnClickListener(v -> {
            AlertDialog.Builder popUp = new AlertDialog.Builder(InsertPlanPhaseOne.this);
            popUp.setTitle("Helyszínek");

            popUp.setSingleChoiceItems(show_these_places, chosen_place-1, (dialog, which) -> {
                place_title = show_these_places[which];
                chosen_place = Integer.parseInt(place_id_list.get(which));
            });

            popUp.setPositiveButton("Rendben", (dialog, which) -> {
                plan_place_spinner.setText(place_title);
                dialog.dismiss();
            });

            popUp.setNegativeButton("Mégse", (dialog, which) -> {
                plan_place_spinner.setText("");
                chosen_place = 0;
                dialog.dismiss();
            });
            popUp.show();
        });

        //Kategória AlertDialog
        plan_category_spinner.setOnClickListener(v -> {
            AlertDialog.Builder popUp = new AlertDialog.Builder(InsertPlanPhaseOne.this);
            popUp.setTitle("Kategóriák");

            popUp.setSingleChoiceItems(show_these_categories, chosen_category-1, (dialog, which) -> {
                category_title = show_these_categories[which];
                chosen_category = Integer.parseInt(category_id_list.get(which));
            });

            popUp.setPositiveButton("Rendben", (dialog, which) -> {
                plan_category_spinner.setText(category_title);
                dialog.dismiss();
            });

            popUp.setNegativeButton("Mégse", (dialog, which) -> {
                plan_category_spinner.setText("");
                chosen_category = 0;
                dialog.dismiss();
            });
            popUp.show();
        });

        //Típus AlertDialog
        plan_type_spinner.setOnClickListener(v -> {
            AlertDialog.Builder popUp = new AlertDialog.Builder(InsertPlanPhaseOne.this);
            popUp.setTitle("Gyakorlat típusok");

            popUp.setSingleChoiceItems(show_these_types, chosen_type-1, (dialog, which) -> {
                exercise_type_title = show_these_types[which];
                chosen_type= Integer.parseInt(type_id_list.get(which));
            });

            popUp.setPositiveButton("Rendben", (dialog, which) -> {
                plan_type_spinner.setText(exercise_type_title);
                dialog.dismiss();
            });

            popUp.setNegativeButton("Mégse", (dialog, which) -> {
                plan_type_spinner.setText("");
                chosen_type = 0;
                dialog.dismiss();
            });
            popUp.show();
        });

        //Muscle AlertDialog
        plan_muscle_spinner.setOnClickListener(v -> {
            AlertDialog.Builder popUp = new AlertDialog.Builder(InsertPlanPhaseOne.this);
            popUp.setTitle("Izomcsoportok");

            popUp.setMultiChoiceItems(show_these_muscles, chosen_muscles, (dialog, which, isChecked) -> {
                if(isChecked) counter++;
                else counter--;
            });

            popUp.setPositiveButton("Kiválaszt", (dialog, which) -> {
                int x = 0;
                chosen_muscles_list.clear();
                StringBuilder writeOut = new StringBuilder();
                for(int i = 0; i < chosen_muscles.length; i++) {
                    if(chosen_muscles[i]) {
                        chosen_muscles_list.add(muscle_id_list.get(i));
                        writeOut.append(show_these_muscles[i]);
                        if(x < counter - 1) {
                            writeOut.append(", ");
                        }
                        x++;
                    }
                }
                plan_muscle_spinner.setText(writeOut);
                dialog.dismiss();
            });

            popUp.setNegativeButton("Mégse", (dialog, which) -> {
                dialog.dismiss();
            });

            popUp.setNeutralButton("Ürít", (dialog, which) -> {
                Arrays.fill(chosen_muscles, false);
                chosen_muscles_list.clear();
                plan_muscle_spinner.setText("");
            });

            popUp.show();
        });

        //Switch
        plan_type_switch.setOnClickListener(v -> {
            if(plan_type_switch.isChecked()) {
                type_title.setText("Ismétléses terv");
                plan_type_state = 1;
            }
            else
            {
                type_title.setText("Intervallumos terv");
                plan_type_state = 0;
            }
        });
    }

    public void openSecondPhase(android.view.View v) {
        boolean youCanMoveToTheSecondPhaseRightNowYesYouCanGoAhead = true;
        if(plan_name.getText().toString().equals("")) {
            youCanMoveToTheSecondPhaseRightNowYesYouCanGoAhead = false;
        }

        if(youCanMoveToTheSecondPhaseRightNowYesYouCanGoAhead) {
            String query = ph.createQueryForGoodExercises("COUNT(*)",chosen_place, chosen_category, chosen_type, chosen_muscles_list);
            if(getCount(query) > 0) {

                Intent secondPhase = new Intent(InsertPlanPhaseOne.this, InsertPlanPhaseTwo.class);
                secondPhase.putExtra("plan_name", plan_name.getText().toString());
                secondPhase.putExtra("category_id", String.valueOf(chosen_category));
                secondPhase.putExtra("exercise_type_id", String.valueOf(chosen_type));
                secondPhase.putExtra("place_id", String.valueOf(chosen_place));
                secondPhase.putExtra("plan_type", plan_type_state);
                secondPhase.putStringArrayListExtra("muscles_list", chosen_muscles_list);
                startActivity(secondPhase);
                finish();
            }
            else 
            {
                Toast.makeText(this, "Nincs elég gyakorlat a választott paramétereknek", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Adj nevet a tervnek", Toast.LENGTH_SHORT).show();
        }
    }

    void putInList(String table, ArrayList<String> id_list, ArrayList<String> name_list) {
        Cursor c = dh.select("SELECT * FROM " + table);
        if (c.getCount() == 0) {
            Toast.makeText(this, "Nincs kategória vagy helyszín rögzítve", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            while (c.moveToNext()) {
                id_list.add(c.getString(0));
                name_list.add(c.getString(1));
            }
        }
    }

    public int getCount(String query) {
        int check_number = 0;
        Cursor c = dh.select(query);
        if(c.getCount() != 0) {
            while (c.moveToNext()) {
                check_number = Integer.parseInt(c.getString(0));
            }
        }
        c.close();
        return check_number;
    }
}