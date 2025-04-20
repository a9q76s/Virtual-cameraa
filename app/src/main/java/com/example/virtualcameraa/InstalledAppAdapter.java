package com.example.virtualcameraa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying installed apps in a RecyclerView
 */
public class InstalledAppAdapter extends RecyclerView.Adapter<InstalledAppAdapter.AppViewHolder> {
    
    private List<AppManager.AppInfo> appList;
    private Context context;
    private AppClickListener listener;
    
    public InstalledAppAdapter(Context context, List<AppManager.AppInfo> appList) {
        this.context = context;
        this.appList = appList;
    }
    
    public void setAppClickListener(AppClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_installed_app, parent, false);
        return new AppViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppManager.AppInfo app = appList.get(position);
        
        holder.appName.setText(app.getName());
        holder.packageName.setText(app.getPackageName());
        holder.appIcon.setImageDrawable(app.getIcon());
        
        holder.btnInstall.setOnClickListener(v -> {
            if (listener != null) {
                listener.onInstallClick(app);
            }
        });
        
        holder.btnLaunch.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLaunchClick(app);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return appList.size();
    }
    
    public void updateData(List<AppManager.AppInfo> newAppList) {
        this.appList = newAppList;
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder for app items
     */
    public static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView packageName;
        Button btnInstall;
        Button btnLaunch;
        
        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            packageName = itemView.findViewById(R.id.package_name);
            btnInstall = itemView.findViewById(R.id.btn_install);
            btnLaunch = itemView.findViewById(R.id.btn_launch);
        }
    }
    
    /**
     * Interface for app click events
     */
    public interface AppClickListener {
        void onInstallClick(AppManager.AppInfo app);
        void onLaunchClick(AppManager.AppInfo app);
    }
}