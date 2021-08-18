package com.example.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import Adapterek.PlanDetailAdapter;
import HelperClasses.DatabaseHelper;
import HelperClasses.PlanHelper;

public class PlanDetails extends AppCompatActivity {

    DatabaseHelper dh;
    PlanHelper ph;
    PlanDetailAdapter pda;

    Toolbar toolbar;
    String plan_id, place_id, category_id;
    TextView plan_name, plan_place, plan_category;
    EditText plan_note;
    int plan_type;

    RecyclerView recView;
    ArrayList<String> exercise_id_list, exercise_name_list, exercise_weight_list, exercise_repeat_list, exercise_repeat_type_list;
    ArrayList<String> exercise_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_details);

        dh = new DatabaseHelper(PlanDetails.this);
        ph = new PlanHelper(this);

        //Vissza gomb
        toolbar = findViewById(R.id.detailsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        plan_name = findViewById(R.id.planName_detail);
        plan_place = findViewById(R.id.planPlace_detail);
        plan_category = findViewById(R.id.planCategory_detail);
        plan_note = findViewById(R.id.plan_detail_note);

        getIntentData();
        getPlanDetails();
        plan_place.setText(ph.loadNameToID(dh.PLACES, place_id, 1));
        plan_category.setText(ph.loadNameToID(dh.CATEGORY, category_id, 1));

        //Gyakorlatok betöltése
        exercise_id_list = new ArrayList<>();
        exercise_name_list = new ArrayList<>();
        exercise_weight_list = new ArrayList<>();
        exercise_repeat_list = new ArrayList<>();
        exercise_repeat_type_list = new ArrayList<>();
        exercise_details = new ArrayList<>();

        ph.loadExerciseFromConnect(Integer.parseInt(plan_id), exercise_id_list, exercise_name_list, exercise_weight_list, exercise_repeat_list, exercise_repeat_type_list);
        loadExerciseDetailList();

        //Recycler View adatok
        recView = findViewById(R.id.rec_view_details);
        pda = new PlanDetailAdapter(this, exercise_id_list, exercise_name_list, exercise_details);
        recView.setAdapter(pda);
        recView.setLayoutManager(new LinearLayoutManager(PlanDetails.this));
    }

    void loadExerciseDetailList() {
        StringBuilder write = new StringBuilder();
        for( int i = 0; i < exercise_id_list.size(); i++) {
            if (!exercise_weight_list.get(i).equals("0")) {
                write.append(exercise_weight_list.get(i)).append(" kg - ");
            }
            write.append(exercise_repeat_list.get(i));
            if(plan_type == 0) {
                write.append(" mp");
            }
            else {
                if (exercise_repeat_type_list.get(i).equals("1")) write.append(" mp");
                else write.append(" db");
            }
            exercise_details.add(write.toString());
            write.setLength(0);
        }
    }

    void getPlanDetails() {
        Cursor c = dh.select("SELECT * FROM " + dh.PLANS + " WHERE ID =" + plan_id);
        if(c.getCount() == 0) {
            Toast.makeText(this,"Nincs ilyen terv", Toast.LENGTH_SHORT).show();
        }
        else
        {
            while(c.moveToNext()){
                plan_name.setText(c.getString(1));
                plan_type = Integer.parseInt(c.getString(2));
                plan_note.setText(c.getString(3));
                category_id = c.getString(4);
                place_id = c.getString(5);
            }
        }
    }

    void getIntentData() {
        if(getIntent().hasExtra("ID")) {
            plan_id = getIntent().getStringExtra("ID");
        }
        else
        {
            Toast.makeText(this, "Nem lehetett átölteni az adatokat", Toast.LENGTH_SHORT).show();
        }
    }
}