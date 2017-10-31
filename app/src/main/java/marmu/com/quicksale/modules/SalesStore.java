package marmu.com.quicksale.modules;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
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
import marmu.com.quicksale.activity.PartySalesDisplay;
import marmu.com.quicksale.api.FireBaseAPI;
import marmu.com.quicksale.utils.Constants;
import marmu.com.quicksale.utils.GenerateSalesReport;
import marmu.com.quicksale.utils.Permissions;


/**
 * Created by azharuddin on 1/8/17.
 */
@SuppressWarnings({"unchecked", "deprecation"})
@SuppressLint("SimpleDateFormat")
public class SalesStore implements Serializable {

    private static String TAG = "Sales";
    private static List<String> salesWiseReport = new ArrayList<>();
    private static List<String> routeWise = new ArrayList<>();
    private static List<String> routeList = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    public static void evaluate(Context context, View itemView) {
        routeWise.clear();
        routeWise.add(Constants.ALL);
        routeWise.add(Constants.ROUTE);
        salesWiseReport.clear();
        salesWiseReport.add(Constants.PARTY);
        salesWiseReport.add(Constants.PRODUCT);
        clearTable(context, itemView);
        try {
            final TextView datePicker = itemView.findViewById(R.id.et_date_picker);

            Date currentDate = new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(currentDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (month <= 9) {
                datePicker.setText(day + "/" + "0" + (month) + "/" + year);
            } else {
                datePicker.setText(day + "/" + (month) + "/" + year);
            }
            setSpinner(context, itemView);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        datePicker(context, itemView);
        generateReport(context, itemView);
    }

    private static void generateReport(final Context context, final View itemView) {
        TextView generateReport = itemView.findViewById(R.id.tv_generate_report);
        generateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Permissions.EXTERNAL_STORAGE(context)) {
                    if (partyCustomerName.size() > 0) {
                        final Spinner spinner1 = itemView.findViewById(R.id.spinner1);
                        final Spinner spinner2 = itemView.findViewById(R.id.spinner2);
                        final TextView datePicker = itemView.findViewById(R.id.et_date_picker);
                        Log.e("generateReport", spinner2.toString());
                        new GenerateSalesReport().generateSalesReport(
                                context,
                                spinner2.getSelectedItem() != null ? spinner2.getSelectedItem().toString() : spinner1.getSelectedItem().toString(),
                                datePicker.getText().toString(),
                                cashAmount, totalBill,
                                partyCustomerName, partiesNetTotal,
                                partiesItems, partiesItemsRate,
                                partiesItemsTotal, partiesBillNo,
                                partiesBillDate, partiesGST, partiesAmountReceived);
                    }
                }
            }
        });
    }

    private static void clearTable(Context context, View itemView) {
        TableLayout productTable = itemView.findViewById(R.id.tl_product);
        productTable.removeAllViews();
        TableLayout partyTable = itemView.findViewById(R.id.tl_party);
        partyTable.removeAllViews();
    }

    private static void setup(Context context, View itemView, String pickedDate,
                              String selectedRoute,
                              String routeList,
                              String selectedItem) throws ParseException {
        LinearLayout productContainer = itemView.findViewById(R.id.product_container);
        LinearLayout partyContainer = itemView.findViewById(R.id.party_container);
        productContainer.setVisibility(View.GONE);
        partyContainer.setVisibility(View.GONE);
        if (selectedItem.equals(Constants.PRODUCT)) {
            productContainer.setVisibility(View.VISIBLE);
            if (selectedRoute.equalsIgnoreCase(Constants.ALL)) {
                //populateProduct(context, itemView, pickedDate);
            } else {
                //populateProduct(context, itemView, pickedDate, routeList);
            }
        } else if (selectedItem.equals(Constants.PARTY)) {
            partyContainer.setVisibility(View.VISIBLE);
            if (selectedRoute.equalsIgnoreCase(Constants.ALL)) {
                populateParty(context, itemView, pickedDate);
            } else {
            }
        }
    }

    private static void getRouteList(String pickedDate) {
        final HashMap<String, Object> partiesBillDate = new HashMap<>();
        HashMap<String, Object> bill = FireBaseAPI.billing;
        for (String key : bill.keySet()) {
            HashMap<String, Object> party = (HashMap<String, Object>) bill.get(key);
            for (String partyKey : party.keySet()) {
                HashMap<String, Object> partyName = (HashMap<String, Object>) party.get(partyKey);
                if (!routeList.contains(partyName.get("sold_route").toString())) {
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        Date soldDate = formatter.parse(partyName.get("sold_date").toString());
                        Date pickedDateDummy = formatter.parse(pickedDate);
                        if (pickedDateDummy.compareTo(soldDate) == 0) {
                            routeList.add(partyName.get("sold_route").toString());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void setSpinner(final Context context, final View itemView) throws ParseException {
        final Spinner spinner1 = itemView.findViewById(R.id.spinner1);
        final Spinner spinner2 = itemView.findViewById(R.id.spinner2);
        final Spinner spinner3 = itemView.findViewById(R.id.spinner3);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView datePicker = itemView.findViewById(R.id.et_date_picker);
                if (!datePicker.getText().toString().isEmpty()) {
                    clearTable(context, itemView);
                    try {
                        String pickedDate = datePicker.getText().toString();
                        if (spinner1.getSelectedItem().toString().equalsIgnoreCase(Constants.ROUTE)) {
                            getRouteList(pickedDate);
                        } else {
                            routeList.clear();
                        }
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(context,
                                android.R.layout.simple_spinner_item, routeList);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(adapter2);
                        setup(context, itemView, pickedDate,
                                spinner1.getSelectedItem() != null ? spinner1.getSelectedItem().toString() : null,
                                spinner2.getSelectedItem() != null ? spinner2.getSelectedItem().toString() : null,
                                spinner3.getSelectedItem() != null ? spinner3.getSelectedItem().toString() : null);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, routeWise);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        /*RouteList*/
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView datePicker = itemView.findViewById(R.id.et_date_picker);
                if (!datePicker.getText().toString().isEmpty()) {
                    clearTable(context, itemView);
                    try {
                        setup(context, itemView, datePicker.getText().toString(),
                                spinner1.getSelectedItem() != null ? spinner1.getSelectedItem().toString() : null,
                                spinner2.getSelectedItem() != null ? spinner2.getSelectedItem().toString() : null,
                                spinner3.getSelectedItem() != null ? spinner3.getSelectedItem().toString() : null);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /*Product or party*/
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView datePicker = itemView.findViewById(R.id.et_date_picker);
                if (!datePicker.getText().toString().isEmpty()) {
                    clearTable(context, itemView);
                    try {
                        setup(context, itemView, datePicker.getText().toString(),
                                spinner1.getSelectedItem() != null ? spinner1.getSelectedItem().toString() : null,
                                spinner2.getSelectedItem() != null ? spinner2.getSelectedItem().toString() : null,
                                spinner3.getSelectedItem() != null ? spinner3.getSelectedItem().toString() : null);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, salesWiseReport);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);
    }

    @SuppressLint("SimpleDateFormat")
    private static void datePicker(final Context context, final View itemView) {
        final TextView datePicker = itemView.findViewById(R.id.et_date_picker);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTable(context, itemView);
                final Calendar c = Calendar.getInstance();
                final int mYear = c.get(Calendar.YEAR);
                final int mMonth = c.get(Calendar.MONTH);
                final int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
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
                                    Date pickedDate = formatter.parse((pDay + "/" + pMonth + "/" + pYear));
                                    Date currentDate = formatter.parse((cDay + "/" + cMonth + "/" + cYear));
                                    if (pickedDate.compareTo(currentDate) <= 0) {
                                        if ((monthOfYear + 1) <= 9) {
                                            datePicker.setText(dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year);
                                        } else {
                                            datePicker.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                        }
                                        datePicker.setError(null);
                                        datePicker.clearFocus();
                                        setSpinner(context, itemView);
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

    private static int cashAmount = 0;
    private static int totalBill = 0;

    private static HashMap<String, Object> partyCustomerName = new HashMap<>();
    private static HashMap<String, Object> partiesSalesMan = new HashMap<>();
    private static HashMap<String, Object> partiesNetTotal = new HashMap<>();
    private static HashMap<String, Object> partiesItems = new HashMap<>();
    private static HashMap<String, Object> partiesItemsRate = new HashMap<>();
    private static HashMap<String, Object> partiesItemsTotal = new HashMap<>();
    private static HashMap<String, Object> partiesBillNo = new HashMap<>();
    private static HashMap<String, Object> partiesBillDate = new HashMap<>();
    private static HashMap<String, Object> partiesGST = new HashMap<>();
    private static HashMap<String, Object> partiesAmountReceived = new HashMap<>();

    private static void populateParty(final Context context, final View itemView, String pickedDate) throws ParseException {
        try {
            cashAmount = 0;
            totalBill = 0;

            partyCustomerName = new HashMap<>();
            partiesSalesMan = new HashMap<>();
            partiesNetTotal = new HashMap<>();
            partiesItems = new HashMap<>();
            partiesItemsRate = new HashMap<>();
            partiesItemsTotal = new HashMap<>();
            partiesBillNo = new HashMap<>();
            partiesBillDate = new HashMap<>();
            partiesGST = new HashMap<>();
            partiesAmountReceived = new HashMap<>();
            // HashMap<String, Object> bill = FireBaseAPI.billing;
            FirebaseFirestore.getInstance()
                    .collection("billing")
                    .orderBy("bill_no", Query.Direction.ASCENDING)
                    .whereEqualTo("sold_date", pickedDate)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                TableLayout tableLayout = itemView.findViewById(R.id.tl_party);
                                tableLayout.removeAllViews();
                                for (final DocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    if (document.contains("amount_received")) {
                                        cashAmount += Integer.parseInt(document.get("amount_received").toString());
                                    }
                                    if (document.contains("net_total")) {
                                        totalBill += Integer.parseInt(document.get("net_total").toString());
                                    }

                                    TableRow tr = new TableRow(context);
                                    tr.setBackground(ContextCompat.getDrawable(context, R.drawable.box_white));

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
                                    TextView name = new TextView(context);
                                    name.setLayoutParams(params);

                                    name.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
                                    name.setPadding(16, 16, 16, 16);
                                    name.setText(document.get("customer_name").toString());
                                    name.setGravity(Gravity.CENTER);
                                    tr.addView(name);

                                    /* Product BillNo --> TextView */
                                    TextView salesMan = new TextView(context);
                                    salesMan.setLayoutParams(params);

                                    salesMan.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
                                    salesMan.setPadding(16, 16, 16, 16);
                                    salesMan.setText(document.get("bill_no").toString());
                                    salesMan.setGravity(Gravity.CENTER);
                                    tr.addView(salesMan);

                                    /* Product Price --> TextView */
                                    TextView rate = new TextView(context);
                                    rate.setLayoutParams(params);

                                    rate.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
                                    rate.setPadding(16, 16, 16, 16);
                                    rate.setText(document.get("net_total").toString());
                                    rate.setGravity(Gravity.CENTER);
                                    tr.addView(rate); // Adding textView to table-row.

                                    tr.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent partySalesDisplay = new Intent(context, PartySalesDisplay.class);
                                            partySalesDisplay.putExtra("party_name", document.get("customer_name").toString());
                                            partySalesDisplay.putExtra("net_total", Integer.parseInt(document.get("net_total").toString()));
                                            partySalesDisplay.putExtra("items_qty", (HashMap<String, Object>) document.get("sold_items"));
                                            partySalesDisplay.putExtra("items_rate", (HashMap<String, Object>) document.get("sold_items_rate"));
                                            partySalesDisplay.putExtra("items_total", (HashMap<String, Object>) document.get("sold_items_total"));
                                            partySalesDisplay.putExtra("party_bill_no", document.get("bill_no").toString());
                                            partySalesDisplay.putExtra("party_bill_date", document.get("sold_date").toString());
                                            if (partiesGST.size() > 0)
                                                partySalesDisplay.putExtra("party_gst", document.get("customer_gst").toString());
                                            if (document.contains("amount_received"))
                                                partySalesDisplay.putExtra("amount_received", Integer.parseInt(document.get("amount_received").toString()));

                                            context.startActivity(partySalesDisplay);
                                        }
                                    });

                                    // Add the TableRow to the TableLayout
                                    tableLayout.addView(tr);

                                }
                                TextView tvCashAmount = itemView.findViewById(R.id.tv_cash_sale);
                                tvCashAmount.setText("");
                                tvCashAmount.append("Cash sale: " + cashAmount);

                                TextView tvCreditAmount = itemView.findViewById(R.id.tv_credit_sale);
                                tvCreditAmount.setText("");
                                tvCreditAmount.append("Credit sale: " + (totalBill - cashAmount));
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}