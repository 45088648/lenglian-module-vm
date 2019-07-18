package com.beetech.module.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.beetech.module.R;
import com.beetech.module.bean.ModuleBuf;
import com.beetech.module.constant.Constant;

import java.util.List;

public class ModuleBufRvAdapter extends RecyclerView.Adapter<ModuleBufRvAdapter.ViewHolder> {

    List<ModuleBuf> mList;

    public ModuleBufRvAdapter(List<ModuleBuf> data) {
        this.mList = data;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.module_buf_list_item, parent, false);
        ModuleBufRvAdapter.ViewHolder viewHolder = new ModuleBufRvAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ModuleBufRvAdapter.ViewHolder holder, int position) {
        ModuleBuf moduleBuf = mList.get(position);
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
