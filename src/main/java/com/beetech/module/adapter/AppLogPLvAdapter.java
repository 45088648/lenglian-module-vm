package com.beetech.module.adapter;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.DiffCallback;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.beetech.module.R;
import com.beetech.module.bean.AppLog;
import com.beetech.module.constant.Constant;

public class AppLogPLvAdapter extends PagedListAdapter<AppLog, AppLogPLvAdapter.ViewHolder> {

    PagedList<AppLog> mPagedList;

    public AppLogPLvAdapter(PagedList<AppLog> mPagedList){
        super(new DiffCallback<AppLog>() {

            @Override
            public boolean areItemsTheSame(@NonNull AppLog oldItem, @NonNull AppLog newItem) {
                Log.d("DiffCallback", "areItemsTheSame");
                return oldItem.get_id() == newItem.get_id();
            }

            @Override
            public boolean areContentsTheSame(@NonNull AppLog oldItem, @NonNull AppLog newItem) {
                Log.d("DiffCallback", "areContentsTheSame");
                return oldItem.get_id() == newItem.get_id();
            }
        });
        this.mPagedList = mPagedList;
    }

    @Override
    public int getItemCount() {
        return mPagedList.size();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_log_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppLog appLog = mPagedList.get(position);
        holder.tvId.setText(appLog.get_id()+"");
        holder.tvContent.setText(appLog.getContent());
        holder.tvInputTime.setText(Constant.dateFormat3.format(appLog.getInputTime()));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvId;
        public TextView tvContent;
        public TextView tvInputTime;

        public ViewHolder(View convertView) {
            super(convertView);

            tvId = (TextView) convertView.findViewById(R.id.tvId);
            tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            tvInputTime = (TextView) convertView.findViewById(R.id.tvInputTime);
        }
    }

}
