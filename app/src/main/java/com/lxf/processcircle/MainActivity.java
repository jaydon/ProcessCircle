package com.lxf.processcircle;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.lxf.processcircle.view.MainProgressCircleView;
import com.lxf.processcircle.view.ProgressCircleView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ProgressCircleView circle;
    private MainProgressCircleView mainCircle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        circle = (ProgressCircleView) findViewById(R.id.circle);
        circle.setOnClickListener(this);
        mainCircle = (MainProgressCircleView) findViewById(R.id.mainCircle);
        mainCircle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circle:
                circle.setProgress(80);
                break;
            case R.id.mainCircle:
                break;
        }
    }
}
