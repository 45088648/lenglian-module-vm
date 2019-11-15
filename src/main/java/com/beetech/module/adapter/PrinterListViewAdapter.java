package com.beetech.module.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beetech.module.R;
import com.beetech.module.activity.QueryDataAllActivity;

import java.util.List;

/**
 * 打印机数据适配器
 */
public class PrinterListViewAdapter extends BaseAdapter {
    private String TAG = PrinterListViewAdapter.class.getSimpleName();

    private Context context;
    private List<String> dataList;
    private QueryDataAllActivity queryDataAllActivity;
 
    public PrinterListViewAdapter(QueryDataAllActivity queryDataAllActivity, List<String> dataList) {
        this.context = queryDataAllActivity;
        this.queryDataAllActivity = queryDataAllActivity;
        this.dataList = dataList;
    }
 
    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }
 
    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //判断是否有缓存
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.printer_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //得到缓存的布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String printer = dataList.get(position);
        //设置图片
        viewHolder.pictureImg.setImageResource(R.mipmap.printer);
        //设置内容
        viewHolder.printTv.setText(printer);

        viewHolder.connectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryDataAllActivity.showLoading();
                queryDataAllActivity.getBlueToothService().print(printer);
            }
        });
 
        return convertView;
    }

    private final class ViewHolder {
 
        ImageView pictureImg;//图片
        TextView printTv;//打印机地址
        Button connectBt;//打印

        /**
         * 构造器
         *
         * @param view 视图组件（ListView的子项视图）
         */
        ViewHolder(View view) {
            pictureImg = (ImageView) view.findViewById(R.id.picture_img);
            printTv = (TextView) view.findViewById(R.id.print_tv);
            connectBt = (Button) view.findViewById(R.id.connect_bt);
        }
    }
}