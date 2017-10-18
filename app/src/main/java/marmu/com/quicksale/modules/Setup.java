package marmu.com.quicksale.modules;

import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;

import marmu.com.quicksale.R;
import marmu.com.quicksale.api.FireBaseAPI;

/**
 * Created by azharuddin on 25/7/17.
 */

@SuppressWarnings("deprecation")
public class Setup {
    private static HashMap<String, Object> productPrice = new HashMap<>();
    private static HashMap<String, Object> productHSN = new HashMap<>();

    public static void evaluate(final Context context, View itemView) {


        productPrice = FireBaseAPI.productPrice;
        productHSN = FireBaseAPI.productHSN;

        populateTable(context, itemView);

        addMoreClick(context, itemView);

        setup(context, itemView);
    }

    private static void populateTable(Context context, View itemView) {
        TableLayout tableLayout = itemView.findViewById(R.id.table_layout);
        tableLayout.removeAllViews();
        for (String prodKey : productPrice.keySet()) {
            /* Create a TableRow dynamically */
            TableRow tr = new TableRow(context);
            tr.setBackgroundColor(context.getResources().getColor(R.color.colorLightWhite));

            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));

            tr.setWeightSum(2);

            /*Params*/
            TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            params.weight = 1.0f;


            /* Product Name --> EditText */
            EditText name = new EditText(context);
            name.setLayoutParams(params);

            name.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
            name.setPadding(16, 16, 16, 16);
            name.setText(prodKey.replace("_", "/"));
            name.setGravity(Gravity.CENTER);
            name.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            tr.addView(name);

            /* Product HSN --> EditText */
            EditText hsn = new EditText(context);
            hsn.setLayoutParams(params);
            hsn.setText(productHSN.get(prodKey).toString());
            hsn.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
            hsn.setPadding(16, 16, 16, 16);
            hsn.setGravity(Gravity.CENTER);
            hsn.setInputType(InputType.TYPE_CLASS_NUMBER);
            hsn.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            tr.addView(hsn);


            /* Product Price --> EditText */
            EditText rate = new EditText(context);
            rate.setLayoutParams(params);

            rate.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
            rate.setPadding(16, 16, 16, 16);
            rate.setText(productPrice.get(prodKey).toString());
            rate.setGravity(Gravity.CENTER);
            rate.setInputType(InputType.TYPE_CLASS_NUMBER);
            rate.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            tr.addView(rate); // Adding textView to table-row.

            // Add the TableRow to the TableLayout
            tableLayout.addView(tr);
        }
    }

    private static void addMoreClick(final Context context, View itemView) {
        final TableLayout tableLayout = itemView.findViewById(R.id.table_layout);
        TextView addMore = itemView.findViewById(R.id.add_more);
        addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* Create a TableRow dynamically */
                TableRow tr = new TableRow(context);
                tr.setBackgroundColor(context.getResources().getColor(R.color.colorLightWhite));
                tr.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT));
                tr.setWeightSum(2);

                /* Product Name --> EditText */
                EditText name = new EditText(context);
                name.setLayoutParams(new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1.0f
                ));
                name.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
                name.setPadding(16, 16, 16, 16);
                name.setGravity(View.TEXT_ALIGNMENT_CENTER);
                name.setHint("product");
                tr.addView(name);  // Adding textView to table-row.

                /* Product HSN --> EditText */
                EditText hsn = new EditText(context);
                hsn.setLayoutParams(new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1.0f
                ));
                hsn.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
                hsn.setPadding(16, 16, 16, 16);
                hsn.setGravity(View.TEXT_ALIGNMENT_CENTER);
                hsn.setInputType(InputType.TYPE_CLASS_NUMBER);
                hsn.setHint("0");
                tr.addView(hsn);  // Adding textView to table-row.

                /* Product Price --> EditText */
                EditText rate = new EditText(context);
                rate.setLayoutParams(new TableRow.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1.0f
                ));
                rate.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
                rate.setPadding(16, 16, 16, 16);
                rate.setGravity(View.TEXT_ALIGNMENT_CENTER);
                rate.setInputType(InputType.TYPE_CLASS_NUMBER);
                rate.setHint("0");
                tr.addView(rate); // Adding textView to table-row.

                // Add the TableRow to the TableLayout
                tableLayout.addView(tr);
            }
        });
    }

    private static void setup(final Context context, final View itemView) {
        final TableLayout tableLayout = itemView.findViewById(R.id.table_layout);
        TextView Setup = itemView.findViewById(R.id.btn_setup);
        Setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < tableLayout.getChildCount(); i++) {
                    TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
                    if (tableRow != null) {
                        EditText productName = (EditText) tableRow.getChildAt(0);
                        String prodName = productName.getText().toString().replace("/", "_");
                        EditText productHSN_NO = (EditText) tableRow.getChildAt(1);
                        String prodHSN = productHSN_NO.getText().toString();
                        EditText productRate = (EditText) tableRow.getChildAt(2);
                        String prodRate = productRate.getText().toString();
                        if (!prodName.isEmpty() && !prodHSN.isEmpty() && !prodRate.isEmpty() && Integer.parseInt(prodRate) > 0) {
                            if (productPrice.containsKey(prodName)) {
                                String rate = String.valueOf(productPrice.get(prodName));
                                if (!rate.equals(prodRate)) {
                                    productPrice.put(prodName, prodRate);
                                }
                            } else {
                                productPrice.put(prodName, prodRate);
                            }

                            if (productHSN.containsKey(prodName)) {
                                String hsn = String.valueOf(productHSN.get(prodName));
                                if (!hsn.equals(prodRate)) {
                                    productHSN.put(prodName, prodHSN);
                                }
                            } else {
                                productHSN.put(prodName, prodHSN);
                            }

                        }
                    }
                }
                //update to DB
                if (productPrice.size() > 0) {
                    FireBaseAPI.productDBRef.updateChildren(productPrice);
                    FireBaseAPI.productHsnDBRef.updateChildren(productHSN);
                }

                populateTable(context, itemView);
            }
        });
    }
}
