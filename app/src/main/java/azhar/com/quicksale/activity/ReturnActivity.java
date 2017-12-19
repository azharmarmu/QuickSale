package azhar.com.quicksale.activity;

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

import azhar.com.quicksale.R;
import azhar.com.quicksale.utils.Constants;

@SuppressWarnings({"unchecked", "deprecation"})
public class ReturnActivity extends AppCompatActivity {
    String key;
    HashMap<String, Object> returnMap = new HashMap<>();
    HashMap<String, Object> takenItems = new HashMap<>();
    HashMap<String, Object> sales = new HashMap<>();

    TextView salesManListView;
    TextView route;
    TableLayout tableLayout;
    List<String> salesMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        salesManListView = findViewById(R.id.sales_man_list);
        route = findViewById(R.id.tv_route_name);
        tableLayout = findViewById(R.id.table_layout);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            key = extras.getString(Constants.KEY);
            returnMap = (HashMap<String, Object>) extras.getSerializable(Constants.TAKEN);
            if (returnMap != null) {
                salesMan = (List<String>) returnMap.get(Constants.TAKEN_SALES_MAN_NAME);
                populateSalesMan(salesMan);
                route.append("Route: " + returnMap.get(Constants.TAKEN_ROUTE));
                sales = (HashMap<String, Object>) returnMap.get(Constants.TAKEN_SALES);
                populateItemsDetails();
            }
        }
    }

    private void populateItemsDetails() {
        for (String prodKey : sales.keySet()) {
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

            HashMap<String, Object> itemsDetails = (HashMap<String, Object>) sales.get(prodKey);

            int qty = Integer.parseInt(itemsDetails.get(Constants.TAKEN_SALES_QTY).toString());
            int stockQty = Integer.parseInt(itemsDetails.get(Constants.TAKEN_SALES_QTY_STOCK).toString());
            int soldQty = qty - stockQty;

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
            taken.setText(String.valueOf(qty));
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
            left.setText(String.valueOf(stockQty));
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
