package com.beetech.module.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beetech.module.R;
import com.beetech.module.bean.ReadDataRealtime;
import com.beetech.module.constant.Constant;
import com.beetech.module.utils.DateUtils;

import java.util.List;

public class ReadDataRealtimeRvAdapter extends RecyclerView.Adapter<ReadDataRealtimeRvAdapter.ViewHolder> {

    List<ReadDataRealtime> mList;

    public ReadDataRealtimeRvAdapter(List<ReadDataRealtime> mList){
        this.mList = mList;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.read_data_realtime_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {
            ReadDataRealtime readDataRealtime = mList.get(position);

            Double temp = readDataRealtime.getTemp();
            double tempLower = readDataRealtime.getTempLower();
            double tempHight = readDataRealtime.getTempHight();
            Integer rssi = readDataRealtime.getRssi();
            Double ssVoltage = readDataRealtime.getSsVoltage();
            Integer wait2 = readDataRealtime.getWaitSentSize2();
            String sensorDataTime = DateUtils.parseDateToString(readDataRealtime.getSensorDataTime(), DateUtils.C_MM_DD_HH_MM);

            holder.tvSensorId.setText(readDataRealtime.getSensorId());
            holder.tvTemp.setText(new StringBuilder(temp.toString()).append("℃"));
            holder.tvRssi.setText(new StringBuilder(rssi.toString()).toString());
            holder.tvSsVoltage.setText(new StringBuilder(ssVoltage.toString()));
            holder.tvWait2.setText(new StringBuilder(wait2.toString()));
            holder.tvSensorDataTime.setText(new StringBuilder(sensorDataTime));

            boolean isAlarm = true;
            boolean isTempAlarm = false;

            if(tempHight != 0 && tempHight != 0 && (temp > tempHight || temp < tempLower)){
                isTempAlarm = true;
            }
            holder.tvTemp.setTextColor((isAlarm && isTempAlarm) ? Constant.COLOR_RED : Color.BLUE);

            boolean isSsVoltageAlarm = ssVoltage < 2.8;
            holder.tvSsVoltage.setTextColor((isAlarm && isSsVoltageAlarm) ? Constant.COLOR_RED : Color.BLACK);

            //判断是否设置了监听器
            if(mOnItemClickListener != null){
                //为ItemView设置监听器
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(v,position); // 2
                    }
                });
            }
            if(mOnItemLongClickListener != null){
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemLongClickListener.onItemLongClick(v, position);
                        //返回true 表示消耗了事件 事件不会继续传递
                        return true;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout ll;

        public TextView tvSensorId;
        public TextView tvTemp;
        public TextView tvSensorDataTime;
        public TextView tvRssi;
        public TextView tvSsVoltage;
        public TextView tvWait2;

        public ViewHolder(View convertView) {
            super(convertView);

            ll = (LinearLayout) convertView.findViewById(R.id.ll);

            tvSensorId = (TextView) convertView.findViewById(R.id.tvSensorId);
            tvTemp = (TextView) convertView.findViewById(R.id.tvTemp);
            tvSensorDataTime = (TextView) convertView.findViewById(R.id.tvSensorDataTime);
            tvRssi = (TextView) convertView.findViewById(R.id.tvRssi);
            tvSsVoltage = (TextView) convertView.findViewById(R.id.tvSsVoltage);
            tvWait2 = (TextView) convertView.findViewById(R.id.tvWait2);
        }
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }


    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view, int position);
    }
}
