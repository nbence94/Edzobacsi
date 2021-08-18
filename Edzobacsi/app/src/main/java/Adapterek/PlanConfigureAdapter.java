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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import HelperClasses.DatabaseHelper;

import com.example.Main.R;

import java.util.ArrayList;

import UpdateActivities.UpdatePlan;

public class PlanConfigureAdapter extends RecyclerView.Adapter<PlanConfigureAdapter.MyViewHolder> implements Filterable {

    Activity activity;
    Context context;
    ArrayList<String> plan_id_list, plan_name_list, plan_place_list;

    private final ArrayList<String> search_id_list;
    private final ArrayList<String> search_name_list;
    private final ArrayList<String> search_place_list;

    private ArrayList<String> result_id_list;
    private ArrayList<String> result_name_list;
    private ArrayList<String> result_place_list;

    public PlanConfigureAdapter(Activity activity, Context context, ArrayList<String> id_list, ArrayList<String> name_list, ArrayList<String> place_list) {
        this.activity = activity;
        this.context = context;
        this.plan_id_list = id_list;
        this.plan_name_list = name_list;
        this.plan_place_list = place_list;

        this.search_id_list = new ArrayList<>(plan_id_list);
        this.search_name_list = new ArrayList<>(plan_name_list);
        this.search_place_list = new ArrayList<>(plan_place_list);
    }

    @NonNull
    @Override
    public PlanConfigureAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(context);
        View view = inf.inflate(R.layout.row_main_items, parent, false);
        return new PlanConfigureAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanConfigureAdapter.MyViewHolder holder, int position) {
        holder.planName.setText(plan_name_list.get(position));
        holder.planPlace.setText(plan_place_list.get(position));

        holder.linOut.setOnClickListener(v -> {
            Intent openUpdate = new Intent(context, UpdatePlan.class);
            openUpdate.putExtra("ID", plan_id_list.get(position));
            activity.startActivityForResult(openUpdate, 1);
        });

        holder.linOut.setOnLongClickListener(v -> {
            AlertDialog.Builder deleteThis = new AlertDialog.Builder(context);
            deleteThis.setMessage("Biztosan törlöd ezt: " + plan_name_list.get(position) + " ?");

            deleteThis.setPositiveButton("Igen", (dialog, which) -> {
                DatabaseHelper dh = new DatabaseHelper(context);
                boolean pe_result = dh.deleteConnect(dh.PLAN_AND_EXE, "planID", plan_id_list.get(position));
                if(!pe_result) {
                    Toast.makeText(context, "Nem lehet törölni!", Toast.LENGTH_SHORT).show();
                }
                else {
                    dh.delete(dh.PLANS, "ID", plan_id_list.get(position));
                }
            });


            deleteThis.setNegativeButton("Nem", (dialog, which) -> {

            });
            deleteThis.show();
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return plan_id_list.size();
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
            result_place_list = new ArrayList<>();

            if(searchField.toString().isEmpty()) {
                result_id_list.addAll(search_id_list);
                result_name_list.addAll(search_name_list);
                result_place_list.addAll(search_place_list);
            } else {
                for(int i = 0; i < search_id_list.size(); i++) {
                    if(search_name_list.get(i).toLowerCase().contains(searchField.toString().toLowerCase())) {
                        result_id_list.add(search_id_list.get(i));
                        result_name_list.add(search_name_list.get(i));
                        result_place_list.add(search_place_list.get(i));
                    }
                }
            }
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            plan_id_list.clear();
            plan_name_list.clear();
            plan_place_list.clear();

            plan_id_list.addAll(result_id_list);
            plan_name_list.addAll(result_name_list);
            plan_place_list.addAll(result_place_list);

            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView planName, planPlace;
        LinearLayout linOut;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            planName = itemView.findViewById(R.id.plans_name_main);
            planPlace = itemView.findViewById(R.id.plans_place_main);
            linOut = itemView.findViewById(R.id.main_row_layoutItem);
        }
    }
}