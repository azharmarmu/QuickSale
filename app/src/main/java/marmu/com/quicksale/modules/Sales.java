package marmu.com.quicksale.modules;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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
public class Sales implements Serializable {

    private static List<String> salesWiseReport = new ArrayList<>();
    private static List<String> routeWise = new ArrayList<>();
    private static List<String> routeList = new ArrayList<>();

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

    private static void setup(Context context, View itemView, Date pickedDate,
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
                populateProduct(context, itemView, pickedDate);
            } else {
                populateProduct(context, itemView, pickedDate, routeList);
            }
        } else if (selectedItem.equals(Constants.PARTY)) {
            partyContainer.setVisibility(View.VISIBLE);
            if (selectedRoute.equalsIgnoreCase(Constants.ALL)) {
                populateParty(context, itemView, pickedDate);
            } else {
                populateParty(context, itemView, pickedDate, routeList);
            }
        }
    }

    private static void getRouteList(Date pickedDate) {
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
                        if (pickedDate.compareTo(soldDate) == 0) {
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
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        Date pickedDate = formatter.parse(datePicker.getText().toString());
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
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        Date pickedDate = formatter.parse(datePicker.getText().toString());
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

        /*Product or party*/
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView datePicker = itemView.findViewById(R.id.et_date_picker);
                if (!datePicker.getText().toString().isEmpty()) {
                    clearTable(context, itemView);
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        Date pickedDate = formatter.parse(datePicker.getText().toString());
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

    private static void populateParty(final Context context, View itemView, Date pickedDate) throws ParseException {
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
            HashMap<String, Object> bill = FireBaseAPI.billing;
            for (String key : bill.keySet()) {
                HashMap<String, Object> party = (HashMap<String, Object>) bill.get(key);
                for (String partyKey : party.keySet()) {
                    HashMap<String, Object> partyName = (HashMap<String, Object>) party.get(partyKey);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date soldDate = formatter.parse(partyName.get("sold_date").toString());
                    if (pickedDate.compareTo(soldDate) == 0) {
                        if (partyName.containsKey("amount_received")) {
                            cashAmount += Integer.parseInt(partyName.get("amount_received").toString());
                        }
                        if (partyName.containsKey("net_total")) {
                            totalBill += Integer.parseInt(partyName.get("net_total").toString());
                        }
                        partyCustomerName.put(partyKey, partyName.get("customer_name"));
                        partiesSalesMan.put(partyKey, partyName.get("sales_man_name"));
                        partiesNetTotal.put(partyKey, partyName.get("net_total"));
                        partiesItems.put(partyKey, partyName.get("sold_items"));
                        partiesItemsRate.put(partyKey, partyName.get("sold_items_rate"));
                        partiesItemsTotal.put(partyKey, partyName.get("sold_items_total"));
                        partiesBillNo.put(partyKey, partyName.get("bill_no"));
                        partiesBillDate.put(partyKey, partyName.get("sold_date"));
                        if (partyName.containsKey("customer_gst")) {
                            partiesGST.put(partyKey, partyName.get("customer_gst"));
                        } else {
                            partiesGST.put(partyKey, "UnRegistered");
                        }
                        partiesAmountReceived.put(partyKey, partyName.get("amount_received") != null ? partyName.get("amount_received") : "0");
                    }
                }
            }

            TextView tvCashAmount = itemView.findViewById(R.id.tv_cash_sale);
            tvCashAmount.setText("");
            tvCashAmount.append("Cash sale: " + cashAmount);

            TextView tvCreditAmount = itemView.findViewById(R.id.tv_credit_sale);
            tvCreditAmount.setText("");
            tvCreditAmount.append("Credit sale: " + (totalBill - cashAmount));

            if (partiesNetTotal.size() > 0) {
                TableLayout tableLayout = itemView.findViewById(R.id.tl_party);
                tableLayout.removeAllViews();
                for (final String prodKey : partiesNetTotal.keySet()) {
            /* Create a TableRow dynamically */
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
                    name.setText(partyCustomerName.get(prodKey).toString());
                    name.setGravity(Gravity.CENTER);
                    tr.addView(name);

            /* Product SalesMan --> TextView */
                    TextView salesMan = new TextView(context);
                    salesMan.setLayoutParams(params);

                    salesMan.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
                    salesMan.setPadding(16, 16, 16, 16);
                    salesMan.setText(partiesSalesMan.get(prodKey).toString());
                    salesMan.setGravity(Gravity.CENTER);
                    tr.addView(salesMan);

            /* Product Price --> TextView */
                    TextView rate = new TextView(context);
                    rate.setLayoutParams(params);

                    rate.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
                    rate.setPadding(16, 16, 16, 16);
                    rate.setText(partiesNetTotal.get(prodKey).toString());
                    rate.setGravity(Gravity.CENTER);
                    tr.addView(rate); // Adding textView to table-row.

                    tr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent partySalesDisplay = new Intent(context, PartySalesDisplay.class);
                            partySalesDisplay.putExtra("party_name", partyCustomerName.get(prodKey).toString());
                            partySalesDisplay.putExtra("net_total", Integer.parseInt(partiesNetTotal.get(prodKey).toString()));
                            partySalesDisplay.putExtra("items_qty", (HashMap<String, Object>) partiesItems.get(prodKey));
                            partySalesDisplay.putExtra("items_rate", (HashMap<String, Object>) partiesItemsRate.get(prodKey));
                            partySalesDisplay.putExtra("items_total", (HashMap<String, Object>) partiesItemsTotal.get(prodKey));
                            partySalesDisplay.putExtra("party_bill_no", partiesBillNo.get(prodKey).toString());
                            partySalesDisplay.putExtra("party_bill_date", partiesBillDate.get(prodKey).toString());
                            if (partiesGST.size() > 0)
                                partySalesDisplay.putExtra("party_gst", partiesGST.get(prodKey).toString());
                            partySalesDisplay.putExtra("amount_received", Integer.parseInt(partiesAmountReceived.get(prodKey).toString()));
                            context.startActivity(partySalesDisplay);
                        }
                    });

                    // Add the TableRow to the TableLayout
                    tableLayout.addView(tr);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    private static void populateParty(final Context context, View itemView, Date pickedDate, String route) throws ParseException {
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
            HashMap<String, Object> bill = FireBaseAPI.billing;
            for (String key : bill.keySet()) {
                HashMap<String, Object> party = (HashMap<String, Object>) bill.get(key);
                for (String partyKey : party.keySet()) {
                    HashMap<String, Object> partyName = (HashMap<String, Object>) party.get(partyKey);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date soldDate = formatter.parse(partyName.get("sold_date").toString());
                    if (route.equalsIgnoreCase(partyName.get("sold_route").toString()))
                        if (pickedDate.compareTo(soldDate) == 0) {
                            if (partyName.containsKey("amount_received")) {
                                cashAmount += Integer.parseInt(partyName.get("amount_received").toString());
                            }
                            if (partyName.containsKey("net_total")) {
                                totalBill += Integer.parseInt(partyName.get("net_total").toString());
                            }
                            partyCustomerName.put(partyKey, partyName.get("customer_name"));
                            partiesSalesMan.put(partyKey, partyName.get("sales_man_name"));
                            partiesNetTotal.put(partyKey, partyName.get("net_total"));
                            partiesItems.put(partyKey, partyName.get("sold_items"));
                            partiesItemsRate.put(partyKey, partyName.get("sold_items_rate"));
                            partiesItemsTotal.put(partyKey, partyName.get("sold_items_total"));
                            partiesBillNo.put(partyKey, partyName.get("bill_no"));
                            if (partyName.containsKey("customer_gst")) {
                                partiesGST.put(partyKey, partyName.get("customer_gst"));
                            } else {
                                partiesGST.put(partyKey, "UnRegistered");
                            }
                            partiesBillDate.put(partyKey, partyName.get("sold_date"));
                            partiesAmountReceived.put(partyKey, partyName.get("amount_received") != null ? partyName.get("amount_received") : "0");
                        }
                }
            }

            TextView tvCashAmount = itemView.findViewById(R.id.tv_cash_sale);
            tvCashAmount.setText("");
            tvCashAmount.append("Cash sale: " + cashAmount);

            TextView tvCreditAmount = itemView.findViewById(R.id.tv_credit_sale);
            tvCreditAmount.setText("");
            tvCreditAmount.append("Credit sale: " + (totalBill - cashAmount));

            if (partiesNetTotal.size() > 0) {
                TableLayout tableLayout = itemView.findViewById(R.id.tl_party);
                tableLayout.removeAllViews();
                for (final String prodKey : partiesNetTotal.keySet()) {
            /* Create a TableRow dynamically */
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
                    name.setText(partyCustomerName.get(prodKey).toString());
                    name.setGravity(Gravity.CENTER);
                    tr.addView(name);

            /* Product SalesMan --> TextView */
                    TextView salesMan = new TextView(context);
                    salesMan.setLayoutParams(params);

                    salesMan.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
                    salesMan.setPadding(16, 16, 16, 16);
                    salesMan.setText(partiesSalesMan.get(prodKey).toString());
                    salesMan.setGravity(Gravity.CENTER);
                    tr.addView(salesMan);

            /* Product Price --> TextView */
                    TextView rate = new TextView(context);
                    rate.setLayoutParams(params);

                    rate.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
                    rate.setPadding(16, 16, 16, 16);
                    rate.setText(partiesNetTotal.get(prodKey).toString());
                    rate.setGravity(Gravity.CENTER);
                    tr.addView(rate); // Adding textView to table-row.

                    tr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent partySalesDisplay = new Intent(context, PartySalesDisplay.class);
                            partySalesDisplay.putExtra("party_name", partyCustomerName.get(prodKey).toString());
                            partySalesDisplay.putExtra("net_total", Integer.parseInt(partiesNetTotal.get(prodKey).toString()));
                            partySalesDisplay.putExtra("items_qty", (HashMap<String, Object>) partiesItems.get(prodKey));
                            partySalesDisplay.putExtra("items_rate", (HashMap<String, Object>) partiesItemsRate.get(prodKey));
                            partySalesDisplay.putExtra("items_total", (HashMap<String, Object>) partiesItemsTotal.get(prodKey));
                            partySalesDisplay.putExtra("party_bill_no", partiesBillNo.get(prodKey).toString());
                            if (partiesGST.size() > 0)
                                partySalesDisplay.putExtra("party_gst", partiesGST.get(prodKey).toString());
                            partySalesDisplay.putExtra("party_bill_date", partiesBillDate.get(prodKey).toString());
                            partySalesDisplay.putExtra("amount_received", Integer.parseInt(partiesAmountReceived.get(prodKey).toString()));
                            context.startActivity(partySalesDisplay);
                        }
                    });

                    // Add the TableRow to the TableLayout
                    tableLayout.addView(tr);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    private static HashMap<String, Object> productsSold = new HashMap<>();
    private static HashMap<String, Object> productsTotal = new HashMap<>();

    private static void populateProduct(Context context, View itemView, Date pickedDate) throws ParseException {
        try {
            cashAmount = 0;
            totalBill = 0;

            productsSold = new HashMap<>();
            productsTotal = new HashMap<>();
            HashMap<String, Object> bill = FireBaseAPI.billing;
            for (String key : bill.keySet()) {
                HashMap<String, Object> party = (HashMap<String, Object>) bill.get(key);
                for (String partyKey : party.keySet()) {
                    HashMap<String, Object> partyName = (HashMap<String, Object>) party.get(partyKey);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date soldDate = formatter.parse(partyName.get("sold_date").toString());
                    if (pickedDate.compareTo(soldDate) == 0) {
                        if (partyName.containsKey("amount_received")) {
                            cashAmount += Integer.parseInt(partyName.get("amount_received").toString());
                        }
                        if (partyName.containsKey("net_total")) {
                            totalBill += Integer.parseInt(partyName.get("net_total").toString());
                        }
                        HashMap<String, Object> itemsSold = (HashMap<String, Object>) partyName.get("sold_items");
                        HashMap<String, Object> itemsTotal = (HashMap<String, Object>) partyName.get("sold_items_total");
                        for (String item : itemsSold.keySet()) {
                            int _Item = Integer.parseInt(itemsSold.get(item).toString());
                            int _Total = Integer.parseInt(itemsTotal.get(item).toString());

                            if (productsSold.containsKey(item)) {
                                int sold = Integer.parseInt(productsSold.get(item).toString());
                                int total = Integer.parseInt(productsTotal.get(item).toString());
                                _Item = sold + _Item;
                                _Total = total + _Total;
                            }
                            productsSold.put(item, _Item);
                            productsTotal.put(item, _Total);
                        }
                    }
                }
            }

            TextView tvCashAmount = itemView.findViewById(R.id.tv_cash_sale);
            tvCashAmount.setText("");
            tvCashAmount.append("Cash sale: " + cashAmount);

            TextView tvCreditAmount = itemView.findViewById(R.id.tv_credit_sale);
            tvCreditAmount.setText("");
            tvCreditAmount.append("Credit sale: " + (totalBill - cashAmount));

            if (productsSold.size() > 0) {
                TableLayout tableLayout = itemView.findViewById(R.id.tl_product);
                tableLayout.removeAllViews();
                for (String prodKey : productsSold.keySet()) {
            /* Create a TableRow dynamically */
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
                    name.setText(prodKey);
                    name.setGravity(Gravity.CENTER);
                    tr.addView(name);

                /* Product Qty --> TextView */
                    TextView Qty = new TextView(context);
                    Qty.setLayoutParams(params);

                    Qty.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
                    Qty.setPadding(16, 16, 16, 16);
                    Qty.setText(productsSold.get(prodKey).toString());
                    Qty.setGravity(Gravity.CENTER);
                    tr.addView(Qty);


                /* Product Price --> TextView */
                    TextView rate = new TextView(context);
                    rate.setLayoutParams(params);

                    rate.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
                    rate.setPadding(16, 16, 16, 16);
                    rate.setText(productsTotal.get(prodKey).toString());
                    rate.setGravity(Gravity.CENTER);
                    tr.addView(rate); // Adding textView to table-row.

                    // Add the TableRow to the TableLayout
                    tableLayout.addView(tr);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    private static void populateProduct(Context context, View itemView, Date pickedDate, String route) throws ParseException {
        try {
            cashAmount = 0;
            totalBill = 0;

            productsSold = new HashMap<>();
            productsTotal = new HashMap<>();
            HashMap<String, Object> bill = FireBaseAPI.billing;
            for (String key : bill.keySet()) {
                HashMap<String, Object> party = (HashMap<String, Object>) bill.get(key);
                for (String partyKey : party.keySet()) {
                    HashMap<String, Object> partyName = (HashMap<String, Object>) party.get(partyKey);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Date soldDate = formatter.parse(partyName.get("sold_date").toString());
                    if (route.equalsIgnoreCase(partyName.get("sold_route").toString()))
                        if (pickedDate.compareTo(soldDate) == 0) {
                            if (partyName.containsKey("amount_received")) {
                                cashAmount += Integer.parseInt(partyName.get("amount_received").toString());
                            }
                            if (partyName.containsKey("net_total")) {
                                totalBill += Integer.parseInt(partyName.get("net_total").toString());
                            }
                            HashMap<String, Object> itemsSold = (HashMap<String, Object>) partyName.get("sold_items");
                            HashMap<String, Object> itemsTotal = (HashMap<String, Object>) partyName.get("sold_items_total");
                            for (String item : itemsSold.keySet()) {
                                int _Item = Integer.parseInt(itemsSold.get(item).toString());
                                int _Total = Integer.parseInt(itemsTotal.get(item).toString());

                                if (productsSold.containsKey(item)) {
                                    int sold = Integer.parseInt(productsSold.get(item).toString());
                                    int total = Integer.parseInt(productsTotal.get(item).toString());
                                    _Item = sold + _Item;
                                    _Total = total + _Total;
                                }
                                productsSold.put(item, _Item);
                                productsTotal.put(item, _Total);
                            }
                        }
                }
            }

            TextView tvCashAmount = itemView.findViewById(R.id.tv_cash_sale);
            tvCashAmount.setText("");
            tvCashAmount.append("Cash sale: " + cashAmount);

            TextView tvCreditAmount = itemView.findViewById(R.id.tv_credit_sale);
            tvCreditAmount.setText("");
            tvCreditAmount.append("Credit sale: " + (totalBill - cashAmount));

            if (productsSold.size() > 0) {
                TableLayout tableLayout = itemView.findViewById(R.id.tl_product);
                tableLayout.removeAllViews();
                for (String prodKey : productsSold.keySet()) {
            /* Create a TableRow dynamically */
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
                    name.setText(prodKey);
                    name.setGravity(Gravity.CENTER);
                    tr.addView(name);

                /* Product Qty --> TextView */
                    TextView Qty = new TextView(context);
                    Qty.setLayoutParams(params);

                    Qty.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
                    Qty.setPadding(16, 16, 16, 16);
                    Qty.setText(productsSold.get(prodKey).toString());
                    Qty.setGravity(Gravity.CENTER);
                    tr.addView(Qty);


                /* Product Price --> TextView */
                    TextView rate = new TextView(context);
                    rate.setLayoutParams(params);

                    rate.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
                    rate.setPadding(16, 16, 16, 16);
                    rate.setText(productsTotal.get(prodKey).toString());
                    rate.setGravity(Gravity.CENTER);
                    tr.addView(rate); // Adding textView to table-row.

                    // Add the TableRow to the TableLayout
                    tableLayout.addView(tr);
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }
}
