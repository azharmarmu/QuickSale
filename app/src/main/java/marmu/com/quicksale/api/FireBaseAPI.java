package marmu.com.quicksale.api;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import marmu.com.quicksale.listeners.CustomerListener;
import marmu.com.quicksale.utils.Constants;

/**
 * Created by azharuddin on 24/7/17.
 */
@SuppressLint("SimpleDateFormat")
@SuppressWarnings("unchecked")
public class FireBaseAPI {
    public static DatabaseReference companyDBRef = Constants.DATABASE.getReference(Constants.COMPANY);
    public static DatabaseReference adminDBRef = Constants.DATABASE.getReference(Constants.ADMIN);
    public static DatabaseReference salesManDBRef = Constants.DATABASE.getReference(Constants.SALES_MAN);
    public static DatabaseReference customerDBRef = Constants.DATABASE.getReference(Constants.CUSTOMER);
    public static DatabaseReference takenDBRef = Constants.DATABASE.getReference(Constants.ADMIN_TAKEN);
    public static DatabaseReference productDBRef = Constants.DATABASE.getReference(Constants.ADMIN_PRODUCT_PRICE);
    public static DatabaseReference productHsnDBRef = Constants.DATABASE.getReference(Constants.ADMIN_PRODUCT_HSN);
    public static DatabaseReference orderDBRef = Constants.DATABASE.getReference(Constants.ADMIN_ORDER);
    public static DatabaseReference billingDBREf = Constants.DATABASE.getReference(Constants.SALES_MAN_BILLING);
    public static DatabaseReference usersDBRef = Constants.DATABASE.getReference(Constants.USERS);
    public static HashMap<String, Object> company = new HashMap<>();
    public static HashMap<String, Object> admin = new HashMap<>();
    public static HashMap<String, Object> salesMan = new HashMap<>();
    public static HashMap<String, Object> customer = new HashMap<>();
    public static HashMap<String, Object> taken = new HashMap<>();
    public static HashMap<String, Object> productPrice = new HashMap<>();
    public static HashMap<String, Object> productHSN = new HashMap<>();
    public static HashMap<String, Object> order = new HashMap<>();
    public static HashMap<String, Object> billing = new HashMap<>();
    public static HashMap<String, Object> billingStore = new HashMap<>();
    public static HashMap<String, Object> users = new HashMap<>();

    public static void getCompany() {
        companyDBRef.keepSynced(true);
        companyDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        company = (HashMap<String, Object>) dataSnapshot.getValue();
                    } else {
                        company.clear();
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

    public static void getAdmin() {
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

    public static void getSalesMan() {
        salesManDBRef.keepSynced(true);
        salesManDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        salesMan = (HashMap<String, Object>) dataSnapshot.getValue();
                    } else {
                        salesMan.clear();
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

    private static CustomerListener customerListener;

    public void setCustomerListener(CustomerListener customerListener) {
        FireBaseAPI.customerListener = customerListener;
    }

    public static void getCustomer() {
        customerDBRef.keepSynced(true);
        customerDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        customer = (HashMap<String, Object>) dataSnapshot.getValue();
                        customerListener.getCustomer(customer);
                    } else {
                        customer.clear();
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

    public static void getTaken() {
        takenDBRef.keepSynced(true);
        takenDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        taken = (HashMap<String, Object>) dataSnapshot.getValue();
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

    public static void getProductPrice() {
        productDBRef.keepSynced(true);
        productDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        productPrice = (HashMap<String, Object>) dataSnapshot.getValue();
                    } else {
                        productPrice.clear();
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

    public static void getProductHSN() {
        productHsnDBRef.keepSynced(true);
        productHsnDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        productHSN = (HashMap<String, Object>) dataSnapshot.getValue();
                    } else {
                        productHSN.clear();
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

    public static void getOrder() {
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
    }

    public static void getBilling() {
        billingDBREf.keepSynced(true);
        billingDBREf.orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    billing.clear();
                    if (dataSnapshot.getValue() != null) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            HashMap<String, Object> history = (HashMap<String, Object>) snapshot.getValue();
                            assert history != null;
                            for (String key : history.keySet()) {
                                billing.put(key, history.get(key));
                            }
                        }
                        Log.e("BillingChild" , billing.toString());
                        billing = (HashMap<String, Object>) dataSnapshot.getValue();
                        Log.e("BillingValue" , billing.toString());
                    } else {
                        billing.clear();
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

    public static void getUsers() {
        usersDBRef.keepSynced(true);
        usersDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null) {
                        users = (HashMap<String, Object>) dataSnapshot.getValue();
                    } else {
                        users.clear();
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
