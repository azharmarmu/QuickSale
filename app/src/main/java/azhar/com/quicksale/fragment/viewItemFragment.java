package azhar.com.quicksale.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.HashMap;

import azhar.com.quicksale.R;
import azhar.com.quicksale.api.ProductsApi;
import azhar.com.quicksale.utils.Constants;
import azhar.com.quicksale.utils.DialogUtils;

@SuppressWarnings("unchecked")
public class viewItemFragment extends Fragment {


    public viewItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View rootView;
    HashMap<String, Object> products;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_view_item_setup, container, false);
        products = ProductsApi.products;
        populateTableBody(getContext());
        return rootView;
    }

    private void populateTableBody(Context context) {
        TableLayout tableLayout = rootView.findViewById(R.id.table_layout);
        tableLayout.removeAllViews();

        for (String prodKey : products.keySet()) {
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

            final HashMap<String, Object> prodDetails = (HashMap<String, Object>) products.get(prodKey);

            /* Product Name --> EditText */
            final EditText name = new EditText(context);
            name.setLayoutParams(params);

            name.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
            name.setPadding(16, 16, 16, 16);
            name.setText(prodDetails.get("name").toString()
                    .replace("_", "/"));
            name.setGravity(Gravity.CENTER);
            name.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            tr.addView(name);

            /* Product HSN --> EditText */
            final EditText hsn = new EditText(context);
            hsn.setLayoutParams(params);
            hsn.setText(prodDetails.get("hsn").toString());
            hsn.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
            hsn.setPadding(16, 16, 16, 16);
            hsn.setGravity(Gravity.CENTER);
            hsn.setInputType(InputType.TYPE_CLASS_NUMBER);
            hsn.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            tr.addView(hsn);


            /* Product Price --> EditText */
            final EditText rate = new EditText(context);
            rate.setLayoutParams(params);

            rate.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
            rate.setPadding(16, 16, 16, 16);
            rate.setText(prodDetails.get("rate").toString());
            rate.setGravity(Gravity.CENTER);
            rate.setInputType(InputType.TYPE_CLASS_NUMBER);
            rate.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            tr.addView(rate); // Adding textView to table-row.

            /* Product status --> EditText */
            Button status = new Button(context);
            status.setLayoutParams(params);

            status.setTextColor(context.getResources().getColor(R.color.colorBlack));
            status.setPadding(16, 16, 16, 16);
            status.setText(Constants.UPDATE);
            status.setGravity(Gravity.CENTER);
            status.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
            tr.addView(status);

            final String oldProductName = name.getText().toString();
            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String productName = name.getText().toString();
                    String prodHSN = hsn.getText().toString();
                    String prodRate = rate.getText().toString();
                    HashMap<String, Object> product = new HashMap<>();
                    product.put("name", productName);
                    product.put("hsn", prodHSN);
                    product.put("rate", prodRate);
                    ProductsApi.productDBRef
                            .child(oldProductName)
                            .removeValue();
                    ProductsApi.productDBRef
                            .child(productName)
                            .updateChildren(product);
                    DialogUtils.appToastShort(getActivity(),
                            "Updated SalesMan Successfully");
                }
            });

            // Add the TableRow to the TableLayout
            tableLayout.addView(tr);
        }
    }
}
