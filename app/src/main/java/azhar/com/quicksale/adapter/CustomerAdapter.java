package azhar.com.quicksale.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.List;

import azhar.com.quicksale.R;
import azhar.com.quicksale.api.CustomerApi;
import azhar.com.quicksale.model.CustomerModel;
import azhar.com.quicksale.utils.DialogUtils;

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
                editCustomerDialog(customer, position);
                //alertDialog(customer, position);
            }
        });

        holder.customerDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomerApi.customerDBRef.child(customer.getKey()).removeValue();
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
        String name = customer.getName();
        String phone = customer.getPhone();
        String gst = customer.getGst();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
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
                    CustomerApi.customerDBRef.child(key).removeValue();
                    customerList.remove(position);
                    customerList.add(new CustomerModel(key, CustomerName, CustomerPhone, CustomerGst));

                    HashMap<String, Object> customerMap = new HashMap<>();
                    customerMap.put("customer_name", CustomerName);
                    customerMap.put("customer_phone", CustomerPhone);
                    customerMap.put("customer_gst", CustomerGst);

                    CustomerApi.customerDBRef.child(key).updateChildren(customerMap);
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

    private void editCustomerDialog(final CustomerModel customerModel, int position) {
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setContentView(R.layout.fragment_add_customer);

        Window window = dialog.getWindow();
        assert window != null;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;

        TextView addCustomer = dialog.findViewById(R.id.btn_add_customer);
        addCustomer.setText("Update");
        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete customer
                String key = customerModel.getKey();
                CustomerApi.customerDBRef.child(key).removeValue();

                HashMap<String, Object> customer = CustomerApi.customer;

                EditText name = dialog.findViewById(R.id.et_customer_name);
                EditText address = dialog.findViewById(R.id.et_customer_address);
                EditText gst = dialog.findViewById(R.id.et_customer_gst);

                String customerName = name.getText().toString();
                String customerAddress = address.getText().toString();
                String customerGst = gst.getText().toString();
                HashMap<String, Object> customerMap = new HashMap<>();
                if (!customerName.isEmpty()
                        && !customerAddress.isEmpty()
                        && !customerGst.isEmpty()) {
                    if (!customer.containsKey(customerGst)) {
                        customerMap.put("customer_name", customerName);
                        customerMap.put("customer_address", customerAddress);
                        customerMap.put("customer_gst", customerGst);
                        name.setText("");
                        address.setText("");
                        gst.setText("");
                        CustomerApi.customerDBRef.child(customerGst)
                                .updateChildren(customerMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            DialogUtils.appToastShort(context,
                                                    "Customer updated");
                                        } else {
                                            DialogUtils.appToastShort(context,
                                                    "Customer not updated");
                                        }
                                    }
                                });
                    }
                } else {
                    name.setError("Customer already exists");
                    name.requestFocus();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
