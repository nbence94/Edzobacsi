package Adapterek;

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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Main.R;

import java.util.ArrayList;

import SelectionActivities.ExercisesForTools;

public class EFTAdapter extends RecyclerView.Adapter<EFTAdapter.MyViewHolder> implements Filterable
{
    Context context;
    ExercisesForTools eft;

    private final ArrayList<String> exercise_id_list;
    private final ArrayList<String> exercise_name_list;
    private ArrayList<String> checked_id_list;

    private final ArrayList<String> search_id_list;
    private final ArrayList<String> search_name_list;

    private ArrayList<String> result_id_list;
    private ArrayList<String> result_name_list;

    public EFTAdapter(Context context, ArrayList<String> id_list, ArrayList<String> name_list, ArrayList<String> checked_list) {
        this.context = context;
        this.exercise_id_list = id_list;
        this.exercise_name_list = name_list;
        this.checked_id_list = checked_list;
        this.eft = (ExercisesForTools) context;

        this.search_id_list = new ArrayList<>(exercise_id_list);
        this.search_name_list = new ArrayList<>(exercise_name_list);
    }

    @NonNull
    @Override
    public EFTAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(context);
        View view = inf.inflate(R.layout.row_items_with_check, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EFTAdapter.MyViewHolder holder, int position) {
        holder.dontshow.setVisibility(View.GONE);
        holder.exercise_name.setClickable(false);
        holder.exercise_name.setFocusable(false);
        holder.exercise_name.setText(exercise_name_list.get(position));

        holder.linOut.setOnClickListener(v -> {
            if(holder.exercise_name.isChecked()) {
                holder.exercise_name.setChecked(false);
            }
            else
            {
                holder.exercise_name.setChecked(true);
            }

            eft.getCheckedItems(holder.exercise_name, position);
        });


        boolean checkedItem = false;
        for(int i = 0; i < checked_id_list.size(); i++) {
            if(Integer.parseInt(exercise_id_list.get(position)) == Integer.parseInt(checked_id_list.get(i))){
                checkedItem = true;
                break;
            }
        }
        if(checkedItem) {
            holder.exercise_name.setChecked(true);
        } else {
            holder.exercise_name.setChecked(false);
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
            if(searchField.toString().isEmpty()) {
                result_id_list.addAll(search_id_list);
                result_name_list.addAll(search_name_list);
            } else {
                for(int i = 0; i < search_id_list.size(); i++) {
                    if(search_name_list.get(i).toLowerCase()
                            .contains(searchField.toString().toLowerCase())) {
                        result_id_list.add(search_id_list.get(i));
                        result_name_list.add(search_name_list.get(i));
                    }
                }
            }
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            exercise_id_list.clear();
            exercise_name_list.clear();
            exercise_id_list.addAll(result_id_list);
            exercise_name_list.addAll(result_name_list);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox exercise_name;
        LinearLayout linOut;
        TextView dontshow;
        CardView cv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            exercise_name = itemView.findViewById(R.id.exercise_name_row);
            linOut = itemView.findViewById(R.id.row_listItem);
            dontshow = itemView.findViewById(R.id.exercise_level_row);
            cv = itemView.findViewById(R.id.theCard);
        }
    }
}
