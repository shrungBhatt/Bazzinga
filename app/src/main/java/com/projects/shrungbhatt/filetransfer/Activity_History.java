package com.projects.shrungbhatt.filetransfer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.projects.shrungbhatt.filetransfer.adpaters.Adapter_HistoryViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Activity_History extends AppCompatActivity {


    @BindView(R.id.history_tab_layout)
    TabLayout mHistoryTabLayout;
    @BindView(R.id.history_view_pager)
    ViewPager mHistoryViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        mHistoryTabLayout.addTab(mHistoryTabLayout.newTab().setText("Sent"));
        mHistoryTabLayout.addTab(mHistoryTabLayout.newTab().setText("Received"));




        Adapter_HistoryViewPager adapterHistoryViewPager = new
                Adapter_HistoryViewPager(getSupportFragmentManager(),mHistoryTabLayout.getTabCount());


        mHistoryViewPager.setAdapter(adapterHistoryViewPager);

        mHistoryTabLayout.setupWithViewPager(mHistoryViewPager);

        mHistoryTabLayout.getTabAt(0).setText("Sent");
        mHistoryTabLayout.getTabAt(1).setText("Received");



    }
}
