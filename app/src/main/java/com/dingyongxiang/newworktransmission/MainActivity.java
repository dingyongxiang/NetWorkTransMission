package com.dingyongxiang.newworktransmission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nettrans.yongxiang.com.networktranslibrary.download.model.DataInfo;

public class MainActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private List<DataInfo> mDownList;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void initData() {
        mDownList = new ArrayList<DataInfo>();
        DataInfo dataInfo = new DataInfo();
        dataInfo.setFilename("虎牙");
        dataInfo.setIcon("http://hd.huya.com/download/img/dl3/star_fefd37e.png");
        dataInfo.setLoadurl("http://rel.huya.com/apk/live.apk");
        mDownList.add(dataInfo);

        DataInfo dataInfo1 = new DataInfo();
        dataInfo1.setFilename("斗鱼");
        dataInfo1.setIcon("https://staticlive.douyucdn.cn/upload/client_icons/8ec7ccb20faa2623cbf1cc2011aedc1a.PNG");
        dataInfo1.setLoadurl("https://staticlive.douyucdn.cn/upload/client/douyu_client_1_0v2_4_9_1.apk");
        mDownList.add(dataInfo1);

        DataInfo dataInfo2 = new DataInfo();
        dataInfo2.setFilename("王者荣耀 ");
        dataInfo2.setIcon("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/049aa54e4de6847b9026af8e29b25b1fe0c0d3b01");
        dataInfo2.setLoadurl("http://f5.market.xiaomi.com/download/AppStore/0646e848d98ca428a20b7a5451b1526c3c15b4e38/com.tencent.tmgp.sgame.apk");
        mDownList.add(dataInfo2);

        DataInfo dataInf3 = new DataInfo();
        dataInf3.setFilename("QQ");
        dataInf3.setIcon("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/072725ca573700292b92e636ec126f51ba4429a50");
        dataInf3.setLoadurl("http://f1.market.xiaomi.com/download/AppStore/0ff0604fd770f481927d1edfad35675a3568ba656/com.tencent.mobileqq.apk");
        mDownList.add(dataInf3);

        DataInfo dataInf4 = new DataInfo();
        dataInf4.setFilename("速度与激情8");
        dataInf4.setIcon("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/00d0605af3b3f4bbd2165d27ef7e73463eaeb055f");
        dataInf4.setLoadurl("http://f1.market.xiaomi.com/download/AppStore/0619515682b774b130dba2c09e60c79e59c2f4d16/com.hzfb.racing8.mi.apk");
        mDownList.add(dataInf4);

        DataInfo dataInf5 = new DataInfo();
        dataInf5.setFilename("天龙八部");
        dataInf5.setIcon("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/06fb89504180f4071132e983219887b056540f4db");
        dataInf5.setLoadurl("http://f3.market.xiaomi.com/download/AppStore/0634845f1b3db45892c1627f366608f1e355342f4/com.tencent.tmgp.tstl.apk");
        mDownList.add(dataInf5);
        adapter = new RecyclerViewAdapter(this, mDownList);
        mRecyclerView.setAdapter(adapter);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

}
