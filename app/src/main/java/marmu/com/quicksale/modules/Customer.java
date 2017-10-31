package marmu.com.quicksale.modules;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import marmu.com.quicksale.R;
import marmu.com.quicksale.adapter.CustomerAdapter;
import marmu.com.quicksale.api.FireBaseAPI;
import marmu.com.quicksale.model.CustomerModel;

/**
 * Created by azharuddin on 24/7/17.
 */

@SuppressWarnings("unchecked")
public class Customer {
    private static HashMap<String, Object> customer = new HashMap<>();
    private static List<CustomerModel> customerList;

    public static void evaluate(Context context, View itemView) {
        customer = FireBaseAPI.customer;
        changeMapToList();
        populateCustomer(context, itemView);
        addCustomer(context, itemView);
    }

    private static void changeMapToList() {
        customerList = new ArrayList<>();
        if (customer != null) {
            for (String key : customer.keySet()) {
                HashMap<String, Object> customerDetails = (HashMap<String, Object>) customer.get(key);
                customerList.add(new CustomerModel(key,
                        (String) customerDetails.get("customer_name"),
                        (String) customerDetails.get("customer_address"),
                        (String) customerDetails.get("customer_gst")));
            }
        }
    }

    private static void populateCustomer(Context context, View itemView) {
        CustomerAdapter adapter = new CustomerAdapter(context, customerList);
        RecyclerView customerView = itemView.findViewById(R.id.rv_customer);
        customerView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        customerView.setLayoutManager(layoutManager);
        customerView.setItemAnimator(new DefaultItemAnimator());
        customerView.setAdapter(adapter);
    }

    private static void addCustomer(final Context context, final View itemView) {
        TextView addCustomer = itemView.findViewById(R.id.btn_add_customer);
        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customer.clear();
                customer = FireBaseAPI.customer;
                EditText name = itemView.findViewById(R.id.et_customer_name);
                EditText address = itemView.findViewById(R.id.et_customer_address);
                EditText gst = itemView.findViewById(R.id.et_customer_gst);
                String CustomerName = name.getText().toString();
                String CustomerAddress = address.getText().toString();
                String CustomerGst = gst.getText().toString();
                HashMap<String, Object> customerMap = new HashMap<>();
                if (!CustomerName.isEmpty() && !CustomerAddress.isEmpty() && !CustomerGst.isEmpty()) {
                    if (customer.size() > 0) {
                        for (String key : customer.keySet()) {
                            HashMap<String, Object> myCustomer = (HashMap<String, Object>) customer.get(key);
                            if (!myCustomer.get("customer_name").toString().equals(CustomerName)) {
                                customerMap.put("customer_name", CustomerName);
                                customerMap.put("customer_address", CustomerAddress);
                                customerMap.put("customer_gst", CustomerGst);
                                name.setText("");
                                address.setText("");
                                gst.setText("");
                                String myKey = FireBaseAPI.customerDBRef.push().getKey();
                                FireBaseAPI.customerDBRef.child(myKey).updateChildren(customerMap);
                                customer.put(myKey, customerMap);
                                changeMapToList();
                                populateCustomer(context, itemView);
                            } else {
                                name.setError("Already Exists");
                                name.requestFocus();
                            }
                        }
                    } else {
                        customerMap.put("customer_name", CustomerName);
                        customerMap.put("customer_address", CustomerAddress);
                        customerMap.put("customer_gst", CustomerGst);
                        name.setText("");
                        address.setText("");
                        gst.setText("");
                        String myKey = FireBaseAPI.customerDBRef.push().getKey();
                        FireBaseAPI.customerDBRef.child(myKey).updateChildren(customerMap);
                        customer.put(myKey, customerMap);
                        changeMapToList();
                        populateCustomer(context, itemView);
                    }

                }
            }
        });
    }
}
