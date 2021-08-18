package Configurations;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import HelperClasses.DatabaseHelper;
import InsertActivities.InsertCategory;

import HelperClasses.ConfigureHelper;
import com.example.Main.R;

import java.util.ArrayList;

import Adapterek.CategoryAdapter;

public class CategoryConfigure extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recView;
    DatabaseHelper dh;
    CategoryAdapter CA;
    ConfigureHelper ch;

    ArrayList<String> category_id_list, category_name_list, category_deletable, category_in_plans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_configure);

        //Vissza gomb
        toolbar = findViewById(R.id.toolbar_category_configure);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Alap elemek
        category_id_list = new ArrayList<>();
        category_name_list = new ArrayList<>();
        category_deletable = new ArrayList<>();
        category_in_plans = new ArrayList<>();

        recView = findViewById(R.id.recycler_category_configuration);
        dh = new DatabaseHelper(this);
        ch = new ConfigureHelper(CategoryConfigure.this);

        //Recycler View elemek
        ch.getIDsandNames(dh, dh.CATEGORY, "Nincsenek kategóráid", category_id_list, category_name_list);
        checkCategoriesForDeletableState();
        ch.getConnectsToList(dh.PLANS,"categoryID", category_id_list, category_in_plans);

        CA = new CategoryAdapter(CategoryConfigure.this,CategoryConfigure.this,
                                    category_id_list, category_name_list, category_deletable, category_in_plans);
        recView.setAdapter(CA);
        recView.setLayoutManager(new LinearLayoutManager(CategoryConfigure.this));
    }

    void checkCategoriesForDeletableState(){
        for(int i = 0; i < category_id_list.size(); i++) {
            if(ch.checkDeletable(dh,dh.PLANS,"categoryID", category_id_list.get(i))) category_deletable.add("1");
            else category_deletable.add("0");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            recreate();
        }
    }

    public void openInsertCategory(android.view.View v) {
        Intent open = new Intent(CategoryConfigure.this, InsertCategory.class);
        startActivityForResult(open, 1);
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
                CA.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}