package com.seeyon.apps.test.download.service.impl;

import com.seeyon.apps.test.download.bo.OpinionBO;
import com.seeyon.apps.test.download.controller.OpinionController;
import com.seeyon.apps.test.download.dao.EdocOpinionDao;
import com.seeyon.apps.test.download.service.EdocOpinionManager;
import com.seeyon.apps.test.download.service.OpinionListManager;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.log.CtpLogFactory;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class OpinionListManagerImpl implements OpinionListManager {

    //日志
    private static final Log log = CtpLogFactory.getLog(OpinionController.class);

    //公文意见查询/修改
    @Autowired
    private EdocOpinionManager edocOpinionManager;
    //公文意见查询/修改
    @Autowired
    private EdocOpinionDao edocOpinionDao;

    @Override
    @AjaxAccess
    public FlipInfo opinionList(FlipInfo fi, Map pas) {
        long startTime = System.currentTimeMillis();
        log.info("开始查询意见------start-----"+ new Date().getTime());
        List<OpinionBO> queryList = new ArrayList<>();
        Long userId = AppContext.currentUserId();
        pas.put("userId", userId);
        try {
            //获得默认密级
            //查询自己处理过的意见
            long time = System.currentTimeMillis();
            Map<String, Object> pa = this.packageSql(pas, userId);
            StringBuffer sql = (StringBuffer) pa.get("sql");
            Map<String, Object> p = (Map<String, Object>) pa.get("p");
            //根据userId 查询edocOpinionList
            long currentTimeMillis = System.currentTimeMillis();
            log.info("0001--开始使用公文id查询意见---" + currentTimeMillis);
            List<Map<String, Object>> opinions = edocOpinionManager.findOpinionList(sql.toString(), p, fi);
            Long ss = System.currentTimeMillis() - currentTimeMillis;
            log.info("0002--开始使用公文id查询意见--所用耗时---" + ss);
            for (Map<String, Object> map : opinions) {
                OpinionBO opinionBO = new OpinionBO();
                //文号
                if (map.get("doc_mark")==null){
                    opinionBO.setDocMark("");
                }else {
                    opinionBO.setDocMark(String.valueOf(map.get("doc_mark")));
                }
                //文档名
                if (map.get("subject")==null){
                    opinionBO.setSubject("");
                }else {
                    opinionBO.setSubject(String.valueOf(map.get("doc_mark")));
                }
                //创建人
                if (map.get("create_person")==null){
                    opinionBO.setCreatePerson("");
                }else {
                    opinionBO.setCreatePerson(String.valueOf(map.get("create_person")));
                }
                //意见审批人
                if (map.get("name")==null){
                    opinionBO.setCheck("");
                }else {
                    opinionBO.setCheck(String.valueOf(map.get("name")));
                }
                //意见
                if (map.get("content")==null){
                    opinionBO.setOpinion("");
                }else {
                    opinionBO.setOpinion(String.valueOf(map.get("content")));
                }

                //当前登录人id
                opinionBO.setId(Long.valueOf(String.valueOf(map.get("id"))));
                //意见类型
                //opinionBO.setOpinionType(Integer.valueOf(String.valueOf(map.get("attribute"))));
                if (map.get("create_time") == null){
                    opinionBO.setCreateTime(new Date());
                }else {
                    opinionBO.setCreateTime((Date) (map.get("create_time")));
                }
                queryList.add(opinionBO);
            }
            Long xx = System.currentTimeMillis() - startTime;
            log.info("00--结束查询意见---" + xx);
            if (queryList != null) {
                fi.setData(queryList);
            }
            //封装数据，向前台传值
        } catch (Exception e) {
            log.error("意见列表出现异常:", e);
        }
        return fi;
    }

    @Override
    public void updateOpinionData(Long opinionId, String opinionContent) {
        edocOpinionDao.update(opinionId, opinionContent);
    }

    public Map<String, Object> packageSql(Map<String, Object> map, Long userId) {
        StringBuffer sql = new StringBuffer();
        Map<String, Object> p = new HashMap();
        Map<String, Object> pa = new HashMap();
        List<Long> longList = new ArrayList();
        sql.append("a.CREATE_ID = "+ String.valueOf(userId));
        //p.put("userId", userId);
        if (map.get("attribute") != null && !"".equals(map.get("attribute"))) {
            Integer type = Integer.valueOf((String) map.get("attribute"));
            if (sql.length() > 0)
                sql.append(" and");
            sql.append(" attribute=:attribute");
            p.put("attribute", type);
        }

        if (map.get("subject") != null) {
            if (sql.length() > 0)
                sql.append(" and");
            sql.append(" c.subject like :subject");
            p.put("subject", "%" + map.get("subject") + "%");
        }
        if (map.get("datetime") != null) {
            if (sql.length() > 0)
                sql.append(" and");
            List object = (List) map.get("datetime");
            sql.append(" to_char( b.create_time, 'yyyy-mm-dd hh24:mi:ss' ) >= :crateaDate AND to_char( b.create_time, 'yyyy-mm-dd hh24:mi:ss' ) <= :endDate ");
            p.put("crateaDate", object.get(0));
            p.put("endDate", object.get(1));
        }
        if (map.get("createPerson") != null) {
            if (sql.length() > 0)
                sql.append(" and");
            sql.append(" d.name like :createPerson");
            p.put("createPerson", "%" + map.get("createPerson") + "%");
        }
        if (map.get("docMark") != null) {
            if (sql.length() > 0)
                sql.append(" and");
            sql.append(" c.doc_Mark like :docMark");
            p.put("docMark", "%" + map.get("docMark") + "%");
        }
        sql.insert(0, "select distinct b.CONTENT,b.ID,b.CREATE_DATE,d.NAME,c.DOC_MARK,c.SUBJECT,c.CREATE_PERSON  " +
                "from ctp_comment_all b left join ctp_comment_all a on a.CREATE_ID = b.CREATE_ID left join EDOC_SUMMARY c on b.CREATE_ID = c.id " +
                "left join org_member d on b.CREATE_ID = d.id "+(sql.length() > 0 ? " where " : ""));
        sql.append(" and b.CONTENT is not null order by b.CREATE_DATE desc");
        pa.put("p", p);
        pa.put("sql", sql);
        return pa;
    }
}
