package com.seeyon.apps.test.sso;

import com.seeyon.ctp.portal.sso.SSOLoginHandshakeAbstract;
import com.seeyon.ctp.util.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 *
 * @author sheHaiTao
 * @Description 用于单点登陆的握手类
 * @createTime 2021/10/27 19:30:36
 */
public class SsoLogin extends SSOLoginHandshakeAbstract {

    private static final Log log = LogFactory.getLog(SsoLogin.class);

    @Override
    public String handshake(String token) {
        if(token == null || token.equals("")) {
            log.error("单点登录token为空， 登录时间：" + (new Date()));
            return null;
        }else {
            String loginName = null;
            try {
                loginName = Base64.decode2String(token);
            } catch (UnsupportedEncodingException e) {
                log.error("单点登录错误:" , e);
            }
            return loginName;
        }
    }


    @Override
    public void logoutNotify(String arg0) {
    //无需注销通知
    }
}
