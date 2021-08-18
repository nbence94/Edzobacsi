package com.example.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import Adapterek.MainAdapter;
import Configurations.CategoryConfigure;
import Configurations.ExerciseConfigure;
import Configurations.PlacesConfigure;
import Configurations.PlansConfigure;
import Configurations.ToolsConfigure;
import HelperClasses.ConfigureHelper;
import HelperClasses.DatabaseHelper;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    DatabaseHelper dh;
    ConfigureHelper ch;

    BasicLoads BL;
    boolean loaded = false;

    RecyclerView recView;
    MainAdapter ma;
    ArrayList<String> plan_id_list, plan_name_list, plan_place_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.tBarX);
        recView = findViewById(R.id.recViewX_main);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.res_open, R.string.res_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        dh = new DatabaseHelper(MainActivity.this);
        ch = new ConfigureHelper(this);

        //Alap adatok feltöltése első alkalommal
        loadData();
        if(!loaded) {
            BL = new BasicLoads(MainActivity.this);
            saveData();
            Toast.makeText(this, "Fontos! Állítsd be az adataid.", Toast.LENGTH_SHORT).show();
        }

        //--
        plan_id_list = new ArrayList<>();
        plan_name_list = new ArrayList<>();
        plan_place_list = new ArrayList<>();
        getDatasIntoList();
        ma = new MainAdapter(MainActivity.this, this, plan_id_list, plan_name_list, plan_place_list);
        recView.setAdapter(ma);
        recView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_options).setVisible(false);

    }

    public void getDatasIntoList() {
        Cursor c = dh.select("SELECT * FROM " + dh.PLANS);
        if(c.getCount() == 0) {
            //Toast.makeText(this,"Nincsenek terveid", Toast.LENGTH_SHORT).show();
        }
        else
        {
            while(c.moveToNext()){
                plan_id_list.add(c.getString(0));
                plan_name_list.add(c.getString(1));
                ch.writeNameOrNothing(dh.PLACES, c.getString(5), plan_place_list);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            recreate();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.nav_plan:
                Intent planAct = new Intent(MainActivity.this, PlansConfigure.class);
                startActivityForResult(planAct, 1);
                break;

            case R.id.nav_places:
                Intent placeAct = new Intent(MainActivity.this, PlacesConfigure.class);
                startActivity(placeAct);
                break;

            case R.id.nav_tools:
                Intent tools = new Intent(MainActivity.this, ToolsConfigure.class);
                startActivity(tools);
                break;

            case R.id.nav_excercise:
                Intent excAct = new Intent(MainActivity.this, ExerciseConfigure.class);
                startActivity(excAct);
                break;

            case R.id.nav_category:
                Intent categoryAct = new Intent(MainActivity.this, CategoryConfigure.class);
                startActivity(categoryAct);
                break;

            case R.id.nav_details:
                Intent details = new Intent(MainActivity.this, UserDetails.class);
                startActivity(details);
                break;
        }
        return true;
    }

    public void saveData() {
        loaded = true;
        SharedPreferences sp = getSharedPreferences("load_check", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("key", loaded);
        edit.apply();
    }

    public void loadData() {
        SharedPreferences sp = getSharedPreferences("load_check", MODE_PRIVATE);
        boolean getData = sp.getBoolean("key", false);
        loaded = getData;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) item.getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ma.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}