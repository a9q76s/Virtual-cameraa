package com.example.virtualcameraa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {
    
    private List<AppManager.AppInfo> appList;
    private OnAppClickListener listener;
    
    // واجهة للتعامل مع أحداث النقر
    public interface OnAppClickListener {
        void onAppClick(AppManager.AppInfo app);
    }
    
    public AppAdapter(List<AppManager.AppInfo> appList, OnAppClickListener listener) {
        this.appList = appList;
        this.listener = listener;
    }
    
    public void updateAppList(List<AppManager.AppInfo> newList) {
        this.appList = newList;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_installed_app, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppManager.AppInfo app = appList.get(position);
        
        holder.appName.setText(app.appName);
        holder.packageName.setText(app.packageName);
        holder.appIcon.setImageDrawable(app.icon);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAppClick(app);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return appList != null ? appList.size() : 0;
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView packageName;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            packageName = itemView.findViewById(R.id.package_name);
        }
    }
}
