package azhar.com.quicksale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import azhar.com.quicksale.R;
import azhar.com.quicksale.api.SalesManApi;
import azhar.com.quicksale.model.SalesManModel;
import azhar.com.quicksale.utils.Constants;
import azhar.com.quicksale.utils.DialogUtils;

/**
 * Created by azharuddin on 24/7/17.
 */

public class SalesManAdapter extends RecyclerView.Adapter<SalesManAdapter.MyViewHolder> {

    private Context context;
    private List<SalesManModel> salesManList;
    private String viewInfo;
    private static List<String> selectedSalesMan = new ArrayList<>();

    public SalesManAdapter(Context context, List<SalesManModel> salesManList, String viewInfo) {
        selectedSalesMan.clear();
        this.context = context;
        this.salesManList = salesManList;
        this.viewInfo = viewInfo;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = null;

        if (viewInfo.equals(Constants.EDIT)) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_common,
                            parent, false);
        } else if (viewInfo.equals(Constants.CHECK)) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_sales_man_check,
                            parent, false);
        }

        return new SalesManAdapter.MyViewHolder(itemView);
    }

    @SuppressLint({"RecyclerView", "SetTextI18n"})
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final SalesManModel men = salesManList.get(position);
        holder.salesManName.setText(men.getName() + "/" + men.getPhone());

        if (viewInfo.equals(Constants.EDIT)) {
            holder.salesEdit.setText("Edit");
            holder.salesEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog(men.getName(), men.getPhone(), position);
                }
            });

            holder.salesDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SalesManApi.salesManDBRef.child(men.getName()).removeValue();
                    salesManList.remove(position);
                    notifyDataSetChanged();
                }
            });
        } else if (viewInfo.equals(Constants.CHECK)) {
            holder.salesManCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        selectedSalesMan.add(men.getName());
                    } else {
                        selectedSalesMan.remove(men.getName());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return salesManList.size();
    }

    public static List<String> getSelectedSalesMan() {
        return selectedSalesMan;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView salesManName;
        CheckBox salesManCheck;
        TextView salesEdit, salesDelete;

        MyViewHolder(View itemView) {
            super(itemView);
            if (viewInfo.equals(Constants.EDIT)) {
                salesManName = itemView.findViewById(R.id.name);
                salesEdit = itemView.findViewById(R.id.view_edit);
                salesDelete = itemView.findViewById(R.id.delete);
            } else if (viewInfo.equals(Constants.CHECK)) {
                salesManName = itemView.findViewById(R.id.sales_man_name);
                salesManCheck = itemView.findViewById(R.id.sales_man_check);
            }
        }
    }

    @SuppressLint("InflateParams")
    private void alertDialog(final String originalName, final String originalPhone, final int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final HashMap<String, Object> salesMan = new HashMap<>();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        final View dialogView = inflater.inflate(R.layout.dialog_sales_man, null);
        dialogBuilder.setView(dialogView);

        final EditText name = dialogView.findViewById(R.id.et_sales_man_name);
        final EditText phone = dialogView.findViewById(R.id.et_sales_man_phone);

        name.setText(originalName);
        phone.setText(originalPhone);

        dialogBuilder.setTitle("Details");
        dialogBuilder.setMessage("Edit Sales Man");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String salesManName = name.getText().toString();
                String salesManPhone = phone.getText().toString();
                if (!salesManName.isEmpty() &&
                        !salesManPhone.isEmpty()) {
                    SalesManApi.salesManDBRef.child(originalPhone).removeValue();
                    salesManList.remove(position);
                    salesManList.add(new SalesManModel(salesManName, salesManPhone));
                    salesMan.put("sales_man_name", salesManName);
                    salesMan.put("sales_man_phone", salesManPhone);
                    SalesManApi.salesManDBRef.child(salesManPhone).
                            updateChildren(salesMan)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        DialogUtils.appToastShort(context,
                                                "Sales man updated");
                                    } else {
                                        DialogUtils.appToastShort(context,
                                                "Sales man not updated");
                                    }
                                }
                            });
                    notifyDataSetChanged();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
