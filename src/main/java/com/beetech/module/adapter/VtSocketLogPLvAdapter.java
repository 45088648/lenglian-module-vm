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
import com.beetech.module.bean.vt.VtSocketLog;
import com.beetech.module.constant.Constant;

public class VtSocketLogPLvAdapter extends PagedListAdapter<VtSocketLog, VtSocketLogPLvAdapter.ViewHolder> {

    PagedList<VtSocketLog> mPagedList;

    public VtSocketLogPLvAdapter(PagedList<VtSocketLog> mPagedList){
        super(new DiffCallback<VtSocketLog>() {

            @Override
            public boolean areItemsTheSame(@NonNull VtSocketLog oldItem, @NonNull VtSocketLog newItem) {
                Log.d("DiffCallback", "areItemsTheSame");
                return oldItem.get_id() == newItem.get_id();
            }

            @Override
            public boolean areContentsTheSame(@NonNull VtSocketLog oldItem, @NonNull VtSocketLog newItem) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vt_socket_log_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VtSocketLog vtSocketLog = mPagedList.get(position);
        holder.tvId.setText(vtSocketLog.get_id()+"");
        holder.tvText.setText(vtSocketLog.getText());
        holder.tvInputTime.setText(Constant.dateFormat3.format(vtSocketLog.getInputTime()));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvId;
        public TextView tvText;
        public TextView tvInputTime;

        public ViewHolder(View convertView) {
            super(convertView);

            tvId = (TextView) convertView.findViewById(R.id.tvId);
            tvText = (TextView) convertView.findViewById(R.id.tvText);
            tvInputTime = (TextView) convertView.findViewById(R.id.tvInputTime);
        }
    }

}
