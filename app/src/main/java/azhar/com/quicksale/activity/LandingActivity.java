package azhar.com.quicksale.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import azhar.com.quicksale.R;
import azhar.com.quicksale.api.BillNoApi;
import azhar.com.quicksale.api.CompanyApi;
import azhar.com.quicksale.api.CustomerApi;
import azhar.com.quicksale.api.ProductsApi;
import azhar.com.quicksale.api.SalesManApi;
import azhar.com.quicksale.listeners.BillNoListener;
import azhar.com.quicksale.listeners.CustomerListener;
import azhar.com.quicksale.listeners.ProductsListener;
import azhar.com.quicksale.listeners.SalesManListener;
import azhar.com.quicksale.modules.Customer;
import azhar.com.quicksale.modules.Man;
import azhar.com.quicksale.modules.Order;
import azhar.com.quicksale.modules.Return;
import azhar.com.quicksale.modules.Sales;
import azhar.com.quicksale.modules.Setup;
import azhar.com.quicksale.modules.Taken;
import azhar.com.quicksale.utils.Constants;

public class LandingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CustomerListener,
        SalesManListener,
        ProductsListener,
        BillNoListener {

    View taken, order, salesStore, returns, customer, salesMan, setup;
    public static int whereIam = 0;

    TextView companyName, companyPhone, companyMail;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        new CustomerApi().setCustomerListener(this);
        new SalesManApi().setSalesManListener(this);
        new ProductsApi().setProductsListener(this);
        new BillNoApi().setBillNoListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Taken");
        }

        taken = findViewById(R.id.taken_holder);
        order = findViewById(R.id.order_holder);
        salesStore = findViewById(R.id.sales_holder);
        returns = findViewById(R.id.return_holder);
        customer = findViewById(R.id.customer_holder);
        salesMan = findViewById(R.id.sales_man_holder);
        setup = findViewById(R.id.setup_holder);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navSetup(navigationView.getHeaderView(0));
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        switchScreen();
    }

    private void navSetup(View headerView) {

        try {
            companyName = headerView.findViewById(R.id.admin_company);
            companyPhone = headerView.findViewById(R.id.admin_phone);
            companyMail = headerView.findViewById(R.id.admin_email);

            HashMap<String, Object> company = CompanyApi.company;

            final String name = company.get("name").toString();
            final String number = company.get("phone").toString();
            final String mail = company.get("email").toString();

            companyName.setText(name.toUpperCase());
            companyPhone.setText(number);
            companyMail.setText(mail.toLowerCase());

            RelativeLayout nav = headerView.findViewById(R.id.company_name);
            nav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editCustomerDialog(name, number, mail);
                }
            });
        } catch (NullPointerException e) {
            Log.e("Error", e.getMessage());
        }
    }

    private void editCustomerDialog(String name, String number, String mail) {
        final Dialog dialog = new Dialog(LandingActivity.this, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_company);

        Window window = dialog.getWindow();
        assert window != null;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP;

        final EditText etName = dialog.findViewById(R.id.et_company_name);
        final EditText etPhone = dialog.findViewById(R.id.et_company_phone);
        final EditText etMail = dialog.findViewById(R.id.et_company_mail);

        etName.setText(name);
        etPhone.setText(number);
        etMail.setText(mail);

        TextView setAdminDetails = dialog.findViewById(R.id.btn_set_admin_details);
        setAdminDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String compName = etName.getText().toString();
                String compPhone = etPhone.getText().toString();
                String compMail = etMail.getText().toString();
                if (!compName.isEmpty() && !compPhone.isEmpty() && !compMail.isEmpty()) {
                    CompanyApi.companyDBRef.removeValue();
                    HashMap<String, Object> companyMap = new HashMap<>();
                    companyMap.put(Constants.COMPANY_NAME, compName);
                    companyMap.put(Constants.COMPANY_PHONE, compPhone);
                    companyMap.put(Constants.COMPANY_EMAIL, compMail);
                    CompanyApi.companyDBRef.updateChildren(companyMap);
                    companyName.setText(compName.toUpperCase());
                    companyPhone.setText(compPhone);
                    companyMail.setText(compMail);

                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_taken) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Taken");
            }
            whereIam = 0;
        } else if (id == R.id.nav_order) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Order");
            }
            whereIam = 1;
        } else if (id == R.id.nav_sales) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Sales" + " (Last bill no:- "
                        + BillNoApi.billNo.get(BillNoApi.billNo.size() - 1) + ")");
            }
            whereIam = 2;
        } else if (id == R.id.nav_returns) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Return");
            }
            whereIam = 3;
        } else if (id == R.id.nav_customer) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Customer");
            }
            whereIam = 4;
        } else if (id == R.id.nav_sales_man) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Sales Man");
            }
            whereIam = 5;
        } else if (id == R.id.nav_setup) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Set-up");
            }
            whereIam = 6;
        }
        switchScreen();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void switchScreen() {
        taken.setVisibility(View.GONE);
        order.setVisibility(View.GONE);
        salesStore.setVisibility(View.GONE);
        returns.setVisibility(View.GONE);
        customer.setVisibility(View.GONE);
        salesMan.setVisibility(View.GONE);
        setup.setVisibility(View.GONE);

        switch (whereIam) {
            case 0:
                taken.setVisibility(View.VISIBLE);
                new Taken().evaluate(this, taken);
                break;
            case 1:
                order.setVisibility(View.VISIBLE);
                new Order().evaluate(this, order);
                break;
            case 2:
                salesStore.setVisibility(View.VISIBLE);
                new Sales().evaluate(this, salesStore);
                break;
            case 3:
                returns.setVisibility(View.VISIBLE);
                new Return().evaluate(this, returns);
                break;
            case 4:
                customer.setVisibility(View.VISIBLE);
                new Customer().evaluate(LandingActivity.this, customer);
                break;
            case 5:
                salesMan.setVisibility(View.VISIBLE);
                new Man().evaluate(this, salesMan);
                break;
            case 6:
                setup.setVisibility(View.VISIBLE);
                new Setup().evaluate(this, setup);
                break;
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public void getCustomer(HashMap<String, Object> customer) {
        switchScreen();
    }

    @Override
    public void getSalesMan(HashMap<String, Object> salesMan) {
        switchScreen();
    }

    @Override
    public void getProductsListener(HashMap<String, Object> customer) {
        switchScreen();
    }

    @Override
    public void getBillNo(List<Integer> billNo) {
        switchScreen();
    }
}
