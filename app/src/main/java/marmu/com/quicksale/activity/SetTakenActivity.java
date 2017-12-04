package marmu.com.quicksale.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import marmu.com.quicksale.R;
import marmu.com.quicksale.api.FireBaseAPI;
import marmu.com.quicksale.utils.Constants;

@SuppressWarnings({"unchecked", "deprecation"})
public class SetTakenActivity extends AppCompatActivity implements Serializable {

    String key;
    HashMap<String, Object> takenMap = new HashMap<>();
    HashMap<String, Object> takenItems = new HashMap<>();
    HashMap<String, Object> itemDetails = new HashMap<>();

    TextView salesManListView;
    EditText route;
    TableLayout tableLayout;
    List<String> salesMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_taken);

        salesManListView = findViewById(R.id.sales_man_list);
        route = findViewById(R.id.et_route_name);
        tableLayout = findViewById(R.id.table_layout);
        isTakenEdit(getIntent().getExtras());
    }

    @SuppressLint("SetTextI18n")
    private void isTakenEdit(Bundle extras) {
        if (extras != null) {
            key = extras.getString("key");
            takenMap = (HashMap<String, Object>) FireBaseAPI.taken.get(key);
            if (takenMap != null) {
                salesMan = (List<String>) takenMap.get("sales_man_name");
                populateSalesMan(salesMan);
                route.setText((String) takenMap.get("sales_route"));
                itemDetails = (HashMap<String, Object>) takenMap.get("sales_order_qty");
                populateItemsDetails();

                if (!takenMap.get("process").toString().equalsIgnoreCase("start")) {
                    viewTaken();
                }
            }
        } else {
            populateItemsDetails();
            salesManListView.setText("NIL");
        }
    }

    private void viewTaken() {
        TextView salesManTextView = findViewById(R.id.sales_man);
        salesManTextView.setClickable(false);
        route.setEnabled(false);
        tableLayout.setEnabled(false);
        Button createOrder = findViewById(R.id.btn_taken_set);
        createOrder.setVisibility(View.GONE);
    }

    private void populateItemsDetails() {
        HashMap<String, Object> products = FireBaseAPI.productPrice;

        for (String prodKey : products.keySet()) {
            /* Create a TableRow dynamically */
            TableRow tr = new TableRow(this);
            tr.setBackground(getResources().getDrawable(R.drawable.box_white));
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            tr.setWeightSum(2);

            /*Params*/
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;


            /* Product Name --> TextView */
            TextView productName = new TextView(this);
            productName.setLayoutParams(params);

            productName.setTextColor(getResources().getColor(R.color.colorLightBlack));
            productName.setPadding(16, 16, 16, 16);
            productName.setText(prodKey);
            productName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            productName.setGravity(Gravity.CENTER);
            tr.addView(productName);

            /* Product QTY --> EditText */
            EditText productQTY = new EditText(this);
            productQTY.setLayoutParams(params);

            productQTY.setTextColor(getResources().getColor(R.color.colorLightBlack));
            productQTY.setPadding(16, 16, 16, 16);
            if (itemDetails != null && itemDetails.containsKey(prodKey)) {
                productQTY.setText((String) itemDetails.get(prodKey));
            } else {
                productQTY.setHint("0");
            }
            productQTY.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            productQTY.setGravity(Gravity.CENTER);
            productQTY.setInputType(InputType.TYPE_CLASS_NUMBER);
            tr.addView(productQTY); // Adding textView to table-row.

            // Add the TableRow to the TableLayout
            tableLayout.addView(tr);
        }
    }

    public void salesManClick(View view) {
        Intent intent = new Intent(SetTakenActivity.this, SalesManActivity.class);
        intent.putExtra("sales_man", (Serializable) salesMan);
        startActivityForResult(intent, Constants.SALES_MAN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SALES_MAN_CODE) {
            try {
                salesMan = (List<String>) data.getSerializableExtra("salesMan");
                populateSalesMan(salesMan);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
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

    public void setTaken(View view) throws ParseException {

        String localSalesMan = (String) salesManListView.getText();
        String localSalesRoute = String.valueOf(route.getText());

        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            if (tableRow != null) {
                TextView productName = (TextView) tableRow.getChildAt(0);
                String prodName = productName.getText().toString().replace("/", "_");
                EditText productQTY = (EditText) tableRow.getChildAt(1);
                String prodQTY = productQTY.getText().toString();
                if (!prodName.isEmpty() && !prodQTY.isEmpty() && Integer.parseInt(prodQTY) > 0) {
                    takenItems.put(prodName, prodQTY);
                }
            }
        }
        if (localSalesMan.equals("NIL") || localSalesMan.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Select Sales Man", Toast.LENGTH_SHORT).show();
        } else if (localSalesRoute.isEmpty()) {
            route.setError("Please Enter sales route");
            route.requestFocus();
        } else if (takenItems.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Select Item for sale", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> taken = new HashMap<>();
            taken.put("process", "start");
            taken.put("date", Constants.currentDate());
            taken.put("sales_man_name", salesMan);
            taken.put("route", localSalesRoute);
            taken.put("sales_order_qty", takenItems);
            taken.put("sales_order_qty_left", takenItems);
            if (key == null) {
                FireBaseAPI.takenDBRef.push().updateChildren(taken);
            } else {
                FireBaseAPI.takenDBRef.child(key).updateChildren(taken);
            }
            finish();
        }
    }
}
