package io.github.meetsandesh.serene.vo;

import android.content.pm.PackageInfo;

import java.io.Serializable;
import java.util.List;

public class IntentMetadata implements Serializable {

    private List<PackageInfo> allApps;
    private List<PackageInfo> installedApps;
    private List<String> appNames;

    public List<PackageInfo> getAllApps() {
        return allApps;
    }

    public void setAllApps(List<PackageInfo> allApps) {
        this.allApps = allApps;
    }

    public List<PackageInfo> getInstalledApps() {
        return installedApps;
    }

    public void setInstalledApps(List<PackageInfo> installedApps) {
        this.installedApps = installedApps;
    }

    public List<String> getAppNames() {
        return appNames;
    }

    public void setAppNames(List<String> appNames) {
        this.appNames = appNames;
    }
}
