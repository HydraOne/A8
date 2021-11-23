package com.seeyon.apps.test.paymentCalendar.dao.impl;

import com.seeyon.apps.test.paymentCalendar.dao.FormTimeViewMapper;
import com.seeyon.apps.timeview.po.TimeViewAuth;
import com.seeyon.apps.timeview.po.TimeViewInfo;
import com.seeyon.ctp.util.DBAgent;

public class FormTimeViewMapperImpl implements FormTimeViewMapper {
    @Override
    public void addTimeViewInfo(TimeViewInfo timeViewInfo) {
        DBAgent.save(timeViewInfo);
    }

    @Override
    public void addTimeViewAuth(TimeViewAuth timeViewAuth) {
        DBAgent.save(timeViewAuth);
    }
}
