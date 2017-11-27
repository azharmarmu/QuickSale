package azhar.com.quicksale.api;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import azhar.com.quicksale.utils.Constants;

/**
 * Created by azharuddin on 22/11/17.
 */

@SuppressWarnings("unchecked")
public class TakenApi {
    public static DatabaseReference takenDBRef = FireBaseAPI.ENVIRONMENT.child(Constants.ADMIN_TAKEN);
    public static HashMap<String, Object> taken = new HashMap<>();

    @SuppressLint("SimpleDateFormat")
    public void getTaken() {
        takenDBRef.keepSynced(true);
        takenDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        taken = (HashMap<String, Object>) dataSnapshot.getValue();

                        //close the taken if it dates expired
                        for (String key : taken.keySet()) {
                            HashMap<String, Object> takenValue = (HashMap<String, Object>) taken.get(key);
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                            String date = formatter.format(new Date(System.currentTimeMillis()));
                            Date pickedDate = formatter.parse(date);
                            Date salesDate = formatter.parse(takenValue.get("sales_date").toString());
                            if (pickedDate.compareTo(salesDate) != 0) {
                                if (!takenValue.get("process").toString().equalsIgnoreCase(Constants.CLOSED)) {
                                    takenDBRef.child(key).child("process").setValue(Constants.CLOSED);
                                }
                            }
                        }
                    } else {
                        taken.clear();
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
}
