package Adapterek;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Main.R;

import java.util.ArrayList;

public class PlanDetailAdapter extends RecyclerView.Adapter<PlanDetailAdapter.MyViewHolder> {

    Context con;
    ArrayList<String> exercise_id_list, exercise_name_list, exercise_details_list;

    public PlanDetailAdapter(Context context, ArrayList<String> id_list, ArrayList<String> name_list, ArrayList<String> details_list) {
        this.con = context;
        this.exercise_id_list = id_list;
        this.exercise_name_list = name_list;
        this.exercise_details_list = details_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(con);
        View view = inf.inflate(R.layout.row_plans_exercises, parent, false);
        return new PlanDetailAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.exercise_name.setText(String.valueOf(exercise_name_list.get(position)));
        holder.exercise_details.setText(String.valueOf(exercise_details_list.get(position)));
    }

    @Override
    public int getItemCount() {
        return exercise_id_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView exercise_name, exercise_details;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            exercise_name = itemView.findViewById(R.id.exe_name_row_detail);
            exercise_details = itemView.findViewById(R.id.exe_details_detail);
        }
    }
}
