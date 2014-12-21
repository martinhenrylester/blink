package com.nashlincoln.blink.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.nashlincoln.blink.R;
import com.nashlincoln.blink.app.BlinkApp;
import com.nashlincoln.blink.app.FragmentPagerAdapter;
import com.nashlincoln.blink.content.Syncro;
import com.nashlincoln.blink.event.Event;
import com.nashlincoln.blink.network.BlinkApi;
import com.nashlincoln.blink.nfc.NfcUtils;
import com.nashlincoln.blink.widget.SlidingTabLayout;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by nash on 10/5/14.
 */
public class BlinkActivity extends ActionBarActivity implements Observer {

    private static final String TAG = "BlinkActivity";
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blink);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new FragmentAdapter(getFragmentManager()));

        mSlidingTabLayout = new SlidingTabLayout(this);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);

        Resources res = getResources();
        mSlidingTabLayout.setSelectedIndicatorColors(res.getColor(R.color.accent));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Gravity.BOTTOM);
        toolbar.addView(mSlidingTabLayout, params);

        if (!BlinkApp.getApp().isConfigured()) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(BlinkApp.EXTRA_NFC_WRITE)) {
            NfcUtils.writeTag(this, intent);
        } else if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            NfcUtils.readTag(this, intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this);
        if (defaultAdapter != null) {
            defaultAdapter.enableForegroundDispatch(
                    this,
                    PendingIntent.getActivity(this, 0, new Intent(this, BlinkActivity.class), PendingIntent.FLAG_UPDATE_CURRENT),
                    new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)},
                    null);
        }

        Event.observe("network", this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this);
        if (defaultAdapter != null) {
            defaultAdapter.disableForegroundDispatch(this);
        }
        Event.ignore("network", this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_blink, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_refresh:
                Syncro.getInstance().fetchDevicesAndGroups();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable observable, Object data) {
        Log.d(TAG, "update: " + BlinkApi.getRequestCount());
        runOnUiThread(mProgressRunnable);
    }

    Runnable mProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (BlinkApi.getRequestCount() > 0) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    };

    class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Devices";
                case 1:
                    return "Groups";
                case 2:
                    return "Scenes";
                case 3:
                    return "Timers";
            }
            return super.getPageTitle(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            String className = null;
            switch (position) {
                case 0:
                    className = DeviceListFragment.class.getName();
                    break;

                case 1:
                    className = GroupListFragment.class.getName();
                    break;

                case 2:
                    className = SceneListFragment.class.getName();
                    break;

                case 3:
                    className = SceneListFragment.class.getName();
                    break;
            }
            return Fragment.instantiate(BlinkActivity.this, className);
        }
    }
}
