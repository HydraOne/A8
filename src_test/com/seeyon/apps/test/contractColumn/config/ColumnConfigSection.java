package com.seeyon.apps.test.contractColumn.config;

import com.seeyon.ctp.portal.section.BaseSectionImpl;
import com.seeyon.ctp.portal.section.templete.BaseSectionTemplete;
import com.seeyon.ctp.portal.section.templete.HtmlTemplete;
import com.seeyon.ctp.util.Strings;

import java.util.Map;


/**
 * @author sheHaiTao
 * @data 2021/10/29 - 15:07
 * @Description 栏目配置类
 */
public class ColumnConfigSection extends BaseSectionImpl {

    /**
     * 栏目ID，必须与spring配置文件中的ID相同;如果是原栏目改造，请尽量保持与原栏目ID一致
     * @return
     */
    @Override
    public String getId() {
        return "ColumnConfigSection";
    }

    /**
     * 栏目显示的名字，必须实现国际化，在栏目属性的“columnsName”中存储
     * @param preference
     * @return
     */
    @Override
    public String getName(Map preference) {
        String name = (String) preference.get("columnsName");
        if(Strings.isBlank(name)){
            return "ColumnConfigSection";
        }else{
            return name;
        }
    }

    /**
     * 栏目需要展现总数据条数时重写
     * @param preference
     * @return
     */
    @Override
    public Integer getTotal(Map preference) {
        return null;
    }

    /**
     * 栏目图标，暂不需要实现
     * @return
     */
    @Override
    public String getIcon() {
        return null;
    }

    /**
     * 栏目解析主方法
     * @param map
     * @return
     */
    @Override
    public BaseSectionTemplete projection(Map<String, String> map) {
        //更新栏目条数和高度
        String sectionCount = "5";
        if (map.get("sectionCount") != null) {
            sectionCount = map.get("sectionCount");
        }
        String sectionHeight = "200";
        if (map.get("sectionHeight") != null) {
            sectionHeight = map.get("sectionHeight");
        }
        //创建直出html代码片段的模板
        HtmlTemplete ht = new HtmlTemplete();
        //访问controller的url和相关的方法
        //内嵌框架请求跳转到写好的jsp中
        String div = "<div style='width:100%;overflow-x:scroll;'>" +
                     "    <iframe style='width:100%;height:200px;' frameborder='no' border='0'" +
                     "            src='/seeyon/columnController.do?method=loadPage&sectionCount=" + sectionCount + "'>" +
                     "    </iframe>" +
                     "</div>";
        ht.setHeight(sectionHeight);
        ht.setHtml(div);
        ht.setModel(HtmlTemplete.ModelType.inner);
        ht.setShowBottomButton(true);
        return ht;
    }
}
