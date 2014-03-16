package com.android.settings.AOSPAL;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.internal.util.paranoid.DeviceUtils;

import com.android.settings.AOSPAL.AnimationSettings;
import com.android.settings.AOSPAL.NavBarSettings;
import com.android.settings.AOSPAL.NotificationDrawerQsSettings;
import com.android.settings.AOSPAL.StatusBarSettings;
import com.android.settings.AOSPAL.SystemSettings;
import com.android.settings.AOSPAL.LockscreenSettings;
import com.android.settings.AOSPAL.adapter.NavDrawerListAdapter;
import com.android.settings.AOSPAL.model.NavDrawerItem;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;


import java.util.ArrayList;
import java.util.List;

public class RemixSettings extends SettingsPreferenceFragment implements
        ActionBar.OnNavigationListener {

    // Fragments
    private static final int System = 0;
    private static final int StatusBar = 1;
    private static final int NavBar = 2;
    private static final int NotifDrawerQs = 3;
    private static final int Lockscreen = 4;
    private static final int Animations = 5;

    private SystemSettings mSystemSettings;
    private StatusBarSettings mStatusBarSettings;
    private NavBarSettings mNavBarSettings;
    private NotificationDrawerQsSettings mNotificationDrawerQsSettings;
    private LockscreenSettings mLockscreenSettings;
    private AnimationSettings mAnimationSettings;
    private Context mContext;

    PagerTabStrip mPagerTabStrip;
    ViewPager mViewPager;

    String titleString[];
    ViewGroup mContainer;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private int mPosition;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainer = container;

        View view = inflater.inflate(R.layout.remix_settings, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mPagerTabStrip = (PagerTabStrip) view.findViewById(R.id.pagerTabStrip);
        mPagerTabStrip.setDrawFullUnderline(true);

        TabsPagerAdapter TabsPagerAdapter = new TabsPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(TabsPagerAdapter);

        /**
         * on swiping the viewpager make respective tab selected
         * */
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

        /*
         * navSpinner (navigation dropdown menu that sit next to navigation drawer
         */

        ActionBar actionBar = getActionBar();

        // Hide the action bar title by setting to false
        if (actionBar != null) actionBar.setDisplayShowTitleEnabled(true);

        // Nav drawer
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        if (!DeviceUtils.isPhone(getActivity())) {
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items_tablets);
        } else {
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items_phones);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4]));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5]));

        // setting the nav drawer list adapter
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        }

        mContext = this;

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.remix_settings_title, // nav drawer open - description for accessibility
                R.string.remix_settings_title) // nav drawer close - description for accessibility
        {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(System);
        }

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    // navigation drawer dropdown menu
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // After confirming PreferenceScreen is available, we call super.
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!DeviceUtils.isTablet(getActivity())) {
            mContainer.setPadding(0, 0, 0, 0);
        }
    }

    class TabsPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 6;
        // Tab Titles
        private String tabtitles[] = new String[]{"System", "Status Bar", "Navigation Bar", "Notification Drawer and QS", "Lockscreen", "Animations"};

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SystemSettings();
            case 1:
                return new StatusBarSettings();
            case 2:
                return new NavBarSettings();
            case 3:
                return new NotificationDrawerQsSettings();
            case 4:
                return new LockscreenSettings();
            case 5:
                return new AnimationSettings();
            }

            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabtitles[position];
        }
    }

     /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case System:
                fragment = mSystemSettings();
                break;
            case StatusBar:
                fragment = mStatusBarSettings();
                break;
            case NavBar:
                fragment = mNavBarSettings();
                break;
            case NotifDrawerQs:
                fragment = mNotificationDrawerQsSettings();
                break;
            case Lockscreen:
                fragment = mLockscreenSettings();
                break;
            case Animations:
                fragment = mAnimationSettings();
                break;

            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

            mPosition = position;
            invalidateOptionsMenu();
        } else {
            // error in creating fragment
            Log.e("RemixSettings", "Error in creating fragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	    // toggle nav drawer on selecting action bar app icon/title
	    if (mDrawerToggle.onOptionsItemSelected(item)) {
	    	return true;
	    }
	    // Handle action bar actions click
	    switch (item.getItemId()) {
	    case R.id.action_settings:
	     	return true;
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        menu.findItem(R.id.action_check).setVisible(mPosition == 0);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        //getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
