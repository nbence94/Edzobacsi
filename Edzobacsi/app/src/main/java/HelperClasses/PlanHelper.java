package HelperClasses;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanHelper {

    Context context;
    DatabaseHelper dh;

    public PlanHelper(Context context) {
        this.context = context;
        dh = new DatabaseHelper(context);
    }

    //PlanDetails, UpdatePlan
    public String loadNameToID(String table, String id, int column_index) {
        String result = "-";
        Cursor c = dh.select("SELECT * FROM " + table + " WHERE ID = " + id);
        if(c.getCount() != 0) {
            while(c.moveToNext()) {
                result = c.getString(column_index);
            }
        }
        c.close();
        return result;
    }

    //PlanDetails, UpdatePlan
    public void loadExerciseFromConnect(int planid, ArrayList<String> id_list,
                                                    ArrayList<String> name_list,
                                                    ArrayList<String> weight_amount_list,
                                                    ArrayList<String> repeat_amount_list,
                                                    ArrayList<String> static_state_list) {
        Cursor c = dh.select("SELECT * FROM " + dh.PLAN_AND_EXE + " WHERE planID=" + planid  + " ORDER BY OrderNum;");
        if(c.getCount() == 0) {
            //Toast.makeText(this,"", Toast.LENGTH_SHORT).show();
        }
        else
        {
            while(c.moveToNext()){
                name_list.add(loadNameToID(dh.EXERCISES, c.getString(1), 1));
                static_state_list.add(loadNameToID(dh.EXERCISES, c.getString(1), 3));
                id_list.add(c.getString(1));
                weight_amount_list.add(c.getString(2));
                repeat_amount_list.add(c.getString(3));
            }
        }
        c.close();
    }

    public String getTableSpecificDetail(int id, String table, String select_column, String where_column) {
        String result = "";
        Cursor c = dh.select("SELECT " + select_column +" FROM " + table + " WHERE " + where_column + "=" + id);
        if(c.getCount() != 0) {
            while(c.moveToNext()) {
                result = c.getString(0);
            }
        }
        c.close();
        return result;
    }

    public int getIfNeedReps(int typeid, int exercise_level, int userlevel, int static_state, int hand_state) {
        int result = 0;
        String column = "";

        if(static_state == 1) {
            column = "StaticTime";
        }
        else
        {
            column = "RepeatAmount";
        }
        Cursor c = dh.select("SELECT " + column + " FROM " + dh.REPEATS + " WHERE typeID = " + typeid + " AND UserLevel=" + userlevel + " AND ExerciseLevel = "+ exercise_level + ";");
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                result = Integer.parseInt(c.getString(0));
            }
        }
        c.close();
        if(hand_state == 1) result = result / 2;
        return result;
    }

    public int getIfNeedIntervals(int max_typeid, int userlevel) {
        int result = 0;
        Cursor c = dh.select("SELECT Time FROM " + dh.INTERVALS + " WHERE typeID = " + max_typeid + " AND UserLevel=" + userlevel + ";");
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                result = Integer.parseInt(c.getString(0));
            }
        }
        c.close();
        return result;
    }

    public String createQueryForGoodExercises(String column, int placeid, int categoryid, int typeid, ArrayList<String> muscles_id_list) {
        String result = "WITH GoodExercises" +
                        " AS ( SELECT E.ID, E.Name, E.Level, E.Type, UL.UserLevel, " +
                        "EM.musclegroupID FROM " + dh.EXERCISES + " AS E, " + dh.TYPES + " AS T, " + dh.USERLEVEL + " AS UL";
        StringBuilder tmp = new StringBuilder();
        tmp.append(result);
        if(placeid > 0) tmp.append(", ").append(dh.PLACE_AND_TOOLS).append(" AS PT, ").append(dh.TOOL_AND_EXE).append(" AS TE");
        if(categoryid > 0) tmp.append(", ").append(dh.EXE_AND_CATE).append(" AS EC");
        tmp.append(", ").append(dh.EXE_AND_MG).append(" AS EM");
        tmp.append(" WHERE T.ID = E.Type AND  UL.typeID = T.ID AND UL.UserLevel >= E.Level AND E.ID = EM.exerciseID");
        if(placeid > 0 || categoryid > 0 || typeid > 0 ) tmp.append(" AND");
        if(placeid > 0) {
            tmp.append(" E.ID = TE.exerciseID AND PT.toolID = TE.toolID AND PT.placeID = ").append(placeid);
        }
        if(categoryid > 0) {
            if(placeid > 0) tmp.append(" AND");
            tmp.append(" E.ID = EC.exerciseID AND EC.categoryID = ").append(categoryid);
        }
        if(typeid > 0) {
            if(placeid > 0 || categoryid > 0) tmp.append(" AND");
            tmp.append(" T.ID = ").append(typeid);
        }
        tmp.append(" ) SELECT " + column + " FROM GoodExercises");
        if(muscles_id_list != null && muscles_id_list.size() > 0) {
            tmp.append(" WHERE");
            for(int i = 0; i < muscles_id_list.size(); i++) {
                tmp.append(" musclegroupID = ").append(muscles_id_list.get(i));
                if(i < muscles_id_list.size() - 1) tmp.append(" OR");
            }
        }
        tmp.append(";");
        result = tmp.toString();
        return result;
    }

    public void putInListTheResult(String query, ArrayList<String> id_list, ArrayList<String> name_list, ArrayList<String> level_list) {
        Cursor c = dh.select(query);
        if (c.getCount() == 0) {
        } else {
            while (c.moveToNext()) {
                id_list.add(c.getString(0));
                name_list.add(c.getString(1));
                level_list.add(c.getString(2));
            }
        }
        c.close();
    }

    public void putQueryResultInList(String query, ArrayList<String> whichList) {
        Cursor c = dh.select(query);
        if (c.getCount() == 0) {
        } else {
            while (c.moveToNext()) {
                whichList.add(c.getString(0));
            }
        }
        c.close();
    }

    public void loadRepsTitleList(int plan_type, ArrayList<String> static_list, ArrayList<String> dbmp_list) {
        if(plan_type == 1) {
            for (int i = 0; i < static_list.size(); i++) {
                if (static_list.get(i).equals("1")) dbmp_list.add("mp");
                else dbmp_list.add("db");
            }
        }
        else
        {
            for (int i = 0; i < static_list.size(); i++) {
                dbmp_list.add("mp");
            }
        }
    }

    public static <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }
        return max.getKey();
    }
}
