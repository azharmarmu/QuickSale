package azhar.com.quicksale.api;

import android.annotation.SuppressLint;

import com.google.firebase.database.DatabaseReference;

import azhar.com.quicksale.utils.Constants;

/**
 * Created by azharuddin on 24/7/17.
 */
@SuppressLint("SimpleDateFormat")
@SuppressWarnings("unchecked")
public class FireBaseAPI {

    static final DatabaseReference ENVIRONMENT = Constants.DATABASE.getReference(Constants.ENV);

    public static DatabaseReference orderDBRef = Constants.DATABASE.getReference(Constants.ADMIN_ORDER);



   /* public static void getOrder() {
        orderDBRef.keepSynced(true);
        orderDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        order = (HashMap<String, Object>) dataSnapshot.getValue();
                        for (String key : order.keySet()) {
                            HashMap<String, Object> orderValue = (HashMap<String, Object>) order.get(key);
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            try {
                                String date = formatter.format(new Date(System.currentTimeMillis()));
                                Date pickedDate = formatter.parse(date);
                                Date salesDate = formatter.parse(orderValue.get("order_date").toString());
                                if (pickedDate.compareTo(salesDate) != 0) {
                                    if (!orderValue.get("process").toString().equalsIgnoreCase(Constants.CLOSED)) {
                                        orderDBRef.child(key).child("process").setValue(Constants.CLOSED);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        order.clear();
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
    }*/
}
