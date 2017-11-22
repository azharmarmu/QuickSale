package azhar.com.quicksale.utils;

import android.annotation.SuppressLint;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by azharuddin on 26/5/17.
 */

public class Constants {
    public static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    public static final FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    public static final String USERS = "users";
    public static final String COMPANY = "company";
    public static final String ADMIN_TAKEN = "taken";
    public static final String ADMIN = "admin";
    public static final String ADMIN_ORDER = "order";
    public static final String ADMIN_PRODUCT_PRICE = "product_and_price";
    public static final String ADMIN_PRODUCT_HSN = "product_and_hsn";
    public static final String SALES_MAN = "sales_man";
    public static final String CUSTOMER = "customer";
    public static final String SALES_MAN_BILLING = "billing";

    public static final String EDIT = "edit";
    public static final String CHECK = "check";

    public static final int SALES_MAN_CODE = 123;

    public static final String PRODUCT = "Product";
    public static final String PARTY = "Party";

    public static final String ALL = "ALL";
    public static final String ROUTE = "Route";

    public static final String CLOSED = "close";
    public static final String START = "start";
    public static final String STARTED = "started";


    @SuppressLint("SimpleDateFormat")
    public static String currentDate() throws ParseException {
        return new SimpleDateFormat("dd/MM/yyyy")
                .format(new Date(System.currentTimeMillis()));
    }
}
