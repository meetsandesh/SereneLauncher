package io.github.meetsandesh.serene.vo;

import java.io.Serializable;

public class PackageMetadata implements Serializable {

    private String packageName;
    private String appName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
