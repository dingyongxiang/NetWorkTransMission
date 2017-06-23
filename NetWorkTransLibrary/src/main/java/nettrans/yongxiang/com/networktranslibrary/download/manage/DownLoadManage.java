package nettrans.yongxiang.com.networktranslibrary.download.manage;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import nettrans.yongxiang.com.networktranslibrary.download.ProgressDownSubscriber;
import nettrans.yongxiang.com.networktranslibrary.download.exception.HttpTimeException;
import nettrans.yongxiang.com.networktranslibrary.download.exception.RetryWhenNetworkException;
import nettrans.yongxiang.com.networktranslibrary.download.listener.DownloadInterceptor;
import nettrans.yongxiang.com.networktranslibrary.download.model.DataInfo;
import nettrans.yongxiang.com.networktranslibrary.download.service.NetService;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dingyongxiang on 2017/5/17.
 */

public class DownLoadManage {


    public static final String DM_TARGET_FOLDER = File.separator + "adyx_download" + File.separator; //下载管理目标文件夹

    private String mTargetFolder;                   //下载目录

    /*记录下载数据*/
    private Set<DataInfo> downLoadDataInfos;
    /*正在下载任务回调队列*/
    private HashMap<String, ProgressDownSubscriber> downLoadSubscriberMap;

    //定义下载状态常量
    public static final int START = 0;         //开始
    public static final int STOP = 1;          //停止
    public static final int DOWN = 2;          //下载
    public static final int PAUSE = 3;         //暂停
    public static final int FINISH = 4;        //完成
    public static final int ERROR = 5;         //错误


    private static final DownLoadManage downloadmanage = new DownLoadManage();

    private DownLoadManage() {
        downLoadDataInfos = new HashSet<>();
        downLoadSubscriberMap = new HashMap<>();
        mTargetFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + DM_TARGET_FOLDER;
    }

    public static DownLoadManage getInstance() {
        return downloadmanage;
    }

    public void addDownTask(final DataInfo dataInfo) {
        // dataInfo 为空或者正在下载直接返回
        if (dataInfo == null || downLoadSubscriberMap.get(dataInfo.getLoadurl()) != null) {
            return;
        }
        // 订阅事件及保存
        ProgressDownSubscriber subscriber = new ProgressDownSubscriber(dataInfo);
        downLoadSubscriberMap.put(dataInfo.getLoadurl(), subscriber);
        NetService netService;
        if (downLoadDataInfos.contains(dataInfo)) {
            netService = dataInfo.getService();
        } else {
            DownloadInterceptor interceptor = new DownloadInterceptor(subscriber);
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
            builder.connectTimeout(dataInfo.getDEFAULT_TIMEOUT(), TimeUnit.SECONDS);
            builder.addInterceptor(interceptor);

            Retrofit retrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(dataInfo.getBaseUrl())
                    .build();
            netService = retrofit.create(NetService.class);
            dataInfo.setService(netService);
            downLoadDataInfos.add(dataInfo);
        }

        netService.download("bytes=" + dataInfo.getReadLength() + "-", dataInfo.getLoadurl())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenNetworkException())
                .map(new Function<ResponseBody, DataInfo>() {
                    @Override
                    public DataInfo apply(@NonNull ResponseBody responseBody) throws Exception {
                        try {
                            writeCache(responseBody, new File(mTargetFolder + dataInfo.getFilename() + ".apk"), dataInfo);
                        } catch (IOException e) {
                            /*失败抛出异常*/
                            throw new HttpTimeException(e.getMessage());
                        }
                        return dataInfo;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribe(subscriber);

    }

    public void pushDownTask(DataInfo dataInfo) {
        if (dataInfo == null) return;
        dataInfo.setState(PAUSE);
        dataInfo.getListener().onPuase();
        if (downLoadSubscriberMap.containsKey(dataInfo.getLoadurl())) {
            ProgressDownSubscriber subscriber = downLoadSubscriberMap.get(dataInfo.getLoadurl());
            subscriber.dispose();
            downLoadSubscriberMap.remove(dataInfo.getLoadurl());
        }
        /*这里需要讲info信息写入到数据中，可自由扩展，用自己项目的数据库*/
    }


    /**
     * 写入文件
     *
     * @param file
     * @param info
     * @throws IOException
     */
    public void writeCache(ResponseBody responseBody, File file, DataInfo info) throws IOException {

        //Log.d("TAG", "filePath" + file.getParentFile().getAbsolutePath());

        if (!file.exists()) {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file.createNewFile();
        }
        long allLength;
        if (info.getCountLength() == 0) {
            allLength = responseBody.contentLength();
        } else {
            allLength = info.getCountLength();
        }
        FileChannel channelOut = null;
        RandomAccessFile randomAccessFile = null;
        randomAccessFile = new RandomAccessFile(file, "rwd");
        channelOut = randomAccessFile.getChannel();
        MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE,
                info.getReadLength(), allLength - info.getReadLength());
        byte[] buffer = new byte[1024 * 8];
        int len;
        int record = 0;
        while ((len = responseBody.byteStream().read(buffer)) != -1) {
            mappedBuffer.put(buffer, 0, len);
            record += len;
        }
        responseBody.byteStream().close();
        if (channelOut != null) {
            channelOut.close();
        }
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
    }

    public String getmTargetFolder() {
        return mTargetFolder;
    }

    public void setmTargetFolder(String mTargetFolder) {
        this.mTargetFolder = mTargetFolder;
    }
}
