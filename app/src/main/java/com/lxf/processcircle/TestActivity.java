package com.lxf.processcircle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.lxf.processcircle.view.MainProgressCircleView;
import com.lxf.processcircle.view.UpLinearLayout;
import com.lxf.processcircle.view.XListView;

import java.util.Arrays;
import java.util.LinkedList;

public class TestActivity extends AppCompatActivity implements View.OnClickListener, XListView.IXListViewListener {
    private MainProgressCircleView mainCircle;
    private UpLinearLayout mainUpLinearLayout;
    private XListView mPullListView;
    private LinkedList<String> mListItems;
    private ArrayAdapter<String> adapter;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initView();
        initData();
    }

    private void initView() {
        mainCircle = (MainProgressCircleView) findViewById(R.id.mainCircle);
        mainCircle.setOnClickListener(this);
        mainUpLinearLayout = (UpLinearLayout) findViewById(R.id.mainUpLinearLayout);
        mPullListView = (XListView) findViewById(R.id.mainListView);
    }

    private void initData() {
        mPullListView.setPullLoadEnable(false);
        mPullListView.setXListViewListener(this);

        mListItems = new LinkedList<String>();
        mListItems.addAll(Arrays.asList(mStrings));

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mListItems);

        mPullListView.setAdapter(adapter);
        mPullListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(TestActivity.this, position + "", Toast.LENGTH_SHORT).show();
            }
        });
        mHandler = new Handler();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainCircle:
                mainUpLinearLayout.setTargetAndNowNum(12455, 6801, 3.4f, 264);
                break;
        }
    }

    private void onLoad() {
        adapter.insert("refresh", 0);
        adapter.notifyDataSetChanged();
        mPullListView.stopRefresh("加载成功", true);
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // mAdapter.notifyDataSetChanged();
                onLoad();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoad();
            }
        }, 2000);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mPullListView.startRefresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private String[] mStrings = {
            "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam",
            "Abondance", "Ackawi", "Acorn", "Adelost", "Affidelice au Chablis",
            "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler"};

}
