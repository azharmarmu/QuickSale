package marmu.com.quicksale.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;

import marmu.com.quicksale.R;

@SuppressWarnings("unchecked")
public class PartySalesDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_sales_display);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            HashMap<String, Object> partySalesDisplay = (HashMap<String, Object>) bundle.getSerializable("billDetails");

            if (partySalesDisplay != null) {
                HashMap<String, Object> items = (HashMap<String, Object>) partySalesDisplay.get("items");
                HashMap<String, Object> party = (HashMap<String, Object>) partySalesDisplay.get("party");

                TextView tvPartyName = findViewById(R.id.sales_man_list);
                tvPartyName.setText(party.get("name").toString());
                if (party.containsKey("gst")) {
                    TextView tvPartyGST = findViewById(R.id.gst);
                    tvPartyGST.append("GSTIN: " + party.get("gst"));
                }

                TextView tvPartyBillDate = findViewById(R.id.party_bill_date);
                tvPartyBillDate.append("Date: " + partySalesDisplay.get("date"));

                TextView tvPartyBillNo = findViewById(R.id.party_bill_no);
                tvPartyBillNo.append("Bill No: " + partySalesDisplay.get("partyBillNo"));


                populateTable(items);

                TextView total = findViewById(R.id.tv_sales_total);
                total.setText(String.valueOf(partySalesDisplay.get("netTotal")));

                TextView amountReceived = findViewById(R.id.tv_amount_received);
                amountReceived.setText("");
                amountReceived.append("Amount received: " + String.valueOf(partySalesDisplay.get("amountReceived")));

                TextView balanceAmount = findViewById(R.id.tv_balance_amount);
                balanceAmount.setText("");
                balanceAmount.append("Balance Amount: " +
                        (Integer.parseInt(partySalesDisplay.get("netTotal").toString()) -
                                Integer.parseInt(partySalesDisplay.get("amountReceived").toString())));
            }
        }

    }

    private void populateTable(HashMap<String, Object> items) {
        TableLayout tableLayout = findViewById(R.id.table_layout);
        tableLayout.removeAllViews();
        for (final String prodKey : items.keySet()) {
            /* Create a TableRow dynamically */
            TableRow tr = new TableRow(this);
            tr.setBackground(ContextCompat.getDrawable(this, R.drawable.box_white));

            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tr.setPadding(16, 16, 16, 16);
            tr.setWeightSum(4);

            /*Params*/
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            params.weight = 1.0f;


            HashMap<String, Object> itemDetails = (HashMap<String, Object>) items.get(prodKey);


            /* Product Name --> TextView */
            TextView name = new TextView(this);
            name.setLayoutParams(params);

            name.setTextColor(ContextCompat.getColor(this, R.color.colorLightBlack));
            name.setPadding(16, 16, 16, 16);
            name.setText(itemDetails.get("name").toString());
            name.setGravity(Gravity.CENTER);
            tr.addView(name);

            /* Product QTY --> TextView */
            TextView salesMan = new TextView(this);
            salesMan.setLayoutParams(params);

            salesMan.setTextColor(ContextCompat.getColor(this, R.color.colorLightBlack));
            salesMan.setPadding(16, 16, 16, 16);
            salesMan.setText(itemDetails.get("qty").toString());
            salesMan.setGravity(Gravity.CENTER);
            tr.addView(salesMan);

            /* Product Price --> TextView */
            TextView rate = new TextView(this);
            rate.setLayoutParams(params);

            rate.setTextColor(ContextCompat.getColor(this, R.color.colorLightBlack));
            rate.setPadding(16, 16, 16, 16);
            rate.setText(itemDetails.get("rate").toString());
            rate.setGravity(Gravity.CENTER);
            tr.addView(rate);

            /* Product total --> TextView */
            TextView total = new TextView(this);
            total.setLayoutParams(params);

            total.setTextColor(ContextCompat.getColor(this, R.color.colorLightBlack));
            total.setPadding(16, 16, 16, 16);
            total.setText(itemDetails.get("total").toString());
            total.setGravity(Gravity.CENTER);
            tr.addView(total); // Adding textView to table-row.

            // Add the TableRow to the TableLayout
            tableLayout.addView(tr);

        }
    }
}
