package io.github.meetsandesh.serene.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.meetsandesh.serene.R;
import io.github.meetsandesh.serene.vo.BroadcastActions;
import io.github.meetsandesh.serene.vo.HomeScreenConstants;
import io.github.meetsandesh.serene.vo.PackageMetadata;

public class AppListService extends Service {

    private static final String TAG = "AppListService";
    private List<PackageInfo> allPackage;
    private List<PackageInfo> installedPackage;
    private Map<String, PackageMetadata> appNames;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        fetchListOfAppNames();
//        this.appNames = getListOfAllAppNames();
        this.appNames = getListOfInstalledAppNames();
        registerBroadcastReceiver();
        return START_STICKY;
    }

    private Map<String, PackageMetadata> getListOfAllAppNames() {
        Map<String, PackageMetadata> appNames = new HashMap<>();
        for(PackageInfo pi : this.allPackage){
            String tName = getPackageManager().getApplicationLabel(pi.applicationInfo).toString();
            PackageMetadata packageMetadata = new PackageMetadata();
            packageMetadata.setAppName(tName);
            packageMetadata.setPackageName(pi.packageName);
            appNames.put(tName, packageMetadata);
        }
        return appNames;
    }

    private Map<String, PackageMetadata> getListOfInstalledAppNames() {
        Map<String, PackageMetadata> appNames = new HashMap<>();
        for(PackageInfo pi : this.installedPackage){
            String tName = getPackageManager().getApplicationLabel(pi.applicationInfo).toString();
            PackageMetadata packageMetadata = new PackageMetadata();
            packageMetadata.setAppName(tName);
            packageMetadata.setPackageName(pi.packageName);
            appNames.put(tName, packageMetadata);
        }
        return appNames;
    }

    private void registerBroadcastReceiver() {
        BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context ctxt, Intent intent) {
                Intent tIntent = new Intent();
                tIntent.setAction(BroadcastActions.BEAR_APP_LIST);
                tIntent.putExtra(HomeScreenConstants.APP_NAMES, (Serializable) appNames);
                sendBroadcast(tIntent);
            }
        };
        this.registerReceiver(mBatInfoReceiver, new IntentFilter(BroadcastActions.FETCH_APP_LIST));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void fetchListOfAppNames() {
        this.allPackage = fetchListOfInstalledApps();
        this.installedPackage = new ArrayList<>();
//        filter system package
        for(PackageInfo pi : this.allPackage) {
            boolean b = isSystemPackage(pi);
            if(!b) {
                installedPackage.add(pi);
            }
        }
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }

    private List<PackageInfo> fetchListOfInstalledApps(){
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageList = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
//        for(PackageInfo packageInfo : packageList){
//            Log.d(TAG, "NAME : "+getPackageManager().getApplicationLabel(packageInfo.applicationInfo));
//            Log.d(TAG, "TAG : "+packageInfo.applicationInfo);
//        }
        return packageList;
    }

}
