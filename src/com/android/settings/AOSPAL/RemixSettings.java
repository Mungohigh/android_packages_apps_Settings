package com.android.settings.AOSPAL;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.internal.util.paranoid.DeviceUtils;

import com.android.settings.AOSPAL.AnimationSettings;
import com.android.settings.AOSPAL.NavBarSettings;
import com.android.settings.AOSPAL.NotificationDrawerQsSettings;
import com.android.settings.AOSPAL.StatusBarSettings;
import com.android.settings.AOSPAL.SystemSettings;
import com.android.settings.AOSPAL.LockscreenSettings;
import com.android.settings.AOSPAL.FragmentNavigationDrawer;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;


import java.util.ArrayList;
import java.util.List;

public class RemixSettings extends FragmentActivity {

    PagerTabStrip mPagerTabStrip;
    ViewPager mViewPager;

    String titleString[];

    ViewGroup mContainer;
    private FragmentNavigationDrawer dlDrawer;

    static Bundle mSavedState;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainer = container;

        View view = inflater.inflate(R.layout.remix_settings, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mPagerTabStrip = (PagerTabStrip) view.findViewById(R.id.pagerTabStrip);
        mPagerTabStrip.setDrawFullUnderline(true);

        StatusBarAdapter StatusBarAdapter = new StatusBarAdapter(getFragmentManager());
        mViewPager.setAdapter(StatusBarAdapter);

        // Find our drawer view
        dlDrawer = (FragmentNavigationDrawer) view.findViewById(R.id.drawer_layout);
        // Setup drawer view
        dlDrawer.setupDrawerConfiguration((ListView) view.findViewById(R.id.lvDrawer),
                     R.layout.drawer_nav_item, R.id.flContent);
        // Add nav items
        dlDrawer.addNavItem("System", "First Fragment", SystemSettings.class);
        dlDrawer.addNavItem("Status Bar", "Second Fragment", StatusBarSettings.class);
        dlDrawer.addNavItem("Navigation Bar", "Third Fragment", NavBarSettings.class);
        dlDrawer.addNavItem("Notification Drawer & QS", "Fourth Fragment", NotificationDrawerQsSettings.class);
        dlDrawer.addNavItem("Lockscreen", "Fifth Fragment", LockscreenSettings.class);
        dlDrawer.addNavItem("Animations", "Sixth Fragment", AnimationSettings.class);
        // Select default
        if (savedInstanceState == null) {
            dlDrawer.selectDrawerItem(0);
        }

        return view;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        if (dlDrawer.isDrawerOpen()) {
            menu.findItem(R.id.action_check).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (dlDrawer.getDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        dlDrawer.getDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        dlDrawer.getDrawerToggle().onConfigurationChanged(newConfig);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle saveState) {
        super.onSaveInstanceState(saveState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity test = (Activity) this;
        if (!DeviceUtils.isTablet(test)) {
            mContainer.setPadding(0, 0, 0, 0);
        }
    }

    class StatusBarAdapter extends FragmentPagerAdapter {
        String titles[] = getTitles();
        private Fragment frags[] = new Fragment[titles.length];

        public StatusBarAdapter(FragmentManager fm) {
            super(fm);
            frags[0] = new SystemSettings();
            frags[1] = new StatusBarSettings();
            frags[2] = new NavBarSettings();
            frags[3] = new NotificationDrawerQsSettings();
            frags[4] = new LockscreenSettings();
            frags[5] = new AnimationSettings();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return frags[position];
        }

        @Override
        public int getCount() {
            return frags.length;
        }
    }

    private String[] getTitles() {
        String titleString[];
        Activity test = (Activity) this;
        if (!DeviceUtils.isPhone(test)) {
        titleString = new String[]{
                    getString(R.string.remix_settings_system_title),
                    getString(R.string.remix_settings_statusbar_title),
                    getString(R.string.navigation_bar),
                    getString(R.string.remix_settings_notification_drawer),
                    getString(R.string.remix_settings_lockscreen_title),
                    getString(R.string.remix_settings_animations_title)};
        } else {
        titleString = new String[]{
                    getString(R.string.remix_settings_system_title),
                    getString(R.string.remix_settings_statusbar_title),
                    getString(R.string.navigation_bar),
                    getString(R.string.remix_settings_notification_drawer_qs),
                    getString(R.string.remix_settings_lockscreen_title),
                    getString(R.string.remix_settings_animations_title)};
        }
        return titleString;
    }
}
