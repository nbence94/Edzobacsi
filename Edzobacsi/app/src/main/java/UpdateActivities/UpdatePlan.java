package UpdateActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import HelperClasses.AlertDialogHelper;
import HelperClasses.ConfigureHelper;
import HelperClasses.PlanHelper;
import com.example.Main.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import Adapterek.PlanUpdateAdapter;

public class UpdatePlan extends AppCompatActivity {

    DatabaseHelper dh;
    PlanHelper ph;
    ConfigureHelper ch;
    AlertDialogHelper adh;

    //GUI elemek
    Toolbar toolbar;
    EditText plan_name;//UPDATE
    TextView plan_place_text, plan_category_text;
    EditText plan_note;//UPDATE
    RecyclerView recView;

    //Listák: Spinnerbe
    ArrayList<String> muscles_id_list;
    ArrayList<String> muscles_name_list;

    //Izomcsoportok
    String[] show_these_muscles_ad;
    boolean[] chosen_muscles_ad;
    ArrayList<String> chosen_muscles_id_list;// <--

    //A megnyitott terv adatai
    PlanUpdateAdapter PUA;
    int plan_id;//UPDATE
    int the_plan_category_id;//UPDATE
    int the_plan_place_id;//UPDATE
    int the_plan_type_state;//UPDATE
    ArrayList<Integer> the_plan_type_list;
    //Betöltött gyakorlat adatai
    ArrayList<String>   helper_exercise_id_list,
                        the_plan_exercise_id_list, //UPDATE (planandexe)
                        the_plan_exercise_name_list,
                        the_plan_exercise_weight_amount_list, //Mennyiség - UPDATE (planandexe)
                        the_plan_exercise_repeat_amount_list, //Mennyiség - UPDATE (planandexe)
                        the_plan_exercise_static_state_list,  //mp/db
                        the_plan_exercise_weight_state_list, //kell-nem kell
                        the_plan_exercise_dbmp_list;

