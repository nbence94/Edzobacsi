package HelperClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    public final String CATEGORY = "Categories";
    public final String MUSCLE_GROUPS = "Muscles";
    public final String EXERCISES= "Exercises";
    public final String EXE_AND_CATE = "CategoryAndExercises";
    public final String EXE_AND_MG = "ExerciseAndMuscles";
    public final String LEVELS = "Levels";
    public final String TYPES = "Type";
    public final String TOOLS = "Tools";
    public final String TOOL_AND_EXE = "ToolsAndExercises";
    public final String PLACES = "Places";
    public final String PLACE_AND_TOOLS = "PlaceAndTools";
    public final String PLANS = "Plans";
    public final String PLAN_AND_EXE = "PlanAndExercises";
    public final String REPEATS = "RepeatValues";
    public final String INTERVALS = "Intervals";
    public final String USERLEVEL = "UserLevels";

    public DatabaseHelper(@Nullable Context context) {
        super(context, "Edzobacsi.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCategory = "CREATE TABLE " + CATEGORY +" (ID INTEGER Primary Key AUTOINCREMENT, Name TEXT);";
        db.execSQL(createCategory);
        String createLevels = "CREATE TABLE " + LEVELS +" (ID INTEGER Primary Key AUTOINCREMENT, Name TEXT);";
        db.execSQL(createLevels);
        String createMuscleGroups = "CREATE TABLE " + MUSCLE_GROUPS +" (ID INTEGER Primary Key AUTOINCREMENT, Name TEXT);";
        db.execSQL(createMuscleGroups);
        String createTypes = "CREATE TABLE " +  TYPES + " (ID INTEGER Primary Key AUTOINCREMENT, Name TEXT);";
        db.execSQL(createTypes);
        String createUserLevel = "CREATE TABLE " +  USERLEVEL + " (typeID INTEGER Primary Key, UserLevel INTEGER, FOREIGN KEY (typeID) REFERENCES " +  TYPES + " (ID), FOREIGN KEY (UserLevel) REFERENCES " + LEVELS + " (ID));";
        db.execSQL(createUserLevel);

        String createExercises = "CREATE TABLE " + EXERCISES + " (ID INTEGER Primary Key AUTOINCREMENT, Name TEXT, OneHand INTEGER, Static INTEGER, Weight INTEGER, Level INTEGER, Type INTEGER, FOREIGN KEY (Type) REFERENCES " +  TYPES + " (ID),  FOREIGN KEY (Level) REFERENCES " + LEVELS + " (ID));";
        db.execSQL(createExercises);
        String createCategoryAndExercises = "CREATE TABLE " + EXE_AND_CATE + " (categoryID INTEGER, exerciseID INTEGER, PRIMARY KEY (categoryID, exerciseID), FOREIGN KEY (categoryID) REFERENCES " + CATEGORY + " (ID), FOREIGN KEY (exerciseID) REFERENCES "+ EXERCISES + " (ID));";
        db.execSQL(createCategoryAndExercises);
        String createExeAndMg = "CREATE TABLE " + EXE_AND_MG + " (exerciseID INTEGER, musclegroupID INTEGER, PRIMARY KEY (exerciseID, musclegroupID), FOREIGN KEY (exerciseID) REFERENCES " + EXERCISES +" (ID), FOREIGN KEY (musclegroupID) REFERENCES " +  MUSCLE_GROUPS +" (ID));";
        db.execSQL(createExeAndMg);
        String createTools = "CREATE TABLE " + TOOLS + " (ID INTEGER Primary Key AUTOINCREMENT, Name TEXT);";
        db.execSQL(createTools);
        String createToolAndExes = "CREATE TABLE " + TOOL_AND_EXE + " (toolID INTEGER, exerciseID INTEGER, PRIMARY KEY (toolID, exerciseID), FOREIGN KEY (toolID) REFERENCES " + TOOLS + " (ID), FOREIGN KEY (exerciseID) REFERENCES " + EXERCISES + " (ID));";
        db.execSQL(createToolAndExes);
        String createPlaces = "CREATE TABLE " + PLACES + " (ID INTEGER Primary Key AUTOINCREMENT, Name TEXT);";
        db.execSQL(createPlaces);
        String createPlaceAndTools = "CREATE TABLE " + PLACE_AND_TOOLS + " (placeID INTEGER, toolID INTEGER, PRIMARY KEY (placeID, toolID), FOREIGN KEY (placeID) REFERENCES " + PLACES + " (ID), FOREIGN KEY (toolID) REFERENCES " + TOOLS + " (ID));";
        db.execSQL(createPlaceAndTools);

        String createPlans = "CREATE TABLE " + PLANS + " (ID INTEGER Primary Key AUTOINCREMENT, Name TEXT, Type INTEGER, Note TEXT, categoryID INTEGER, placeID INTEGER);";
        db.execSQL(createPlans);
        String createPlanAndExercies = "CREATE TABLE " + PLAN_AND_EXE + " (planID INTEGER NOT NULL, exerciseID INTEGER NOT NULL, Weight INTEGER, Repeat INTEGER, OrderNum INTEGER, Primary Key (planId, exerciseID), FOREIGN KEY (planID) REFERENCES " + PLANS + " (ID), FOREIGN KEY (exerciseID) REFERENCES " + EXERCISES + " (ID));";
        db.execSQL(createPlanAndExercies);

        String createRepeats = "CREATE TABLE " + REPEATS + " (typeID INTEGER, UserLevel INTEGER, ExerciseLevel INTEGER, RepeatAmount INTEGER, StaticTime INTEGER," +
                "PRIMARY KEY (typeID, UserLevel, ExerciseLevel)," +
                "FOREIGN KEY (typeID) REFERENCES " + TYPES + " (ID),  " +
                "FOREIGN KEY (UserLevel) REFERENCES " + LEVELS +  " (ID)," +
                "FOREIGN KEY (ExerciseLevel) REFERENCES " + LEVELS +  " (ID)" +
                ");";
        db.execSQL(createRepeats);

        String createIntervals = "CREATE TABLE " + INTERVALS + "(typeID INTEGER, UserLevel INTEGER, Time INTEGER,"+
                " PRIMARY KEY (typeID, UserLevel)," +
                " FOREIGN KEY (typeID) REFERENCES " + TYPES + " (ID),  " +
                " FOREIGN KEY (UserLevel) REFERENCES " + LEVELS +  " (ID)"
                + " );";
        db.execSQL(createIntervals);

    }

    public void drop() {
            context.deleteDatabase("Edzoba.db");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + MUSCLE_GROUPS);
        db.execSQL("DROP TABLE IF EXISTS " + EXERCISES);
        db.execSQL("DROP TABLE IF EXISTS " + EXE_AND_CATE);

        db.execSQL("DROP TABLE IF EXISTS " + EXE_AND_MG);
        db.execSQL("DROP TABLE IF EXISTS " + LEVELS);
        db.execSQL("DROP TABLE IF EXISTS " + TYPES);
        db.execSQL("DROP TABLE IF EXISTS " + TOOLS);

        db.execSQL("DROP TABLE IF EXISTS " + TOOL_AND_EXE);
        db.execSQL("DROP TABLE IF EXISTS " + PLACES);
        db.execSQL("DROP TABLE IF EXISTS " + PLACE_AND_TOOLS);
        db.execSQL("DROP TABLE IF EXISTS " + PLANS);

        db.execSQL("DROP TABLE IF EXISTS " + PLAN_AND_EXE);
        db.execSQL("DROP TABLE IF EXISTS " + REPEATS);
        db.execSQL("DROP TABLE IF EXISTS " + INTERVALS);
        db.execSQL("DROP TABLE IF EXISTS " + USERLEVEL);
        onCreate(db);
    }

    //Repeats
     public void insertRepeats(int tid, int ulevel, int elevel, int rAmount, int sTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("typeID", tid);
        cv.put("UserLevel", ulevel);
        cv.put("ExerciseLevel", elevel);
        cv.put("RepeatAmount", rAmount);
        cv.put("StaticTime", sTime);
        long result = db.insert(REPEATS,null, cv);
        if(result == -1) {
            Log.d("Hiba","Adat feltöltése NEM sikerült! (Repeats)");
        }
        else
        {
            Log.d("Sikeres","Adat feltöltése sikeres! (Repeats)");
        }
     }

    public void insertIntervals(int tid, int ulevel, int time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("typeID", tid);
        cv.put("UserLevel", ulevel);
        cv.put("Time", time);
        long result = db.insert(INTERVALS,null, cv);
        if(result == -1) {
            Log.d("Hiba","Adat feltöltése NEM sikerült! (Intervals)");
        }
        else
        {
            Log.d("Sikeres","Adat feltöltése sikeres! (Intervals)");
        }
    }

    //Tervek módosítása
    public boolean updatePlan(String plan_name, int type, String note, int categoryid, int placeid, String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Name", plan_name);
        cv.put("Type", type);
        cv.put("Note", note);
        cv.put("categoryID", categoryid);
        cv.put("placeID", placeid);
        long result = db.update(PLANS, cv,"ID=?", new String[] {row_id});
        if(result == -1) {
            Log.d("Hiba","Adat módosítása NEM sikerült! (Plan)");
            return false;
        }
        else
        {
            Log.d("Sikeres","Adat módosítása sikeres! (Plan)");
            return true;
        }
    }

    //Tervek feltöltés
    public boolean insertPlanConnect(int planid, int exerciseid, int weight, int repeat, int ordernum) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("planID", planid);
        cv.put("exerciseID", exerciseid);
        cv.put("Weight", weight);
        cv.put("Repeat", repeat);
        cv.put("OrderNum", ordernum);
        long result = db.insert(PLAN_AND_EXE,null, cv);
        if(result == -1) {
            Log.d("Hiba","Adat feltöltése NEM sikerült! (PlanAndExercises)");
            return false;
        }
        else
        {
            Log.d("Sikeres","Adat feltöltése sikeres! (PlanAndExercises)");
            return true;
        }
    }

    public boolean insertPlan(String plan_name, int type, String note, int categoryid, int placeid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Name", plan_name);
        cv.put("Type", type);
        cv.put("Note", note);
        cv.put("categoryID", categoryid);
        cv.put("placeID", placeid);
        long result = db.insert(PLANS,null, cv);
        if(result == -1) {
            Log.d("Hiba","Adat feltöltése NEM sikerült! (Plan)");
            return false;
        }
        else
        {
            Log.d("Sikeres","Adat feltöltése sikeres! (Plan)");
            return true;
        }
    }

    //Gyakorlatok
    public boolean updateExercises(String name, int hand_state, int static_state, int weight_state, int level, int type, String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Name", name);
        cv.put("OneHand", hand_state);
        cv.put("Static", static_state);
        cv.put("Weight", weight_state);
        cv.put("Level", level);
        cv.put("Type", type);
        long result = db.update(EXERCISES, cv,"ID=?", new String[] {row_id});
        if(result == -1) {
            Log.d("Hiba","Adat módosítása NEM sikerült! (Exercises)");
            return false;
        }
        else {
            Log.d("Sikeres","Adat módosítása sikeres! (Exercises)");
            return true;
        }
    }

    //ID - Name - Hand - Static - Weight - Level - Type
    public boolean insertExercises(String name, int hand, int statiq, int weight, int level, int type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Name", name);
        cv.put("OneHand", hand);
        cv.put("Static", statiq);
        cv.put("Weight", weight);
        cv.put("Level", level);
        cv.put("Type", type);
        long result =  db.insert(EXERCISES, null, cv);
        if(result == -1) {
            Log.d("Hiba","Adat feltöltése NEM sikerült! (Exercises)");
            return false;
        }
        else
        {
            Log.d("Sikeres","Adat feltöltése sikeres! (Exercises)");
            return true;
        }
    }

    //Összekötő táblák
    public void insertConnectTable(String table, String firstColumn, int firstID, String secondCloumn, int secondID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(firstColumn, firstID);
        cv.put(secondCloumn, secondID);
        long result = db.insert(table, null, cv);
        if(result == -1) {
            Log.d("Hiba","Adat feltöltése NEM sikerült! (" + table + ")");
        }
        else
        {
            Log.d("Sikeres","Adat feltöltése sikeres! (" + table + ")");
        }
    }

    public boolean deleteConnect(String table, String firstColumn, String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(table,firstColumn + "=?", new String[] {row_id});
        if(result == -1){
            Log.d("Hiba","Adat törlése NEM sikerült! (" + table + ")");
            return false;
        }
        else
        {
            Log.d("Sikeres","Adat feltöltése sikeres! (" + table + ")");
            return true;
        }
    }

    //Táblák két attribútummal
    public boolean update(String table, String column, String changeThis, String where, String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(column, changeThis);
        long result = db.update(table, cv,where + "=?", new String[] {row_id});
        if(result == -1) {
            Log.d("Hiba","Adat módosítása NEM sikerült! (" + table + ")");
            return false;
        }
        else {
            Log.d("Sikeres","Adat módosítása sikeres! (" + table + ")");
            return true;
        }
    }

    public boolean insert(String column, String value, String table){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(column, value);
        long result = db.insert(table,null, cv);
        if(result == -1) {
            Log.d("Hiba","Adat feltöltése NEM sikerült! (" + table + ")");
            return false;
        }
        else
        {
            Log.d("Sikeres","Adat feltöltése sikeres! (" + table + ")");
            return true;
        }
    }

    public boolean delete(String table, String where, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(table,where + "=?", new String[] {id});
        if(result == -1) {
            Log.d("Hiba","Adat törlése NEM sikerült! (" + table + ")");
            return false;
        }
        else {
            Log.d("Sikeres","Adat törlése sikeres! (" + table + ")");
            return true;
        }
    }

    public Cursor select(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        if(db != null) {
            c = db.rawQuery(query, null);
        }
        return c;
    }

    public Cursor getID(String table, String where) {
        String query = "SELECT * FROM " + table + " WHERE ID = " + where;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        if(db != null) {
            c = db.rawQuery(query, null);
        }
        return c;
    }

    private Cursor getNewID(String table) {
        Cursor ID = null;
        String query = "SELECT MAX(ID) FROM " + table;
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null) {
            ID = db.rawQuery(query, null);
        }
        return ID;
    }

    public int getTheNewID(String table){
        Cursor c = getNewID(table);
        int newID = -1;
        if (c.getCount() == 0) {
            Toast.makeText(context, "Nincs adat", Toast.LENGTH_SHORT).show();
        } else {
            while (c.moveToNext()) {
                newID = Integer.parseInt(c.getString(0));
            }
        }
        return newID;
    }
}
