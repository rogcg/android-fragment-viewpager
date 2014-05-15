package com.myapp.asd.app;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TabHost;
import android.widget.TabWidget;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {

        private TabHost mTabHost;
        private ViewPager mViewPager;
        private TabsAdapter mTabsAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle instance)
        {
            super.onCreate(instance);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_main, container, false);

            mTabHost = (TabHost) v.findViewById(android.R.id.tabhost);
            mTabHost.setup();

            mViewPager = (ViewPager) v.findViewById(R.id.pager);
            mTabsAdapter = new TabsAdapter(getActivity(), mTabHost, mViewPager);

            mTabsAdapter.addTab(mTabHost.newTabSpec("one").setIndicator("One"), PageOneFragment.class, null);
            mTabsAdapter.addTab(mTabHost.newTabSpec("two").setIndicator("Two"), PageTwoFragment.class, null);

            return v;
        }

        public static class TabsAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener
        {
            private final Context mContext;
            private final TabHost mTabHost;
            private final ViewPager mViewPager;
            private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

            static final class TabInfo
            {
                private final String tag;
                private final Class<?> clss;
                private final Bundle args;

                TabInfo(String _tag, Class<?> _class, Bundle _args)
                {
                    tag = _tag;
                    clss = _class;
                    args = _args;
                }
            }

            static class DummyTabFactory implements TabHost.TabContentFactory
            {
                private final Context mContext;

                public DummyTabFactory(Context context)
                {
                    mContext = context;
                }

                public View createTabContent(String tag)
                {
                    View v = new View(mContext);
                    v.setMinimumWidth(0);
                    v.setMinimumHeight(0);
                    return v;
                }
            }

            public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager)
            {
                super(activity.getSupportFragmentManager());
                mContext = activity;
                mTabHost = tabHost;
                mViewPager = pager;
                mTabHost.setOnTabChangedListener(this);
                mViewPager.setAdapter(this);
                mViewPager.setOnPageChangeListener(this);
            }

            public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args)
            {
                tabSpec.setContent(new DummyTabFactory(mContext));
                String tag = tabSpec.getTag();

                TabInfo info = new TabInfo(tag, clss, args);
                mTabs.add(info);
                mTabHost.addTab(tabSpec);
                notifyDataSetChanged();
            }

            @Override
            public int getCount()
            {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position)
            {
                TabInfo info = mTabs.get(position);

                return Fragment.instantiate(mContext, info.clss.getName(), info.args);

            }

            public void onTabChanged(String tabId)
            {
                int position = mTabHost.getCurrentTab();
                mViewPager.setCurrentItem(position);
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            public void onPageSelected(int position)
            {
                // Unfortunately when TabHost changes the current tab, it kindly
                // also takes care of putting focus on it when not in touch mode.
                // The jerk.
                // This hack tries to prevent this from pulling focus out of our
                // ViewPager.
                TabWidget widget = mTabHost.getTabWidget();
                int oldFocusability = widget.getDescendantFocusability();
                widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                mTabHost.setCurrentTab(position);
                widget.setDescendantFocusability(oldFocusability);
            }

            public void onPageScrollStateChanged(int state)
            {
            }
        }
    }
}
