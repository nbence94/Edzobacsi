package UpdateActivities;

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
import SelectionActivities.ToolsForPlaces;

public class UpdatePlace extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseHelper dh;
    AlertDialogHelper adh;
    EditText place_name;
    ListView tools_listview;
    TextView title;

    String place_id = "";// Ez alapján UPDATE
    ArrayList<String> tools_name_list, tools_id_list, chosen_tools_id_list;


    boolean[] chosen_tools;
    String[] show_these_tools;
    ArrayAdapter<String> listView_adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_place);

        //Vissza gomb
        toolbar = findViewById(R.id.toolbar_update_place);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Alap inicializálás
        dh = new DatabaseHelper(UpdatePlace.this);
        adh = new AlertDialogHelper();

        place_name = findViewById(R.id.name_update_place);
        title = findViewById(R.id.chosen_exercises_category_upgade);
        title.setVisibility(View.GONE);


        tools_name_list = new ArrayList<>();
        tools_id_list = new ArrayList<>();
        chosen_tools_id_list = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.TOOLS, tools_id_list, tools_name_list);

        //AlertDialoghoz
        show_these_tools = new String[tools_name_list.size()];
        chosen_tools = new boolean[tools_name_list.size()];
        show_these_tools = tools_name_list.toArray(show_these_tools);

        //Megjeleníteni a kiválasztott elemeket list view-ban
        tools_listview = findViewById(R.id.tool_lview_update_place);
        listView_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.elements_to_show);
        tools_listview.setAdapter(listView_adapter);

        getIntentData();
        adh.loadListView(listView_adapter, chosen_tools_id_list, tools_name_list, title);
        setResult(1);
    }

    public void doUpdateInPlaceUpdate(android.view.View V) {
        boolean updateable = true;
        if(place_name.getText().toString().equals("")) {
            updateable = false;
        }

        if(updateable){
            //Hely módosítása
            if(dh.update(dh.PLACES, "Name", place_name.getText().toString(), "ID", place_id)) {
                //Kapcsolatok törlése
                if(dh.deleteConnect(dh.PLACE_AND_TOOLS,"placeID", place_id))
                {
                    //Kapcsolatok újra töltése
                    if(chosen_tools_id_list.size() > 0) {
                        for (int i = 0; i < chosen_tools_id_list.size(); i++) {
                                dh.insertConnectTable(dh.PLACE_AND_TOOLS, "placeID", Integer.parseInt(place_id), "toolID", Integer.parseInt(chosen_tools_id_list.get(i)));
                        }
                    }
                    finish();
                    Toast.makeText(this, "Sikeres módosítás", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this,"Sikertelen módosítás", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this,"Sikertelen módosítás", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this,"Adj nevet a helyszínnek", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchTools(android.view.View V) {
        Intent open = new Intent(UpdatePlace.this, ToolsForPlaces.class);
        open.putStringArrayListExtra("chosen_tools", chosen_tools_id_list);
        open.putExtra("id", place_id);
        open.putExtra("name", place_name.getText().toString());
        startActivityForResult(open, 1);
        finish();
    }

    void getIntentData() {
        if(getIntent().hasExtra("id") || getIntent().hasExtra("name")) {
            place_id = getIntent().getStringExtra("id");
            place_name.setText(getIntent().getStringExtra("name"));
        }
        else
        {
            Toast.makeText(this, "Nem sikerült átadni a kért adatokat", Toast.LENGTH_SHORT).show();
        }
        if(getIntent().hasExtra("chosen_tools")) {
            chosen_tools_id_list = getIntent().getStringArrayListExtra("chosen_tools");
        } else {
            chosen_tools_id_list = new ArrayList<>();
            adh.getDatasFromConnect(dh, dh.PLACE_AND_TOOLS, "placeID", place_id, chosen_tools_id_list,1);
        }
    }
}