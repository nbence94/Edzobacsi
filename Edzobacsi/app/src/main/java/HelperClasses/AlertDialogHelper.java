package HelperClasses;

import android.database.Cursor;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AlertDialogHelper {

    public void loadChoseableListsForSpinners(DatabaseHelper dh, String table, ArrayList<String> full_id_list, ArrayList<String> full_name_list) {
        Cursor c = dh.select("SELECT * FROM " + table);
        if(c.getCount() != 0) {
            while(c.moveToNext()) {
                full_id_list.add(c.getString(0));
                full_name_list.add(c.getString(1));
            }
        }
        c.close();
    }

    public void getDatasFromConnect( DatabaseHelper dh, String table,
                                     String where_column,
                                     String where_value,
                                     ArrayList<String> chosen_id_list,
                                     int index) {

        Cursor c = dh.select("SELECT * FROM " + table + " WHERE " + where_column + "=" + where_value + ";");
        if(c.getCount() == 0) {
        }
        else
        {
            while(c.moveToNext()) {
                chosen_id_list.add(c.getString(index));
            }
        }
        c.close();
    }

    public void checkAndWriteChosenItems(ArrayList<String> chosen_id_list,
                                         ArrayList<String> full_id_list,
                                         ArrayList<String> full_name_list,
                                         boolean[] chosen_boolean_array,
                                         TextView spinner_text) {
        StringBuilder write = new StringBuilder();
        int actual_id;

        for(int i = 0; i < chosen_id_list.size(); i++) {
            actual_id = Integer.parseInt(chosen_id_list.get(i));

            for(int j = 0; j < full_id_list.size(); j++) {
                if(String.valueOf(actual_id).equals(full_id_list.get(j))) {
                    chosen_boolean_array[j] = true;
                    write.append(full_name_list.get(j));
                    if(i < chosen_id_list.size() - 1 ) write.append(", ");
                }
            }
        }
        spinner_text.setText(write.toString());
    }

    public String choseItems(ArrayList<String> chosen_id_list, String[] item_names, boolean[] chosen_boolean_array) {
        StringBuilder sb = new StringBuilder();
        int x = 0;
        for(int i = 0; i < chosen_boolean_array.length; i++) {
            if(chosen_boolean_array[i]) {
                sb.append(item_names[i]);
                if (x < chosen_id_list.size() - 1) {
                    sb.append(", ");
                }
                x++;
            }
        }
        return sb.toString();
    }

    //Place, Tools, Category
    public void loadListView(ArrayAdapter<String> arrayadapter, ArrayList<String> id_list, ArrayList<String> name_list, TextView title) {
        arrayadapter.clear();
        if(id_list.size() > 0) title.setVisibility(View.VISIBLE);
        else title.setVisibility(View.GONE);
        int bottom = 0;
        for(int i = 0; i < id_list.size(); i++){
            arrayadapter.insert(name_list.get(Integer.parseInt(id_list.get(i))-1), bottom++);
        }
    }

}
