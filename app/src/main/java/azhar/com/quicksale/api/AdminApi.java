package azhar.com.quicksale.api;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import azhar.com.quicksale.utils.Constants;

/**
 * Created by azharuddin on 22/11/17.
 */

@SuppressWarnings("unchecked")
public class AdminApi {
    private static DatabaseReference adminDBRef = FireBaseAPI.ENVIRONMENT.child(Constants.ADMIN);
    public static HashMap<String, Object> admin = new HashMap<>();

    public void getAdmin() {
        adminDBRef.keepSynced(true);
        adminDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        admin = (HashMap<String, Object>) dataSnapshot.getValue();
                    } else {
                        admin.clear();
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
