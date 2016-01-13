package com.lxf.processcircle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lxf.processcircle.view.MainProgressCircleView;
import com.lxf.processcircle.view.PullToRefreshListView;
import com.lxf.processcircle.view.UpLinearLayout;

import java.util.Arrays;
import java.util.LinkedList;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {
    private MainProgressCircleView mainCircle;
    private TextView mainNum;
    private UpLinearLayout mainUpLinearLayout;
    private PullToRefreshListView mPullListView;
    private LinkedList<String> mListItems;
    private ArrayAdapter<String> adapter;


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
        mainNum = (TextView) findViewById(R.id.mainNum);
        mainUpLinearLayout = (UpLinearLayout) findViewById(R.id.mainUpLinearLayout);
        mPullListView = (PullToRefreshListView) findViewById(R.id.mainListView);
    }

    private void initData() {
        mPullListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainCircle:
                mainUpLinearLayout.setTargetAndNowNum(12455, 6801, 3.4f, 264);
                break;
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            return mStrings;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            mPullListView.setLastUpdated("刚刚刷新了");
            mPullListView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPullListView.setLastUpdated(getString(R.string.pull_to_refresh_pull_label));
                    mListItems.addFirst("Added after refresh...");
                    adapter.notifyDataSetChanged();
                    // Call onRefreshComplete when the list has been refreshed.
                    mPullListView.onRefreshComplete();

                }
            }, 1000);
        }
    }

    private String[] mStrings = {
            "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam",
            "Abondance", "Ackawi", "Acorn", "Adelost", "Affidelice au Chablis",
            "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler"};

}
