package Adapterek;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Main.R;

import java.util.ArrayList;

import InsertActivities.InsertPlanPhaseTwo;

public class PhaseTwoAdapter extends RecyclerView.Adapter<PhaseTwoAdapter.MyViewHolder> implements Filterable {

    private final Context con;
    Activity activity;
    InsertPlanPhaseTwo phaseTwo;

    private final ArrayList<String> exercise_id_list;
    private final ArrayList<String> exercise_name_list;
    private final ArrayList<String> exercise_level_list;

    private final ArrayList<String> search_id_list;
    private final ArrayList<String> search_name_list;
    private final ArrayList<String> search_level_list;

    private ArrayList<String> result_id_list;
    private ArrayList<String> result_name_list;
    private ArrayList<String> result_level_list;

    public PhaseTwoAdapter(Activity activity, Context context, ArrayList<String> id_list, ArrayList<String> name_list, ArrayList<String> level_list) {
        this.con = context;
        this.exercise_id_list = id_list;
        this.exercise_name_list = name_list;
        this.exercise_level_list = level_list;
        this.activity = activity;
        this.phaseTwo = (InsertPlanPhaseTwo) context;
        this.search_id_list = new ArrayList<>(exercise_id_list);
        this.search_name_list = new ArrayList<>(exercise_name_list);
        this.search_level_list = new ArrayList<>(exercise_level_list);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(con);
        View view = inf.inflate(R.layout.row_items_with_check, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.exercise_name_text.setText(String.valueOf(exercise_name_list.get(position)));
        holder.exercise_level_text.setText(exercise_level_list.get(position));

        holder.exercise_name_text.setOnClickListener(v -> phaseTwo.getCheckedItems(v, position));

        boolean checkedItem = false;
        for(int i = 0; i < phaseTwo.checked_exercises_list.size(); i++) {
            if(Integer.parseInt(exercise_id_list.get(position)) == Integer.parseInt(phaseTwo.checked_exercises_list.get(i))){
                checkedItem = true;
                break;
            }
        }
        if(checkedItem) {
            holder.exercise_name_text.setChecked(true);
        } else {
            holder.exercise_name_text.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return exercise_id_list.size();
    }


    @Override
    public Filter getFilter() {
        return searching;
    }


    Filter searching = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence searchField) {
            result_id_list = new ArrayList<>();
            result_name_list = new ArrayList<>();
            result_level_list = new ArrayList<>();

            if(searchField.toString().isEmpty()) {
                result_id_list.addAll(search_id_list);
                result_name_list.addAll(search_name_list);
                result_level_list.addAll(search_level_list);
            } else {
                for(int i = 0; i < search_id_list.size(); i++) {
                    if(search_name_list.get(i).toLowerCase().contains(searchField.toString().toLowerCase())) {
                        result_id_list.add(search_id_list.get(i));
                        result_name_list.add(search_name_list.get(i));
                        result_level_list.add(search_level_list.get(i));
                    }
                }
            }
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            exercise_id_list.clear();
            exercise_name_list.clear();
            exercise_level_list.clear();

            exercise_id_list.addAll(result_id_list);
            exercise_name_list.addAll(result_name_list);
            exercise_level_list.addAll(result_level_list);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox exercise_name_text;
        TextView exercise_level_text;
        LinearLayout linOut;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            exercise_name_text = itemView.findViewById(R.id.exercise_name_row);
            exercise_level_text = itemView.findViewById(R.id.exercise_level_row);
            linOut = itemView.findViewById(R.id.row_listItem);
        }
    }
}
