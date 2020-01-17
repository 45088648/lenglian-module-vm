package com.beetech.module.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beetech.module.R;
import com.beetech.module.code.response.ReadDataResponse;
import com.beetech.module.utils.DateUtils;

import java.util.List;

public class ReadDataRvAdapter extends RecyclerView.Adapter<ReadDataRvAdapter.ViewHolder> {

    List<ReadDataResponse> mList;

    public ReadDataRvAdapter(List<ReadDataResponse> data) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.read_data_list_item, parent, false);
        ReadDataRvAdapter.ViewHolder viewHolder = new ReadDataRvAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReadDataRvAdapter.ViewHolder holder, int position) {
        ReadDataResponse readData = mList.get(position);
        holder.tvId.setText(readData.get_id()+"");
        holder.tvSensorId.setText(readData.getSensorId());
        holder.tvSerialNo.setText(readData.getSerialNo()+"");
        holder.tvTemp.setText(readData.getTemp()+"");
        holder.tvRh.setText(readData.getRh()+"");
        holder.tvSsVoltage.setText(readData.getSsVoltage()+"");
        holder.tvRssi.setText(readData.getRssi()+"");
        holder.tvSensorDataTime.setText(DateUtils.parseDateToString(readData.getSensorDataTime(), DateUtils.C_YYMMDDHHMMSS));
        holder.tvSsTransfTime.setText(DateUtils.parseDateToString(readData.getSsTransfTime(), DateUtils.C_YYMMDDHHMMSS));
        holder.tvGwTime.setText(DateUtils.parseDateToString(readData.getGwTime(), DateUtils.C_YYMMDDHHMMSS));
        holder.tvWait1.setText(readData.getWaitSentSize1()+"");
        holder.tvWait2.setText(readData.getWaitSentSize2()+"");
        holder.tvErrorcode.setText(readData.getErrorcode()+"");
        holder.tvSendFlag.setText(readData.getSendFlag()+"");
        holder.tvResponseFlag.setText(readData.getResponseFlag()+"");
    }

     class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvId;
        public TextView tvSensorId;
        public TextView tvSerialNo;
        public TextView tvTemp;
        public TextView tvRh;
        public TextView tvSsVoltage;
        public TextView tvRssi;
        public TextView tvSensorDataTime;
        public TextView tvSsTransfTime;
        public TextView tvGwTime;
        public TextView tvSendFlag;
        public TextView tvWait1;
        public TextView tvWait2;
        public TextView tvErrorcode;
        public TextView tvResponseFlag;

        public ViewHolder(View convertView) {
            super(convertView);

            tvId = (TextView) convertView.findViewById(R.id.tvId);
            tvSerialNo = (TextView) convertView.findViewById(R.id.tvSerialNo);
            tvSensorId = (TextView) convertView.findViewById(R.id.tvSensorId);
            tvTemp = (TextView) convertView.findViewById(R.id.tvTemp);
            tvRh = (TextView) convertView.findViewById(R.id.tvRh);
            tvSsVoltage = (TextView) convertView.findViewById(R.id.tvSsVoltage);
            tvRssi = (TextView) convertView.findViewById(R.id.tvRssi);
            tvSensorDataTime = (TextView) convertView.findViewById(R.id.tvSensorDataTime);
            tvSsTransfTime = (TextView) convertView.findViewById(R.id.tvSsTransfTime);
            tvGwTime = (TextView) convertView.findViewById(R.id.tvGwTime);
            tvWait1 = (TextView) convertView.findViewById(R.id.tvWait1);
            tvWait2 = (TextView) convertView.findViewById(R.id.tvWait2);
            tvErrorcode = (TextView) convertView.findViewById(R.id.tvErrorcode);
            tvSendFlag = (TextView) convertView.findViewById(R.id.tvSendFlag);
            tvResponseFlag = (TextView) convertView.findViewById(R.id.tvResponseFlag);
        }
    }

}
