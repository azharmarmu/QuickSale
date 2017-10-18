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
import marmu.com.quicksale.adapter.SalesManAdapter;
import marmu.com.quicksale.api.FireBaseAPI;
import marmu.com.quicksale.utils.Constants;
import marmu.com.quicksale.model.SalesManModel;

/**
 * Created by azharuddin on 24/7/17.
 */

public class Man {
    private static HashMap<String, Object> salesMan = new HashMap<>();
    private static List<SalesManModel> salesManList;

    public static void evaluate(Context context, View itemView) {
        salesMan = FireBaseAPI.salesMan;
        changeMapToList();
        populateSalesMan(context, itemView);
        addSalesMan(context, itemView);
    }

    private static void changeMapToList() {
        salesManList = new ArrayList<>();
        if (salesMan != null) {
            for (String key : salesMan.keySet()) {
                salesManList.add(new SalesManModel(key, String.valueOf(salesMan.get(key))));
            }
        }
    }

    private static void populateSalesMan(Context context, View itemView) {
        SalesManAdapter adapter = new SalesManAdapter(context, salesManList, Constants.EDIT);
        RecyclerView salesMan = itemView.findViewById(R.id.rv_sales_man);
        salesMan.removeAllViews();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        salesMan.setLayoutManager(layoutManager);
        salesMan.setItemAnimator(new DefaultItemAnimator());
        salesMan.setAdapter(adapter);
    }

    private static void addSalesMan(final Context context, final View itemView) {
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
                if (!SalesManName.isEmpty() && !SalesManPhone.isEmpty()) {
                    if (!salesMan.containsKey(SalesManName)) {
                        salesMan.put(SalesManName, SalesManPhone);
                        name.setText("");
                        phone.setText("");
                        FireBaseAPI.salesManDBRef.updateChildren(salesMan);
                        changeMapToList();
                        populateSalesMan(context, itemView);
                    } else {
                        name.setError("Already Exists");
                        name.requestFocus();
                    }
                }
            }
        });
    }
}
