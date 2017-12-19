package azhar.com.quicksale.modules;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import azhar.com.quicksale.R;
import azhar.com.quicksale.activity.LandingActivity;
import azhar.com.quicksale.activity.PartySalesDisplay;
import azhar.com.quicksale.listeners.DateListener;
import azhar.com.quicksale.utils.Constants;
import azhar.com.quicksale.utils.DateUtils;
import azhar.com.quicksale.utils.DialogUtils;
import azhar.com.quicksale.reports.GenerateSalesReport;
import azhar.com.quicksale.utils.Permissions;


/**
 * Created by azharuddin on 1/8/17.
 */
@SuppressWarnings({"unchecked", "deprecation"})
@SuppressLint("SimpleDateFormat")
public class Sales implements DateListener {

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

            new DateUtils().dateListener(this);
            new DateUtils().currentDate(datePicker);
            new DateUtils().datePicker(activity, datePicker);

            routeTagClickHandle();
            salesTagClickHandle();

            populate(datePicker.getText().toString());
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
                .collection(Constants.BILLING)
                .orderBy(Constants.BILL_NO, Query.Direction.ASCENDING)
                .whereEqualTo(Constants.BILL_DATE, pickedDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        routeList.clear();
                        if (task.isSuccessful()) {
                            for (final DocumentSnapshot document : task.getResult()) {
                                if (document.contains(Constants.BILL_ROUTE) &&
                                        routeList.contains(document.get(Constants.BILL_ROUTE).toString())) {
                                    routeList.add(document.get(Constants.BILL_ROUTE).toString());
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


    private void generateReport() {
        TextView generateReport = itemView.findViewById(R.id.tv_generate_report);
        generateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection StatementWithEmptyBody
                if (Permissions.EXTERNAL_STORAGE(activity)) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setTitle(activity.getString(R.string.app_name));
                    dialog.setMessage("Do you want to Generate Report ?");
                    dialog.setCancelable(true);

                    //positive button
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new GenerateSalesReport().generateSalesReport(activity, salesTask,
                                    currentRoute, datePicker.getText().toString());
                            dialog.cancel();
                        }
                    });

                    //negative button
                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    dialog.show();
                }
            }
        });
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    private Task<QuerySnapshot> salesTask;

    private void populate(String pickedDate) throws ParseException {
        try {

            partyTable.removeAllViews();
            productTable.removeAllViews();
            tvCashAmount.setText("");
            tvCreditAmount.setText("");

            DialogUtils.showProgressDialog(activity, "Loading...");

            if (RouteTAG.equalsIgnoreCase(Constants.ROUTE)) {
                FirebaseFirestore.getInstance()
                        .collection(Constants.BILLING)
                        .orderBy(Constants.BILL_NO, Query.Direction.ASCENDING)
                        .whereEqualTo(Constants.BILL_DATE, pickedDate)
                        .whereEqualTo(Constants.BILL_ROUTE, currentRoute)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                DialogUtils.dismissProgressDialog();
                                if (task.isSuccessful()) {
                                    salesTask = task;
                                    LinearLayout productContainer = itemView.findViewById(R.id.product_container);
                                    LinearLayout partyContainer = itemView.findViewById(R.id.party_container);
                                    productContainer.setVisibility(View.GONE);
                                    partyContainer.setVisibility(View.GONE);
                                    if (SalesTAG.equalsIgnoreCase(Constants.PARTY)) {
                                        partyContainer.setVisibility(View.VISIBLE);
                                        populateParty();
                                    } else if (SalesTAG.equalsIgnoreCase(Constants.PRODUCT)) {
                                        productContainer.setVisibility(View.VISIBLE);
                                        populateProduct();
                                    }
                                }
                            }
                        });

            } else {
                FirebaseFirestore.getInstance()
                        .collection(Constants.BILLING)
                        .orderBy(Constants.BILL_NO, Query.Direction.ASCENDING)
                        .whereEqualTo(Constants.BILL_DATE, pickedDate)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                DialogUtils.dismissProgressDialog();
                                if (task.isSuccessful()) {
                                    salesTask = task;
                                    LinearLayout productContainer = itemView.findViewById(R.id.product_container);
                                    LinearLayout partyContainer = itemView.findViewById(R.id.party_container);
                                    productContainer.setVisibility(View.GONE);
                                    partyContainer.setVisibility(View.GONE);
                                    if (SalesTAG.equalsIgnoreCase(Constants.PARTY)) {
                                        partyContainer.setVisibility(View.VISIBLE);
                                        populateParty();
                                    } else if (SalesTAG.equalsIgnoreCase(Constants.PRODUCT)) {
                                        productContainer.setVisibility(View.VISIBLE);
                                        populateProduct();
                                    }
                                }
                            }
                        });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateParty() {
        int cashAmount = 0;
        int totalBill = 0;
        for (final DocumentSnapshot document : salesTask.getResult()) {
            //Log.d(TAG, document.getId() + " => " + document.getData());
            if (document.contains(Constants.BILL_AMOUNT_RECEIVED)) {
                cashAmount += Integer.parseInt(document.get(Constants.BILL_AMOUNT_RECEIVED).toString());
            }
            if (document.contains(Constants.BILL_NET_TOTAL)) {
                totalBill += Integer.parseInt(document.get(Constants.BILL_NET_TOTAL).toString());
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

            /* Party Name --> TextView */
            TextView name = new TextView(activity);
            name.setLayoutParams(params);

            HashMap<String, Object> customerDetails = (HashMap<String, Object>) document.get(Constants.CUSTOMER);
            name.setTextColor(activity.getResources().getColor(R.color.colorLightBlack));
            name.setPadding(16, 16, 16, 16);
            name.setText(customerDetails.get(Constants.CUSTOMER_NAME).toString());
            name.setGravity(Gravity.CENTER);
            tr.addView(name);

            /* Party BillNo --> TextView */
            TextView salesMan = new TextView(activity);
            salesMan.setLayoutParams(params);

            salesMan.setTextColor(activity.getResources().getColor(R.color.colorLightBlack));
            salesMan.setPadding(16, 16, 16, 16);
            salesMan.setText(document.get(Constants.BILL_NO).toString());
            salesMan.setGravity(Gravity.CENTER);
            tr.addView(salesMan);

            /* Party Price --> TextView */
            TextView rate = new TextView(activity);
            rate.setLayoutParams(params);

            rate.setTextColor(activity.getResources().getColor(R.color.colorLightBlack));
            rate.setPadding(16, 16, 16, 16);
            rate.setText(document.get(Constants.BILL_NET_TOTAL).toString());
            rate.setGravity(Gravity.CENTER);
            tr.addView(rate); // Adding textView to table-row.

            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, Object> items = (HashMap<String, Object>) document.get(Constants.BILL_SALES);
                    HashMap<String, Object> customer = (HashMap<String, Object>) document.get(Constants.BILL_CUSTOMER);

                    HashMap<String, Object> salesDisplay = new HashMap<>();

                    salesDisplay.put(Constants.BILL_CUSTOMER, customer);
                    salesDisplay.put(Constants.BILL_SALES, items);
                    salesDisplay.put(Constants.BILL_NET_TOTAL, document.get(Constants.BILL_NET_TOTAL).toString());
                    salesDisplay.put(Constants.BILL_NO, document.get(Constants.BILL_NO).toString());
                    salesDisplay.put(Constants.BILL_DATE, document.get(Constants.BILL_DATE).toString());
                    if (document.contains(Constants.BILL_AMOUNT_RECEIVED)) {
                        salesDisplay.put(Constants.BILL_AMOUNT_RECEIVED,
                                document.get(Constants.BILL_AMOUNT_RECEIVED).toString());
                    }

                    //navigate to PartyDisplay activity
                    Intent partySalesDisplay = new Intent(activity, PartySalesDisplay.class);
                    partySalesDisplay.putExtra(Constants.BILL_SALES, salesDisplay);
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

    private void populateProduct() {
        int cashAmount = 0;
        int totalBill = 0;
        HashMap<String, Object> wholeItems = new HashMap<>();
        for (final DocumentSnapshot document : salesTask.getResult()) {
            //Log.d(TAG, document.getId() + " => " + document.getData());
            if (document.contains(Constants.BILL_AMOUNT_RECEIVED)) {
                cashAmount += Integer.parseInt(document.get(Constants.BILL_AMOUNT_RECEIVED).toString());
            }
            if (document.contains(Constants.BILL_NET_TOTAL)) {
                totalBill += Integer.parseInt(document.get(Constants.BILL_NET_TOTAL).toString());
            }

            if (document.contains(Constants.BILL_SALES)) {
                HashMap<String, Object> items = (HashMap<String, Object>) document.get(Constants.BILL_SALES);
                for (String key : items.keySet()) {
                    HashMap<String, Object> itemsDetails = (HashMap<String, Object>) items.get(key);
                    if (wholeItems.containsKey(key)) { // ---> Update new Data
                        HashMap<String, Object> currentItem = (HashMap<String, Object>) wholeItems.get(key);
                        String name = currentItem.get(Constants.PRODUCT_NAME).toString();
                        int qty = Integer.parseInt(currentItem.get(Constants.PRODUCT_QTY).toString()) +
                                Integer.parseInt(itemsDetails.get(Constants.PRODUCT_QTY).toString());
                        int amount = Integer.parseInt(currentItem.get(Constants.PRODUCT_TOTAL).toString()) +
                                Integer.parseInt(itemsDetails.get(Constants.PRODUCT_TOTAL).toString());
                        currentItem.put(Constants.PRODUCT_NAME, name);
                        currentItem.put(Constants.PRODUCT_QTY, qty);
                        currentItem.put(Constants.PRODUCT_TOTAL, amount);
                        wholeItems.remove(key);
                        wholeItems.put(key, currentItem);
                    } else { // ---> Insert new Data
                        HashMap<String, Object> currentItem = new HashMap<>();
                        String name = itemsDetails.get(Constants.PRODUCT_NAME).toString();
                        int qty = Integer.parseInt(itemsDetails.get(Constants.PRODUCT_QTY).toString());
                        int amount = Integer.parseInt(itemsDetails.get(Constants.PRODUCT_TOTAL).toString());
                        currentItem.put(Constants.PRODUCT_NAME, name);
                        currentItem.put(Constants.PRODUCT_QTY, qty);
                        currentItem.put(Constants.PRODUCT_TOTAL, amount);
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
            name.setText(itemsDetails.get(Constants.PRODUCT_NAME).toString());
            name.setGravity(Gravity.CENTER);
            tr.addView(name);

            /* Product BillNo --> TextView */
            TextView salesMan = new TextView(activity);
            salesMan.setLayoutParams(params);

            salesMan.setTextColor(activity.getResources().getColor(R.color.colorLightBlack));
            salesMan.setPadding(16, 16, 16, 16);
            salesMan.setText(itemsDetails.get(Constants.PRODUCT_QTY).toString());
            salesMan.setGravity(Gravity.CENTER);
            tr.addView(salesMan);

            /* Product Price --> TextView */
            TextView rate = new TextView(activity);
            rate.setLayoutParams(params);

            rate.setTextColor(activity.getResources().getColor(R.color.colorLightBlack));
            rate.setPadding(16, 16, 16, 16);
            rate.setText(itemsDetails.get(Constants.PRODUCT_TOTAL).toString());
            rate.setGravity(Gravity.CENTER);
            tr.addView(rate); // Adding textView to table-row.

            productTable.addView(tr);
        }

        tvCashAmount.setText("");
        tvCashAmount.append("Cash sale: " + cashAmount);

        tvCreditAmount.setText("");
        tvCreditAmount.append("Credit sale: " + (totalBill - cashAmount));
    }

    @Override
    public void getDate(String date) throws ParseException {
        populate(date);
    }
}