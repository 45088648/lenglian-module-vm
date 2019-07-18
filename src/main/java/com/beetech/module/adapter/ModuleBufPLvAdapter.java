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
import com.beetech.module.bean.ModuleBuf;
import com.beetech.module.constant.Constant;

import java.text.SimpleDateFormat;

public class ModuleBufPLvAdapter extends PagedListAdapter<ModuleBuf, ModuleBufPLvAdapter.ViewHolder> {

    PagedList<ModuleBuf> mPagedList;

    public ModuleBufPLvAdapter(PagedList<ModuleBuf> mPagedList){
        super(new DiffCallback<ModuleBuf>() {

            @Override
            public boolean areItemsTheSame(@NonNull ModuleBuf oldItem, @NonNull ModuleBuf newItem) {
                Log.d("DiffCallback", "areItemsTheSame");
                return oldItem.get_id() == newItem.get_id();
            }

            @Override
            public boolean areContentsTheSame(@NonNull ModuleBuf oldItem, @NonNull ModuleBuf newItem) {
                Log.d("DiffCallback", "areContentsTheSame");
                return oldItem.get_id() == newItem.get_id();
            }
        });
        this.mPagedList = mPagedList;
    }

    private class DataBean {
        public int id;
        public String content;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.module_buf_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ModuleBuf moduleBuf = mPagedList.get(position);
        holder.tvId.setText(moduleBuf.get_id()+"");
        holder.tvType.setText(moduleBuf.getType() == 0 ? "W" : "R");
        holder.tvCmd.setText(moduleBuf.getCmd()+"");
        holder.tvBuf.setText(moduleBuf.getBufHex());
        holder.tvResult.setText(moduleBuf.isResult() ? "1" : "0");
        holder.tvInputTime.setText(Constant.dateFormat4.format(moduleBuf.getInputTime()));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvId;
        public TextView tvType;
        public TextView tvCmd;
        public TextView tvBuf;
        public TextView tvResult;
        public TextView tvInputTime;

        public ViewHolder(View convertView) {
            super(convertView);

            tvId = (TextView) convertView.findViewById(R.id.tvId);
            tvType = (TextView) convertView.findViewById(R.id.tvType);
            tvCmd = (TextView) convertView.findViewById(R.id.tvCmd);
            tvBuf = (TextView) convertView.findViewById(R.id.tvBuf);
            tvResult = (TextView) convertView.findViewById(R.id.tvResult);
            tvInputTime = (TextView) convertView.findViewById(R.id.tvInputTime);
        }
    }

}
