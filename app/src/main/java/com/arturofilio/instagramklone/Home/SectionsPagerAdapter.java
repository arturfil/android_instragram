package com.arturofilio.instagramklone.Home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arturofiliovilla on 1/18/18.
 */

/**Class for storing fragments for tabs
*/

public class SectionsPagerAdapter extends FragmentPagerAdapter {
   private static final String TAG = "SectionsPagerAdapter";

   private final List<Fragment> mFragmentList = new ArrayList<>();

   public SectionsPagerAdapter(FragmentManager fm) {
       super(fm);
   }

    @Override
    public Fragment getItem(int position) {
       return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
       return mFragmentList.size();
    }

    public void addFragment(Fragment fragment){
       mFragmentList.add(fragment);
    }
}
