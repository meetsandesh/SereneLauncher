package io.github.meetsandesh.serene;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import io.github.meetsandesh.serene.listeners.OnSwipeTouchListener;
import io.github.meetsandesh.serene.service.AppListService;
import io.github.meetsandesh.serene.util.ImageUtil;
import io.github.meetsandesh.serene.vo.HomeScreenConstants;
import io.github.meetsandesh.serene.widget.BatteryWidget;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Map<String, TextView> homeTextActions;
    private Map<String, ImageView> homeImageActions;
    private OnSwipeTouchListener onSwipeTouchListener;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setup global variables
        setupGlobalVars();
        //setup battery status
        addBatteryStatus();
        //add home screen buttons
        addPhone();
        //add app settings
        addAppSettings();
        //add app drawer action
        addAppDrawer();
        //setup device
        setupDevice();
    }

    private void addBatteryStatus() {
        TextView textView = findViewById(R.id.tv3);
        homeTextActions.put(HomeScreenConstants.BATTERY, textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            progressBar.setMinWidth(width);
        }
        BatteryWidget batteryWidget = new BatteryWidget(this, progressBar, textView);
        batteryWidget.registerBroadcastReceiver();
    }

    private void addAppSettings() {
        TextView settings= this.findViewById(R.id.tv2);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Under Development.", Toast.LENGTH_SHORT).show();
            }
        });
        this.homeTextActions.put(HomeScreenConstants.APP_SETTINGS, settings);
    }

    private void addAppDrawer() {
        ImageView appDrawer = this.findViewById(R.id.img1);
        appDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAppDrawer();
            }
        });
        homeImageActions.put(HomeScreenConstants.APP_DRAWER, appDrawer);
    }

    private void openAppDrawer() {
        Intent intent = new Intent(this, AppDrawer.class);
        startActivity(intent);
    }

    private void openNotificationPanel() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.EXPAND_STATUS_BAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            try {
                Object service = getSystemService("statusbar");
                Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                Method expand = statusbarManager.getMethod("expandNotificationsPanel");
                expand.invoke(service);
            } catch (Exception e) {
                Log.e("StatusBar", e.toString());
                Toast.makeText(getApplicationContext(), "Expansion Not Working", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupDevice() {
        setSereneWallpaper();
        startService(new Intent(getBaseContext(), AppListService.class));
    }

    private void setSereneWallpaper() {
        WallpaperManager wm = WallpaperManager.getInstance(this);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_default_wallpaper);
        Bitmap bitmap = ImageUtil.drawableToBitmap(drawable);
        try {
            wm.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupGlobalVars() {
        this.homeTextActions = new HashMap<>();
        this.homeImageActions = new HashMap<>();
        setupSwipeListener();
    }

    private void setupSwipeListener() {
        onSwipeTouchListener = new OnSwipeTouchListener(this, findViewById(R.id.relativeLayout));
        onSwipeTouchListener.overrideSwipeAction(new OnSwipeTouchListener.OnSwipeListener() {
            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeTop() {
                openAppDrawer();
            }

            @Override
            public void swipeBottom() {
                openNotificationPanel();
            }

            @Override
            public void swipeLeft() {

            }
        });
    }

    private void addPhone() {
        TextView phone= this.findViewById(R.id.tv1);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_DIAL);
                try {
                    startActivity(i);
                } catch (SecurityException s) {
                    // show() method display the toast with
                    // exception message.
                    Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
                }
            }
        });
        this.homeTextActions.put(HomeScreenConstants.PHONE, phone);
    }

}