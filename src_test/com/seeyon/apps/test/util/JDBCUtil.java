package com.seeyon.apps.test.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

//import com.seeyon.apps.work2.manager.FtpManager;
import org.apache.commons.logging.Log;

import com.seeyon.ctp.common.log.CtpLogFactory;

/**
 * @author 
 * @version 1.0.0
 * @Description TODO  第三方数据库配置
 */
public class JDBCUtil {
   private static final Log log = CtpLogFactory.getLog(JDBCUtil.class);
   

    public void insert(String sql) {
        //连接第三方数据库
        String URL = "jdbc:mysql://localhost:3306/a8?serverTimezone=UTC&characterEncoding=utf-8&useSSL=false";
        String USER = "root";
        String PASSWORD = "root2020";

        Connection conn = null;
        Statement st = null;
        try {
            //1.加载驱动程序
            Class.forName("com.mysql.jdbc.Driver");
            //2.获得数据库链接
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            //3.通过数据库的连接操作数据库，实现增删改查（使用Statement类）
            st = conn.createStatement();
            st.execute(sql);
        } catch (Exception e) {
          log.error("数据库连接、操作错误", e);
        } finally {
            try {
                assert st != null;
                st.close();
                conn.close();
            } catch (Exception e) {
               log.error("关闭资源错误", e);
            }
        }
    }
}
