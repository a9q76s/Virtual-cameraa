package com.example.virtualcameraa;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SettingsActivity extends AppCompatActivity {
    
    private static final int REQUEST_SELECT_IMAGE = 2001;
    private static final int REQUEST_SELECT_VIDEO = 2002;
    
    private RadioGroup sourceTypeGroup;
    private TextView currentImagePath;
    private TextView currentVideoPath;
    private Button selectImageButton;
    private Button selectVideoButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // تهيئة العناصر
        sourceTypeGroup = findViewById(R.id.source_type_group);
        currentImagePath = findViewById(R.id.current_image_path);
        currentVideoPath = findViewById(R.id.current_video_path);
        selectImageButton = findViewById(R.id.select_image_button);
        selectVideoButton = findViewById(R.id.select_video_button);
        
        // تحميل الإعدادات الحالية
        loadCurrentSettings();
        
        // تعيين معالجات الأحداث
        sourceTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_image) {
                Settings.setSourceType(this, FakeCameraProvider.SourceType.IMAGE);
            } else if (checkedId == R.id.radio_video) {
                Settings.setSourceType(this, FakeCameraProvider.SourceType.VIDEO);
            } else if (checkedId == R.id.radio_stream) {
                Settings.setSourceType(this, FakeCameraProvider.SourceType.STREAM);
            }
            
            updateUI();
        });
        
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_SELECT_IMAGE);
        });
        
        selectVideoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(intent, REQUEST_SELECT_VIDEO);
        });
        
        // زر الحفظ
        findViewById(R.id.save_button).setOnClickListener(v -> {
            // إعادة تشغيل خدمة الكاميرا
            stopService(new Intent(this, CameraService.class));
            startService(new Intent(this, CameraService.class));
            
            Toast.makeText(this, "تم حفظ الإعدادات", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
    
    private void loadCurrentSettings() {
        // تحميل نوع المصدر
        FakeCameraProvider.SourceType sourceType = Settings.getSourceType(this);
        
        switch (sourceType) {
            case IMAGE:
                sourceTypeGroup.check(R.id.radio_image);
                break;
            case VIDEO:
                sourceTypeGroup.check(R.id.radio_video);
                break;
            case STREAM:
                sourceTypeGroup.check(R.id.radio_stream);
                break;
        }
        
        // تحميل مسارات الملفات
        String imagePath = Settings.getDefaultImagePath(this);
        if (imagePath != null) {
            currentImagePath.setText(imagePath);
        } else {
            currentImagePath.setText("لم يتم تحديد صورة");
        }
        
        String videoPath = Settings.getDefaultVideoPath(this);
        if (videoPath != null) {
            currentVideoPath.setText(videoPath);
        } else {
            currentVideoPath.setText("لم يتم تحديد فيديو");
        }
        
        updateUI();
    }
    
    private void updateUI() {
        // تحديث واجهة المستخدم بناءً على نوع المصدر المحدد
        FakeCameraProvider.SourceType sourceType = Settings.getSourceType(this);
        
        switch (sourceType) {
            case IMAGE:
                currentImagePath.setVisibility(View.VISIBLE);
                selectImageButton.setVisibility(View.VISIBLE);
                currentVideoPath.setVisibility(View.GONE);
                selectVideoButton.setVisibility(View.GONE);
                break;
            case VIDEO:
                currentImagePath.setVisibility(View.GONE);
                selectImageButton.setVisibility(View.GONE);
                currentVideoPath.setVisibility(View.VISIBLE);
                selectVideoButton.setVisibility(View.VISIBLE);
                break;
            case STREAM:
                currentImagePath.setVisibility(View.GONE);
                selectImageButton.setVisibility(View.GONE);
                currentVideoPath.setVisibility(View.GONE);
                selectVideoButton.setVisibility(View.GONE);
                break;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                if (requestCode == REQUEST_SELECT_IMAGE) {
                    // حفظ الصورة المحددة
                    String imagePath = saveMediaFile(uri, "images", "image_");
                    if (imagePath != null) {
                        Settings.setDefaultImagePath(this, imagePath);
                        currentImagePath.setText(imagePath);
                    }
                } else if (requestCode == REQUEST_SELECT_VIDEO) {
                    // حفظ الفيديو المحدد
                    String videoPath = saveMediaFile(uri, "videos", "video_");
                    if (videoPath != null) {
                        Settings.setDefaultVideoPath(this, videoPath);
                        currentVideoPath.setText(videoPath);
                    }
                }
            }
        }
    }
    
    // حفظ الصور والفيديوهات في ملفات داخلية
    private String saveMediaFile(Uri uri, String subfolder, String prefix) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;
            
            File mediaDir = new File(getFilesDir(), "media/" + subfolder);
            if (!mediaDir.exists()) {
                mediaDir.mkdirs();
            }
            
            File outputFile = new File(mediaDir, prefix + System.currentTimeMillis());
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            
            inputStream.close();
            outputStream.close();
            
            return outputFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "فشل في حفظ الملف: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
