package Adapterek;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Main.R;
import com.example.Main.UserDetails;


import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    Context con;
    ArrayList<String> type_id_list, type_name_list, type_user_level_list;
    int max_level;
    UserDetails UD;

    public UserAdapter(Context context, ArrayList<String> id_list, ArrayList<String> name_list, ArrayList<String> level_list, int numberoflevels) {
        this.type_id_list = id_list;
        this.type_name_list = name_list;
        this.type_user_level_list = level_list;
        this.max_level = numberoflevels;
        this.con = context;
        this.UD = (UserDetails) context;
    }

    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(con);
        View view = inf.inflate(R.layout.row_user_items, parent, false);
        return new UserAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, int position) {
        holder.type_name.setText(type_name_list.get(position));
        holder.type_level.setText(type_user_level_list.get(position));
        holder.seek_type_level.setMax(max_level-1);
        holder.seek_type_level.setProgress(Integer.parseInt(type_user_level_list.get(position))-1);

        holder.seek_type_level.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                holder.type_level.setText(String.valueOf(progress+1));
                UD.getTypeLevels(seekBar, position, String.valueOf(progress+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return type_id_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView type_name, type_level;
        SeekBar seek_type_level;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            type_name = itemView.findViewById(R.id.type_name_text);
            type_level = itemView.findViewById(R.id.type_level);
            seek_type_level = itemView.findViewById(R.id.seek_the_level);
        }
    }
}
