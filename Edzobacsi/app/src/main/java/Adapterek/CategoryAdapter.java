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
import UpdateActivities.UpdateCategory;
import com.example.Main.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> implements Filterable {

    private Context con;
    Activity activity;

    private final ArrayList<String> category_id_list;
    private final ArrayList<String> category_name_list;
    private final ArrayList<String> deletable_list;
    private final ArrayList<String> connect_list;

    private final ArrayList<String> search_id_list;
    private final ArrayList<String> search_name_list;
    private final ArrayList<String> search_deletable_list;
    private final ArrayList<String> search_connect_list;

    private ArrayList<String> result_id_list;
    private ArrayList<String> result_name_list;
    private ArrayList<String> result_deletable_list;
    private ArrayList<String> result_connect_list;

    public CategoryAdapter(Activity act, Context context, ArrayList<String> cid, ArrayList<String> cName, ArrayList<String> deletable, ArrayList<String> connects) {
        this.activity = act;
        this.con = context;
        this.category_id_list = cid;
        this.category_name_list = cName;
        this.deletable_list = deletable;
        this.connect_list = connects;
        this.search_id_list = new ArrayList<>(category_id_list);
        this.search_name_list = new ArrayList<>(category_name_list);
        this.search_deletable_list = new ArrayList<>(deletable_list);
        this.search_connect_list = new ArrayList<>(connect_list);
    }

    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(con);
        View view = inf.inflate(R.layout.row_withname, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder holder, int position) {
        holder.cName.setText(String.valueOf(category_name_list.get(position)));

        if(Integer.parseInt(connect_list.get(position)) > 0) {
            holder.check.setImageResource(R.drawable.check_correct);
        }
        else
        {
            holder.check.setImageResource(R.drawable.check_wrong);
        }

        holder.check.setOnClickListener(v -> {
            Toast.makeText(con, "Ezt a kategóriát " + connect_list.get(position) + " terv tárolja.", Toast.LENGTH_SHORT).show();
        });


        holder.openThis.setOnClickListener(v -> {
            Intent openUpdate = new Intent(con, UpdateCategory.class);
            openUpdate.putExtra("id", String.valueOf(category_id_list.get(position)));
            openUpdate.putExtra("name", String.valueOf(category_name_list.get(position)));
            activity.startActivityForResult(openUpdate, 1);
        });

        holder.openThis.setOnLongClickListener(v -> {
            AlertDialog.Builder adb = new AlertDialog.Builder(con);
            adb.setTitle("Törlés");
            adb.setMessage("Biztosan törlöd ezt: " + category_name_list.get(position) + " ?");

            adb.setPositiveButton("Igen", (dialog, which) -> {
                if(deletable_list.get(position).equals("1")) {
                    DatabaseHelper dh = new DatabaseHelper(con);
                    boolean success_delete2 = dh.delete(dh.EXE_AND_CATE, "categoryID", category_id_list.get(position));
                    boolean success_delete1 = dh.delete(dh.CATEGORY, "ID", category_id_list.get(position));
                    
                    if(success_delete1 && success_delete2) {
                        category_id_list.remove(position);
                        category_name_list.remove(position);
                        deletable_list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, category_id_list.size());
                    }
                }
                else
                {
                    Toast.makeText(con, "Nem törölhető, mert egy tervhez tartozik", Toast.LENGTH_SHORT).show();
                }
            });
            adb.setNegativeButton("Nem", (dialog, which) -> {
                //Nem kell ide semmi, mert csak simán bezáródik
            });
            adb.create().show();
            return false;
        }
        );
    }

    @Override
    public int getItemCount() {
        return category_id_list.size();
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
            result_deletable_list = new ArrayList<>();
            result_connect_list = new ArrayList<>();

            if(searchField.toString().isEmpty()) {
                result_id_list.addAll(search_id_list);
                result_name_list.addAll(search_name_list);
                result_deletable_list.addAll(search_deletable_list);
                result_connect_list.addAll(search_connect_list);
            } else {
                for(int i = 0; i < search_id_list.size(); i++) {
                    if(search_name_list.get(i).toLowerCase().contains(searchField.toString().toLowerCase())) {
                        result_id_list.add(search_id_list.get(i));
                        result_name_list.add(search_name_list.get(i));
                        result_deletable_list.add(search_deletable_list.get(i));
                        result_connect_list.add(search_connect_list.get(i));
                    }
                }
            }
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            category_id_list.clear();
            category_name_list.clear();
            deletable_list.clear();
            connect_list.clear();

            category_id_list.addAll(result_id_list);
            category_name_list.addAll(result_name_list);
            deletable_list.addAll(result_deletable_list);
            connect_list.addAll(result_connect_list);

            notifyDataSetChanged();
        }
    };


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView cName;
        LinearLayout openThis;
        ImageView check;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cName = itemView.findViewById(R.id.exe_name_row);
            openThis = itemView.findViewById(R.id.rowListItem);
            check = itemView.findViewById(R.id.connect_check_conf);
        }
    }
}
