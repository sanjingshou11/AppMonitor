package com.cat.appmonitor;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cat.appmonitor.UI.AppInfo;
import com.cat.appmonitor.UI.ClearEditText;
import com.cat.appmonitor.UI.PackageInfoAdapter;
import com.cat.appmonitor.util.FileIOUtils;
import com.cat.appmonitor.util.PermissionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * https://github.com/FunnyParty/AppMonitor
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private List<String> selectedApp;
    private List<AppInfo> appInfoList;
    private boolean[] isSeleted;
    private TextView tv_selectapps;
    private String selectapps = "";
    private SharedPreferences pkgsPref;
    private PackageInfoAdapter packgaeAdapter;
    private ClearEditText et_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.process_list);

        //当android版本高于M时需要添加权限检测
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            PermissionUtil.addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            PermissionUtil.addPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            PermissionUtil.checkPermission(this,100);
        }else {
            pkgsPref = this.getSharedPreferences("pkgs", Context.MODE_WORLD_READABLE);
        }

        Button save = (Button) findViewById(R.id.saveButton);
        Button exit = (Button) findViewById(R.id.exitButton);
        tv_selectapps = findViewById(R.id.select_pkg);
        et_search = (ClearEditText) findViewById(R.id.et_search);
        ListView packageList = (ListView) findViewById(R.id.packageList);
        appInfoList = new ArrayList<AppInfo>();
        selectedApp = new ArrayList<String>();

        getPkgList();
        loadInit();

        packgaeAdapter = new PackageInfoAdapter(this, appInfoList, isSeleted);
        packageList.setAdapter(packgaeAdapter);
        packageList.setOnItemClickListener(this);
        packageList.setAlwaysDrawnWithCacheEnabled(true);
        intiEditView();

        tv_selectapps.setText(selectapps);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whichSelect(appInfoList, isSeleted);
                saveData(selectedApp);
                tv_selectapps.setText(selectapps);
                Toast.makeText(MainActivity.this.getApplicationContext(), "monitor begin", Toast.LENGTH_SHORT).show();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



    private void saveData(List<String> selectedApp)
        {
            try
            {

                selectapps = "";
                for (String app:selectedApp){
                    app += "\n";
                    selectapps += app;
                }
                FileIOUtils.writeFileFromString(Config.SD_SETTING_FILE, selectapps, false);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Config.createfile();
                    FileIOUtils.writeFileFromString(Config.SETTING_FILE, selectapps, false);
                }else {
                    //适配android4.4
                    SharedPreferences.Editor edit = pkgsPref.edit();
                    edit.clear();
                    edit.putStringSet("pkgs", new HashSet<String>(selectedApp));
                    edit.apply();
                }

            } catch (Throwable e)
            {
                System.out.println(e.getMessage());
            }
        }

        //初始化
        public void loadInit() {
            isSeleted = new boolean[appInfoList.size()];
            String pkgs = FileIOUtils.readFile2String(Config.SD_SETTING_FILE);

            selectapps = "";
            if (pkgs != null){
                HashSet<String> sets = new HashSet<>();
                String[] apps = pkgs.split("\n");
                for (String app: apps){
                    sets.add(app);
                }
                for (AppInfo appinfo : appInfoList) {
                    if (sets.contains(appinfo.getPkgName())) {
                        appinfo.setSelect(true);
                        int i = appInfoList.indexOf(appinfo);
                        isSeleted[i] = true;
                        selectapps += appinfo.getPkgName();
                        selectapps += "\n";
                    }
                }
            }
        }

    //获取所有的APP包名信息
    public void getPkgList() {
        PackageManager packManager = this.getPackageManager();
        List<PackageInfo> packageInfoList = packManager.getInstalledPackages(0);

        for (int i = 0; i < packageInfoList.size(); i++) {
            AppInfo appInfo = new AppInfo();
            PackageInfo packageInfo = packageInfoList.get(i);
            appInfo.setAppIcon(packManager
                    .getApplicationIcon(packageInfo.applicationInfo));
            appInfo.setAppLabel(packManager.getApplicationLabel(
                    packageInfo.applicationInfo).toString());
            appInfo.setPkgName(packageInfo.applicationInfo.packageName);
            appInfo.setSelect(false);
            appInfoList.add(appInfo);
        }

        //按包名排序
        Collections.sort(this.appInfoList, new Comparator<AppInfo>() {
            public int compare(AppInfo lhs, AppInfo rhs) {
                return lhs.getPkgName().compareTo(rhs.getPkgName());
            }
        });
    }

    //将选中的APP保存起来
    public void whichSelect(List<AppInfo> appInfo, boolean[] selected) {
        selectedApp.clear();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                selectedApp.add(appInfo.get(i).getPkgName());
            }
        }
    }

    //将选中的app设置为以选中
    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        appInfoList = packgaeAdapter.getPackageInfo();
        //String pkgName = appInfoList.get(position).getPkgName();
        isSeleted = new boolean[appInfoList.size()];
        RelativeLayout lr = (RelativeLayout) view;
        CheckBox tmp = (CheckBox) lr.getChildAt(3);
        tmp.toggle();
        isSeleted[position] = tmp.isChecked();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        selectedApp.clear();
        saveData(selectedApp);
        for (int i = 0; i< isSeleted.length; i++){
            isSeleted[i] = false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.handlePermissionResult(this,requestCode,permissions,grantResults);
    }

    private void intiEditView() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                packgaeAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
