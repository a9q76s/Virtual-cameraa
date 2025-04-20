package com.example.virtualcameraa;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.os.VEnvironment;
import com.lody.virtual.remote.InstallResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AppManager {
    private Context context;
    
    public AppManager(Context context) {
        this.context = context;
    }
    
    // صنف داخلي لتمثيل معلومات التطبيق
    public static class AppInfo {
        public String packageName;
        public String appName;
        public Drawable icon;
        public Intent launchIntent;
        
        public AppInfo(String packageName, String appName, Drawable icon, Intent launchIntent) {
            this.packageName = packageName;
            this.appName = appName;
            this.icon = icon;
            this.launchIntent = launchIntent;
        }
    }
    
    // الحصول على قائمة التطبيقات المثبتة افتراضياً
    public List<AppInfo> getInstalledVirtualApps() {
        List<AppInfo> appList = new ArrayList<>();
        
        try {
            PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> installedApps = VirtualCore.get().getInstalledApplications(0);
            
            for (ApplicationInfo appInfo : installedApps) {
                String packageName = appInfo.packageName;
                
                // إنشاء Intent لتشغيل التطبيق
                Intent launchIntent = VirtualCore.get().getLaunchIntent(packageName, 0);
                if (launchIntent == null) continue;
                
                // الحصول على اسم التطبيق وأيقونته
                String appName = pm.getApplicationLabel(appInfo).toString();
                Drawable icon = pm.getApplicationIcon(appInfo);
                
                appList.add(new AppInfo(packageName, appName, icon, launchIntent));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return appList;
    }
    
    // تثبيت تطبيق من URI
    public boolean installApp(Uri apkUri) {
        try {
            // تحويل URI إلى ملف مؤقت
            InputStream inputStream = context.getContentResolver().openInputStream(apkUri);
            if (inputStream == null) return false;
            
            File tempFile = File.createTempFile("install_", ".apk", context.getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            
            inputStream.close();
            outputStream.close();
            
            // تثبيت التطبيق باستخدام VirtualApp
            InstallResult result = VirtualCore.get().installPackage(tempFile.getAbsolutePath(), 0);
            
            // مسح الملف المؤقت
            tempFile.delete();
            
            return result.isSuccess;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // إزالة تثبيت تطبيق افتراضي
    public boolean uninstallApp(String packageName) {
        try {
            return VirtualCore.get().uninstallPackage(packageName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
