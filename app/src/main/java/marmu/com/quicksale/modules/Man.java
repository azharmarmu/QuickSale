package marmu.com.quicksale.modules;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import marmu.com.quicksale.R;
import marmu.com.quicksale.activity.LandingActivity;
import marmu.com.quicksale.adapter.SalesManAdapter;
import marmu.com.quicksale.api.FireBaseAPI;
import marmu.com.quicksale.model.SalesManModel;
import marmu.com.quicksale.utils.Constants;
import marmu.com.quicksale.utils.DialogUtils;

/**
 * Created by azharuddin on 24/7/17.
 */

@SuppressWarnings("unchecked")
public class Man {
    private HashMap<String, Object> salesMan = new HashMap<>();
    private List<SalesManModel> salesManList;

    private Activity activity;
    private View itemView;

    public void evaluate(LandingActivity activity, View itemView) {

        this.activity = activity;
        this.itemView = itemView;

        salesMan = FireBaseAPI.salesMan;
        changeMapToList();
        populateSalesMan();
        addSalesMan();
    }

    private void changeMapToList() {
        salesManList = new ArrayList<>();
        if (salesMan != null) {
            for (String key : salesMan.keySet()) {
                HashMap<String, Object> salesManDetails = (HashMap<String, Object>) salesMan.get(key);
                salesManList.add(new SalesManModel(key,
                        (String) salesManDetails.get("salesMan_name"),
                        (String) salesManDetails.get("salesMan_phone")));
            }
        }
    }

    private void populateSalesMan() {
        SalesManAdapter adapter = new SalesManAdapter(activity, salesManList, Constants.EDIT);
        RecyclerView salesMan = itemView.findViewById(R.id.rv_sales_man);
        salesMan.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        salesMan.setLayoutManager(layoutManager);
        salesMan.setItemAnimator(new DefaultItemAnimator());
        salesMan.setAdapter(adapter);
    }

    private void addSalesMan() {
        TextView addMan = itemView.findViewById(R.id.btn_add_sales_man);
        addMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salesMan.clear();
                salesMan = FireBaseAPI.salesMan;
                EditText name = itemView.findViewById(R.id.et_sales_man_name);
                EditText phone = itemView.findViewById(R.id.et_sales_man_phone);
                String SalesManName = name.getText().toString();
                String SalesManPhone = phone.getText().toString();
                HashMap<String, Object> salesManMap = new HashMap<>();
                if (!SalesManName.isEmpty()
                        && !SalesManPhone.isEmpty()) {
                    if (salesMan.size() > 0) {
                        for (String key : salesMan.keySet()) {
                            HashMap<String, Object> mySalesMan = (HashMap<String, Object>) salesMan.get(key);
                            if (!mySalesMan.get("salesMan_name").toString().equals(key)) {
                                salesManMap.put("salesMan_name", SalesManName);
                                salesManMap.put("salesMan_phone", SalesManPhone);
                                name.setText("");
                                phone.setText("");
                                String myKey = FireBaseAPI.customerDBRef.push().getKey();
                                FireBaseAPI.customerDBRef.child(myKey).updateChildren(salesManMap);
                            } else {
                                name.setError("Already Exists");
                                name.requestFocus();
                            }
                        }
                    } else {
                        salesManMap.put("salesMan_name", SalesManName);
                        salesManMap.put("salesMan_phone", SalesManPhone);
                        name.setText("");
                        phone.setText("");
                        String myKey = FireBaseAPI.customerDBRef.push().getKey();
                        FireBaseAPI.customerDBRef.child(myKey).updateChildren(salesManMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            DialogUtils.appToastShort(activity, "SalesMan added");
                                        } else {
                                            DialogUtils.appToastShort(activity, "SalesMan not added");
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }
}
