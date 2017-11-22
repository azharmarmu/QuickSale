package azhar.com.quicksale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.HashMap;

import azhar.com.quicksale.api.FireBaseAPI;
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

        FireBaseAPI.getCompany();
        FireBaseAPI.getOrder();
        FireBaseAPI.getCustomer();
        FireBaseAPI.getTaken();
        FireBaseAPI.getBilling();
        FireBaseAPI.getSalesMan();
        FireBaseAPI.getProductPrice();
        FireBaseAPI.getProductHSN();
        FireBaseAPI.getAdmin();
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

    private void updateTaken() {
        HashMap<String, Object> taken = FireBaseAPI.taken;

    }

    private void updateOrder() {
        HashMap<String, Object> order = FireBaseAPI.order;
    }

    private boolean isLoggedIn() {
        return Constants.AUTH.getCurrentUser() != null;
    }
}
