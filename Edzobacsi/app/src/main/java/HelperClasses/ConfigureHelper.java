package HelperClasses;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;

public class ConfigureHelper {

    Context context;
    DatabaseHelper dh;

    public ConfigureHelper(Context context) {
        this.context = context;
        dh = new DatabaseHelper(context);
    }

    public void getIDsandNames(DatabaseHelper dh, String table, String error_message, ArrayList<String> id_list, ArrayList<String> name_list) {
        Cursor c = dh.select("SELECT * FROM " + table);
        if(c.getCount() == 0) {
            Toast.makeText(context,error_message, Toast.LENGTH_SHORT).show();
        }
        else
        {
            while(c.moveToNext()){
                id_list.add(c.getString(0));
                name_list.add(c.getString(1));
            }
        }
        c.close();
    }

    public void getConnectsToList(String table, String column, ArrayList<String> id_list, ArrayList<String> put_in_this){
        for(int i = 0; i < id_list.size(); i++) {
            put_in_this.add(String.valueOf(getConnectCount(dh, table, column, id_list.get(i))));
        }
    }

    public int getConnectCount(DatabaseHelper dh, String table, String column, String id) {
        int check_number = 0;
        Cursor c = dh.select("SELECT COUNT(*) FROM " + table + " WHERE " + column + "=" + id);
        if(c.getCount() != 0) {
            while (c.moveToNext()) {
                check_number = Integer.parseInt(c.getString(0));
            }
        }
        c.close();
        return check_number;
    }


    public boolean checkDeletable(DatabaseHelper dh, String table, String column, String id) {
        boolean deletable = true;
        Cursor c = dh.select("SELECT * FROM " + table + " WHERE " + column + "=" + id);
        if(c.getCount() == 0) {
            deletable = true;
        }
        else
        {
            while(c.moveToNext()){
                deletable = false;
            }
        }
        c.close();
        return deletable;
    }

    public void writeNameOrNothing(String table, String ID, ArrayList<String> name_list) {
        Cursor c = dh.select("SELECT * FROM "+ table+" WHERE ID=" + ID + ";");
        if(c.getCount() == 0) {
            name_list.add("-");
        }
        else
        {
            while(c.moveToNext()){
                name_list.add(c.getString(1));
            }
        }
        c.close();
    }

    public void getNameForID(DatabaseHelper dh, String table, String ID, String error_msg, ArrayList<String> list) {
        Cursor c = dh.select("SELECT * FROM "+ table +" WHERE ID=" + ID + ";");
        if(c.getCount() == 0) {
            Toast.makeText(context, error_msg, Toast.LENGTH_SHORT).show();
        }
        else
        {
            while(c.moveToNext()){
                list.add(c.getString(1));
            }
        }
        c.close();
    }

}
