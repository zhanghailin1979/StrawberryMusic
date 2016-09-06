package learning.moliying.com.strawberrymusic.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * description:
 * company: moliying.com
 * Created by vince on 16/8/6.
 */
public class CommonFragmentAdapter extends FragmentPagerAdapter {
    private String[] titles;
    private Fragment[] fragments;

    public CommonFragmentAdapter(FragmentManager fm, String[] titles, Fragment[] fragments) {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }
}
