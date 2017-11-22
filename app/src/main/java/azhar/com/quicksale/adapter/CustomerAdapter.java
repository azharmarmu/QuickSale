package azhar.com.quicksale.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import azhar.com.quicksale.R;
import azhar.com.quicksale.api.FireBaseAPI;
import azhar.com.quicksale.model.CustomerModel;

/**
 * Created by azharuddin on 24/7/17.
 */

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder> {

    private Context context;
    private List<CustomerModel> customerList;

    public CustomerAdapter(Context context, List<CustomerModel> customerList) {
        this.context = context;
        this.customerList = customerList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_common,
                        parent, false);

        return new CustomerAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CustomerModel customer = customerList.get(position);
        holder.customerName.setText(customer.getName());

        holder.customerEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog(customer, position);
            }
        });

        holder.customerDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FireBaseAPI.customerDBRef.child(customer.getKey()).removeValue();
                customerList.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView customerName;
        TextView customerEdit, customerDelete;

        MyViewHolder(View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.name);
            customerEdit = itemView.findViewById(R.id.view_edit);
            customerDelete = itemView.findViewById(R.id.delete);
        }
    }

    @SuppressLint("InflateParams")
    private void alertDialog(CustomerModel customer, final int position) {
        final String key = customer.getKey();
        final String name = customer.getName();
        final String phone = customer.getPhone();
        String gst = customer.getGst();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final HashMap<String, Object> customerMap = new HashMap<>();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_customer, null);
        dialogBuilder.setView(dialogView);

        final EditText etName = dialogView.findViewById(R.id.et_customer_name);
        final EditText etPhone = dialogView.findViewById(R.id.et_customer_phone);
        final EditText etGst = dialogView.findViewById(R.id.et_customer_gst);

        etName.setText(name);
        etPhone.setText(phone);
        etGst.setText(gst);

        dialogBuilder.setTitle("Details");
        dialogBuilder.setMessage("Edit Customer");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String CustomerName = etName.getText().toString();
                String CustomerPhone = etPhone.getText().toString();
                String CustomerGst = etGst.getText().toString();
                if (!CustomerName.isEmpty() && !CustomerPhone.isEmpty() && !CustomerGst.isEmpty()) {
                    FireBaseAPI.customerDBRef.child(key).removeValue();
                    customerList.remove(position);
                    customerList.add(new CustomerModel(key, CustomerName, CustomerPhone, CustomerGst));
                    customerMap.put("customer_name", CustomerName);
                    customerMap.put("customer_phone", CustomerPhone);
                    customerMap.put("customer_gst", CustomerGst);
                    FireBaseAPI.customerDBRef.child(key).updateChildren(customerMap);
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
