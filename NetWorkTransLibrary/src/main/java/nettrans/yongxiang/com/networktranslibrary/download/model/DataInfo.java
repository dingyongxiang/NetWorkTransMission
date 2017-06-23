package nettrans.yongxiang.com.networktranslibrary.download.model;

import nettrans.yongxiang.com.networktranslibrary.download.listener.NetTransProgressOnNextListener;
import nettrans.yongxiang.com.networktranslibrary.download.service.NetService;

/**
 * Created by dingyongxiang on 2017/5/17.
 */

public class DataInfo {

    private String loadurl;

    /*基础url*/
    private String baseUrl;

    private String filename;

    private String icon;

    /*文件总长度*/
    private long countLength;
    /*下载长度*/
    private long readLength;
    /*下载唯一的HttpService*/
    private NetService service;
    /*回调监听*/
    private NetTransProgressOnNextListener listener;
    /*超时设置*/
    private  int DEFAULT_TIMEOUT = 6;
    /*下载状态*/
    private int state;
    /*存储位置*/
    private String savePath;

    public String getLoadurl() {

        return loadurl;
    }

    public void setLoadurl(String loadurl) {

        this.loadurl = loadurl;

        setBaseUrl(getBasUrl(loadurl));

    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }



    public long getCountLength() {
        return countLength;
    }

    public void setCountLength(long countLength) {
        this.countLength = countLength;
    }

    public long getReadLength() {
        return readLength;
    }

    public void setReadLength(long readLength) {
        this.readLength = readLength;
    }

    public NetService getService() {
        return service;
    }

    public void setService(NetService service) {
        this.service = service;
    }

    public NetTransProgressOnNextListener getListener() {
        return listener;
    }

    public void setListener(NetTransProgressOnNextListener listener) {
        this.listener = listener;
    }

    public int getDEFAULT_TIMEOUT() {
        return DEFAULT_TIMEOUT;
    }

    public void setDEFAULT_TIMEOUT(int DEFAULT_TIMEOUT) {
        this.DEFAULT_TIMEOUT = DEFAULT_TIMEOUT;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * 读取baseurl
     * @param url
     * @return
     */
    protected String getBasUrl(String url) {
        String head = "";
        int index = url.indexOf("://");
        if (index != -1) {
            // http://
            head = url.substring(0, index + 3);
            url = url.substring(index + 3);
        }
        index = url.indexOf("/");
        if (index != -1) {
            url = url.substring(0, index + 1);
        }
        return head + url;
    }
}
