package Adapterek;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import HelperClasses.DatabaseHelper;
import com.example.Main.R;

import java.util.ArrayList;

import UpdateActivities.UpdateExercise;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.MyViewHolder> implements Filterable
{
    private final Context con;
    private final Activity activity;
    private final ArrayList<String> exercise_id_list;
    private final ArrayList<String> exercise_name_list;
    private final ArrayList<String> exercise_level_list;
    private final ArrayList<String> exercise_deletable_state_list;
    private final ArrayList<String> exercise_connects_list;

    private final ArrayList<String> search_id_list;
    private final ArrayList<String> search_name_list;
    private final ArrayList<String> search_level_list;
    private final ArrayList<String> search_deletable_state_list;
    private final ArrayList<String> search_connects_list;

    private ArrayList<String> result_id_list;
    private ArrayList<String> result_name_list;
    private ArrayList<String> result_level_list;
    private ArrayList<String> result_deletable_state_list;
    private ArrayList<String> result_connects_list;

    public ExerciseAdapter(Activity act, Context c, ArrayList<String> exerciseID, ArrayList<String> exerciseName, ArrayList<String> exerciseLevel, ArrayList<String> exerciseDeletable, ArrayList<String> connect_number) {
        this.con = c;
        this.activity = act;
        this.exercise_id_list = exerciseID;
        this.exercise_name_list = exerciseName;
        this.exercise_level_list = exerciseLevel;
        this.exercise_deletable_state_list = exerciseDeletable;
        this.exercise_connects_list = connect_number;

        this.search_id_list = new ArrayList<>(exercise_id_list);
        this.search_name_list = new ArrayList<>(exercise_name_list);
        this.search_level_list = new ArrayList<>(exercise_level_list);
        this.search_deletable_state_list = new ArrayList<>(exercise_deletable_state_list);
        this.search_connects_list = new ArrayList<>(exercise_connects_list);
    }

    @NonNull
    @Override
    public ExerciseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(con);
        View view = inf.inflate(R.layout.row_exercise, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseAdapter.MyViewHolder holder, int position) {
        holder.exercise_name.setText(String.valueOf(exercise_name_list.get(position)));
        holder.exercise_level.setText(String.valueOf(exercise_level_list.get(position)));

        if(Integer.parseInt(exercise_connects_list.get(position)) > 0) {
            holder.connect_check.setImageResource(R.drawable.check_correct);
        }
        else
        {
            holder.connect_check.setImageResource(R.drawable.check_wrong);
        }

        holder.connect_check.setOnClickListener(v -> {
            Toast.makeText(con, "Ez a gyakorlat " + exercise_connects_list.get(position) + " eszközhöz csatlakozik", Toast.LENGTH_SHORT).show();
        });

        //Kattintható legyen
        holder.linOut.setOnClickListener(v -> {
            Intent openUpdate = new Intent(con, UpdateExercise.class);
            openUpdate.putExtra("id", String.valueOf(exercise_id_list.get(position)));
            activity.startActivityForResult(openUpdate, 1);
        });

        holder.linOut.setOnLongClickListener(v -> {
            AlertDialog.Builder deleteThis = new AlertDialog.Builder(con);
            deleteThis.setMessage("Biztosan törlöd ezt: " + exercise_name_list.get(position) + " ?");

            deleteThis.setPositiveButton("Igen", (dialog, which) -> {
                if(exercise_deletable_state_list.get(position).equals("1")) {
                    DatabaseHelper dh = new DatabaseHelper(con);

                    boolean successEACDelete = dh.delete(dh.EXE_AND_CATE, "exerciseID", exercise_id_list.get(position));
                    boolean successEAMDelete = dh.delete(dh.EXE_AND_MG, "exerciseID", exercise_id_list.get(position));
                    boolean successEAT = dh.delete(dh.TOOL_AND_EXE, "exerciseID", exercise_id_list.get(position));
                    boolean successExerciseDelete = dh.delete(dh.EXERCISES, "ID", exercise_id_list.get(position));

                    if(successEACDelete && successEAMDelete && successExerciseDelete && successEAT) {
                        exercise_id_list.remove(position);
                        exercise_name_list.remove(position);
                        exercise_deletable_state_list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, exercise_id_list.size());
                    }
                }
                else
                {
                    Toast.makeText(con, "Nem törölhető, egy tervhez tartozik", Toast.LENGTH_SHORT).show();
                }

            });

            deleteThis.setNegativeButton("Nem", (dialog, which) -> {
                //Maradhat üres
            });
            deleteThis.show();
            return false;
        });
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
            result_deletable_state_list = new ArrayList<>();
            result_connects_list = new ArrayList<>();

            if(searchField.toString().isEmpty()) {
                result_id_list.addAll(search_id_list);
                result_name_list.addAll(search_name_list);
                result_level_list.addAll(search_level_list);
                result_deletable_state_list.addAll(search_deletable_state_list);
                result_connects_list.addAll(search_connects_list);
            } else {
                for(int i = 0; i < search_id_list.size(); i++) {
                    if(search_name_list.get(i).toLowerCase().contains(searchField.toString().toLowerCase())) {
                        result_id_list.add(search_id_list.get(i));
                        result_name_list.add(search_name_list.get(i));
                        result_level_list.add(search_level_list.get(i));
                        result_deletable_state_list.add(search_deletable_state_list.get(i));
                        result_connects_list.add(search_connects_list.get(i));
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
            exercise_deletable_state_list.clear();
            exercise_connects_list.clear();

            exercise_id_list.addAll(result_id_list);
            exercise_name_list.addAll(result_name_list);
            exercise_level_list.addAll(result_level_list);
            exercise_deletable_state_list.addAll(result_deletable_state_list);
            exercise_connects_list.addAll(result_connects_list);

            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView exercise_name, exercise_level;
        ImageView connect_check;
        LinearLayout linOut;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            exercise_name = itemView.findViewById(R.id.gyakneve_listelement);
            exercise_level = itemView.findViewById(R.id.gyakszint_listelement);
            connect_check = itemView.findViewById(R.id.exercise_connect_check);
            linOut = itemView.findViewById(R.id.exerciseListItem);
        }
    }
}
