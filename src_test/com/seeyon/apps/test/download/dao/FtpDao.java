package com.seeyon.apps.test.download.dao;

import java.util.List;
import java.util.Map;


/**
 * @author sheHaiTao
 * @data 2021/11/11 - 18:52
 * FTP上传文件写入第三方数据库数据
 */
public interface FtpDao {

    /**
     * 查找正文
     * @param edocId
     * @return
     */
    public Map findCtpContentAll(String edocId);

    /**
     * 查找附件
     * @param edocId
     * @return
     */
    public List findCtpAttAll(String edocId);

}
