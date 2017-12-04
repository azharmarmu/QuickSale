package marmu.com.quicksale.modules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import marmu.com.quicksale.R;
import marmu.com.quicksale.activity.LandingActivity;
import marmu.com.quicksale.activity.PartySalesDisplay;
import marmu.com.quicksale.utils.Constants;
import marmu.com.quicksale.utils.DialogUtils;
import marmu.com.quicksale.utils.Permissions;

/**
 * Created by azharuddin on 1/8/17.
 */

@SuppressWarnings({"unchecked", "deprecation"})
@SuppressLint("SimpleDateFormat")
public class SalesStore implements Serializable {

    //private String TAG = "Sales";
    private List<String> routeList = new ArrayList<>();

    private String RouteTAG = Constants.ALL;
    private String SalesTAG = Constants.PARTY;
    private String currentRoute;

    private Activity activity;
    private View itemView;

    private TextView datePicker, allTAG, routeTAG, partyTAG, productTAG, tvCashAmount, tvCreditAmount;
    private RelativeLayout spinnerLayout;
    private TableLayout productTable, partyTable;

    @SuppressLint("SetTextI18n")
    public void evaluate(LandingActivity activity, View itemView) {
        try {

            this.activity = activity;
            this.itemView = itemView;

            initViews();
            currentDate();

            routeTagClickHandle();
            salesTagClickHandle();

            datePicker();
            generateReport();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        datePicker = itemView.findViewById(R.id.et_date_picker);
        allTAG = itemView.findViewById(R.id.tvAllTAG);
        routeTAG = itemView.findViewById(R.id.tvRouteTAG);
        partyTAG = itemView.findViewById(R.id.tvPartyTAG);
        productTAG = itemView.findViewById(R.id.tvProductTAG);
        spinnerLayout = itemView.findViewById(R.id.spinner_layout);
        partyTable = itemView.findViewById(R.id.tl_party);
        productTable = itemView.findViewById(R.id.tl_product);
        tvCashAmount = itemView.findViewById(R.id.tv_cash_sale);
        tvCreditAmount = itemView.findViewById(R.id.tv_credit_sale);
    }

    private void currentDate() throws ParseException {
        Date currentDate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (month <= 9) {
            datePicker.setText("");
            datePicker.append(day + "/" + "0" + (month) + "/" + year);
        } else {
            datePicker.setText("");
            datePicker.append(day + "/" + (month) + "/" + year);
        }
        populate(datePicker.getText().toString());
    }

    private void routeTagClickHandle() {
        allTAG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouteTAG = Constants.ALL;
                allTAG.setBackground(activity.getResources().getDrawable(R.drawable.box_primary_corner));
                routeTAG.setBackground(activity.getResources().getDrawable(R.drawable.box_white_thick_border_corner_ripple));
                spinnerLayout.setVisibility(View.GONE);
                try {
                    populate(datePicker.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        routeTAG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouteTAG = Constants.ROUTE;
                routeTAG.setBackground(activity.getResources().getDrawable(R.drawable.box_primary_corner));
                allTAG.setBackground(activity.getResources().getDrawable(R.drawable.box_white_thick_border_corner_ripple));
                spinnerLayout.setVisibility(View.VISIBLE);
                getBillingData(datePicker.getText().toString());
            }
        });
    }

