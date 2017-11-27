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
import azhar.com.quicksale.adapter.SalesManAdapter;
import azhar.com.quicksale.api.SalesManApi;
import azhar.com.quicksale.model.SalesManModel;
import azhar.com.quicksale.utils.Constants;

@SuppressWarnings("unchecked")
public class viewSalesManFragment extends Fragment {


    public viewSalesManFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_view_sales_man, container, false);
        changeMapToList();
        return rootView;
    }

    private void changeMapToList() {
        List<SalesManModel> salesManList = new ArrayList<>();
        HashMap<String, Object> salesMan = SalesManApi.salesMan;
        if (salesMan != null) {
            for (String key : salesMan.keySet()) {
                HashMap<String, Object> salesManDetails = (HashMap<String, Object>) salesMan.get(key);
                salesManList.add(new SalesManModel(key,
                        (String) salesManDetails.get("sales_man_name")));
            }
        }
        populateSalesMan(salesManList);
    }

    private void populateSalesMan(List<SalesManModel> salesManList) {
        SalesManAdapter adapter = new SalesManAdapter(getContext(), salesManList, Constants.EDIT);
        RecyclerView salesMan = rootView.findViewById(R.id.rv_sales_man);
        salesMan.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        salesMan.setLayoutManager(layoutManager);
        salesMan.setItemAnimator(new DefaultItemAnimator());
        salesMan.setAdapter(adapter);
    }
}
