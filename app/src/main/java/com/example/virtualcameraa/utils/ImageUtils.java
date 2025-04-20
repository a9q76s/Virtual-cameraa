package com.example.virtualcameraa.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.virtualcameraa.Settings;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for handling images for virtual camera feed
 */
public class ImageUtils {
    private static final String TAG = "ImageUtils";
    private static Handler handler;
    private static Runnable refreshRunnable;
    private static SurfaceTextureListener textureListener;
    
    /**
     * Start image feed for the virtual camera
     * @param context Context to access resources
     * @param listener Callback for when the surface texture is ready
     */
    public static void startImageFeed(Context context, SurfaceTextureListener listener) {
        textureListener = listener;
        
        // Create and prepare the surface texture
        SurfaceTexture texture = new SurfaceTexture(0);
        texture.setDefaultBufferSize(640, 480); // Default size
        
        if (listener != null) {
            listener.onSurfaceTextureReady(texture);
        }
        
        // Load image and start periodic update
        handler = new Handler(Looper.getMainLooper());
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                refreshImageTexture(context, texture);
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(refreshRunnable);
    }
    
    /**
     * Stop the image feed and release resources
     */
    public static void stop() {
        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
        handler = null;
        refreshRunnable = null;
        textureListener = null;
    }
    
    /**
     * Refresh the image texture with the current image
     */
    private static void refreshImageTexture(Context context, SurfaceTexture texture) {
        try {
            Bitmap bitmap = loadImageBitmap(context);
            if (bitmap != null) {
                // Here you would update the texture with the bitmap
                // This is a simplified example that would need actual OpenGL code
                // to render the bitmap to the texture
                Log.d(TAG, "Image texture refreshed");
                
                // Clean up
                bitmap.recycle();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing image texture: " + e.getMessage());
        }
    }
    
    /**
     * Load image bitmap from the configured source
     */
    private static Bitmap loadImageBitmap(Context context) {
        try {
            String imagePath = Settings.getImagePath();
            if (imagePath.isEmpty()) {
                return BitmapFactory.decodeResource(context.getResources(), 
                        context.getResources().getIdentifier("default_image", "drawable", context.getPackageName()));
            }
            
            InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(imagePath));
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                return bitmap;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading image bitmap: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Interface for surface texture readiness callback
     */
    public interface SurfaceTextureListener {
        void onSurfaceTextureReady(SurfaceTexture texture);
    }
}