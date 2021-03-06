package marmu.com.quicksale.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import marmu.com.quicksale.R;

@SuppressWarnings({"unchecked", "deprecation"})
public class ReturnActivity extends AppCompatActivity {
    String key;
    HashMap<String, Object> returnMap = new HashMap<>();
    HashMap<String, Object> takenItems = new HashMap<>();
    HashMap<String, Object> itemDetailsQTY = new HashMap<>();
    HashMap<String, Object> itemDetailsQTYLeft = new HashMap<>();

    TextView salesManListView;
    TextView route;
    TableLayout tableLayout;
    List<String> salesMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        salesManListView = (TextView) findViewById(R.id.sales_man_list);
        route = (TextView) findViewById(R.id.tv_route_name);
        tableLayout = (TableLayout) findViewById(R.id.table_layout);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            key = extras.getString("key");
            returnMap = (HashMap<String, Object>) extras.getSerializable("returnMap");
            if (returnMap != null) {
                salesMan = (List<String>) returnMap.get("sales_man_name");
                populateSalesMan(salesMan);
                route.append("Route: " + returnMap.get("sales_route"));
                itemDetailsQTY = (HashMap<String, Object>) returnMap.get("sales_order_qty");
                itemDetailsQTYLeft = (HashMap<String, Object>) returnMap.get("sales_order_qty_left");
                populateItemsDetails();
            }
        }
    }

    private void populateItemsDetails() {
        for (String prodKey : itemDetailsQTY.keySet()) {
            /* Create a TableRow dynamically */
            TableRow tr = new TableRow(this);
            tr.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorLightWhite));
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tr.setWeightSum(4);

            /*Params*/
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;

            int takenQty = Integer.parseInt(itemDetailsQTY.get(prodKey).toString());
            int leftQty = Integer.parseInt(itemDetailsQTYLeft.get(prodKey).toString());
            int soldQty = takenQty - leftQty;

                       /* Product Name --> TextView */
            TextView name = new TextView(getApplicationContext());
            name.setLayoutParams(params);

            name.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLightBlack));
            name.setPadding(16, 16, 16, 16);
            name.setText(prodKey.replace("_", "/"));
            name.setGravity(Gravity.CENTER);
            name.setBackgroundColor(getApplicationContext().getResources().getColor(android.R.color.transparent));
            tr.addView(name);


            /* Product Taken --> TextView */
            TextView taken = new TextView(getApplicationContext());
            taken.setLayoutParams(params);

            taken.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLightBlack));
            taken.setPadding(16, 16, 16, 16);
            taken.setText(String.valueOf(takenQty));
            taken.setGravity(Gravity.CENTER);
            taken.setInputType(InputType.TYPE_CLASS_NUMBER);
            taken.setBackgroundColor(getApplicationContext().getResources().getColor(android.R.color.transparent));
            tr.addView(taken);

            /* Product Sold --> TextView */
            TextView sold = new TextView(getApplicationContext());
            sold.setLayoutParams(params);

            sold.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLightBlack));
            sold.setPadding(16, 16, 16, 16);
            sold.setText(String.valueOf(soldQty));
            sold.setGravity(Gravity.CENTER);
            sold.setInputType(InputType.TYPE_CLASS_NUMBER);
            sold.setBackgroundColor(getApplicationContext().getResources().getColor(android.R.color.transparent));
            tr.addView(sold);

            /* Product Left --> TextView */
            TextView left = new TextView(getApplicationContext());
            left.setLayoutParams(params);

            left.setTextColor(getApplicationContext().getResources().getColor(R.color.colorLightBlack));
            left.setPadding(16, 16, 16, 16);
            left.setText(String.valueOf(leftQty));
            left.setGravity(Gravity.CENTER);
            left.setInputType(InputType.TYPE_CLASS_NUMBER);
            left.setBackgroundColor(getApplicationContext().getResources().getColor(android.R.color.transparent));
            tr.addView(left);

            // Adding textView to table-row.

            // Add the TableRow to the TableLayout
            tableLayout.addView(tr);
        }
    }


    @SuppressWarnings("deprecation")
    private void populateSalesMan(List<String> salesMan) {
        String salesManName = "";
        for (int i = 0; i < salesMan.size(); i++) {
            if (!salesMan.get(i).isEmpty()) {
                if (i == 0) {
                    salesManName = salesMan.get(i);
                } else {
                    salesManName = salesManName + ", " + salesMan.get(i);
                }
            }
        }
        salesManListView.setText(salesManName);
    }


}
