package com.example.virtualcameraa;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VActivityManager;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_APP = 1001;
    private AppAdapter appAdapter;
    private AppManager appManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // تهيئة مدير التطبيقات
        appManager = new AppManager(this);
        
        // تهيئة RecyclerView لعرض التطبيقات
        RecyclerView appList = findViewById(R.id.app_list);
        appList.setLayoutManager(new LinearLayoutManager(this));
        
        // تهيئة adapter
        appAdapter = new AppAdapter(appManager.getInstalledVirtualApps(), app -> {
            // عند النقر على التطبيق، يتم تشغيله
            try {
                VActivityManager.get().startActivity(
                    app.launchIntent, 
                    0
                );
            } catch (Exception e) {
                Toast.makeText(this, "فشل في تشغيل التطبيق: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        
        appList.setAdapter(appAdapter);
        
        // تعيين زر تثبيت تطبيق جديد
        findViewById(R.id.fab_add_app).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/vnd.android.package-archive");
            startActivityForResult(intent, REQUEST_SELECT_APP);
        });

        // تشغيل خدمة الكاميرا الافتراضية
        startService(new Intent(this, CameraService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_APP && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                // تثبيت التطبيق المحدد
                boolean success = appManager.installApp(uri);
                if (success) {
                    // تحديث قائمة التطبيقات
                    appAdapter.updateAppList(appManager.getInstalledVirtualApps());
                    Toast.makeText(this, "تم تثبيت التطبيق بنجاح", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "فشل في تثبيت التطبيق", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // تحديث قائمة التطبيقات عند العودة للنشاط
        appAdapter.updateAppList(appManager.getInstalledVirtualApps());
    }
}
