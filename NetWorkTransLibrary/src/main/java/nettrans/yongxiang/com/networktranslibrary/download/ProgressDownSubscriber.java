package nettrans.yongxiang.com.networktranslibrary.download;


import java.lang.ref.WeakReference;

import io.reactivex.Flowable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import nettrans.yongxiang.com.networktranslibrary.download.listener.DownloadProgressListener;
import nettrans.yongxiang.com.networktranslibrary.download.listener.NetTransProgressOnNextListener;
import nettrans.yongxiang.com.networktranslibrary.download.manage.DownLoadManage;
import nettrans.yongxiang.com.networktranslibrary.download.model.DataInfo;

/**
 * Created by dingyongxiang on 2017/5/17.
 */

public class ProgressDownSubscriber<T> implements DownloadProgressListener, Observer<T> {

    //弱引用结果回调
    private WeakReference<NetTransProgressOnNextListener> mSubscriberOnNextListener;

    // 下载数据
    private DataInfo dataInfo;

    private Disposable disposable;


    public ProgressDownSubscriber(DataInfo dataInfo) {
        this.dataInfo = dataInfo;
        this.mSubscriberOnNextListener = new WeakReference<NetTransProgressOnNextListener>(dataInfo.getListener());
    }


    @Override
    public void update(long read, long count, boolean done) {
        if (dataInfo.getCountLength() > count) {
            read = dataInfo.getCountLength() - count + read;
        } else {
            dataInfo.setCountLength(count);
        }
        dataInfo.setReadLength(read);
        if (mSubscriberOnNextListener.get() != null) {
            Flowable.just(read).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            if (dataInfo.getState() == DownLoadManage.PAUSE || dataInfo.getState() == DownLoadManage.STOP)
                                return;
                            dataInfo.setState(DownLoadManage.DOWN);
                            mSubscriberOnNextListener.get().updateProgress(aLong, dataInfo.getCountLength());
                        }
                    });
        }
    }




    /*@Override
    protected void onStart() {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onStart();
        }
        dataInfo.setState(DownLoadManage.START);
    }*/


    @Override
    public void onSubscribe(@NonNull Disposable d) {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onStart();
        }
        dataInfo.setState(DownLoadManage.START);
        disposable = d;
    }

    @Override
    public void onNext(@NonNull T t) {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onNext(t);
        }
    }


    @Override
    public void onError(@NonNull Throwable e) {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onError(e);
        }
        dataInfo.setState(DownLoadManage.ERROR);
    }

    @Override
    public void onComplete() {
        if (mSubscriberOnNextListener.get() != null) {
            mSubscriberOnNextListener.get().onComplete();
        }
        dataInfo.setState(DownLoadManage.FINISH);
    }

    public void dispose(){
        if(disposable!=null){
            disposable.dispose();
        }
    }
}