    //Plusz Gyakorlatok hozzáadása
    ArrayList<String> plus_exercises_id_list;
    ArrayList<String> plus_exercises_name_list;
    String[] show_these_exercises;
    boolean[] chosen_exercises_boolean;
    public boolean editable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_plan);

        dh = new DatabaseHelper(UpdatePlan.this);
        ph = new PlanHelper(this);
        adh = new AlertDialogHelper();
        ch = new ConfigureHelper(this);

        //Visszagomb
        toolbar = findViewById(R.id.plan_update_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //GUI
        plan_name = findViewById(R.id.plan_name_update);
        plan_place_text = findViewById(R.id.plan_place_update);
        plan_category_text = findViewById(R.id.plan_category_spinner_update);
        plan_note = findViewById(R.id.plan_note_update);
        recView = findViewById(R.id.plan_update_recycler);

        //Listák Spinnerhez
        muscles_id_list = new ArrayList<>();
        muscles_name_list = new ArrayList<>();

        //Spinner-listák feltöltése
        ch.getIDsandNames(dh, dh.MUSCLE_GROUPS, "Nem talált izomcsoportokat",muscles_id_list, muscles_name_list);//Ez lehet nem is kell


        //Spinnerek feltöltése: Izomcsoportok (searchMuscles_update)
        chosen_muscles_ad = new boolean[muscles_id_list.size()];
        show_these_muscles_ad = new String[muscles_id_list.size()];
        show_these_muscles_ad = muscles_name_list.toArray(show_these_muscles_ad);
        chosen_muscles_id_list = new ArrayList<>();

        //A megnyitott terv adatai
        getIntentData();
        getPlanDetails();
        plan_place_text.setText(ph.loadNameToID(dh.PLACES, String.valueOf(the_plan_place_id), 1));
        plan_category_text.setText(ph.loadNameToID(dh.CATEGORY, String.valueOf(the_plan_category_id), 1));


        //Gyakorlatok betöltése
        the_plan_exercise_id_list = new ArrayList<>();
        the_plan_exercise_name_list = new ArrayList<>();
        the_plan_type_list = new ArrayList<>();
        the_plan_exercise_weight_amount_list = new ArrayList<>();
        the_plan_exercise_repeat_amount_list = new ArrayList<>();
        the_plan_exercise_static_state_list = new ArrayList<>();
        the_plan_exercise_dbmp_list = new ArrayList<>();
        the_plan_exercise_weight_state_list = new ArrayList<>();//<-- adapter mia

        ph.loadExerciseFromConnect(plan_id, the_plan_exercise_id_list, the_plan_exercise_name_list, the_plan_exercise_weight_amount_list, the_plan_exercise_repeat_amount_list, the_plan_exercise_static_state_list);
        helper_exercise_id_list = the_plan_exercise_id_list;
        System.out.println("Betöltés: " + helper_exercise_id_list);//TODO x
        loadExerciseDetailList();
        getThose(the_plan_type_list);

        //Recycler View adatok
        PUA = new PlanUpdateAdapter(UpdatePlan.this, this, the_plan_exercise_id_list, the_plan_exercise_name_list, the_plan_exercise_weight_amount_list, the_plan_exercise_repeat_amount_list, the_plan_exercise_dbmp_list);
        recView.setAdapter(PUA);
        recView.setLayoutManager(new LinearLayoutManager(UpdatePlan.this));

        ItemTouchHelper moving = new ItemTouchHelper(setItems);
        moving.attachToRecyclerView(recView);

        //Plusz gyakorlatok
        plus_exercises_id_list = new ArrayList<>();
        plus_exercises_name_list = new ArrayList<>();
    }

    public void doUpdateInPlanUpdate(android.view.View v) {
        boolean insertable = true;
        if(plan_name.getText().toString().equals("")) insertable = false;
        if(the_plan_exercise_id_list.size() < 1) insertable = false;

        for(int i = 0; i < the_plan_exercise_weight_amount_list.size(); i++) {
            if(the_plan_exercise_weight_amount_list.get(i).equals("") || the_plan_exercise_weight_amount_list.get(i).equals("-")) {
                insertable = false;
                Toast.makeText(this, "Nincs megadva minden súly", Toast.LENGTH_SHORT).show();
                break;
            }
            if(the_plan_exercise_repeat_amount_list.get(i).equals("")) {
                insertable = false;
                Toast.makeText(this, "Nincs megadva minden ismétlés", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        //Plusz ellenőrzés
        System.out.println("CSEKK: " + the_plan_exercise_weight_state_list);//TODO x
        System.out.println(the_plan_exercise_id_list.size()); //TODO  x
        for(int i = 0; i < the_plan_exercise_id_list.size(); i++) {
            int state = Integer.parseInt(ph.getTableSpecificDetail(Integer.parseInt(the_plan_exercise_id_list.get(i)), dh.EXERCISES,"Weight", "ID"));
            if(state == 0) {
                the_plan_exercise_weight_amount_list.set(i, "0");
            }
        }

        if(insertable) {
            boolean successDelete = dh.deleteConnect(dh.PLAN_AND_EXE, "planID", String.valueOf(plan_id));


            if(successDelete) {
                String name = plan_name.getText().toString();
                String note = plan_note.getText().toString();
                boolean successUpdate = dh.updatePlan(name, the_plan_type_state, note, the_plan_category_id, the_plan_place_id, String.valueOf(plan_id));
                boolean success = true;
                for(int i = 0; i < the_plan_exercise_id_list.size(); i++) {
                    success = dh.insertPlanConnect(plan_id, Integer.parseInt(the_plan_exercise_id_list.get(i)), Integer.parseInt(the_plan_exercise_weight_amount_list.get(i)), Integer.parseInt(the_plan_exercise_repeat_amount_list.get(i)), (i+1));
                    if(!success) break;
                }

                if (successUpdate && success) {
                    Toast.makeText(this, "Sikeres mentés", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Mentés sikertelen", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else {
            Toast.makeText(this, "Mentés sikertelen", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeReps(int index, String value) {

        int ex_id = Integer.parseInt(helper_exercise_id_list.get(index));
        System.out.println(value);
        for(int i = 0; i < the_plan_exercise_id_list.size(); i++) {
            if(String.valueOf(ex_id).equals(the_plan_exercise_id_list.get(i))) {
                the_plan_exercise_repeat_amount_list.set(i, value);
                System.out.println("HELPER ID: " + ex_id + " - ID: " + i + " INDEX: " + index);
                Debug(index);
            }
        }
    }

    public void changeWeights(int index, String value) {
        int ex_id = Integer.parseInt(helper_exercise_id_list.get(index));
        for(int i = 0; i < the_plan_exercise_id_list.size(); i++) {
            if(String.valueOf(ex_id).equals(the_plan_exercise_id_list.get(i)))
                the_plan_exercise_weight_amount_list.set(i, value);
                Debug(index);
        }
    }

    void loadExerciseDetailList() {
        for( int i = 0; i < the_plan_exercise_id_list.size(); i++) {
            if(the_plan_type_state == 0) {
                the_plan_exercise_dbmp_list.add("mp");
            }
            else {
                if (the_plan_exercise_static_state_list.get(i).equals("1")) the_plan_exercise_dbmp_list.add(" mp");
                else the_plan_exercise_dbmp_list.add(" db");
            }
        }
    }

    public void searchExercises_update(android.view.View v) {
        String idQuery = ph.createQueryForGoodExercises("DISTINCT ID", the_plan_place_id, the_plan_category_id, 0, chosen_muscles_id_list);
        String nameQuery = ph.createQueryForGoodExercises("DISTINCT Name", the_plan_place_id, the_plan_category_id, 0, chosen_muscles_id_list);
        System.out.println(idQuery);//debug

        plus_exercises_id_list.clear();
        plus_exercises_name_list.clear();

        ph.putQueryResultInList(idQuery, plus_exercises_id_list);
        ph.putQueryResultInList(nameQuery, plus_exercises_name_list);

        show_these_exercises = null;
        chosen_exercises_boolean = null;
//TODO Ezt kellene megcisnálni....
        //Olyan gyakorlatok kivonása a kínálati listából, amik már a tervben vannak
        for(int i = 0; i < the_plan_exercise_id_list.size(); i++) {
            String check = the_plan_exercise_id_list.get(i);

            for(int j = 0; j < plus_exercises_id_list.size(); j++) {
                if(check.equals(plus_exercises_id_list.get(j))) {
                    plus_exercises_id_list.remove(j);
                    plus_exercises_name_list.remove(j);
                }
            }
        }

        //Gyakorlatok megjelenítése
        show_these_exercises = new String[plus_exercises_id_list.size()];
        show_these_exercises = plus_exercises_name_list.toArray(show_these_exercises);
        chosen_exercises_boolean = new boolean[plus_exercises_id_list.size()];

        AlertDialog.Builder open = new AlertDialog.Builder(this);
        open.setTitle("Gyakorlatok hozzáadása");

        open.setMultiChoiceItems(show_these_exercises, chosen_exercises_boolean, (dialog, which, isChecked) -> {
            if(isChecked) the_plan_exercise_id_list.add(plus_exercises_id_list.get(which));
            else the_plan_exercise_id_list.remove(plus_exercises_id_list.get(which));
        });

        open.setPositiveButton("Rendben", (dialog, which) -> {
            int reps;
            int decrease = 0;
            int increase = 0;

            for(int i = 0; i < chosen_exercises_boolean.length; i++) {
                if(chosen_exercises_boolean[i]) {
                    the_plan_exercise_name_list.add(plus_exercises_name_list.get(i));
                    int number_of_elements = the_plan_exercise_id_list.size();
                    int exe_type = Integer.parseInt(ph.getTableSpecificDetail(Integer.parseInt(plus_exercises_id_list.get(i)), dh.EXERCISES,"Type", "ID"));
                    int exe_lvl = Integer.parseInt(ph.getTableSpecificDetail(Integer.parseInt(plus_exercises_id_list.get(i)), dh.EXERCISES,"Level", "ID"));
                    int exe_static =  Integer.parseInt(ph.getTableSpecificDetail(Integer.parseInt(plus_exercises_id_list.get(i)), dh.EXERCISES,"Static", "ID"));
                    int exe_hand =  Integer.parseInt(ph.getTableSpecificDetail(Integer.parseInt(plus_exercises_id_list.get(i)), dh.EXERCISES,"OneHand", "ID"));
                    int user_lvl = Integer.parseInt(ph.getTableSpecificDetail(exe_type, dh.USERLEVEL,"UserLevel", "typeID"));

                    if(number_of_elements > 9) {
                        decrease = number_of_elements - 9;
                    }
                    else if (number_of_elements < 5) {
                        increase = 5 - number_of_elements;
                    }
                    if(the_plan_type_state == 1) {
                        for (int j = 0; j < the_plan_exercise_id_list.size(); j++) {
                            reps = ph.getIfNeedReps(exe_type, exe_lvl, user_lvl, exe_static, exe_hand);
                            if(reps - decrease > 1) reps = reps - decrease;
                            reps = reps + increase;
                            the_plan_exercise_repeat_amount_list.add(String.valueOf(reps));
                        }
                    } else {
                        int typeid, user_level;
                        typeid = ph.mostCommon(the_plan_type_list);
                        user_level = getDatasFromUserLevel(typeid);

                        reps = ph.getIfNeedIntervals(typeid, user_level);
                        if(reps - (decrease * 5) > 1) reps = reps - (decrease * 5);
                        reps = reps + increase;
                        for(int j = 0; j < the_plan_exercise_id_list.size(); j++) {
                            the_plan_exercise_repeat_amount_list.add(String.valueOf(reps));
                        }
                    }

                    int exe_weight = Integer.parseInt(ph.getTableSpecificDetail(Integer.parseInt(plus_exercises_id_list.get(i)), dh.EXERCISES,"Weight", "ID"));
                    if(exe_weight == 1) {
                        the_plan_exercise_weight_amount_list.add("-");
                    }
                    else {
                        the_plan_exercise_weight_amount_list.add("0");
                    }

                    if(the_plan_type_state == 0) the_plan_exercise_dbmp_list.add("mp");
                    else{
                        if(exe_static == 1) {
                            the_plan_exercise_dbmp_list.add("mp");
                        }
                        else
                        {
                            the_plan_exercise_dbmp_list.add("db");
                        }
                    }

                }
            }
            System.out.println("Plusz: " + helper_exercise_id_list);//TODO x
            PUA.notifyItemInserted(the_plan_exercise_id_list.size());
        });
        open.show();
    }

    void Debug(int pos) {
        System.out.println("Index: " + pos );
        System.out.println("Helper: " + helper_exercise_id_list);
        System.out.println("ID: " + the_plan_exercise_id_list);

        System.out.println("Reps: " + the_plan_exercise_repeat_amount_list);
        System.out.println("Weight: " + the_plan_exercise_weight_amount_list);
    }

    public void searchMuscles_updatePlan(android.view.View v) {
        AlertDialog.Builder open = new AlertDialog.Builder(UpdatePlan.this);
        open.setTitle("Izomcsoportok");
        open.setCancelable(false);

        open.setMultiChoiceItems(show_these_muscles_ad, chosen_muscles_ad, (dialog, which, isChecked) -> {
            if(isChecked) chosen_muscles_id_list.add(muscles_id_list.get(which));
            else chosen_muscles_id_list.remove(muscles_id_list.get(which));
            Collections.sort(chosen_muscles_id_list);
        });

        open.setPositiveButton("Rendben", (dialog, which) -> {
            String result = adh.choseItems(chosen_muscles_id_list, show_these_muscles_ad, chosen_muscles_ad);
            System.out.println("Választott izomcsoportok: " + result);
        });

        open.setNeutralButton("Ürít", (dialog, which) -> {
            Arrays.fill(chosen_muscles_ad, false);
            chosen_muscles_id_list.clear();
        });
        open.show();
    }

    ItemTouchHelper.SimpleCallback setItems = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT ) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(the_plan_exercise_id_list, fromPosition, toPosition);
            Collections.swap(the_plan_exercise_name_list, fromPosition, toPosition);
            Collections.swap(the_plan_exercise_repeat_amount_list, fromPosition, toPosition);
            Collections.swap(the_plan_exercise_weight_amount_list, fromPosition, toPosition);
            Collections.swap(the_plan_exercise_dbmp_list, fromPosition, toPosition);
            PUA.notifyItemMoved(fromPosition, toPosition);
            System.out.println("Csere: ");
            Debug(fromPosition);
            System.out.println("Plusz: " + helper_exercise_id_list);//TODO x
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                the_plan_exercise_id_list.remove(viewHolder.getAdapterPosition());
                the_plan_exercise_name_list.remove(viewHolder.getAdapterPosition());
                the_plan_exercise_repeat_amount_list.remove(viewHolder.getAdapterPosition());
                the_plan_exercise_weight_amount_list.remove(viewHolder.getAdapterPosition());
                the_plan_exercise_dbmp_list.remove(viewHolder.getAdapterPosition());
                editable = false;
                PUA.notifyItemRemoved(viewHolder.getAdapterPosition());
                System.out.println("Törlés: ");
                Debug(viewHolder.getAdapterPosition());
        }
    };

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

    void getThose(ArrayList<Integer> putInThis){
        for(int i = 0; i < the_plan_exercise_name_list.size(); i++){
            putInThis.add(getTypes(the_plan_exercise_id_list.get(i)));
        }
    }

    int getTypes(String id) {
        Cursor c = dh.select("SELECT Type FROM " + dh.EXERCISES + " WHERE ID = " + id);
        if (c.getCount() == 0) {
        } else {
            while (c.moveToNext()) {
                return Integer.parseInt(c.getString(0));
            }
        }
        return 0;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            recreate();
        }
    }

    void getPlanDetails() {
        Cursor c = dh.select("SELECT * FROM " + dh.PLANS + " WHERE ID=" + plan_id);
        if(c.getCount() == 0) {
            Toast.makeText(this,"Nincsenek terveid", Toast.LENGTH_SHORT).show();
        }
        else
        {
            while(c.moveToNext()){
                plan_name.setText(c.getString(1));
                the_plan_place_id = Integer.parseInt(c.getString(5));
                the_plan_category_id = Integer.parseInt(c.getString(4));
                plan_note.setText(c.getString(3));
                the_plan_type_state = Integer.parseInt(c.getString(2));
            }
        }
    }

    void getIntentData() {
        if(getIntent().hasExtra("ID")) {
            plan_id = Integer.parseInt(getIntent().getStringExtra("ID"));
        }
        else
        {
            Toast.makeText(this, "Nem lehetett átölteni az adatokat", Toast.LENGTH_SHORT).show();
        }
    }
}