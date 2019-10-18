package com.beetech.module.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beetech.module.R;
import com.beetech.module.bean.GpsDataBean;
import com.beetech.module.utils.DateUtils;

import java.util.List;

public class GpsDataRvAdapter extends RecyclerView.Adapter<GpsDataRvAdapter.ViewHolder> {

    List<GpsDataBean> mList;

    public GpsDataRvAdapter(List<GpsDataBean> data) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gps_data_list_item, parent, false);
        GpsDataRvAdapter.ViewHolder viewHolder = new GpsDataRvAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GpsDataRvAdapter.ViewHolder holder, int position) {
        GpsDataBean readData = mList.get(position);
        holder.tvId.setText(readData.get_id()+"");
        int locType = readData.getLocType();
        String locTypeStr = locType+"";
        switch (locType){
            case 61:
                locTypeStr = "GPS";
                break;

            case 66:
                locTypeStr = "离线";
                break;

            case 161:
                locTypeStr = "网络";
                break;
            default:

        }
        holder.tvLocType.setText(locTypeStr);
        holder.tvLat.setText(readData.getLat()+"");
        holder.tvLng.setText(readData.getLng()+"");

        holder.tvAddress.setText(readData.getAddress());
        holder.tvDataTime.setText(DateUtils.parseDateToString(readData.getDataTime(), DateUtils.C_YYYY_MM_DD_HH_MM_SS));

        holder.tvSendFlag.setText(readData.getSendFlag() == 0 ? "否" : "是");
    }

     class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvId;
        public TextView tvLocType;
        public TextView tvLat;
        public TextView tvLng;
        public TextView tvAddress;
        public TextView tvDataTime;
        public TextView tvSendFlag;

        public ViewHolder(View convertView) {
            super(convertView);

            tvId = (TextView) convertView.findViewById(R.id.tvId);

            tvLocType = (TextView) convertView.findViewById(R.id.tvLocType);
            tvLat = (TextView) convertView.findViewById(R.id.tvLat);
            tvLng = (TextView) convertView.findViewById(R.id.tvLng);
            tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);

            tvDataTime = (TextView) convertView.findViewById(R.id.tvDataTime);

            tvSendFlag = (TextView) convertView.findViewById(R.id.tvSendFlag);
        }
    }

}
