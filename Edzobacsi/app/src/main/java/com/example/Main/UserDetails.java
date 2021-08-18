package com.example.Main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import Adapterek.UserAdapter;
import HelperClasses.ConfigureHelper;
import HelperClasses.DatabaseHelper;

public class UserDetails extends AppCompatActivity {

    RecyclerView recView;
    DatabaseHelper dh;
    Toolbar toolbar;
    UserAdapter ua;
    ImageView info;

    ArrayList<String> type_id_list, type_name_list, user_level_list;
    int number_of_levels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        //Vissza gomb
        toolbar = findViewById(R.id.userDatas_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dh = new DatabaseHelper(UserDetails.this);
        recView  = findViewById(R.id.user_details_rView);

        //Listák
        type_id_list = new ArrayList<>();
        type_name_list = new ArrayList<>();
        user_level_list = new ArrayList<>();

        putTypesInList();
        number_of_levels = getLevelsCount();

        //Megjelenítés
        ua = new UserAdapter(UserDetails.this, type_id_list, type_name_list, user_level_list, number_of_levels);
        recView.setAdapter(ua);
        recView.setLayoutManager(new LinearLayoutManager(UserDetails.this));

        info = findViewById(R.id.info_btn_user_details);
        info.setOnClickListener(v -> {
            AlertDialog.Builder open = new AlertDialog.Builder(this);
            open.setTitle("Hogyan állítsd be?");
            open.setMessage("Fontos tudni, hogy az erőszintek bármikor módosíthatók! Próbáld megtalálni a neked megfelelő szinteket 1-5 pont között. \n\n ");
            open.show();
        });

    }

    public void doSave(android.view.View v) {
        boolean success = true;
        for(int i = 0; i < type_id_list.size(); i++) {
            success = dh.update(dh.USERLEVEL, "UserLevel", user_level_list.get(i), "typeID", type_id_list.get(i));
            if(!success) break;
        }
        if(!success) Toast.makeText(this, "Sikertelen mentés", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Mentés sikeres", Toast.LENGTH_SHORT).show();
    }

    public void getTypeLevels(View v, int index, String new_value) {
        user_level_list.set(index, new_value);
    }

    void putTypesInList() {
        Cursor c = dh.select("SELECT * FROM " + dh.TYPES);
        if(c.getCount() == 0) {
            Toast.makeText(this,"Hoppá! Nincsenek típusok", Toast.LENGTH_SHORT).show();
        }
        else
        {
            while(c.moveToNext()){
                type_id_list.add(c.getString(0));
                type_name_list.add(c.getString(1));
                getNameForID(dh, dh.USERLEVEL, c.getString(0), "Hiba", user_level_list);
            }
        }
    }

    public void getNameForID(DatabaseHelper dh, String table, String ID, String error_msg, ArrayList<String> list) {
        Cursor c = dh.select("SELECT * FROM "+ table +" WHERE typeID =" + ID + ";");
        if(c.getCount() == 0) {
            Toast.makeText(this, error_msg, Toast.LENGTH_SHORT).show();
        }
        else
        {
            while(c.moveToNext()){
                list.add(c.getString(1));
            }
        }
        c.close();
    }

    int getLevelsCount() {
        int amount = 1;
        Cursor c = dh.select("SELECT COUNT(*) FROM " + dh.LEVELS);
        if(c.getCount() == 0) {
            Toast.makeText(this,"Hoppá! Nincsenek szintek", Toast.LENGTH_SHORT).show();
        }
        else
        {
            while(c.moveToNext()){
                amount = Integer.parseInt(c.getString(0));
            }
        }
        return amount;
    }
}