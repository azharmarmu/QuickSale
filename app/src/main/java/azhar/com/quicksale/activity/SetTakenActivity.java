package azhar.com.quicksale.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import azhar.com.quicksale.R;
import azhar.com.quicksale.api.ProductsApi;
import azhar.com.quicksale.api.TakenApi;
import azhar.com.quicksale.utils.Constants;
import azhar.com.quicksale.utils.DialogUtils;
import azhar.com.quicksale.utils.SoftInputUtil;

@SuppressWarnings({"unchecked", "deprecation"})
public class SetTakenActivity extends AppCompatActivity implements Serializable {

    String key;
    HashMap<String, Object> takenDetails = new HashMap<>();
    HashMap<String, Object> takenProductDetails = new HashMap<>();
    HashMap<String, Object> itemDetails = new HashMap<>();

    TextView salesManListView;
    EditText route;
    TableLayout tableLayout;
    List<String> salesMan = new ArrayList<>();

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
            key = extras.getString(Constants.KEY);
            takenDetails = (HashMap<String, Object>) TakenApi.taken.get(key);
            if (takenDetails.size() > 0) {
                salesMan = (List<String>) takenDetails.get(Constants.TAKEN_SALES_MAN_NAME);
                populateSalesMan();
                route.setText((String) takenDetails.get(Constants.TAKEN_ROUTE));

                itemDetails = (HashMap<String, Object>) takenDetails.get(Constants.TAKEN_SALES);
                populateItemsDetails();

                if (!takenDetails.get(Constants.TAKEN_PROCESS).toString()
                        .equalsIgnoreCase(Constants.START)) {
                    viewTaken();
                }
            }
        } else {
            populateItemsDetails();
            salesManListView.setText(R.string.nil);
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
        HashMap<String, Object> products = ProductsApi.products;

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

            HashMap<String, Object> itemQtyDetails = (HashMap<String, Object>) itemDetails.get(prodKey);

            /* Product QTY --> EditText */
            EditText productQTY = new EditText(this);
            productQTY.setLayoutParams(params);

            productQTY.setTextColor(getResources().getColor(R.color.colorLightBlack));
            productQTY.setPadding(16, 16, 16, 16);
            if (itemDetails != null && itemDetails.containsKey(prodKey)) {
                productQTY.setText((String) itemQtyDetails.get(Constants.TAKEN_SALES_QTY_STOCK));
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
        intent.putExtra(Constants.SALES_MAN, (Serializable) salesMan);
        startActivityForResult(intent, Constants.SALES_MAN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SALES_MAN_CODE) {
            try {
                salesMan = (List<String>) data.getSerializableExtra(Constants.SALES_MAN);
                populateSalesMan();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void populateSalesMan() {
        StringBuilder salesManName = new StringBuilder();
        for (int i = 0; i < salesMan.size(); i++) {
            if (!salesMan.get(i).isEmpty()) {
                if (i == 0) {
                    salesManName = new StringBuilder(salesMan.get(i));
                } else {
                    salesManName.append(", ").append(salesMan.get(i));
                }
            }
        }
        salesManListView.setText(salesManName.toString());
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
                    HashMap<String, Object> productDetails = new HashMap<>();
                    productDetails.put(Constants.TAKEN_SALES_PRODUCT_NAME, prodName);
                    productDetails.put(Constants.TAKEN_SALES_QTY, prodQTY);
                    productDetails.put(Constants.TAKEN_SALES_QTY_STOCK, prodQTY);
                    takenProductDetails.put(prodName, productDetails);
                }
            }
        }
        if (localSalesMan.equals(getString(R.string.nil)) || localSalesMan.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Select Sales Man", Toast.LENGTH_SHORT).show();
        } else if (localSalesRoute.isEmpty()) {
            route.setError("Please Enter sales route");
            route.requestFocus();
        } else if (takenProductDetails.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Select Item for sale", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, Object> taken = new HashMap<>();
            taken.put(Constants.TAKEN_PROCESS, Constants.START);
            taken.put(Constants.TAKEN_DATE, Constants.currentDate());
            taken.put(Constants.TAKEN_SALES, takenProductDetails);
            taken.put(Constants.TAKEN_SALES_MAN_NAME, salesMan);
            taken.put(Constants.TAKEN_ROUTE, localSalesRoute);

            FirebaseFirestore dbStore = FirebaseFirestore.getInstance();

            DialogUtils.showProgressDialog(SetTakenActivity.this, getString(R.string.loading));

            if (key != null) {
                dbStore.collection(Constants.TAKEN)
                        .document(key)
                        .delete();
            }

            dbStore.collection(Constants.TAKEN)
                    .add(taken)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            DialogUtils.dismissProgressDialog();
                            if (task.isSuccessful()) {
                                DialogUtils.appToastShort(getApplicationContext(), "Taken set successfully");
                                RelativeLayout holder = findViewById(R.id.holder);
                                new SoftInputUtil().hideSoftInput(holder, SetTakenActivity.this);
                                finish();
                            } else {
                                DialogUtils.appToastShort(getApplicationContext(), "Taken not set");
                            }
                        }
                    });
        }
    }
}
