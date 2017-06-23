package com.dingyongxiang.newworktransmission;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import nettrans.yongxiang.com.networktranslibrary.download.listener.NetTransProgressOnNextListener;
import nettrans.yongxiang.com.networktranslibrary.download.manage.DownLoadManage;
import nettrans.yongxiang.com.networktranslibrary.download.model.DataInfo;

/**
 * Created by dingyongxiang on 2017/5/19.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyHolder> {

    public Activity mActivity;

    private List<DataInfo> mDownList;

    private DownLoadManage downLoadManage;

    public RecyclerViewAdapter(Activity mActivity, List<DataInfo> mDownList) {
        this.mActivity = mActivity;
        this.mDownList = mDownList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyHolder myHolder = new MyHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_recyclerviwe, parent, false));
        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        Glide.with(mActivity).load(mDownList.get(position).getIcon()).into(holder.mPic);
        holder.mName.setText(mDownList.get(position).getFilename());
        holder.setDataInfo(mDownList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDownList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mName;
        private TextView mState;
        private TextView mSize;
        private Button mDown;
        private Button mPush;
        private ImageView mPic;
        private DataInfo dataInfo;

        public MyHolder(View itemView) {
            super(itemView);
            downLoadManage = DownLoadManage.getInstance();
            mName = (TextView) itemView.findViewById(R.id.name);
            mState = (TextView) itemView.findViewById(R.id.state);
            mSize = (TextView) itemView.findViewById(R.id.size);
            mDown = (Button) itemView.findViewById(R.id.down);
            mPush = (Button) itemView.findViewById(R.id.push);
            mPic = (ImageView) itemView.findViewById(R.id.pic);
            mDown.setOnClickListener(this);
            mPush.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.down:
                    downLoadManage.addDownTask(dataInfo);
                    break;
                case R.id.push:
                    downLoadManage.pushDownTask(dataInfo);
                    break;
            }
        }

        public void setDataInfo(DataInfo dataInfo) {
            this.dataInfo = dataInfo;
            dataInfo.setListener(netProgressOnNextListener);
        }

        NetTransProgressOnNextListener netProgressOnNextListener = new NetTransProgressOnNextListener<DataInfo>() {
            @Override
            public void onNext(DataInfo o) {
                Toast.makeText(mActivity, o.getFilename(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStart() {
                mState.setText("开始下载");

            }

            @Override
            public void onComplete() {
                mState.setText("下载完成");
            }

            @Override
            public void updateProgress(long readLength, long countLength) {
                mState.setText("正在下载");
//            progressBar.setMax((int) countLength);
//            progressBar.setProgress((int) readLength);
                Log.i("GGGG", "countLength:" + countLength + "  readLength:" + readLength);
                float rate = ((float) readLength / countLength) * 100;
                int progress = (int) rate;
                mSize.setText(progress + "%");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mState.setText("下载失败:" + e.toString());
                Log.i("GGGG", "e.toString():" + e.toString());
            }

            @Override
            public void onPuase() {
                super.onPuase();
                mState.setText("暂停");
            }

            @Override
            public void onStop() {
                super.onStop();
                mState.setText("");
            }
        };

    }


}
