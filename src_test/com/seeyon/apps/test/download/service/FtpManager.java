package com.seeyon.apps.test.download.service;


/**
 * @author sheHaiTao
 * @data 2021/11/11 - 17:55
 * FTP上传文件写入第三方数据库数据
 */
public interface FtpManager {

    /**
     * 初始化加载
     */
    public void init();

    /**
     * 公文归档对接
     */
    public void official();
}