    private void salesTagClickHandle() {
        partyTAG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalesTAG = Constants.PARTY;
                partyTAG.setBackground(activity.getResources().getDrawable(R.drawable.box_primary_corner));
                productTAG.setBackground(activity.getResources().getDrawable(R.drawable.box_white_thick_border_corner_ripple));
                try {
                    populate(datePicker.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        productTAG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalesTAG = Constants.PRODUCT;
                productTAG.setBackground(activity.getResources().getDrawable(R.drawable.box_primary_corner));
                partyTAG.setBackground(activity.getResources().getDrawable(R.drawable.box_white_thick_border_corner_ripple));
                try {
                    populate(datePicker.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //Show routes of the day
    private void getBillingData(final String pickedDate) {
        FirebaseFirestore.getInstance()
                .collection("billing")
                .orderBy("billNo", Query.Direction.ASCENDING)
                .whereEqualTo("date", pickedDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        routeList.clear();
                        if (task.isSuccessful()) {
                            for (final DocumentSnapshot document : task.getResult()) {
                                if (document.contains("route")) {
                                    routeList.add(document.get("route").toString());
                                }
                            }
                            try {
                                setSpinner();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void setSpinner() throws ParseException {
        final Spinner spinner = itemView.findViewById(R.id.spinner);
        /*RouteList*/
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!datePicker.getText().toString().isEmpty()) {
                    try {
                        currentRoute = spinner.getItemAtPosition(position).toString();
                        populate(datePicker.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item, routeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @SuppressLint("SimpleDateFormat")
    private void datePicker() {
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                final int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                final int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String pYear = String.valueOf(year);
                                String pMonth = String.valueOf(monthOfYear + 1);
                                String pDay = String.valueOf(dayOfMonth);

                                String cYear = String.valueOf(mYear);
                                String cMonth = String.valueOf(mMonth + 1);
                                String cDay = String.valueOf(mDay);

                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    Date chosenDate = formatter.parse((pDay + "/" + pMonth + "/" + pYear));
                                    Date currentDate = formatter.parse((cDay + "/" + cMonth + "/" + cYear));
                                    if (chosenDate.compareTo(currentDate) <= 0) {
                                        if ((monthOfYear + 1) <= 9) {
                                            datePicker.setText("");
                                            datePicker.append(dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year);
                                        } else {
                                            datePicker.setText("");
                                            datePicker.append(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                        }
                                        datePicker.setError(null);
                                        datePicker.clearFocus();
                                        if (RouteTAG.equalsIgnoreCase(Constants.ROUTE))
                                            getBillingData(datePicker.getText().toString());
                                        else {
                                            populate(datePicker.getText().toString());
                                        }
                                    } else {
                                        datePicker.setError("Choose Valid date");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void generateReport() {
        TextView generateReport = itemView.findViewById(R.id.tv_generate_report);
        generateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection StatementWithEmptyBody
                if (Permissions.EXTERNAL_STORAGE(activity)) {
                    /*if (partyCustomerName.size() > 0) {
                        *//*final Spinner spinner1 = itemView.findViewById(R.id.spinner1);
                        final Spinner spinner2 = itemView.findViewById(R.id.spinner2);
                        final TextView datePicker = itemView.findViewById(R.id.et_date_picker);
                        Log.e("generateReport", spinner2.toString());
                        new GenerateSalesReport().generateSalesReport(
                                activity,
                                spinner2.getSelectedItem() != null ? spinner2.getSelectedItem().toString() : spinner1.getSelectedItem().toString(),
                                datePicker.getText().toString(),
                                cashAmount, totalBill,
                                partyCustomerName, partiesNetTotal,
                                partiesItems, partiesItemsRate,
                                partiesItemsTotal, partiesBillNo,
                                partiesBillDate, partiesGST, partiesAmountReceived);*//*
                    }*/
                }
            }
        });
    }


    /*----------------------------------------------------------------------------------------------------------------------------*/


    private void populate(String pickedDate) throws ParseException {
        try {

            partyTable.removeAllViews();
            productTable.removeAllViews();
            tvCashAmount.setText("");
            tvCreditAmount.setText("");

            DialogUtils.showProgressDialog(activity, "Loading...");

            if (RouteTAG.equalsIgnoreCase(Constants.ROUTE)) {
                FirebaseFirestore.getInstance()
                        .collection("billing")
                        .orderBy("billNo", Query.Direction.ASCENDING)
                        .whereEqualTo("date", pickedDate)
                        .whereEqualTo("route", currentRoute)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                DialogUtils.dismissProgressDialog();
                                if (task.isSuccessful()) {
                                    LinearLayout productContainer = itemView.findViewById(R.id.product_container);
                                    LinearLayout partyContainer = itemView.findViewById(R.id.party_container);
                                    productContainer.setVisibility(View.GONE);
                                    partyContainer.setVisibility(View.GONE);
                                    if (SalesTAG.equalsIgnoreCase(Constants.PARTY)) {
                                        partyContainer.setVisibility(View.VISIBLE);
                                        populateParty(task);
                                    } else if (SalesTAG.equalsIgnoreCase(Constants.PRODUCT)) {
                                        productContainer.setVisibility(View.VISIBLE);
                                        populateProduct(task);
                                    }
                                }
                            }
                        });

            } else {
                FirebaseFirestore.getInstance()
                        .collection("billing")
                        .orderBy("billNo", Query.Direction.ASCENDING)
                        .whereEqualTo("date", pickedDate)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                DialogUtils.dismissProgressDialog();
                                if (task.isSuccessful()) {
                                    LinearLayout productContainer = itemView.findViewById(R.id.product_container);
                                    LinearLayout partyContainer = itemView.findViewById(R.id.party_container);
                                    productContainer.setVisibility(View.GONE);
                                    partyContainer.setVisibility(View.GONE);
                                    if (SalesTAG.equalsIgnoreCase(Constants.PARTY)) {
                                        partyContainer.setVisibility(View.VISIBLE);
                                        populateParty(task);
                                    } else if (SalesTAG.equalsIgnoreCase(Constants.PRODUCT)) {
                                        productContainer.setVisibility(View.VISIBLE);
                                        populateProduct(task);
                                    }
                                }
                            }
                        });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateParty(Task<QuerySnapshot> task) {
        int cashAmount = 0;
        int totalBill = 0;
        for (final DocumentSnapshot document : task.getResult()) {
            //Log.d(TAG, document.getId() + " => " + document.getData());
            if (document.contains("amountReceived")) {
                cashAmount += Integer.parseInt(document.get("amountReceived").toString());
            }
            if (document.contains("netTotal")) {
                totalBill += Integer.parseInt(document.get("netTotal").toString());
            }

            TableRow tr = new TableRow(activity);
            tr.setBackground(ContextCompat.getDrawable(activity, R.drawable.box_white));

            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tr.setPadding(16, 16, 16, 16);
            tr.setWeightSum(3);

                                    /*Params*/
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            params.weight = 1.0f;

            /* Product Name --> TextView */
            TextView name = new TextView(activity);
            name.setLayoutParams(params);

            HashMap<String, Object> customerDetails = (HashMap<String, Object>) document.get("customer");
            name.setTextColor(activity.getResources().getColor(R.color.colorLightBlack));
            name.setPadding(16, 16, 16, 16);
            name.setText(customerDetails.get("name").toString());
            name.setGravity(Gravity.CENTER);
            tr.addView(name);

            /* Product BillNo --> TextView */
            TextView salesMan = new TextView(activity);
            salesMan.setLayoutParams(params);

            salesMan.setTextColor(activity.getResources().getColor(R.color.colorLightBlack));
            salesMan.setPadding(16, 16, 16, 16);
            salesMan.setText(document.get("billNo").toString());
            salesMan.setGravity(Gravity.CENTER);
            tr.addView(salesMan);

            /* Product Price --> TextView */
            TextView rate = new TextView(activity);
            rate.setLayoutParams(params);

            rate.setTextColor(activity.getResources().getColor(R.color.colorLightBlack));
            rate.setPadding(16, 16, 16, 16);
            rate.setText(document.get("netTotal").toString());
            rate.setGravity(Gravity.CENTER);
            tr.addView(rate); // Adding textView to table-row.

            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, Object> items = (HashMap<String, Object>) document.get("items");
                    HashMap<String, Object> customer = (HashMap<String, Object>) document.get("customer");

                    HashMap<String, Object> salesDisplay = new HashMap<>();

                    salesDisplay.put("party", customer);
                    salesDisplay.put("items", items);
                    salesDisplay.put("netTotal", document.get("netTotal").toString());
                    salesDisplay.put("partyBillNo", document.get("billNo").toString());
                    salesDisplay.put("date", document.get("date").toString());
                    if (document.contains("amountReceived")) {
                        salesDisplay.put("amountReceived", document.get("amountReceived").toString());
                    }

                    //navigate to PartyDisplay activity
                    Intent partySalesDisplay = new Intent(activity, PartySalesDisplay.class);
                    partySalesDisplay.putExtra("billDetails", salesDisplay);
                    activity.startActivity(partySalesDisplay);

                }
            });

            // Add the TableRow to the TableLayout
            partyTable.addView(tr);

        }
        tvCashAmount.setText("");
        tvCashAmount.append("Cash sale: " + cashAmount);

        tvCreditAmount.setText("");
        tvCreditAmount.append("Credit sale: " + (totalBill - cashAmount));
    }

    private void populateProduct(Task<QuerySnapshot> task) {
        int cashAmount = 0;
        int totalBill = 0;
        HashMap<String, Object> wholeItems = new HashMap<>();
        for (final DocumentSnapshot document : task.getResult()) {
            //Log.d(TAG, document.getId() + " => " + document.getData());
            if (document.contains("amountReceived")) {
                cashAmount += Integer.parseInt(document.get("amountReceived").toString());
            }
            if (document.contains("netTotal")) {
                totalBill += Integer.parseInt(document.get("netTotal").toString());
            }

            if (document.contains("items")) {
                HashMap<String, Object> items = (HashMap<String, Object>) document.get("items");
                for (String key : items.keySet()) {
                    HashMap<String, Object> itemsDetails = (HashMap<String, Object>) items.get(key);
                    if (wholeItems.containsKey(key)) { // ---> Update new Data
                        HashMap<String, Object> currentItem = (HashMap<String, Object>) wholeItems.get(key);
                        String name = currentItem.get("name").toString();
                        int qty = Integer.parseInt(currentItem.get("qty").toString()) +
                                Integer.parseInt(itemsDetails.get("qty").toString());
                        int amount = Integer.parseInt(currentItem.get("total").toString()) +
                                Integer.parseInt(itemsDetails.get("total").toString());
                        currentItem.put("name", name);
                        currentItem.put("qty", qty);
                        currentItem.put("total", amount);
                        wholeItems.remove(key);
                        wholeItems.put(key, currentItem);
                    } else { // ---> Insert new Data
                        HashMap<String, Object> currentItem = new HashMap<>();
                        String name = itemsDetails.get("name").toString();
                        int qty = Integer.parseInt(itemsDetails.get("qty").toString());
                        int amount = Integer.parseInt(itemsDetails.get("total").toString());
                        currentItem.put("name", name);
                        currentItem.put("qty", qty);
                        currentItem.put("total", amount);
                        wholeItems.put(key, currentItem);
                    }
                }
            }
        }

        for (String key : wholeItems.keySet()) {
            TableRow tr = new TableRow(activity);
            tr.setBackground(ContextCompat.getDrawable(activity, R.drawable.box_white));

            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tr.setPadding(16, 16, 16, 16);
            tr.setWeightSum(3);

                                    /*Params*/
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            params.weight = 1.0f;

            /* Product Name --> TextView */
            TextView name = new TextView(activity);
            name.setLayoutParams(params);

            HashMap<String, Object> itemsDetails = (HashMap<String, Object>) wholeItems.get(key);
            name.setTextColor(activity.getResources().getColor(R.color.colorLightBlack));
            name.setPadding(16, 16, 16, 16);
            name.setText(itemsDetails.get("name").toString());
            name.setGravity(Gravity.CENTER);
            tr.addView(name);

            /* Product BillNo --> TextView */
            TextView salesMan = new TextView(activity);
            salesMan.setLayoutParams(params);

            salesMan.setTextColor(activity.getResources().getColor(R.color.colorLightBlack));
            salesMan.setPadding(16, 16, 16, 16);
            salesMan.setText(itemsDetails.get("qty").toString());
            salesMan.setGravity(Gravity.CENTER);
            tr.addView(salesMan);

            /* Product Price --> TextView */
            TextView rate = new TextView(activity);
            rate.setLayoutParams(params);

            rate.setTextColor(activity.getResources().getColor(R.color.colorLightBlack));
            rate.setPadding(16, 16, 16, 16);
            rate.setText(itemsDetails.get("total").toString());
            rate.setGravity(Gravity.CENTER);
            tr.addView(rate); // Adding textView to table-row.

            productTable.addView(tr);
        }

        tvCashAmount.setText("");
        tvCashAmount.append("Cash sale: " + cashAmount);

        tvCreditAmount.setText("");
        tvCreditAmount.append("Credit sale: " + (totalBill - cashAmount));
    }
}