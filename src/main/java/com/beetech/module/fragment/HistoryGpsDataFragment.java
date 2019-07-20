package com.beetech.module.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.beetech.module.R;
import com.beetech.module.activity.MainActivity;
import com.beetech.module.adapter.GpsDataRvAdapter;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.GpsDataBean;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import java.util.ArrayList;
import java.util.List;

public class HistoryGpsDataFragment extends Fragment {

    private static final String TAG = HistoryGpsDataFragment.class.getSimpleName();
    private MyApplication myApp;
    private MainActivity mContext;

    // 列表数据
    private List<GpsDataBean> readDataList = new ArrayList<GpsDataBean>();

    // 每一页显示的行数
    public int pageSize = 100;
    // 当前页数
    public int pageNum = 1;

    private GpsDataRvAdapter rvAdapter;

    @ViewInject(R.id.rvData)
    RecyclerView rvData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.history_gps_data_fragment,  container, false);
        ViewUtils.inject(this, v);
        return v;
    }
    private Handler handler = new Handler(){};

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            initData();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (MainActivity) getActivity();
        init();
//        handler.postDelayed(task, 1000*60*5);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void init() {
        myApp = (MyApplication) getContext().getApplicationContext();
        registerForContextMenu(rvData);
    }

    private void initData() {
        readDataList = myApp.gpsDataSDDao.queryAll(pageSize, 0);

        rvData.setLayoutManager(new GridLayoutManager(mContext, 1));
        rvAdapter = new GpsDataRvAdapter(readDataList);
        rvData.setAdapter(rvAdapter);
        rvAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }
}
