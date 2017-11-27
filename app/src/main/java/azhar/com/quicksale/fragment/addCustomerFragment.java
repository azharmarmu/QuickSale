package azhar.com.quicksale.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

import azhar.com.quicksale.R;
import azhar.com.quicksale.api.CustomerApi;
import azhar.com.quicksale.utils.DialogUtils;

@SuppressWarnings("unchecked")
public class addCustomerFragment extends Fragment {

    public addCustomerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_customer, container, false);
        addCustomer();
        return rootView;
    }

    private void addCustomer() {
        TextView addCustomer = rootView.findViewById(R.id.btn_add_customer);
        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> customer = CustomerApi.customer;

                EditText name = rootView.findViewById(R.id.et_customer_name);
                EditText address = rootView.findViewById(R.id.et_customer_address);
                EditText gst = rootView.findViewById(R.id.et_customer_gst);

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
                                            DialogUtils.appToastShort(getContext(),
                                                    "Customer added");
                                        } else {
                                            DialogUtils.appToastShort(getContext(),
                                                    "Customer not added");
                                        }
                                    }
                                });
                    } else {
                        name.setError("Customer already exists");
                        name.requestFocus();
                    }
                }
            }
        });
    }
}
