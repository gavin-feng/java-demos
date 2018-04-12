package com.power.page.base.bean;

import com.power.page.BasePage;

public class MenuInfoBean {
    private BasePage page;
    private String menuXPath;
    private String subMenuXPath;
    private String iframeId;
    
    public MenuInfoBean(BasePage page, String menuXPath, String subMenuXPath, String iframeId) {
        this.page = page;
        this.menuXPath = menuXPath;
        this.subMenuXPath = subMenuXPath;
        this.iframeId = iframeId;
    }
    
    public BasePage getPage() {
        return page;
    }
    public void setPage(BasePage page) {
        this.page = page;
    }
    public String getMenuXPath() {
        return menuXPath;
    }
    public void setMenuXPath(String menuXPath) {
        this.menuXPath = menuXPath;
    }
    public String getSubMenuXPath() {
        return subMenuXPath;
    }
    public void setSubMenuXPath(String subMenuXPath) {
        this.subMenuXPath = subMenuXPath;
    }
    public String getIframeId() {
        return iframeId;
    }
    public void setIframeId(String iframeId) {
        this.iframeId = iframeId;
    }
}
