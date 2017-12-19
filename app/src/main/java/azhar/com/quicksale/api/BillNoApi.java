package azhar.com.quicksale.api;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import azhar.com.quicksale.listeners.BillNoListener;
import azhar.com.quicksale.utils.Constants;

import static azhar.com.quicksale.api.FireBaseAPI.ENVIRONMENT;

/**
 * Created by azharuddin on 26/11/17.
 */

@SuppressWarnings("unchecked")
public class BillNoApi {
    public static DatabaseReference billNoDBRef = ENVIRONMENT.child(Constants.BILL_NO);
    public static List<Integer> billNo = new ArrayList<>();

    public void getBillNo() {
        billNoDBRef.keepSynced(true);
        billNoDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        billNo = (List<Integer>) dataSnapshot.getValue();
                        billNoListener.getBillNo(billNo);
                    } else {
                        billNo.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FireError", databaseError.getMessage());
            }
        });
    }

    private static BillNoListener billNoListener;

    public void setBillNoListener(BillNoListener billNoListener) {
        BillNoApi.billNoListener = billNoListener;
    }
}
