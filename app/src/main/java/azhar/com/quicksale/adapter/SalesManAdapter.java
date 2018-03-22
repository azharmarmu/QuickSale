package azhar.com.quicksale.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
                    editCustomerDialog(men, position);
                }
            });

            holder.salesDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SalesManApi.salesManDBRef.child(men.getPhone()).removeValue();
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

    private void editCustomerDialog(final SalesManModel salesManModel, int position) {
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setContentView(R.layout.fragment_add_sales_man);

        Window window = dialog.getWindow();
        assert window != null;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP;

        final EditText name = dialog.findViewById(R.id.et_sales_man_name);
        final EditText phone = dialog.findViewById(R.id.et_sales_man_phone);
        phone.setKeyListener(null);

        name.append(salesManModel.getName());
        phone.append(salesManModel.getPhone());

        TextView addSalesMan = dialog.findViewById(R.id.btn_add_sales_man);
        addSalesMan.setText("");
        addSalesMan.append("Update");
        addSalesMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete customer
                final String key = salesManModel.getKey();
                SalesManApi.salesManDBRef.child(key).removeValue();

                String salesManName = name.getText().toString();
                String salesManPhone = phone.getText().toString();
                HashMap<String, Object> salesManMap = new HashMap<>();
                if (!salesManName.isEmpty()
                        && !salesManPhone.isEmpty()) {
                    salesManMap.put(Constants.SALES_MAN_NAME, salesManName);
                    salesManMap.put(Constants.SALES_MAN_PHONE, salesManPhone);
                    name.setText("");
                    phone.setText("");
                    SalesManApi.salesManDBRef.child(salesManPhone)
                            .updateChildren(salesManMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        DialogUtils.appToastShort(context,
                                                "Sales Man updated");
                                    } else {
                                        DialogUtils.appToastShort(context,
                                                "Sales Man not updated");
                                    }
                                }
                            });
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
