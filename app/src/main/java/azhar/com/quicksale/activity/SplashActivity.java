package azhar.com.quicksale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import azhar.com.quicksale.api.AdminApi;
import azhar.com.quicksale.api.BillNoApi;
import azhar.com.quicksale.api.CompanyApi;
import azhar.com.quicksale.api.CustomerApi;
import azhar.com.quicksale.api.OrderNoApi;
import azhar.com.quicksale.api.ProductsApi;
import azhar.com.quicksale.api.SalesManApi;
import azhar.com.quicksale.utils.Constants;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = new RelativeLayout(this);
        ProgressBar progressBar = new ProgressBar(SplashActivity.this, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);

        setContentView(layout);

        new CompanyApi().getCompany(); //calling company details Api
        new AdminApi().getAdmin(); //calling admin details Api
        new SalesManApi().getSalesMan(); //calling salesman Api
        new CustomerApi().getCustomer(); //calling Customer Api
        new BillNoApi().getBillNo(); //calling BillNo Api
        new OrderNoApi().getOrderNo(); //calling OrderNo Api
        new ProductsApi().getProducts(); //calling Products Api

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent activity;
                if (isLoggedIn()) {
                    activity = new Intent(SplashActivity.this, LandingActivity.class);
                } else {
                    activity = new Intent(SplashActivity.this, LoginActivity.class);
                }
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(activity);
                finish();
            }
        }, 1000);

    }

    private boolean isLoggedIn() {
        return Constants.AUTH.getCurrentUser() != null;
    }
}
