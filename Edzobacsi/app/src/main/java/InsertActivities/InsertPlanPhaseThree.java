package InsertActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import HelperClasses.DatabaseHelper;
import com.example.Main.R;

import java.util.ArrayList;
import java.util.Collections;

import Adapterek.PhaseThreeAdapter;
import Configurations.PlansConfigure;
import HelperClasses.ConfigureHelper;
import HelperClasses.PlanHelper;

public class InsertPlanPhaseThree extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseHelper dh;
    PlanHelper ph;
    ConfigureHelper ch;
    PhaseThreeAdapter PTA;
    RecyclerView recView;

    ArrayList<String> phase_two_chosen_exercises;
    ArrayList<String> exercises_id_for_plan, exercises_name_list, exercise_onehand, exercise_static, exercise_weight, exercise_level_list;
    ArrayList<Integer> exercise_type_list;
    ArrayList<String> user_type_id_list, user_type_level_list;
    TextView plan_name, plan_place, plan_category;
    ArrayList<String> exercise_dbmp_list;

    //Inserthez
    String get_plan_name, get_plan_place, get_plan_category;
    EditText plan_note;
    int plan_type_state;
    ArrayList<String> exercise_reps_list, exercise_weights_list;
    ArrayList<String> exercises_id_helper_list;
    //
    public boolean editable = true;
    int number_of_elements = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_plan_phase_three);

        dh = new DatabaseHelper(InsertPlanPhaseThree.this);
        ph = new PlanHelper(this);
        ch = new ConfigureHelper(this);

        //Vissza gomb
        toolbar = findViewById(R.id.phase_three_toolbar_insert);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //GUI elemek
        plan_name = findViewById(R.id.phase_three_name_insert);
        plan_place = findViewById(R.id.phase_three_place_insert);
        plan_category = findViewById(R.id.phase_three_category_insert);
        plan_note = findViewById(R.id.plan_note_insert);

        //Szükséges listák
        exercises_id_for_plan = new ArrayList<>();//INSERT
        exercises_id_helper_list = new ArrayList<>();
        exercise_reps_list = new ArrayList<>();//Ismétlések - INSERT
        exercise_weights_list = new ArrayList<>();

        exercises_name_list = new ArrayList<>();
        exercise_onehand = new ArrayList<>();
        exercise_static = new ArrayList<>();
        exercise_weight = new ArrayList<>();
        exercise_level_list = new ArrayList<>();
        exercise_type_list = new ArrayList<>();
        user_type_id_list = new ArrayList<>();
        user_type_level_list = new ArrayList<>();

        exercise_dbmp_list = new ArrayList<>();

        //Adatok begyűjtése
        getIntentData();
        putInListTheChosenExercisesFromPhaseTwo(getChosenExercisesQuery());
        getUserLevelsGroupByTypesAndPutInList();
        loadRepsListAccordingToType();
        loadWeightList();
        ph.loadRepsTitleList(plan_type_state, exercise_static, exercise_dbmp_list);

        //Adatok kiírása
        plan_name.setText(get_plan_name);
        showCategoryAndPlace();//Kategória és Helyszín kiírása

        //Recycler View
        recView = findViewById(R.id.phase_three_recycler_insert);
        PTA = new PhaseThreeAdapter(InsertPlanPhaseThree.this, this, exercises_id_for_plan, exercises_name_list, exercise_weights_list, exercise_reps_list, exercise_dbmp_list);
        recView.setAdapter(PTA);
        recView.setLayoutManager(new LinearLayoutManager(InsertPlanPhaseThree.this));

        //Csúsztatás
        ItemTouchHelper moving = new ItemTouchHelper(delItem);
        moving.attachToRecyclerView(recView);
    }

    public void doInsert(android.view.View v) {
        boolean insertable = true;

        if(get_plan_name.equals("")) {
            insertable = false;
        }
        if(Integer.parseInt(get_plan_place) <= 0) {
            get_plan_place = "-1";
        }
        if(Integer.parseInt(get_plan_category) <= 0) {
            get_plan_category = "-1";
        }
        if(exercises_id_for_plan.size() <= 0) {
            insertable = false;
            Toast.makeText(this, "Gyakorlatok nélkül nem lehet menteni", Toast.LENGTH_SHORT).show();
        }
        for(int i = 0; i < exercise_weights_list.size(); i++) {
            if(exercise_weights_list.get(i).equals("") || exercise_weights_list.get(i).equals("-")) {
                insertable = false;
                Toast.makeText(this, "Nincs megadva minden súly", Toast.LENGTH_SHORT).show();
                break;
            }
            if(exercise_reps_list.get(i).equals("")) {
                insertable = false;
                Toast.makeText(this, "Nincs megadva minden ismétlés", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        if(insertable) {
            boolean insertSuccess = dh.insertPlan(get_plan_name, plan_type_state, plan_note.getText().toString(), Integer.parseInt(get_plan_category), Integer.parseInt(get_plan_place));

            if(insertSuccess) {
                int planID = dh.getTheNewID(dh.PLANS);
                boolean success = true;
                for(int i = 0; i < exercises_id_for_plan.size(); i++) {
                    success = dh.insertPlanConnect(planID, Integer.parseInt(exercises_id_for_plan.get(i)), Integer.parseInt(exercise_weights_list.get(i)), Integer.parseInt(exercise_reps_list.get(i)), (i+1));
                    //planID, exerciseID, Weight, Repeat,
                    if(!success) break;
                }
                if(!success) Toast.makeText(this, "Sikertelen mentés", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Mentés sikeres", Toast.LENGTH_SHORT).show();
                finish();
                Intent openPlans = new Intent(InsertPlanPhaseThree.this, PlansConfigure.class);
                startActivity(openPlans);
            }
            else
            {
                Toast.makeText(this, "Sikertelen mentés", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Sikertelen mentés", Toast.LENGTH_SHORT).show();
        }
    }

    ItemTouchHelper.SimpleCallback delItem = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(exercises_id_for_plan, fromPosition, toPosition);
            Collections.swap(exercises_name_list, fromPosition, toPosition);
            Collections.swap(exercise_reps_list, fromPosition, toPosition);
            Collections.swap(exercise_weights_list, fromPosition, toPosition);
            Collections.swap(exercise_dbmp_list, fromPosition, toPosition);


            PTA.notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            exercises_id_for_plan.remove(viewHolder.getAdapterPosition());
            exercises_name_list.remove(viewHolder.getAdapterPosition());
            exercise_reps_list.remove(viewHolder.getAdapterPosition());
            exercise_weights_list.remove(viewHolder.getAdapterPosition());
            exercise_dbmp_list.remove(viewHolder.getAdapterPosition());
            editable = false;
            PTA.notifyItemRemoved(viewHolder.getAdapterPosition());
            PTA.notifyItemRangeChanged(viewHolder.getAdapterPosition(), exercises_id_for_plan.size());
        }
    };

    public void changeReps(int index, String value) {
        int ex_id = Integer.parseInt(exercises_id_helper_list.get(index));
        for(int i = 0; i < exercises_id_for_plan.size(); i++) {
            if(String.valueOf(ex_id).equals(exercises_id_for_plan.get(i))) {
                exercise_reps_list.set(i, value);
            }
        }
    }

    public void changeWeights(int index, String value) {
        int actual_rep;
        int ex_id = Integer.parseInt(exercises_id_helper_list.get(index));
        for(int i = 0; i < exercises_id_for_plan.size(); i++) {
            if(String.valueOf(ex_id).equals(exercises_id_for_plan.get(i))) {
                exercise_weights_list.set(i, value);

                try {
                    actual_rep = Integer.parseInt(exercise_weights_list.get(i));

                    if(plan_type_state == 1) {
                        if(actual_rep/10 > 0) {
                            if(actual_rep - (actual_rep / 10) > 0) {
                                Toast.makeText(this, "Javaslat: Csökkentsd az ismétlést ennyivel: " + (actual_rep / 10), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Javaslat: Csökkentsd az ismétlést ennyire: 2", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        if(actual_rep - (actual_rep / 10 * 5) > 0) {
                            actual_rep = (actual_rep / 10) * 5;
                            if (actual_rep > 0) {
                                Toast.makeText(this, "Javaslat: Csökkentsd az intervallumot " + actual_rep + " másodperccel.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Javaslat: Csökkentsd az intervallumot 10 másodpercre.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e)
                {

                }
            }
        }

    }

    void loadRepsListAccordingToType() {
        int reps;
        int decrease = 0;
        int increase = 0;
        if(number_of_elements > 9) {
            decrease = number_of_elements - 9;
        }
        else if (number_of_elements < 5) {
            increase = 5 - number_of_elements;
        }
        if(plan_type_state == 1) {
            int exe_type, exe_lvl, user_lvl, exe_static, exe_hand;
            for (int i = 0; i < exercises_id_for_plan.size(); i++) {
                exe_type = exercise_type_list.get(i);
                exe_lvl = Integer.parseInt(exercise_level_list.get(i));
                user_lvl = Integer.parseInt(user_type_level_list.get(exe_type - 1));
                exe_static = Integer.parseInt(exercise_static.get(i));
                exe_hand = Integer.parseInt(exercise_onehand.get(i));
                reps = ph.getIfNeedReps(exe_type, exe_lvl, user_lvl, exe_static, exe_hand);
                if(reps - decrease > 1) reps = reps - decrease;
                reps = reps + increase;
                exercise_reps_list.add(String.valueOf(reps));
            }
        } else {
            int typeid, user_level;
            typeid = ph.mostCommon(exercise_type_list);
            user_level = getDatasFromUserLevel(typeid);
            reps = ph.getIfNeedIntervals(typeid, user_level);
            if(reps - (decrease * 5) > 1) reps = reps - (decrease * 5);
            reps = reps + increase;
            for(int i = 0; i < exercises_id_for_plan.size(); i++) {
                exercise_reps_list.add(String.valueOf(reps));
            }
        }
    }

    void loadWeightList() {
        for(int i = 0; i < exercise_weight.size(); i++){
            if(exercise_weight.get(i).equals("1"))  exercise_weights_list.add("-");
            else  exercise_weights_list.add("0");
        }
    }

    void getUserLevelsGroupByTypesAndPutInList() {
        Cursor c = dh.select("SELECT UserLevel FROM " + dh.USERLEVEL);
        if (c.getCount() == 0) {
        } else {
            while (c.moveToNext()) {
                user_type_level_list.add(c.getString(0));
            }
        }
    }

    int getDatasFromUserLevel(int typeid) {
        Cursor c = dh.select("SELECT UserLevel FROM " + dh.USERLEVEL + " WHERE typeID = " + typeid);
        if (c.getCount() == 0) {
        } else {
            while (c.moveToNext()) {
                return Integer.parseInt(c.getString(0));
            }
        }
        return 0;
    }

    void showCategoryAndPlace() {
        plan_place.setText(ph.loadNameToID(dh.PLACES, get_plan_place, 1));
        plan_category.setText(ph.loadNameToID(dh.CATEGORY, get_plan_category,1));
    }

    void putInListTheChosenExercisesFromPhaseTwo(String query) {
        Cursor c = dh.select(query);
        if (c.getCount() == 0) {
            //Toast.makeText(this, "Nincsenek gyakorlataid", Toast.LENGTH_SHORT).show();
        } else {
            while (c.moveToNext()) {
                exercises_id_for_plan.add(c.getString(0));
                exercises_id_helper_list.add(c.getString(0));//<-- segéd
                exercises_name_list.add(c.getString(1));
                exercise_onehand.add(c.getString(2));
                exercise_static.add(c.getString(3));
                exercise_weight.add(c.getString(4));
                exercise_level_list.add(c.getString(5));
                exercise_type_list.add(Integer.parseInt(c.getString(6)));
                //Típus - 6
            }
        }
    }

    String getChosenExercisesQuery() {
        String query = "SELECT * FROM Exercises WHERE";
        StringBuilder tmp = new StringBuilder();
        tmp.append(query);
        for(int i = 0; i < phase_two_chosen_exercises.size(); i++) {
            tmp.append(" ID = ").append(phase_two_chosen_exercises.get(i));
            if(i < phase_two_chosen_exercises.size() - 1) {
                tmp.append(" OR");
            }
        }
        tmp.append(";");
        query = tmp.toString();
        return query;
    }

    void getIntentData() {
        if(getIntent().hasExtra("getExercises") && getIntent().hasExtra("name") && getIntent().hasExtra("place_id") && getIntent().hasExtra("category_id")) {
            phase_two_chosen_exercises = getIntent().getStringArrayListExtra("getExercises");
            get_plan_name = getIntent().getStringExtra("name");
            get_plan_place = getIntent().getStringExtra("place_id");
            get_plan_category = getIntent().getStringExtra("category_id");
            plan_type_state = getIntent().getIntExtra("plan_type", 0);
            number_of_elements = phase_two_chosen_exercises.size();
        }
        else
        {
            Toast.makeText(this, "Nem sikerült átadni a kért adatokat", Toast.LENGTH_SHORT).show();
        }
    }
}