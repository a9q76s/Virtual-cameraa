package com.example.virtualcameraa;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

public class Settings {
    
    private static final String PREFS_NAME = "VirtualCameraaPrefs";
    private static final String KEY_DEFAULT_IMAGE_PATH = "default_image_path";
    private static final String KEY_DEFAULT_VIDEO_PATH = "default_video_path";
    private static final String KEY_SOURCE_TYPE = "source_type";
    
    private static SharedPreferences prefs;
    
    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
        
        // إنشاء الدلائل الافتراضية إذا لم تكن موجودة
        createDefaultDirs(context);
    }
    
    private static void createDefaultDirs(Context context) {
        File mediaDir = new File(context.getFilesDir(), "media");
        File imagesDir = new File(mediaDir, "images");
        File videosDir = new File(mediaDir, "videos");
        
        if (!imagesDir.exists()) {
            imagesDir.mkdirs();
        }
        
        if (!videosDir.exists()) {
            videosDir.mkdirs();
        }
    }
    
    public static String getDefaultImagePath(Context context) {
        if (prefs == null) {
            init(context);
        }
        
        return prefs.getString(KEY_DEFAULT_IMAGE_PATH, null);
    }
    
    public static void setDefaultImagePath(Context context, String path) {
        if (prefs == null) {
            init(context);
        }
        
        prefs.edit().putString(KEY_DEFAULT_IMAGE_PATH, path).apply();
    }
    
    public static String getDefaultVideoPath(Context context) {
        if (prefs == null) {
            init(context);
        }
        
        return prefs.getString(KEY_DEFAULT_VIDEO_PATH, null);
    }
    
    public static void setDefaultVideoPath(Context context, String path) {
        if (prefs == null) {
            init(context);
        }
        
        prefs.edit().putString(KEY_DEFAULT_VIDEO_PATH, path).apply();
    }
    
    public static FakeCameraProvider.SourceType getSourceType(Context context) {
        if (prefs == null) {
            init(context);
        }
        
        int sourceTypeValue = prefs.getInt(KEY_SOURCE_TYPE, 0);
        switch (sourceTypeValue) {
            case 1:
                return FakeCameraProvider.SourceType.VIDEO;
            case 2:
                return FakeCameraProvider.SourceType.STREAM;
            default:
                return FakeCameraProvider.SourceType.IMAGE;
        }
    }
    
    public static void setSourceType(Context context, FakeCameraProvider.SourceType sourceType) {
        if (prefs == null) {
            init(context);
        }
        
        int value;
        switch (sourceType) {
            case VIDEO:
                value = 1;
                break;
            case STREAM:
                value = 2;
                break;
            default:
                value = 0;
                break;
        }
        
        prefs.edit().putInt(KEY_SOURCE_TYPE, value).apply();
    }
}
