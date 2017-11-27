package azhar.com.quicksale.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import azhar.com.quicksale.R;
import azhar.com.quicksale.adapter.CustomerAdapter;
import azhar.com.quicksale.api.CustomerApi;
import azhar.com.quicksale.model.CustomerModel;

@SuppressWarnings("unchecked")
public class viewCustomerFragment extends Fragment {


    public viewCustomerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_view_customer, container, false);
        changeMapToList();
        return rootView;
    }

    private void changeMapToList() {
        List<CustomerModel> customerList = new ArrayList<>();
        HashMap<String, Object> customer = CustomerApi.customer;
        if (customer != null) {
            for (String key : customer.keySet()) {
                HashMap<String, Object> customerDetails = (HashMap<String, Object>) customer.get(key);
                customerList.add(new CustomerModel(key,
                        (String) customerDetails.get("customer_name"),
                        (String) customerDetails.get("customer_address"),
                        (String) customerDetails.get("customer_gst")));
            }
        }
        populateCustomer(customerList);
    }

    private void populateCustomer(List<CustomerModel> customerList) {
        CustomerAdapter adapter = new CustomerAdapter(getContext(), customerList);
        RecyclerView customerView = rootView.findViewById(R.id.rv_customer);
        customerView.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        customerView.setLayoutManager(layoutManager);
        customerView.setItemAnimator(new DefaultItemAnimator());
        customerView.setAdapter(adapter);
    }
}
