package azhar.com.quicksale.modules;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import azhar.com.quicksale.R;
import azhar.com.quicksale.activity.LandingActivity;
import azhar.com.quicksale.fragment.addCustomerFragment;
import azhar.com.quicksale.fragment.viewCustomerFragment;
import azhar.com.quicksale.model.CustomerModel;

/**
 * Created by azharuddin on 24/7/17.
 */

@SuppressWarnings("unchecked")
public class Customer {
    private static HashMap<String, Object> customer = new HashMap<>();
    private static List<CustomerModel> customerList;

    public void evaluate(LandingActivity activity, View itemView) {
        ViewPager viewPager = itemView.findViewById(R.id.customer_viewpager);
        setupViewPager(activity, viewPager);

        TabLayout tabLayout = itemView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(LandingActivity activity, ViewPager viewPager) {
        CustomerViewPagerAdapter adapter = new CustomerViewPagerAdapter(activity.getSupportFragmentManager());
        adapter.addFragment(new addCustomerFragment(), "ADD");
        adapter.addFragment(new viewCustomerFragment(), "View");
        viewPager.setAdapter(adapter);
    }

    public class CustomerViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        CustomerViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
