package com.example.virtualcameraa;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class CameraService extends Service {
    
    private static final String CHANNEL_ID = "VirtualCameraChannel";
    private static final int NOTIFICATION_ID = 1001;
    
    private FakeCameraProvider cameraProvider;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // إنشاء مزود الكاميرا المزيفة
        cameraProvider = new FakeCameraProvider(this);
        
        // تعيين مصدر افتراضي (يجب تعديله حسب الإعدادات المخزنة)
        String defaultImagePath = Settings.getDefaultImagePath(this);
        if (defaultImagePath != null) {
            cameraProvider.setSource(FakeCameraProvider.SourceType.IMAGE, defaultImagePath);
        }
        
        // إنشاء قناة الإشعارات للخدمة الأمامية
        createNotificationChannel();
        
        // بدء الخدمة كخدمة أمامية
        Notification notification = createNotification();
        startForeground(NOTIFICATION_ID, notification);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // بدء وظيفة الكاميرا المزيفة
        cameraProvider.start();
        
        // عودة ليتم إعادة تشغيل الخدمة إذا توقفت
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // إيقاف الكاميرا المزيفة
        if (cameraProvider != null) {
            cameraProvider.stop();
        }
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Virtual Camera Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("خدمة الكاميرا الافتراضية قيد التشغيل");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    
    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("الكاميرا الافتراضية نشطة")
                .setContentText("خدمة الكاميرا الافتراضية قيد التشغيل")
                .setSmallIcon(R.drawable.ic_camera)
                .setPriority(NotificationCompat.PRIORITY_LOW);
        
        return builder.build();
    }
}
