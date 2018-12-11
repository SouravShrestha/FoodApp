package com.canteen.sourav_shrestha.khanaapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new FragmentToday();
        }
        else if (position == 1)
        {
            fragment = new FragmentTomorrow();
        }
        else if (position == 2)
        {
            fragment = new TomorrowFragment();
        }
        else if (position == 3)
        {
            fragment = new TomorrowFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Today";
        }
        else if (position == 1)
        {
            title = "Tomorrow";
        }
        else if (position == 2)
        {
            title = "Monday";
        }
        else if (position == 3)
        {
            title = "Tuesday";
        }
        return title;
    }
}
