package com.example.virtualcameraa.utils;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;

import com.example.virtualcameraa.Settings;

import java.io.IOException;

/**
 * Utility class for handling video for virtual camera feed
 */
public class VideoUtils {
    private static final String TAG = "VideoUtils";
    private static MediaPlayer mediaPlayer;
    private static SurfaceTexture surfaceTexture;
    private static ImageUtils.SurfaceTextureListener textureListener;
    
    /**
     * Start video feed for the virtual camera
     * @param context Context to access resources
     * @param listener Callback for when the surface texture is ready
     */
    public static void startVideoFeed(Context context, ImageUtils.SurfaceTextureListener listener) {
        textureListener = listener;
        
        // Create surface texture
        surfaceTexture = new SurfaceTexture(0);
        surfaceTexture.setDefaultBufferSize(640, 480);
        
        if (textureListener != null) {
            textureListener.onSurfaceTextureReady(surfaceTexture);
        }
        
        try {
            // Create media player and set up video
            mediaPlayer = new MediaPlayer();
            String videoPath = Settings.getVideoPath();
            
            if (!videoPath.isEmpty()) {
                mediaPlayer.setDataSource(context, Uri.parse(videoPath));
                
                // Set loop playback
                mediaPlayer.setLooping(true);
                
                // Set surface
                Surface surface = new Surface(surfaceTexture);
                mediaPlayer.setSurface(surface);
                
                // Prepare and start playback
                mediaPlayer.prepare();
                mediaPlayer.start();
                
                Log.d(TAG, "Video feed started successfully");
            } else {
                Log.e(TAG, "No video path specified");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error starting video feed: " + e.getMessage());
        }
    }
    
    /**
     * Start stream feed for the virtual camera
     * @param context Context to access resources
     * @param streamUrl URL of the streaming feed
     * @param listener Callback for when the surface texture is ready
     */
    public static void startStreamFeed(Context context, String streamUrl, ImageUtils.SurfaceTextureListener listener) {
        textureListener = listener;
        
        // Create surface texture
        surfaceTexture = new SurfaceTexture(0);
        surfaceTexture.setDefaultBufferSize(640, 480);
        
        if (textureListener != null) {
            textureListener.onSurfaceTextureReady(surfaceTexture);
        }
        
        if (streamUrl == null || streamUrl.isEmpty()) {
            Log.e(TAG, "Stream URL is empty");
            return;
        }
        
        try {
            // Create media player and set up stream
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(streamUrl);
            
            // Set surface
            Surface surface = new Surface(surfaceTexture);
            mediaPlayer.setSurface(surface);
            
            // Set error listener
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                return false;
            });
            
            // Prepare and start playback
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                Log.d(TAG, "Stream feed started successfully");
            });
        } catch (IOException e) {
            Log.e(TAG, "Error starting stream feed: " + e.getMessage());
        }
    }
    
    /**
     * Stop the video/stream feed and release resources
     */
    public static void stop() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, "Error stopping media player: " + e.getMessage());
            }
            mediaPlayer = null;
        }
        
        if (surfaceTexture != null) {
            surfaceTexture.release();
            surfaceTexture = null;
        }
        
        textureListener = null;
        Log.d(TAG, "Video utils resources released");
    }
}