package io.particle.hydroalert;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.particle.hydroalert.util.DataHolder;



public class EventFragmentPagerAdapter extends FragmentPagerAdapter {

    Context mContext;

    public EventFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        StatusFragment sFragment = new StatusFragment();
        ValuesFragment vFragment = new ValuesFragment();

        if (position == 0) {
            return sFragment;
        }
        if (position == 1) {
            return vFragment;
        }
        if(position == 2){
            return new GraphWebViewFragment();
        }

        return vFragment;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return "Current Status";

        }
        if(position == 1){
            return "Recent Readings";
        }
        if(position == 2){
            return "Analytics";
        }

        return DataHolder.getInstance().getSelectedDevice().getName();
    }
}
