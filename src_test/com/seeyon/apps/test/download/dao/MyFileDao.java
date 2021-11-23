package com.seeyon.apps.test.download.dao;

import com.seeyon.ctp.common.exceptions.BusinessException;

import java.util.List;

/**
 * @author sheHaiTao
 * @data 2021/11/10 - 14:21
 * MyFile类
 */
public interface MyFileDao {
    /**
     * 获取文件
     * @param id
     * @return File文件
     * @throws BusinessException
     */
    List getFile(Long id) throws BusinessException;

    /**
     * 获取文件类型
     * @param id
     * @return 查询的结果集
     * @throws BusinessException
     */
    String getFileType(Long id) throws  BusinessException;
}
