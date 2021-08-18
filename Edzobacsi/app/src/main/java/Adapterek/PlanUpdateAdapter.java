package Adapterek;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.Main.R;

import java.util.ArrayList;

import UpdateActivities.UpdatePlan;


public class PlanUpdateAdapter extends RecyclerView.Adapter<PlanUpdateAdapter.MyViewHolder> {

    private final Context con;
    private final ArrayList<String> exercise_id_list;
    private final ArrayList<String> exercise_name_list;
    private final ArrayList<String> exercise_weight_list;
    private final ArrayList<String> exercise_reps_list;
    private final ArrayList<String> exercise_dbmp_list;
    UpdatePlan updatePlan;
    Activity activity;

    public PlanUpdateAdapter(Activity activity, Context context, ArrayList<String> eID, ArrayList<String> eName, ArrayList<String> eWeight, ArrayList<String> reps, ArrayList<String> dbmp) {
        this.con = context;
        this.exercise_id_list = eID;
        this.exercise_name_list = eName;
        this.exercise_weight_list = eWeight;
        this.exercise_reps_list = reps;
        this.exercise_dbmp_list = dbmp;
        this.activity = activity;
        this.updatePlan = (UpdatePlan) context;
    }

    @NonNull
    @Override
    public PlanUpdateAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(con);
        View view = inf.inflate(R.layout.row_planitem_edit, parent, false);
        return new PlanUpdateAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanUpdateAdapter.MyViewHolder holder, int position) {
        holder.exercise_name.setText(String.valueOf(exercise_name_list.get(position)));
        holder.exercise_reps_title.setText(String.valueOf(exercise_reps_list.get(position)));
        holder.exercise_reps_amount.setText(String.valueOf(exercise_dbmp_list.get(position)));

        //Megnézni, hogy súlyzós-e a gyakorlat
        if(exercise_weight_list.get(position).equals("0")) {
            holder.exercise_weight_title.setVisibility(View.GONE);
            holder.exercise_weight_kg.setVisibility(View.GONE);
        }
        else
        {
            holder.exercise_weight_title.setText(exercise_weight_list.get(position));
            holder.exercise_weight_title.setVisibility(View.VISIBLE);
            holder.exercise_weight_kg.setVisibility(View.VISIBLE);

        }


        holder.exercise_reps_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(updatePlan.editable) {
                    updatePlan.changeReps(position, holder.exercise_reps_title.getText().toString());
                }
                    else
                {
                    updatePlan.editable = true;
                }
            }

        });

        holder.exercise_weight_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePlan.changeWeights(position, holder.exercise_weight_title.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercise_id_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView exercise_name;
        LinearLayout linOut;

        EditText exercise_weight_title, exercise_reps_title;
        TextView exercise_weight_kg, exercise_reps_amount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            exercise_name = itemView.findViewById(R.id.exe_name_row);
            linOut = itemView.findViewById(R.id.phaseThree_listItem);
            exercise_weight_title = itemView.findViewById(R.id.exe_weight_title);
            exercise_weight_kg = itemView.findViewById(R.id.exe_weight_kg);
            exercise_reps_title = itemView.findViewById(R.id.exe_reps_title);
            exercise_reps_amount = itemView.findViewById(R.id.exe_reps_amount);
        }
    }
}
