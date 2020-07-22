package com.cat.appmonitor.UI;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private boolean isSelect;
    private String appLabel;
    private Drawable appIcon;
    private String pkgName;

    public AppInfo() {
    }

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appName) {
        this.appLabel = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public boolean getSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }
}
