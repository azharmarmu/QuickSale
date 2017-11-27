package azhar.com.quicksale.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

import azhar.com.quicksale.R;
import azhar.com.quicksale.api.ProductsApi;
import azhar.com.quicksale.utils.DialogUtils;

@SuppressWarnings("unchecked")
public class addItemFragment extends Fragment {

    public addItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_item, container, false);
        addItem();
        return rootView;
    }

    private void addItem() {
        TextView addMan = rootView.findViewById(R.id.btn_add_item);
        addMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etProductName = rootView.findViewById(R.id.et_product_name);
                EditText etHSN = rootView.findViewById(R.id.et_hsn);
                EditText etRate = rootView.findViewById(R.id.et_rate);

                String productName = etProductName.getText().toString()
                        .replace("/", "_");
                String prodHSN = etHSN.getText().toString();
                String prodRate = etRate.getText().toString();

                HashMap<String, Object> products = ProductsApi.products;
                if (!productName.isEmpty() &&
                        !prodHSN.isEmpty() &&
                        !prodRate.isEmpty()) {
                    boolean itemExists = false;
                    for (String key : products.keySet()) {
                        if (key.equalsIgnoreCase(productName)) {
                            itemExists = true;
                            break;
                        }
                    }
                    if (!itemExists) {
                        HashMap<String, Object> product = new HashMap<>();
                        product.put("name", productName);
                        product.put("hsn", prodHSN);
                        product.put("rate", prodRate);
                        etProductName.setText("");
                        etHSN.setText("");
                        etRate.setText("");
                        ProductsApi.productDBRef
                                .child(productName)
                                .updateChildren(product);
                        DialogUtils.appToastShort(getActivity(),
                                "Added SalesMan Successfully");
                    } else {
                        etProductName.setError("Already Exists");
                        etProductName.requestFocus();
                    }
                } else {
                    DialogUtils.appToastShort(getContext(), "Please fill all fields");
                }
            }
        });
    }
}
