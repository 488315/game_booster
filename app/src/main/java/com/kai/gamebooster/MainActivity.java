package com.kai.gamebooster;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        setContentView(R.layout.activity_main);

        // Start GameLaunchAccessibilityService if it's not already running
        startAccessibilityService();
    }

    public void setMaxRefreshRate() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            Log.e(TAG, "WindowManager is null, unable to set refresh rate.");
            return;
        }

        Display display = windowManager.getDefaultDisplay();
        Display.Mode maxRefreshRateMode = display.getMode();

        for (Display.Mode mode : display.getSupportedModes()) {
            if (mode.getRefreshRate() > maxRefreshRateMode.getRefreshRate()) {
                maxRefreshRateMode = mode;
            }
        }

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.preferredDisplayModeId = maxRefreshRateMode.getModeId();
        getWindow().setAttributes(layoutParams);
        Log.d(TAG, "Display set to max refresh rate: " + maxRefreshRateMode.getRefreshRate() + "Hz");
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("com.kai.gamebooster.SET_MAX_REFRESH_RATE");
        registerReceiver(maxRefreshRateReceiver, filter, RECEIVER_EXPORTED);
    }
    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(maxRefreshRateReceiver);
    }

    private final BroadcastReceiver maxRefreshRateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setMaxRefreshRate();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_bookmarks) {
            Toast.makeText(this, "Bookmarks Clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_game_booster) {
            // Start GameBoosterService directly from menu if required
            Intent gameBoosterIntent = new Intent(this, GameBoosterService.class);
            startService(gameBoosterIntent);
            Toast.makeText(this, "Game Booster Activated", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_notices) {
            Toast.makeText(this, "Notices Clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void startAccessibilityService() {
        // Start the accessibility service if it’s not enabled
        Intent intent = new Intent(this, GameLaunchAccessibilityService.class);
        startService(intent);
    }
}
