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

import UpdateActivities.UpdateTool;

public class ToolsAdapter extends RecyclerView.Adapter<ToolsAdapter.MyViewHolder> implements Filterable
{

    private final Context con;
    private final Activity activity;
    private final ArrayList<String> tool_id_list;
    private final ArrayList<String> tool_name_list;
    private final ArrayList<String> tool_connect;

    private final ArrayList<String> search_id_list;
    private final ArrayList<String> search_name_list;
    private final ArrayList<String> search_connect_list;

    private ArrayList<String> result_id_list;
    private ArrayList<String> result_name_list;
    private ArrayList<String> result_connect_list;


    public ToolsAdapter(Activity act, Context c, ArrayList<String> tid, ArrayList<String> tName, ArrayList<String> connect) {
        this.con = c;
        this.activity = act;
        this.tool_name_list = tName;
        this.tool_id_list = tid;
        this.tool_connect = connect;

        this.search_id_list = new ArrayList<>(tool_id_list);
        this.search_name_list = new ArrayList<>(tool_name_list);
        this.search_connect_list = new ArrayList<>(tool_connect);
    }

    @NonNull
    @Override
    public ToolsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(con);
        View view = inf.inflate(R.layout.row_withname, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToolsAdapter.MyViewHolder holder, int position) {
        holder.toolName.setText(String.valueOf(tool_name_list.get(position)));

        if(Integer.parseInt(tool_connect.get(position)) > 0) {
            holder.check.setImageResource(R.drawable.check_correct);
        }
        else
        {
            holder.check.setImageResource(R.drawable.check_wrong);
        }

        holder.check.setOnClickListener(v -> {
            Toast.makeText(con, "Ez az eszköz " + tool_connect.get(position) + " helyen található", Toast.LENGTH_SHORT).show();
        });

        //Kattintható legyen
        holder.linOut.setOnClickListener(v -> {
            Intent openUpdate = new Intent(con, UpdateTool.class);
            openUpdate.putExtra("id", String.valueOf(tool_id_list.get(position)));
            openUpdate.putExtra("name", String.valueOf(tool_name_list.get(position)));
            activity.startActivityForResult(openUpdate, 1);
        });

        holder.linOut.setOnLongClickListener(v -> {
            AlertDialog.Builder deleteThis = new AlertDialog.Builder(con);
            deleteThis.setMessage("Biztosan törlöd ezt: " + tool_name_list.get(position) + " ?");

            deleteThis.setPositiveButton("Igen", (dialog, which) -> {
                DatabaseHelper dh = new DatabaseHelper(con);

                boolean successTAEDelete = dh.delete(dh.TOOL_AND_EXE, "toolID", tool_id_list.get(position));
                boolean successPATDelete = dh.delete(dh.PLACE_AND_TOOLS, "toolID", tool_id_list.get(position));
                boolean successToolDelete = dh.delete(dh.TOOLS, "ID", tool_id_list.get(position));

                if(successPATDelete && successTAEDelete && successToolDelete) {
                    tool_id_list.remove(position);
                    tool_name_list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, tool_id_list.size());
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
        return tool_name_list.size();
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
            result_connect_list = new ArrayList<>();

            if(searchField.toString().isEmpty()) {
                result_id_list.addAll(search_id_list);
                result_name_list.addAll(search_name_list);
                result_connect_list.addAll(search_connect_list);
            } else {
                for(int i = 0; i < search_id_list.size(); i++) {
                    if(search_name_list.get(i).toLowerCase().contains(searchField.toString().toLowerCase())) {
                        result_id_list.add(search_id_list.get(i));
                        result_name_list.add(search_name_list.get(i));
                        result_connect_list.add(search_connect_list.get(i));
                    }
                }
            }
            return null;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            tool_id_list.clear();
            tool_name_list.clear();
            tool_connect.clear();

            tool_id_list.addAll(result_id_list);
            tool_name_list.addAll(result_name_list);
            tool_connect.addAll(result_connect_list);

            notifyDataSetChanged();
        }
    };



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView toolName;
        LinearLayout linOut;
        ImageView check;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            toolName = itemView.findViewById(R.id.exe_name_row);
            linOut = itemView.findViewById(R.id.rowListItem);
            check = itemView.findViewById(R.id.connect_check_conf);
        }
    }
}
