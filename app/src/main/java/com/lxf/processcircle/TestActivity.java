package com.lxf.processcircle;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.lxf.processcircle.view.MainProgressCircleView;
import com.lxf.processcircle.view.ProgressCircleView;
import com.lxf.processcircle.view.UpLinearLayout;

public class TestActivity extends AppCompatActivity implements View.OnClickListener{
    private MainProgressCircleView mainCircle;
    private TextView mainNum;
    private UpLinearLayout mainUpLinearLayout;

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
    }

    private void initView() {
        mainCircle = (MainProgressCircleView) findViewById(R.id.mainCircle);
        mainCircle.setOnClickListener(this);
        mainNum = (TextView) findViewById(R.id.mainNum);
        mainUpLinearLayout = (UpLinearLayout) findViewById(R.id.mainUpLinearLayout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainCircle:
                mainUpLinearLayout.setTargetAndNowNum(12455, 6801, 3.4f, 264);
                break;
        }
    }
}
