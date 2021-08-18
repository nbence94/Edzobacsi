package InsertActivities;

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

public class InsertPlace extends AppCompatActivity {

    DatabaseHelper dh;
    AlertDialogHelper adh;
    //Intent elemek
    Toolbar toolbar;
    EditText place_name;
    TextView title;
    ListView lView = null;

    //Adatok tárolása
    ArrayList<String> tool_id_list, tool_name_list, chosen_tool_id_list;

    //AlertDialoghoz
    String[] show_these_tools;
    boolean[] chosen_tools;
    ArrayAdapter<String> listView_adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_place);

        dh = new DatabaseHelper(InsertPlace.this);
        adh = new AlertDialogHelper();

        //Vissza gomb
        toolbar = findViewById(R.id.toolbar_insert_place);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        place_name = findViewById(R.id.name_insert_place);
        title = findViewById(R.id.chosentools_title);
        title.setVisibility(View.GONE);

        //Listák
        tool_id_list = new ArrayList<>();
        tool_name_list = new ArrayList<>();
        chosen_tool_id_list = new ArrayList<>();
        adh.loadChoseableListsForSpinners(dh, dh.TOOLS, tool_id_list, tool_name_list);

        //AlertDialoghoz
        show_these_tools = new String[tool_id_list.size()];
        chosen_tools = new boolean[tool_id_list.size()];
        show_these_tools = tool_name_list.toArray(show_these_tools);

        //Megjeleníteni a kiválasztott elemeket list view-ban
        lView = findViewById(R.id.tool_lview_insert_place);
        listView_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.elements_to_show);
        lView.setAdapter(listView_adapter);
        getIntentData();
        adh.loadListView(listView_adapter, chosen_tool_id_list, tool_name_list, title);
        setResult(1);
    }

    public void doInsert(android.view.View v) {
        boolean insertable = true;
        if(place_name.getText().toString().equals("")) {
            insertable = false;
        }

        if(insertable) {
            boolean insertSuccess = dh.insert("Name", place_name.getText().toString(), dh.PLACES);
            if(insertSuccess) {
                if (chosen_tool_id_list.size() > 0) {
                    int new_place_id = dh.getTheNewID(dh.PLACES);
                    for (int i = 0; i < chosen_tool_id_list.size(); i++) {
                        dh.insertConnectTable(dh.PLACE_AND_TOOLS, "placeID", new_place_id,
                                            "toolID", Integer.parseInt(chosen_tool_id_list.get(i)));
                    }
                }
                finish();
                Toast.makeText(this, "Sikeres mentés", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Sikertelen mentés", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Adj nevet a helyszínnek", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchTools(android.view.View V) {
        Intent open = new Intent(InsertPlace.this, ToolsForPlaces.class);
        open.putStringArrayListExtra("chosen_tools", chosen_tool_id_list);
        open.putExtra("name", place_name.getText().toString());
        startActivityForResult(open, 1);
        finish();
    }

    void getIntentData() {
        if (getIntent().hasExtra("chosen_tools")) {
            chosen_tool_id_list = getIntent().getStringArrayListExtra("chosen_tools");
            place_name.setText(getIntent().getStringExtra("name"));
        }
    }
}