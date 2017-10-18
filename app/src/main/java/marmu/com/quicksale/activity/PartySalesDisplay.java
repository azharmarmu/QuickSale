package marmu.com.quicksale.activity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;

import marmu.com.quicksale.R;

@SuppressWarnings("unchecked")
public class PartySalesDisplay extends AppCompatActivity {

    int cashReceived = 0;
    int partiesNetTotal = 0;
    String partyName, partyBillNo, partyBillDate, partyGST;
    HashMap<String, Object> partiesItems = new HashMap<>();
    HashMap<String, Object> partiesItemsRate = new HashMap<>();
    HashMap<String, Object> partiesItemsTotal = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_sales_display);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            partyName = bundle.getString("party_name");
            partiesNetTotal = bundle.getInt("net_total");
            partiesItems = (HashMap<String, Object>) bundle.getSerializable("items_qty");
            partiesItemsRate = (HashMap<String, Object>) bundle.getSerializable("items_rate");
            partiesItemsTotal = (HashMap<String, Object>) bundle.getSerializable("items_total");
            partiesItemsTotal = (HashMap<String, Object>) bundle.getSerializable("items_total");
            partyBillNo = bundle.getString("party_bill_no");
            partyBillDate = bundle.getString("party_bill_date");
            cashReceived = bundle.getInt("amount_received");
            if (bundle.containsKey("party_gst")) {
                partyGST = bundle.getString("party_gst");
            } else {
                partyGST = "NIL";
            }

            TextView tvPartyName = (TextView) findViewById(R.id.sales_man_list);
            tvPartyName.setText(partyName);

            TextView tvPartyBillDate = (TextView) findViewById(R.id.party_bill_date);
            tvPartyBillDate.append("Date: " + partyBillDate);

            TextView tvPartyBillNo = (TextView) findViewById(R.id.party_bill_no);
            tvPartyBillNo.append("Bill No: " + partyBillNo);

            TextView tvPartyGST = (TextView) findViewById(R.id.gst);
            tvPartyGST.append("GSTIN: " + partyGST);

            populateTable(getApplicationContext());

            TextView total = (TextView) findViewById(R.id.tv_sales_total);
            total.setText(String.valueOf(partiesNetTotal));

            TextView amountReceived = (TextView) findViewById(R.id.tv_amount_received);
            amountReceived.setText("");
            amountReceived.append("Amount received: " + String.valueOf(cashReceived));

            TextView balanceAmount = (TextView) findViewById(R.id.tv_balance_amount);
            balanceAmount.setText("");
            balanceAmount.append("Balance Amount: " + (partiesNetTotal - cashReceived));

        }
    }

    private void populateTable(Context context) {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.table_layout);
        tableLayout.removeAllViews();
        for (final String prodKey : partiesItems.keySet()) {
            /* Create a TableRow dynamically */
            TableRow tr = new TableRow(context);
            tr.setBackground(ContextCompat.getDrawable(context, R.drawable.box_white));

            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tr.setPadding(16, 16, 16, 16);
            tr.setWeightSum(4);

            /*Params*/
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            params.weight = 1.0f;


            /* Product Name --> TextView */
            TextView name = new TextView(context);
            name.setLayoutParams(params);

            name.setTextColor(ContextCompat.getColor(context, R.color.colorLightBlack));
            name.setPadding(16, 16, 16, 16);
            name.setText(prodKey);
            name.setGravity(Gravity.CENTER);
            tr.addView(name);

            /* Product QTY --> TextView */
            TextView salesMan = new TextView(context);
            salesMan.setLayoutParams(params);

            salesMan.setTextColor(ContextCompat.getColor(context, R.color.colorLightBlack));
            salesMan.setPadding(16, 16, 16, 16);
            salesMan.setText(partiesItems.get(prodKey).toString());
            salesMan.setGravity(Gravity.CENTER);
            tr.addView(salesMan);

            /* Product Price --> TextView */
            TextView rate = new TextView(context);
            rate.setLayoutParams(params);

            rate.setTextColor(ContextCompat.getColor(context, R.color.colorLightBlack));
            rate.setPadding(16, 16, 16, 16);
            rate.setText(partiesItemsRate.get(prodKey).toString());
            rate.setGravity(Gravity.CENTER);
            tr.addView(rate);

            /* Product Price --> TextView */
            TextView total = new TextView(context);
            total.setLayoutParams(params);

            total.setTextColor(ContextCompat.getColor(context, R.color.colorLightBlack));
            total.setPadding(16, 16, 16, 16);
            total.setText(partiesItemsTotal.get(prodKey).toString());
            total.setGravity(Gravity.CENTER);
            tr.addView(total); // Adding textView to table-row.

            // Add the TableRow to the TableLayout
            tableLayout.addView(tr);

        }
    }
}
