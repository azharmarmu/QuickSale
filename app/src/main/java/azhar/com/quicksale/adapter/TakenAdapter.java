package azhar.com.quicksale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import azhar.com.quicksale.R;
import azhar.com.quicksale.activity.SetTakenActivity;
import azhar.com.quicksale.model.TakenModel;
import azhar.com.quicksale.utils.Constants;

/**
 * Created by azharuddin on 24/7/17.
 */

@SuppressWarnings("unchecked")
public class TakenAdapter extends RecyclerView.Adapter<TakenAdapter.MyViewHolder> {

    private Context context;
    private List<TakenModel> takenList;
    private static List<String> selectedSalesMan = new ArrayList<>();

    public TakenAdapter(Context context, List<TakenModel> takenList) {
        this.context = context;
        this.takenList = takenList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_common,
                        parent, false);
        return new TakenAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final TakenModel taken = takenList.get(position);
        final HashMap<String, Object> takenMap = taken.getTakenMap();

        holder.takenName.setText("");
        holder.takenName.append("Sales Man : " + takenMap.get(Constants.TAKEN_SALES_MAN_NAME)
                + "\n"
                + "Route : " + takenMap.get(Constants.TAKEN_ROUTE));

        if (takenMap.get(Constants.TAKEN_PROCESS).toString().equalsIgnoreCase(Constants.START)) {
            holder.takenViewEdit.setText(R.string.edit);
        } else {
            holder.takenViewEdit.setText(R.string.view);
        }

        holder.takenViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(context, SetTakenActivity.class);
                editIntent.putExtra(Constants.KEY, taken.getKey());
                editIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(editIntent);
            }
        });

        holder.takenDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore dbStore = FirebaseFirestore.getInstance();
                dbStore.collection(Constants.TAKEN)
                        .document(taken.getKey())
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context,
                                    "Taken deleted successfully!",
                                    Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return takenList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView takenName, takenViewEdit, takenDelete;

        MyViewHolder(View itemView) {
            super(itemView);
            takenName = itemView.findViewById(R.id.name);
            takenViewEdit = itemView.findViewById(R.id.view_edit);
            takenDelete = itemView.findViewById(R.id.delete);
        }
    }

}
