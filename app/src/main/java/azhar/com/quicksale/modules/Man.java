package azhar.com.quicksale.modules;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import azhar.com.quicksale.R;
import azhar.com.quicksale.activity.LandingActivity;
import azhar.com.quicksale.fragment.addSalesManFragment;
import azhar.com.quicksale.fragment.viewSalesManFragment;

/**
 * Created by azharuddin on 24/7/17.
 */

public class Man {

    public void evaluate(LandingActivity activity, View itemView) {
        ViewPager viewPager = itemView.findViewById(R.id.salesman_viewpager);
        setupViewPager(activity, viewPager);

        TabLayout tabLayout = itemView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(LandingActivity activity, ViewPager viewPager) {
        ManViewPagerAdapter adapter = new ManViewPagerAdapter(activity.getSupportFragmentManager());
        adapter.addFragment(new addSalesManFragment(), "ADD");
        adapter.addFragment(new viewSalesManFragment(), "View");
        viewPager.setAdapter(adapter);
    }

    public class ManViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ManViewPagerAdapter(FragmentManager manager) {
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
