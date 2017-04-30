package app.hacareem.com.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import app.hacareem.com.fragments.DropOffFragment;
import app.hacareem.com.fragments.PickupFragment;

/**
 * Created by Numair Qadir on 4/30/2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                PickupFragment tab1 = new PickupFragment();
                return tab1;

            case 1:
                DropOffFragment tab2 = new DropOffFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }
}