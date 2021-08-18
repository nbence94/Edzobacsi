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

import UpdateActivities.UpdatePlace;


public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder> implements Filterable {

    private final Context con;
    private final Activity activity;
    private final ArrayList<String> place_id_list;
    private final ArrayList<String> place_name_list;
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

    public PlaceAdapter(Activity act, Context c, ArrayList<String> pid, ArrayList<String> pName, ArrayList<String> pDeletable, ArrayList<String> connect) {
        this.con = c;
        this.activity = act;
        this.place_name_list = pName;
        this.place_id_list = pid;
        this.deletable_list = pDeletable;
        this.connect_list = connect;

        this.search_id_list = new ArrayList<>(place_id_list);
        this.search_name_list = new ArrayList<>(place_name_list);
        this.search_deletable_list = new ArrayList<>(deletable_list);
        this.search_connect_list = new ArrayList<>(connect_list);
    }

    @NonNull
    @Override
    public PlaceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(con);
        View view = inf.inflate(R.layout.row_withname, parent, false);
        return new PlaceAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.placeName.setText(String.valueOf(place_name_list.get(position)));

        if(Integer.parseInt(connect_list.get(position)) > 0) {
            holder.check.setImageResource(R.drawable.check_correct);
        }
        else
        {
            holder.check.setImageResource(R.drawable.check_wrong);
        }

        holder.check.setOnClickListener(v -> {
            Toast.makeText(con, "Ez a helyszín " + connect_list.get(position) + " tervhez tartozik", Toast.LENGTH_SHORT).show();
        });


        holder.linOut.setOnClickListener(v -> {
            Intent openUpdate = new Intent(con, UpdatePlace.class);
            openUpdate.putExtra("id", String.valueOf(place_id_list.get(position)));
            openUpdate.putExtra("name", String.valueOf(place_name_list.get(position)));
            activity.startActivityForResult(openUpdate, 1);
        });

        holder.linOut.setOnLongClickListener(v -> {
            AlertDialog.Builder deleteThis = new AlertDialog.Builder(con);
            deleteThis.setMessage("Biztosan törlöd ezt: " + place_name_list.get(position) + " ?");

            deleteThis.setPositiveButton("Igen", (dialog, which) -> {
                if(deletable_list.get(position).equals("1")) {
                    DatabaseHelper dh = new DatabaseHelper(con);

                    boolean successPATDelete = dh.delete(dh.PLACE_AND_TOOLS, "placeID", place_id_list.get(position));
                    boolean successPlaceDelete = dh.delete(dh.PLACES, "ID", place_id_list.get(position));

                    if(successPATDelete && successPlaceDelete) {
                        place_id_list.remove(position);
                        place_name_list.remove(position);
                        deletable_list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, place_id_list.size());
                    }
                }
                else {
                    Toast.makeText(con, "A helyszín nem törölhető, szerepel egy tervben", Toast.LENGTH_SHORT).show();
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
        return place_name_list.size();
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
            place_id_list.clear();
            place_name_list.clear();
            deletable_list.clear();
            connect_list.clear();

            place_id_list.addAll(result_id_list);
            place_name_list.addAll(result_name_list);
            deletable_list.addAll(result_deletable_list);
            connect_list.addAll(result_connect_list);

            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView placeName;
        LinearLayout linOut;
        ImageView check;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            placeName = itemView.findViewById(R.id.exe_name_row);
            linOut = itemView.findViewById(R.id.rowListItem);
            check = itemView.findViewById(R.id.connect_check_conf);
        }
    }
}
