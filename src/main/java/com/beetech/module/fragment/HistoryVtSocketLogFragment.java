package com.beetech.module.fragment;

import android.arch.paging.PagedList;
import android.arch.paging.TiledDataSource;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.beetech.module.R;
import com.beetech.module.activity.MainActivity;
import com.beetech.module.adapter.VtSocketLogPLvAdapter;
import com.beetech.module.application.MyApplication;
import com.beetech.module.bean.ModuleBuf;
import com.beetech.module.bean.vt.VtSocketLog;
import com.beetech.module.dao.VtSocketLogSDDao;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class HistoryVtSocketLogFragment extends Fragment {

    private static final String TAG = HistoryVtSocketLogFragment.class.getSimpleName();
    private MyApplication myApp;
    private PagedList<VtSocketLog> mPagedList;
    private MyDataSource mDataSource;

    private class MyDataSource extends TiledDataSource<VtSocketLog> {

        @Override
        public int countItems() {
            return TiledDataSource.COUNT_UNDEFINED;
        }

        /**
         * 注意，这里需要后台线程化。
         *
         * @param startPosition
         * @param count
         * @return
         */
        @Override
        public List<VtSocketLog> loadRange(int startPosition, int count) {
            Log.d("MyDataSource", "loadRange:" + startPosition + "," + count);
            List<VtSocketLog> list = loadData(startPosition, count);
            return list;
        }


    }

    /**
     * 假设这里需要做一些后台线程的数据加载任务。
     *
     * @param startPosition
     * @param count
     * @return
     */
    private List<VtSocketLog> loadData(int startPosition, int count) {
        List<VtSocketLog> dataList = new ArrayList();

        Message msg = new Message();
        msg.obj = "startPosition="+startPosition+", count = "+count;
        handlerToast.sendMessage(msg);

        dataList = myApp.vtSocketLogSDDao.queryAll(count, startPosition);
        return dataList;
    }
    PagedList.Config mPagedListConfig;
    private void makePageList() {
        mPagedListConfig = new PagedList.Config.Builder()
                .setPageSize(pageSize) //分页数据的数量。在后面的DataSource之loadRange中，count即为每次加载的这个设定值。
                .setPrefetchDistance(pageSize) //初始化时候，预取数据数量。
                .setEnablePlaceholders(false)
                .build();

        mPagedList = new PagedList.Builder()
                .setConfig(mPagedListConfig)
                .setDataSource(mDataSource)
                .setMainThreadExecutor(new BackgroundThreadTask()) //初始化阶段启用
                .setBackgroundThreadExecutor(new MainThreadTask()) //初始化阶段启动
                .build();

    }

    private class BackgroundThreadTask implements Executor {
        public BackgroundThreadTask() {
            this.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d("BackgroundThreadTask", "run");
                }
            });
        }

        @Override
        public void execute(@NonNull Runnable runnable) {
            runnable.run();
        }
    }

    private class MainThreadTask implements Executor {
        public MainThreadTask() {
            this.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d("MainThreadTask", "run");
                }
            });
        }

        @Override
        public void execute(@NonNull Runnable runnable) {
            runnable.run();
        }
    }

    private MainActivity mContext;

    private VtSocketLogSDDao dao;
    // 列表数据
    private List<ModuleBuf> dataList = new ArrayList<ModuleBuf>();

    // 每一页显示的行数
    public int pageSize = 100;

    private VtSocketLogPLvAdapter rvAdapter;

    @ViewInject(R.id.rvVtSocketLogData)
    RecyclerView rvVtSocketLogData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.history_vt_socket_log_fragment,  container, false);
        ViewUtils.inject(this, v);
        return v;
    }
    private Handler handler = new Handler(){};

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            initData();
            handler.postDelayed(this, 1000*60*10);
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
        myApp = (MyApplication) mContext.getApplicationContext();
        registerForContextMenu(rvVtSocketLogData);
    }
    private LinearLayoutManager mLayoutManager;
    private void initData() {

        mDataSource = new MyDataSource();
        makePageList();

        mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayout.VERTICAL);
        rvVtSocketLogData.setLayoutManager(mLayoutManager);
        rvAdapter = new VtSocketLogPLvAdapter(mPagedList);
        rvVtSocketLogData.setAdapter(rvAdapter);

        rvVtSocketLogData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastPos;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                lastPos = mLayoutManager.findLastVisibleItemPosition();

                mPagedList.loadAround(lastPos);//触发Android Paging的加载事务逻辑。

                Message msg = new Message();
                msg.obj = "lastPos="+lastPos;
                handlerToast.sendMessage(msg);
            }
        });
    }

    private Handler handlerToast = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Object toastMsg = msg.obj;
            if(toastMsg != null){
//                Toast.makeText(mContext, toastMsg.toString(), Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }
}
