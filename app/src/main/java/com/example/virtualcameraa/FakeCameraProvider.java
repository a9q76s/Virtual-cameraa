package com.example.virtualcameraa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FakeCameraProvider {
    
    private Context context;
    private Camera camera;
    private SurfaceTexture surfaceTexture;
    private Handler handler;
    private Runnable frameUpdater;
    private boolean isRunning = false;
    
    // نوع مصدر الكاميرا
    public enum SourceType {
        IMAGE,
        VIDEO,
        STREAM
    }
    
    private SourceType currentSourceType = SourceType.IMAGE;
    private String currentSourcePath;
    
    public FakeCameraProvider(Context context) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
        this.surfaceTexture = new SurfaceTexture(0);
    }
    
    public void setSource(SourceType type, String path) {
        this.currentSourceType = type;
        this.currentSourcePath = path;
    }
    
    public void start() {
        if (isRunning) return;
        
        try {
            // إغلاق الكاميرا الحقيقية إذا كانت متاحة
            releaseCamera();
            
            // فتح الكاميرا (الأمامية عادة)
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            
            // تعيين معلمات الكاميرا
            Camera.Parameters parameters = camera.getParameters();
            
            // البحث عن أفضل حجم معاينة
            List<Camera.Size> supportedSizes = parameters.getSupportedPreviewSizes();
            Camera.Size optimalSize = getOptimalPreviewSize(supportedSizes, 640, 480);
            
            if (optimalSize != null) {
                parameters.setPreviewSize(optimalSize.width, optimalSize.height);
            }
            
            // تعيين مرشحات الكاميرا
            parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
            
            // تطبيق المعلمات
            camera.setParameters(parameters);
            
            // بدء المعاينة
            camera.setPreviewTexture(surfaceTexture);
            camera.startPreview();
            
            // إذا كان المصدر صورة أو فيديو، نبدأ تشغيل الإطارات المزيفة
            if (currentSourceType != SourceType.STREAM) {
                startFakeFrames();
            }
            
            isRunning = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stop() {
        if (!isRunning) return;
        
        // إيقاف تحديث الإطارات
        if (frameUpdater != null) {
            handler.removeCallbacks(frameUpdater);
            frameUpdater = null;
        }
        
        releaseCamera();
        isRunning = false;
    }
    
    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
    
    private void startFakeFrames() {
        if (frameUpdater != null) {
            handler.removeCallbacks(frameUpdater);
        }
        
        frameUpdater = new Runnable() {
            @Override
            public void run() {
                if (camera == null || !isRunning) return;
                
                try {
                    // تحميل الصورة من المصدر
                    Bitmap sourceBitmap = loadSourceImage();
                    if (sourceBitmap == null) return;
                    
                    // تحديث معاينة الكاميرا
                    // (هذا سيختلف حسب كيفية دمج الكاميرا المزيفة في نظام أندرويد)
                    
                    // إعادة جدولة التحديث التالي
                    int delay = (currentSourceType == SourceType.VIDEO) ? 33 : 500;  // ~30 fps للفيديو، تحديث أبطأ للصور
                    handler.postDelayed(this, delay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        handler.post(frameUpdater);
    }
    
    private Bitmap loadSourceImage() {
        if (currentSourcePath == null) return null;
        
        try {
            File file = new File(currentSourcePath);
            if (!file.exists()) return null;
            
            FileInputStream fis = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            fis.close();
            
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // البحث عن أفضل حجم معاينة للكاميرا
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        if (sizes == null || sizes.isEmpty()) return null;
        
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        
        for (Camera.Size size : sizes) {
            double diff = Math.abs(size.width - width) + Math.abs(size.height - height);
            if (diff < minDiff) {
                optimalSize = size;
                minDiff = diff;
            }
        }
        
        return optimalSize;
    }
}
