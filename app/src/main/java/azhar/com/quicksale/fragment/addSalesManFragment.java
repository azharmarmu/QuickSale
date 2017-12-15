package azhar.com.quicksale.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

import azhar.com.quicksale.R;
import azhar.com.quicksale.api.SalesManApi;
import azhar.com.quicksale.utils.Constants;
import azhar.com.quicksale.utils.DialogUtils;

@SuppressWarnings("unchecked")
public class addSalesManFragment extends Fragment {

    public addSalesManFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_sales_man, container, false);
        addSalesMan();
        return rootView;
    }

    private void addSalesMan() {
        TextView addMan = rootView.findViewById(R.id.btn_add_sales_man);
        addMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> salesMan = SalesManApi.salesMan;

                EditText name = rootView.findViewById(R.id.et_sales_man_name);
                EditText phone = rootView.findViewById(R.id.et_sales_man_phone);

                String salesManName = name.getText().toString();
                String salesManPhone = phone.getText().toString();
                if (!salesManName.isEmpty()
                        && !salesManPhone.isEmpty()) {
                    if (!salesMan.containsKey(salesManPhone)) {
                        salesMan.put(Constants.SALES_MAN_NAME, salesManName);
                        salesMan.put(Constants.SALES_MAN_PHONE, salesManPhone);
                        name.setText("");
                        phone.setText("");
                        SalesManApi.salesManDBRef.child(salesManPhone)
                                .updateChildren(salesMan)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            DialogUtils.appToastShort(getContext(),
                                                    "Sales man added");
                                        } else {
                                            DialogUtils.appToastShort(getContext(),
                                                    "Sales man not added");
                                        }
                                    }
                                });
                        DialogUtils.appToastShort(getActivity(), "Added SalesMan Successfully");
                    } else {
                        name.setError("Already Exists");
                        name.requestFocus();
                    }
                }
            }
        });
    }
}
